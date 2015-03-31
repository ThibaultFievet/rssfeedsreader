package fievetthibault.rssfeedreader;

import java.io.Serializable;

public class Settings implements Serializable {

    private int id;
    private boolean readArticles;
    private String sort;
    private boolean sortSource;
    private String deleteDate;

    public Settings() {}

    public Settings(boolean read, String sort, boolean sortSource, String deleteDate) {
        this.readArticles = read;
        this.sort = sort;
        this.sortSource = sortSource;
        this.deleteDate = deleteDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReadArticles() {
        return readArticles;
    }

    public void setReadArticles(boolean readArticles) {
        this.readArticles = readArticles;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public boolean isSortSource() {
        return sortSource;
    }

    public void setSortSource(boolean sortSource) {
        this.sortSource = sortSource;
    }

    public String getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(String deleteDate) {
        this.deleteDate = deleteDate;
    }

}

