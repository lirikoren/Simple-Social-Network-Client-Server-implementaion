package bgu.spl.net.impl.messages;

public class Message {
    private short opcode;

    public Message(short opcode){
        this.opcode=opcode;
    }

    public short getOpcode() {
        return opcode;
    }
}
