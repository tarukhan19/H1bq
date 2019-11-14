package com.dbvertex.company.h1bq.model;

public class SearchDTO {
    private String title;
    private String postid;
    private String bucketId,postType;

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String userId;
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title ;
    }
    public String getPostId() {
        return postid;
    }

    public void setPostId(String postId) {
        this.postid = postId;
    }


    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }
}
