package utils;

import java.io.IOException;
import java.time.Instant;

public class FeedCommentPostData {

    public String text;
    public String origPostId;
    public String id;
    public long postingDate;

    public FeedCommentPostData() {
    }


    public FeedCommentPostData(String text, String sender, String origPostId) {
        this.text = text;
        this.id = sender + postingDate;
        this.origPostId = origPostId;
    }


    public String generateId(String sender) {
        this.postingDate = Instant.now().toEpochMilli();
        this.id = sender + postingDate;
        return this.id;
    }
}
