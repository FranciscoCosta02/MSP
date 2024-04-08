package utils;

import com.google.cloud.Timestamp;

public class BugDataList {

    public String id;
    public String sender;
    public String reason;
    public String text;
    public String formattedTime;

    public BugDataList() {}


    public void setBugData(String id, String sender, String reason, String text, String formattedTime) {
        this.id = id;
        this.sender = sender;
        this.reason = reason;
        this.text = text;
        this.formattedTime = formattedTime;
    }
}
