package utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class AnomalyData {

    public String id;
    public String sender;
    public String reason;
    public String text;
    public String formattedTime;
    public boolean solved;

    public AnomalyData() {}

    public AnomalyData(String sender, String reason, String text) {
        this.sender = sender;
        this.reason = reason;
        this.text = text;
        convertTime(System.currentTimeMillis());
        this.id = sender + "." + reason;
        this.solved = false;
    }


    private void convertTime(long creationTime) {
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTime),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.formattedTime = triggerTime.format(myFormatObj);
    }
    public void setAnomalyData(String id, String sender, String reason, String text, String formattedTime, boolean solved) {
        this.id = id;
        this.sender = sender;
        this.reason = reason;
        this.text = text;
        this.formattedTime = formattedTime;
        this.solved = solved;
    }
}
