package com.example.remotedebugger.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document
public class MainObjective {
    private String subreddit;
    private String selftext;
    private String authorfullname;
    private String title;
    private Integer createdutc;
    private String author;
    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public String getSelftext() {
        return selftext;
    }

    public void setSelftext(String selftext) {
        this.selftext = selftext;
    }


    public String getAuthorfullname() {
        return authorfullname;
    }

    @JsonProperty("author_fullname")
    public void setAuthorfullname(String authorfullname) {
        this.authorfullname = authorfullname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCreated_utc() {
        return createdutc;
    }

    @JsonProperty("created_utc")
    public void setCreated_utc(Integer createdutc) {
        this.createdutc = createdutc;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
