package utils;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.time.Instant;

public class FeedPostData {

    public String title;
    public String text;
    public String photo; // base64 encoded image

    public long postingDate;

    public String id;

    public FeedPostData() {
    }


    public FeedPostData(String title, String text, String photo) {
        this.title = title;
        this.text = text;
        this.photo = photo;
        this.postingDate = Instant.now().toEpochMilli();
    }


    public String generateId(String sender) throws IOException {
        this.id = sender + postingDate;
        return this.id;
    }
}
