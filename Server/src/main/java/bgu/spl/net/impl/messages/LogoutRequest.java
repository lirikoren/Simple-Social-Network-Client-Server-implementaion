package bgu.spl.net.impl.messages;

public class LogoutRequest extends Message{
    private static short opcode=3;

    public LogoutRequest(){
        super(opcode);
    }
}
