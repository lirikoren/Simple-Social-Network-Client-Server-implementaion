package bgu.spl.net.impl.messages;

public class RegisterRequest extends Message {
    private String username;
    private String password;
    private String birthday;
    private static short opcode = 1;

    public RegisterRequest(String _username,String _password, String _birthday) {
        super(opcode);
        this.password = _password;
        this.username = _username;
        this.birthday = _birthday;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }
}
