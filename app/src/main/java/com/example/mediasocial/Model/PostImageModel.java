package com.example.mediasocial.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostImageModel {

    private String imageURL, id, description;
    @ServerTimestamp
    private Date timestamp;

    public PostImageModel() {
    }

    public PostImageModel(String imageURL, String id, String description, Date timestamp) {
        this.imageURL = imageURL;
        this.id = id;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
