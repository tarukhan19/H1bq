package com.dbvertex.company.h1bq.model;

public class NotificationListDto {
    String sender_user,id,message,created_at,post_id,post_Type;

    public String getPost_Type() {
        return post_Type;
    }

    public void setPost_Type(String post_Type) {
        this.post_Type = post_Type;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getSender_user() {
        return sender_user;
    }

    public void setSender_user(String sender_user) {
        this.sender_user = sender_user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
