package bgu.spl.net.impl.messages;

import java.util.LinkedList;
import java.util.List;

public class StatRequest extends Message{
    private LinkedList<String> usernames;
    private static short opcode=8;

    public StatRequest(LinkedList<String> _usernames) {
        super(opcode);
        this.usernames = _usernames;
    }

    public LinkedList<String> getUsernames() {
        return usernames;
    }

}
