package bgu.spl.net.srv;

import bgu.spl.net.impl.messages.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private String userName;
    private String password;
    private int connectionId;
    private boolean isLoggedIn;
    private String birthday;
    private ConcurrentLinkedQueue<Message> messagesToRecieve;
    private ConcurrentLinkedQueue<String> followers;
    private ConcurrentLinkedQueue<String> following;
    private ConcurrentLinkedQueue<String> posts;
    private ConcurrentLinkedQueue<String> blockedBy;

    public Client(String userName, String password,int connectionId,String birthday){
        this.userName=userName;
        this.password=password;
        this.connectionId=connectionId;
        this.birthday = birthday;
        isLoggedIn = false;
        messagesToRecieve = new ConcurrentLinkedQueue<>();
        followers= new ConcurrentLinkedQueue<>();
        following = new ConcurrentLinkedQueue<>();
        posts=new ConcurrentLinkedQueue<>();
        blockedBy=new ConcurrentLinkedQueue<>();
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public ConcurrentLinkedQueue<Message> getMessagesToRecieve() {
        return messagesToRecieve;
    }

    public ConcurrentLinkedQueue<String> getFollowers() {
        return followers;
    }

    public ConcurrentLinkedQueue<String> getFollowing() {
        return following;
    }

    public ConcurrentLinkedQueue<String> getPosts() {
        return posts;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isFollowing(String userName){
        return following.contains(userName);
    }

    public boolean isFollower(String userName){
        return followers.contains(userName);
    }

    public boolean startFollow(String userName){
        if(isFollowing(userName)){
            return false;
        }
        else{
            following.add(userName);
            return true;
        }
    }

    public boolean stopFollow(String userName){
        return following.remove(userName);
    }

    public void setFollowers(String userName,boolean toAdd){
        if(toAdd){
            followers.add(userName);
        }
        else {
            followers.remove(userName);
        }
    }

    public void addPost(String post){
        posts.add(post);
    }

    public short getNumOfPost(){
        return (short)posts.size();
    }

    public short getAge(){
        int year = Integer.valueOf(birthday.substring(6));
        int age = 2022-year;
        return (short)age;
    }

    public ConcurrentLinkedQueue<String> getBlockedBy() {
        return blockedBy;
    }

    public void addToBlockedByList(String userName) {
        blockedBy.add(userName);
    }
}
