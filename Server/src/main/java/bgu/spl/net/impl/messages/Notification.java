package bgu.spl.net.impl.messages;

public class Notification extends Message{
    private byte notificationType;
    private String postingUser;
    private String content;
    private static short opcode=9;

    public Notification(String content, byte _notificationType, String postingUser) {
        super(opcode);
        this.notificationType = _notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }
    public byte getNotificationType() {
        return notificationType;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }

    public short getOpcode() {
        return opcode;
    }
}
