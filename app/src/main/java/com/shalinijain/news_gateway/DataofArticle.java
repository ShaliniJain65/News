package com.shalinijain.news_gateway;

import java.io.Serializable;

/**
 * Created by Admin on 23-04-2018.
 */

public class DataofArticle implements Serializable {

    private String author;
    private String title;
    private String description;
    private String urlToimage;
    private String publisheddate;
    private String weburl;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToimage() {
        return urlToimage;
    }

    public void setUrlToimage(String urlToimage) {
        this.urlToimage = urlToimage;
    }

    public String getPublisheddate() {
        return publisheddate;
    }

    public void setPublisheddate(String publisheddate) {
        this.publisheddate = publisheddate;
    }

    public String getWeburl() {
        return weburl;
    }

    public void setWeburl(String weburl) {
        this.weburl = weburl;
    }

    @Override
    public String toString() {
        return "DataofArticle{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", urlToimage='" + urlToimage + '\'' +
                ", publisheddate='" + publisheddate + '\'' +
                ", weburl='" + weburl + '\'' +
                '}';
    }
}
