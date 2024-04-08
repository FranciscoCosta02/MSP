package utils;


public class ActivationData {
    public String email;
    public String clipUser;
    public String clipPass;

    public String[] clipInfo;

    public ActivationData(){}

    public ActivationData(String email, String clipUser, String clipPass, String[] clipInfo) {
        this.email = email;
        this.clipUser = clipUser;
        this.clipPass = clipPass;
        this.clipInfo = clipInfo;
    }

    public boolean clipExists() {
        return clipUser.equals(clipInfo[0]) && clipPass.equals(clipInfo[1]);
    }
}
