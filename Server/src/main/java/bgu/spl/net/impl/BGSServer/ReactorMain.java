package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.ConnectionsImpl;
import bgu.spl.net.impl.messages.Message;
import bgu.spl.net.srv.*;

import javax.xml.crypto.Data;
import java.util.function.Supplier;

public class ReactorMain {
    public static void main(String[] args) {
        DataBase dataBase=DataBase.getInstance();
        Supplier<MessageEncoderDecoder<Message>> encdec= BidiEncoderDecoder::new;
        Supplier<BidiMessagingProtocol<Message>> protocolSupplier= ()->new BidiMessagingProtocolImpl(dataBase);

        Server.reactor(
                Integer.parseInt(args[1]),//threads
                Integer.parseInt(args[0]), //port
                protocolSupplier, //protocol factory
                encdec, //message encoder decoder factory
                new ConnectionsImpl<Message>()

        ).serve();
    }


}