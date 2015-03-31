package fievetthibault.rssfeedreader;

import java.io.Serializable;

public class RssItem implements Serializable {

    private int id;
    private String title;
    private String link;
    private String description;
    private String date;
    private String source;
    private boolean read;

    public RssItem() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean getRead() {
        return read;
    }

    public String toString(){
        return "{"+title+","+link+","+description+","+date+","+source+"}";
    }
}
