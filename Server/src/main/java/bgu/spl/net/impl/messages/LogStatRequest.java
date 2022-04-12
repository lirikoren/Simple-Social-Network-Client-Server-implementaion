package bgu.spl.net.impl.messages;

public class LogStatRequest extends Message{
    private static short opcode=7;

    public LogStatRequest()
    {
        super(opcode);
    }

}
