package bgu.spl.net.impl.messages;

public class PostRequest extends Message{
    private static short opcode = 5;
    private String content;

    public PostRequest(String content) {
        super(opcode);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
