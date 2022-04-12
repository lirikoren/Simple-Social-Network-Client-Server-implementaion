package bgu.spl.net.impl.messages;

public class ErrorResponse extends Message{
    private static short opcode=11;
    private short messageOpcode;

    public ErrorResponse(short _messageOpcode) {
        super(opcode);
        this.messageOpcode=_messageOpcode;
    }
    public short getMessageOpcode(){
        return messageOpcode;
    }
}
