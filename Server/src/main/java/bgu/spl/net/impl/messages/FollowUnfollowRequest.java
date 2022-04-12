package bgu.spl.net.impl.messages;

public class FollowUnfollowRequest extends Message{
    private byte followUnfollow;
    private final String requestedUsername;
    private static short opcode=4;

    public FollowUnfollowRequest(byte followUnfollow, String _requestedUsername) {
        super(opcode);
        this.followUnfollow = followUnfollow;
        requestedUsername = _requestedUsername;
    }

    public byte getFollowUnfollow() {
        return followUnfollow;
    }

    public String getRequestedUsername() {
        return requestedUsername;
    }
}
