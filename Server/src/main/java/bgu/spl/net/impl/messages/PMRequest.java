package bgu.spl.net.impl.messages;

public class PMRequest extends Message{
    private String username;
    private String content;
    private static short opcode = 6;

    public PMRequest(String _username,String _content) {
        super(opcode);
        this.username=_username;
        this.content=_content;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
