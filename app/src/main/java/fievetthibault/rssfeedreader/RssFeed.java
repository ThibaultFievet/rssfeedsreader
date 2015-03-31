package fievetthibault.rssfeedreader;

import java.io.Serializable;

public class RssFeed implements Serializable {

    private int id;
    private String url;
    private String name;

    public RssFeed() {}

    public RssFeed(String _url, String _name) {
        this.url = _url;
        this.name = _name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString(){
        return "{"+url+","+name+"}";
    }
}
