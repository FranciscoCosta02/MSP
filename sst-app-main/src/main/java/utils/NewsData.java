package utils;


public class NewsData {

    public String text;

    public String image; // base64 image needs to be converted

    public String link;


    public NewsData(){}

    public NewsData(String text, String image, String link) {

        this.text = text;
        this.image = image;
        this.link = link;

    }

}
