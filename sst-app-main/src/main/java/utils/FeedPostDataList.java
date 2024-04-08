package utils;

import com.google.cloud.datastore.Value;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class FeedPostDataList {

    public String title;
    public String text;
    public String photo; // base64 encoded image
    public long postingDate;
    public String postId;
    public String[] comments;

    public FeedPostDataList() {
    }


    public FeedPostDataList(String postId, String title, String text, String photo, long postingDate, String[] comments) {
        this.postId = postId;
        this.title = title;
        this.text = text;
        this.photo = photo;
        this.postingDate = Instant.now().toEpochMilli();
        this.comments = comments;
    }


    public String generateId(String sender) throws IOException {
        this.postId = sender + postingDate;
        return this.postId;
    }
}
