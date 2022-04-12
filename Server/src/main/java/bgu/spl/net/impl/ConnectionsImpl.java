package bgu.spl.net.impl;

import bgu.spl.net.srv.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<Integer, ConnectionHandler> connectionHandlers = new ConcurrentHashMap<>();

    public ConnectionsImpl(){

    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(connectionHandlers.containsKey(connectionId)){
            connectionHandlers.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for(ConnectionHandler handler : connectionHandlers.values()){
            handler.send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connectionHandlers.remove(connectionId);
    }

    public void connect(int connectionId,ConnectionHandler handler){
        connectionHandlers.put(connectionId,handler);
    }
}
