package bgu.spl.net.impl.messages;

import java.util.LinkedList;

public class Ack extends Message{

    private short messageOpcode;
    private static short opcode=10;
    //for follow unfollow
    private String userName;
    //for stat
    private short age;
    private short numOfPosts;
    short numOfFollowers;
    short numOfFollowing;



    public Ack(short _messageOpcode) {
        super(opcode);
        this.messageOpcode=_messageOpcode;
    }
    public Ack(short messageOpcode,String userName){
        super(opcode);
        this.userName=userName;
        this.messageOpcode=messageOpcode;
    }
    public Ack(short messageOpcode,short age,short numOfPosts,short numOfFollowers,short numOfFollowing){
        super(opcode);
        this.messageOpcode=messageOpcode;
        this.age=age;
        this.numOfPosts=numOfPosts;
        this.numOfFollowers=numOfFollowers;
        this.numOfFollowing=numOfFollowing;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

    public short getAge() {
        return age;
    }

    public short getNumOfPosts() {
        return numOfPosts;
    }

    public short getNumOfFollowers() {
        return numOfFollowers;
    }

    public short getNumOfFollowing() {
        return numOfFollowing;
    }

    public String getUserName() {
        return userName;
    }

}
