package utils;

import com.google.cloud.Timestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class BugData {

    public String id;
    public String sender;
    public String reason;
    public String text;
    public Timestamp formattedTime;

    public BugData() {}

    public BugData(String sender, String reason, String text) {
        this.sender = sender;
        this.reason = reason;
        this.text = text;
        this.id = sender + "." + reason;
        this.formattedTime=Timestamp.now();
    }


    public void setBugData(String id, String sender, String reason, String text, Timestamp formattedTime) {
        this.id = id;
        this.sender = sender;
        this.reason = reason;
        this.text = text;
        this.formattedTime = formattedTime;
    }
}
