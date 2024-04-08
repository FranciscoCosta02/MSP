package utils;

public class StatsData {

    public String username;
    public Long nLogins;
    public Long nFails;
    public String firstLogin;
    public String lastLogin;
    public String lastAttempt;

    public StatsData() {}

    public StatsData(String username, Long nLogins, Long nFails, String firstLogin, String lastLogin, String lastAttempt) {
        this.username = username;
        this.nLogins = nLogins;
        this.nFails = nFails;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.lastAttempt = lastAttempt;
    }

    public StatsData(Long nLogins, Long nFails, String firstLogin, String lastLogin, String lastAttempt) {
        this.username = "";
        this.nLogins = nLogins;
        this.nFails = nFails;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.lastAttempt = lastAttempt;
    }
}
