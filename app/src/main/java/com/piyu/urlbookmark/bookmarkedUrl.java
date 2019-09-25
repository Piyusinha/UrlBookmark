package com.piyu.urlbookmark;

public class bookmarkedUrl {
    private String foldername;
    private String url;
    public bookmarkedUrl(String foldername, String url) {
        this.foldername = foldername;
        this.url = url;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
