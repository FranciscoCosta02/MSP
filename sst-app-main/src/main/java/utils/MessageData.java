package utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.UUID;

public class MessageData {
    public String id;
    public String sender;
    public String dest;
    public String text;
    public String formattedDate;

    public MessageData(){}

    public MessageData(String sender, String dest, String text) {
        UUID uuid = UUID.randomUUID();
        this.id = String.valueOf(uuid.getLeastSignificantBits() & Long.MAX_VALUE);
        this.sender = sender;
        this.dest = dest;
        this.text = text;
        convertTimestamp(System.currentTimeMillis());
    }

    public MessageData(String id, String sender, String dest, String formattedDate, String text) {
        this.id = id;
        this.sender = sender;
        this.dest = dest;
        this.formattedDate = formattedDate;
        this.text = text;
    }

    private void convertTimestamp(long creationTime) {
        LocalDateTime triggerTime =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(creationTime),
                        TimeZone.getDefault().toZoneId());
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        this.formattedDate = triggerTime.format(myFormatObj);
    }

    public boolean checkSpam(String timestamp) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime mine = LocalDateTime.parse(formattedDate, myFormatObj).minusSeconds(30);
        LocalDateTime comparing = LocalDateTime.parse(timestamp, myFormatObj);
        return mine.isBefore(comparing);
    }

}
