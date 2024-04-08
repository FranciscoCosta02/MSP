package utils;

import com.google.cloud.datastore.Value;

import java.util.List;

public class GroupInfo {

    String name;
    String owner;
    String[] participants;
    String privacy;
    public GroupInfo() {}

    public GroupInfo(String name, String privacy, String owner, List<Value<String>> participants) {
        this.name = name;
        this.owner = owner;
        this.privacy = privacy;
        this.participants = new String[participants.size()];
        int current = 0;
        for(Value<String> v: participants){
            this.participants[current++] = v.get();
        }
    }
}
