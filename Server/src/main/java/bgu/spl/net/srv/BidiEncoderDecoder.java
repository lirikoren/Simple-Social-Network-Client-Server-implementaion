package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.messages.*;

import javax.management.remote.NotificationResult;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.prefs.PreferenceChangeListener;


public class BidiEncoderDecoder implements MessageEncoderDecoder<Message> {
    private byte[] bytes = new byte[1 << 10]; //start with more than 1k
    private int len = 0;
    private short opcode;

    public BidiEncoderDecoder() {
        //maybe to do?
    }

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (nextByte == ';') {
            return buildMessage();
        }
        //; is 59
        pushByte(nextByte);
        return null; //not a line yet
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }

    public Message buildMessage() {
        Message result = null;
        if (bytes.length >= 2) {
            byte[] opcodeBytes = {bytes[0], bytes[1]};
            opcode = bytesToShort(opcodeBytes);
            switch (opcode) {
                case 1:
                    result = buildRegister();
                    break;
                case 2:
                    result = buildLogin();
                    break;
                case 3:
                    result = new LogoutRequest();
                    break;
                case 4:
                    result = buildFollowUnfollow();
                    break;
                case 5:
                    result = buildPost();
                    break;
                case 6:
                    result = buildPM();
                    break;
                case 7:
                    result = new LogStatRequest();
                    break;
                case 8:
                    result = buildStat();
                    break;
                case 12:
                    result = buildBlock();
                    break;
            }
        }
        bytes = new byte[1<<10];
        len = 0;
        return result;
    }

    public Message buildRegister() {
        int start = 2;
        int curr = 2;

        while (bytes[curr] != '\0') {
            curr++;
        }
        String username = new String(bytes, 2, curr - 2, StandardCharsets.UTF_8);
        start = curr + 1;
        curr++;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String password = new String(bytes, start, curr - start, StandardCharsets.UTF_8);
        curr++;
        start=curr;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String birthday = new String(bytes, start, curr - start, StandardCharsets.UTF_8);
        return new RegisterRequest(username, password, birthday);
    }

    public Message buildLogin() {
        int start = 2;
        int curr = 2;

        while (bytes[curr] != '\0') {
            curr++;
        }
        String username = new String(bytes, 2, curr - 2, StandardCharsets.UTF_8);
        start = curr + 1;
        curr++;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String password = new String(bytes, start, curr - start, StandardCharsets.UTF_8);
        curr++;
        byte captcha;
        captcha=bytes[curr];
        return new LoginRequest(username, password, captcha);
    }

    public Message buildFollowUnfollow() {
        int curr = 3;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String username = new String(bytes, 3, curr - 3, StandardCharsets.UTF_8);
        return new FollowUnfollowRequest(bytes[2], username);
    }

    public Message buildPost() {
        int curr = 2;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String content = new String(bytes, 2, curr - 2, StandardCharsets.UTF_8);
        return new PostRequest(content);
    }

    public Message buildPM() {
        int start = 2;
        int curr = 2;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String username = new String(bytes, 2, curr - 2, StandardCharsets.UTF_8);
        start = curr + 1;
        curr++;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String content = new String(bytes, start, curr - start, StandardCharsets.UTF_8);
        return new PMRequest(username, content);
    }

    public Message buildStat() {
        LinkedList<String> usernames = new LinkedList<>();
        int start = 2;
        int curr = 2;
        while (bytes[curr] != '\0') {
            curr++;
            if (bytes[curr] == '|') {
                usernames.add(new String(bytes, start, curr - start, StandardCharsets.UTF_8));
                start = curr + 1;
            }
        }
        return new StatRequest(usernames);
    }

    public Message buildBlock() {
        int curr = 2;
        while (bytes[curr] != '\0') {
            curr++;
        }
        String username = new String(bytes, 2, curr - 2, StandardCharsets.UTF_8);
        return new BlockRequest(username);
    }


    @Override
    public byte[] encode(Message message) {
        byte[] result;
        byte[] opcodeInBytes = shortToBytes(message.getOpcode());
        switch (message.getOpcode()) {
            //----------Notification -----------//
            case (9): {
                byte notificationType = ((Notification) message).getNotificationType();
                byte[] postingUser = ((Notification) message).getPostingUser().getBytes();
                byte[] content = ((Notification) message).getContent().getBytes();
                LinkedList<Byte> outputList = new LinkedList<>();
                outputList.add(opcodeInBytes[0]);
                outputList.add(opcodeInBytes[1]);
                outputList.add(notificationType);

                for (int i = 0; i < postingUser.length; i++)
                    outputList.add(postingUser[i]);
                outputList.add((byte) '\0');
                for (int i = 0; i < content.length; i++)
                    outputList.add(content[i]);
                outputList.add((byte) '\0');
                result = new byte[outputList.size()+1];
                for (int i = 0; i < outputList.size(); i++)
                    result[i] = outputList.get(i);
                result[outputList.size()]=(byte)';';
                return result;
            }
            //----------  ACK  -------------//
            case (10): {
                switch (((Ack) message).getMessageOpcode()) {
                    ///----Follow Ack---
                    case (4): {
                        byte[] messageOpcode = shortToBytes(((Ack) message).getMessageOpcode());
                        byte[] userNameInByte = ((Ack) message).getUserName().getBytes();
                        result = new byte[6 + userNameInByte.length];
                        result[0] = opcodeInBytes[0];
                        result[1] = opcodeInBytes[1];
                        result[2] = messageOpcode[0];
                        result[3] = messageOpcode[1];

                        for (int i = 0; i < userNameInByte.length; i++) {
                            result[4 + i] = userNameInByte[i];
                        }
                        result[userNameInByte.length + 4] =  '\0';
                        result[userNameInByte.length + 5]=';';
                        return result;
                    }
                    case(7):
                    case (8): { //ack to Stat and logstat
                        result=new byte[13];
                        byte[] messageOpcode = shortToBytes(((Ack) message).getMessageOpcode());
                        byte[] age=shortToBytes((short)((Ack) message).getAge());
                        byte[] numOfPost = shortToBytes(((Ack) message).getNumOfPosts());
                        byte[] numOfFollowers = shortToBytes(((Ack) message).getNumOfFollowers());
                        byte[] numOfFollowing = shortToBytes(((Ack) message).getNumOfFollowing());

                        result[0] = opcodeInBytes[0];
                        result[1] = opcodeInBytes[1];
                        result[2] = messageOpcode[0];
                        result[3] = messageOpcode[1];
                        result[4] = age[0];
                        result[5] = age[1];
                        result[6] = numOfPost[0];
                        result[7] = numOfPost[1];
                        result[8] = numOfFollowers[0];
                        result[9] = numOfFollowers[1];
                        result[10] = numOfFollowing[0];
                        result[11] = numOfFollowing[1];
                        result[12]=(byte)';';
                        return result;
                    }

                    default: {
                        //all other acks
                        result = new byte[5];
                        byte[] messageOpcode = shortToBytes(((Ack) message).getMessageOpcode());
                        result[0] = opcodeInBytes[0];
                        result[1] = opcodeInBytes[1];
                        result[2] = messageOpcode[0];
                        result[3] = messageOpcode[1];
                        result[4]=(byte)';';
                        return result;
                    }
                }
            }
                //---------- Error ------------//
                case (11): {
                    result = new byte[5];
                    byte[] messageOpcode = shortToBytes(((ErrorResponse) message).getMessageOpcode());
                    result[0] = opcodeInBytes[0];
                    result[1] = opcodeInBytes[1];
                    result[2] = messageOpcode[0];
                    result[3] = messageOpcode[1];
                    result[4]=(byte)';';
                    return result;
                }
                default: {
                    return null;
                }
            }
    }

    public short bytesToShort(byte[] byteArr) //given function
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num) //given function
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
