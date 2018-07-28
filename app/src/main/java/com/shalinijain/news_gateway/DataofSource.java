package com.shalinijain.news_gateway;

import java.io.Serializable;

/**
 * Created by Admin on 23-04-2018.
 */

public class DataofSource implements Serializable {
    private String source_id;
    private String source_name;
    private String source_category;
    private String source_url;

    @Override
    public String toString() {
        return "DataofSource{" +
                "source_id='" + source_id + '\'' +
                ", source_name='" + source_name + '\'' +
                ", source_category='" + source_category + '\'' +
                ", source_url='" + source_url + '\'' +
                '}';
    }

    public String getSource_id() {
        return source_id;
    }

    public void setSource_id(String source_id) {
        this.source_id = source_id;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getSource_category() {
        return source_category;
    }

    public void setSource_category(String source_category) {
        this.source_category = source_category;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }
}

