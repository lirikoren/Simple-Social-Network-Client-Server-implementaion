package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.Bidi;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    private HashMap<Integer,ConnectionHandler> connectionHandlerHashMap;
    private int numOfClients;
    private Connections connections;
    //private String[] wordsToFilter;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory,
            Connections connections
    ) {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
        this.sock = null;
        numOfClients=0;
        connectionHandlerHashMap=new HashMap<>();
        this.connections=connections;
        //String[] wordsToFilter={"Tramp","war","Boaz"};
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {
            System.out.println("Server started");

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                numOfClients++;

                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get(),
                        numOfClients,
                        connections
                );

                execute(handler);
                connectionHandlerHashMap.put(numOfClients,handler);

            }


        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
        if (sock != null)
            sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

//    public String[] getWordsToFilter(){
//        return wordsToFilter;
//    }
}