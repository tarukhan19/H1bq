package com.dbvertex.company.h1bq.model;

public class NestedCommentDTO {

    String userId;
    String nestedCommentId;
    String userName;
    String commentMsg;
    String time;
    String count_like;
    String count_comment;
    String likestatus;
    String commentId;
    String mentionusername;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    String image;
    private static boolean isLikeSelected;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMentionusername() {
        return mentionusername;
    }

    public void setMentionusername(String mentionusername) {
        this.mentionusername = mentionusername;
    }

    public String getNestedCommentId() {
        return nestedCommentId;
    }

    public void setNestedCommentId(String nestedCommentId) {
        this.nestedCommentId = nestedCommentId;
    }

    public String getCommentMsg() {
        return commentMsg;
    }

    public void setCommentMsg(String commentMsg) {
        this.commentMsg = commentMsg;
    }



    public void setLikeSelected(boolean isLikeSelected) {
        isLikeSelected = isLikeSelected;
    }


    public static boolean isLikeSelected() {
        return isLikeSelected;
    }





    public String getLikeStatus() {
        return likestatus;
    }

    public void setLikestatus(String likestatus) {
        this.likestatus = likestatus;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName  = userName ;
    }



    public String getCommentCount() {
        return count_comment;
    }

    public void setCommentCount(String count_comment) {
        this.count_comment =count_comment ;
    }



    public String getTimeCount() {
        return time;
    }

    public void setTimeCount(String time) {
        this.time =time ;
    }

    public String getLikeCount() {
        return count_like;
    }

    public void setLikeCount(String count_like) {
        this.count_like =count_like ;
    }

}
