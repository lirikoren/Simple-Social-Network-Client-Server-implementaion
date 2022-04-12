package bgu.spl.net.srv;

import bgu.spl.net.impl.messages.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    private int connectionId;
    private Connections<Message> connections;
    private static DataBase dataBase;
    private boolean shouldTerminate;



    public BidiMessagingProtocolImpl(DataBase dataBase){
        this.dataBase = DataBase.getInstance();
    }

    @Override
    public void start(int connectionId, Connections<Message> connections) {
        this.connectionId=connectionId;
        this.connections=connections;
    }

    @Override
    public void process(Message message) {
        final Message msg = message;
        switch (message.getOpcode()) {
            //case 1 - REGISTER
            case (1): {
                String userName = ((RegisterRequest) message).getUsername();
                String password = ((RegisterRequest) message).getPassword();
                String birthday = ((RegisterRequest) message).getBirthday();
                if (dataBase.register(userName, password, connectionId, birthday)) {
                    connections.send(connectionId, new Ack((short) 1));
                } else {
                    connections.send(connectionId, new ErrorResponse((short) 1));
                }
                break;
            }
            //case 2 - LOGIN
            case (2): {
                String userName = ((LoginRequest) message).getUsername();
                String password = ((LoginRequest) message).getPassword();
                byte captch = ((LoginRequest) message).getCaptcha();
                if(!(captch=='1')) {
                    connections.send(connectionId, new ErrorResponse((short) 2));
                    break;
                }
                if(dataBase.logIn(userName,password,connectionId)) {
                    connections.send(connectionId, new Ack((short) 2));
                    //when user name was log out - we collect all the messages sent to him, now we will handle it
                    if(dataBase.GetQueue(userName).size()!=0) {
                        for (Message m : dataBase.getMessages(userName)) {
                            connections.send(connectionId, m);
                            dataBase.getMessages(userName).poll();
                        }

                    }
                }
                else
                    connections.send(connectionId, new ErrorResponse((short)2));
                break;
            }

                //case 3 - LOGOUT
                case (3): {
                    if (!dataBase.logOut(connectionId)) {
                        connections.send(connectionId, new ErrorResponse((short) 3));
                    } else {
                        connections.send(connectionId, new Ack((short) 3));
                        connections.disconnect(connectionId);
                    }
                    break;
                }
                //case 4 - FOLLOW
                case (4): {
                    if (!dataBase.isLoggedIn(connectionId)) {
                        connections.send(connectionId, new ErrorResponse((short) 4));
                        return;
                    }
                    String userNameToFollow = ((FollowUnfollowRequest) message).getRequestedUsername();
                    if(!dataBase.isRegister(userNameToFollow)){
                        connections.send(connectionId, new ErrorResponse((short) 4));
                        return;
                    }
                    byte followUnfollow = ((FollowUnfollowRequest) message).getFollowUnfollow();
                    if((dataBase.getClient(connectionId)!=null)&&(dataBase.getClient(connectionId)).getBlockedBy()!=null){
                    if ((dataBase.getClient(connectionId)).getBlockedBy().contains(dataBase.getUserName(dataBase.getId(userNameToFollow))))
                        return;}
                    if (dataBase.follow(followUnfollow, userNameToFollow, connectionId)) {
                        connections.send(connectionId, new Ack((short) 4, userNameToFollow));
                    } else {
                        connections.send(connectionId, new ErrorResponse((short) 4));
                    }
                    break;
                }

                //case 5 - POST
                case (5): {
                    if (!dataBase.isLoggedIn(connectionId)) {
                        connections.send(connectionId, new ErrorResponse(message.getOpcode()));
                        return;
                    }
                    String content = ((PostRequest) message).getContent();
                    LinkedList<Integer> usersToSend = dataBase.post(content, connectionId);
                    for (Integer userToSent_id : usersToSend) {
                        synchronized (dataBase.getClient(userToSent_id)) {
                            if (dataBase.isLoggedIn(userToSent_id)) {
                                connections.send(userToSent_id, new Notification(content, (byte) '\1', dataBase.getUserName(connectionId)));
                            } else {
                                dataBase.getMessages(dataBase.getClient(userToSent_id).getUserName()).add(new Notification(content, (byte) '\1', dataBase.getUserName(connectionId)));
                            }
                        }
                    }
                    connections.send(connectionId, new Ack((short) 5));
                    break;
                }
                //case 6 - PM
                case (6): {
                    if (!dataBase.isLoggedIn(connectionId)) {
                        connections.send(connectionId, new ErrorResponse((short) 6));
                        return;
                    }
                    String userName = ((PMRequest) message).getUsername();
                    String content = ((PMRequest) message).getContent();
                    Iterator<String> iter =(dataBase.getFilterWord()).iterator();
                    while (iter.hasNext()) {
                        content = content.replaceAll(iter.next(), "<Filtered>");
                    }
                    if(!dataBase.isRegister(userName)){
                        connections.send(connectionId, new ErrorResponse((short) 6));
                        return;
                    }
                    if((dataBase.getClient(connectionId)!=null)&&(dataBase.getClient(connectionId)).getBlockedBy()!=null){
                        if ((dataBase.getClient(connectionId)).getBlockedBy().contains(dataBase.getUserName(dataBase.getId(userName))))
                            return;}

                    if (dataBase.postPM(userName, content, connectionId)) {
                        if (!dataBase.isLoggedIn(dataBase.getId(userName)))
                            dataBase.getMessages(userName).add(new Notification(content, (byte) '\0', dataBase.getUserName(connectionId)));
                        else
                            connections.send(dataBase.getId(userName), new Notification(content, (byte) '\0', dataBase.getUserName(connectionId)));
                        connections.send(connectionId, new Ack((short) 6));
                    } else
                        connections.send(connectionId, new ErrorResponse(message.getOpcode()));
                    break;
                }
                //case 7 - LOGSTAT
            case (7): {
                if (!dataBase.isLoggedIn(connectionId)) {
                    connections.send(connectionId, new ErrorResponse((short)7));
                    return;
                }if (!dataBase.isRegister(dataBase.getUserName(connectionId))) {
                    connections.send(connectionId, new ErrorResponse((short)7));
                    return;
                }
                LinkedList<String> allUsers = dataBase.usersList();
                if (allUsers == null) {
                    connections.send(connectionId, new ErrorResponse((short)7));
                    return;
                } else {
                    for (String user : allUsers) {
                        if (dataBase.isLoggedIn(dataBase.getId(user))) {
                            if (((dataBase.getClient(connectionId) != null) && (dataBase.getClient(connectionId)).getBlockedBy() != null)) {
                                if (!((dataBase.getClient(connectionId)).getBlockedBy().contains(dataBase.getUserName(dataBase.getId(user)))))
                                    if (((dataBase.getClient(user) != null) && (dataBase.getClient(user)).getBlockedBy() != null)) {
                                        if (!((dataBase.getClient(user)).getBlockedBy().contains(dataBase.getUserName(connectionId))))
                                            connections.send(connectionId, new Ack((short) 7, (short) dataBase.getAge(user), dataBase.stat_getNumOfPost(user), dataBase.stat_getNumOfFollowers(user), dataBase.stat_getNumOfFollowing(user)));
                                    }
                            }
                        }
                    }
                }
                break;
            }
                //case 8 - STAT
            case (8): {
                if (!dataBase.isLoggedIn(connectionId)) {
                    connections.send(connectionId, new ErrorResponse((short)8));
                    return;
                }
                LinkedList<String> userNames = ((StatRequest) message).getUsernames();
                if((dataBase.getClient(connectionId))!=null) {
                    if((dataBase.getClient(connectionId)).getBlockedBy()!= null){
                        ConcurrentLinkedQueue<String> blocked = (dataBase.getClient(connectionId)).getBlockedBy();
                        for (String block : blocked) {
                            if (userNames.contains(block)){
                                connections.send(connectionId,new ErrorResponse((short) 8));
                                return;
                            }
                        }
                    }
                }
                if (userNames.size() != 0) {
                    for (String userName : userNames) {
                        if (dataBase.isRegister(userName)) {
                            connections.send(connectionId, new Ack((short) 8, (short) dataBase.getAge(userName), dataBase.stat_getNumOfPost(userName), dataBase.stat_getNumOfFollowers(userName), dataBase.stat_getNumOfFollowing(userName)));
                        }
                    }
                }
                break;
            }
                //case 12 - BLOCK
                case (12): {
                    if(!dataBase.isLoggedIn(connectionId)){
                        connections.send(connectionId,new ErrorResponse((short)12));
                        return;
                    }
                    String userName = ((BlockRequest) message).getUsernameToBlock();
                    if (!dataBase.isRegister(userName)) {
                        connections.send(connectionId, new ErrorResponse((short) 12));
                        return;
                    } else {
                        dataBase.block(connectionId, userName);
                        connections.send(connectionId, new Ack((short)12));
                    }
                    break;
                }
            }
        }


    @Override
    public boolean shouldTerminate() {
        return this.shouldTerminate;
    }
}
