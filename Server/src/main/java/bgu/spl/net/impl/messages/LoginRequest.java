package bgu.spl.net.impl.messages;

public class LoginRequest extends Message{
    private String  username;
    private String password;
    private byte captcha;
    private static short opcode=2;

    public LoginRequest(String _username, String _password, byte captcha) {
        super(opcode);
        this.username = _username;
        this.password = _password;
        this.captcha = captcha;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public byte getCaptcha() {
        return captcha;
    }
}
