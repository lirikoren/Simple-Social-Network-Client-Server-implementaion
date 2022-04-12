package bgu.spl.net.srv;

import bgu.spl.net.impl.messages.Message;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {
    private ConcurrentHashMap<String, Client> clients;
    private ConcurrentHashMap<Integer,String> clientsId_Names;
    private LinkedList<String> clientsByOrder ;
    private final Object lock;
    private static DataBase instance = null;
    //words to filter
    private LinkedList<String>  FilterWord;



    private DataBase() {
        clients = new ConcurrentHashMap<>();
        clientsId_Names = new ConcurrentHashMap<>();
        clientsByOrder = new LinkedList<>();
        lock = new Object();
        FilterWord=new LinkedList<>();
        FilterWord.add("Tramp"); FilterWord.add("war");FilterWord.add("Boaz");


    }
    public static synchronized DataBase getInstance(){
        if(instance==null)
            instance=new DataBase();
        return instance;
    }


    public String getUserName(int connectionId){
        return clientsId_Names.get(connectionId);
    }
    public Client getClient(int id){return clients.get(getUserName(id));}


    public int getId(String userName){
        for(String client : clients.keySet()){
            if(clients.get(client).getUserName().equals(userName)){
                return clients.get(client).getConnectionId();
            }
        }
        return -1;
    }

    public ConcurrentLinkedQueue<Message> GetQueue(String userName){
        return clients.get(userName).getMessagesToRecieve();
    }

    public synchronized boolean register(String userName,String password, int connectionId,String birthday){
        if(clients.containsKey(userName)){
            return false;
        }
        clients.putIfAbsent(userName,new Client(userName,password,connectionId,birthday));
        clientsId_Names.putIfAbsent(connectionId,userName);
        clientsByOrder.addLast(userName);
        return true;
    }

    public boolean logIn(String userName,String password,int connectionId){
        if (clientsId_Names.containsKey(connectionId)&&clients.get(clientsId_Names.get(connectionId)).isLoggedIn())//this connection id is not already logged in
            return false;

        if (clients.containsKey(userName)){
            ////user name is register
            //we synchronized the userName (inside client) which is unuiq to the client so only one login for this user name.
            //(because it is the field of the client and not the function pararameter it is unuiqe)
            synchronized (clients.get(userName)) {
                if (clients.get(userName).getPassword().equals(password)//correct password
                        && !clients.get(userName).isLoggedIn()) {
                    clients.get(userName).setConnectionId(connectionId);
                    clients.get(userName).setLoggedIn(true);
                    clientsId_Names.putIfAbsent(connectionId,userName);
                    return true;
                }
            }
        }
        return false;
    }
    public boolean isLoggedIn(int connectionId){
        if(clientsId_Names.containsKey(connectionId)){
            return clients.get(clientsId_Names.get(connectionId)).isLoggedIn();
        }
        return false;
    }

    public boolean isRegister(String userName){
        return (clients.containsKey(userName));
    }

    public boolean logOut(int connectionId){
        synchronized (lock){
            if(!clientsId_Names.containsKey(connectionId)){
                return false;
            }
            if(clients.get(clientsId_Names.get(connectionId)).isLoggedIn()){
                clients.get(clientsId_Names.get(connectionId)).setLoggedIn(false);
                return true;
            }
        }
        return false;
    }

    public boolean follow(byte follow_unfollow,String userName,int connectionId){
        synchronized (lock){
            if(follow_unfollow == '\0'){
                if(clients.containsKey(userName)&&clients.get(clientsId_Names.get(connectionId)).startFollow(userName)){
                    clients.get(userName).setFollowers(getUserName(connectionId),true);
                    return true;
                }
                return false;
            }
            if(clients.containsKey(userName)&&clients.get(clientsId_Names.get(connectionId)).stopFollow(userName)){

                clients.get(userName).setFollowers(getUserName(connectionId),false);
                return true;
            }
            return false;
        }
    }

    public ConcurrentLinkedQueue<Message> getMessages(String userName){
        return clients.get(userName).getMessagesToRecieve();
    }

    public LinkedList<Integer> post(String content,int connectionId){
        synchronized (clients.get(clientsId_Names.get(connectionId))){
            LinkedList<Integer> usersToSend = new LinkedList<>();
            String[] arrSplit = content.split(" ");
            for(String str: arrSplit){
                if(str.charAt(0)=='@' && clients.containsKey(str.substring(1)) && !usersToSend.contains(getId(str.substring(1)))){
                    usersToSend.add(getId(str.substring(1)));
                }
            }
            for(String follower: clients.get(clientsId_Names.get(connectionId)).getFollowers()){
                if(!usersToSend.contains(getId(follower)))
                    usersToSend.add(getId(follower));
            }
            clients.get(clientsId_Names.get(connectionId)).addPost(content);
            return usersToSend;
        }
    }
    public boolean postPM(String userName,String msgContent, int connectionId){
        if(!clients.containsKey(userName))
            return false;
        clients.get(clientsId_Names.get(connectionId)).addPost(msgContent);
        return true;
    }

    public boolean block(int connectionId, String userNameToBlock){
        if(getClient(connectionId).isFollowing(userNameToBlock)) {
            getClient(connectionId).stopFollow(userNameToBlock);
        }
        if(getClient(connectionId).isFollower(userNameToBlock)) {
            getClient(connectionId).setFollowers(userNameToBlock, false);
        }
        getClient(getId(userNameToBlock)).addToBlockedByList(getUserName(connectionId));
        return true;
    }


    public short stat_getNumOfPost(String userName){
      if(!clients.containsKey(userName))
          return -1;
      return clients.get(userName).getNumOfPost();
    }

    public short stat_getNumOfFollowing(String userName){
        if (!clients.containsKey(userName))
            return -1;
        return (short)clients.get(userName).getFollowing().size();
    }

    public short stat_getNumOfFollowers(String userName){
        if(!clients.containsKey(userName))
            return -1;
        return (short)clients.get(userName).getFollowers().size();
    }

    public LinkedList<String> usersList(){
        return clientsByOrder;
    }

    public int getAge(String userName){
        if(!clients.containsKey(userName))
            return -1;
        Client c =clients.get(userName);
        return c.getAge();
    }

    public Client getClient(String userName){
        if (clients.containsKey(userName)){
            return clients.get(userName);
        }
        return null;
    }

    public LinkedList<String> getFilterWord() {
        return FilterWord;
    }
}
