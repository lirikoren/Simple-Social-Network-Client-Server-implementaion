package bgu.spl.net.impl.messages;

public class BlockRequest extends Message{

    private static short opcode=12;
    private String usernameToBlock;

    public BlockRequest(String _usernameToBlock){
        super(opcode);
        this.usernameToBlock=_usernameToBlock;
    }

    public String getUsernameToBlock() {
        return usernameToBlock;
    }
}
