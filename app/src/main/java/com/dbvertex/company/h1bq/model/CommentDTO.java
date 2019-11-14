package com.dbvertex.company.h1bq.model;

import java.util.List;

public class CommentDTO {
    String userId;
    String commentId;
    String nestedCommentId;
    String userName;
    String commentMsg;
    String time;
    String count_like;
    String count_comment;
    String likestatus;
    String image;
    private List<NestedCommentDTO> nestedCommentDTOS;
    private static boolean isLikeSelected;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
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


    public List<NestedCommentDTO> getNestedCommentDTOS() {
        return nestedCommentDTOS;
    }

    public void setNestedCommentDTOS(List<NestedCommentDTO> nestedCommentDTOS) {
        this.nestedCommentDTOS = nestedCommentDTOS;
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
        this.userName = userName;
    }


    public String getCommentCount() {
        return count_comment;
    }

    public void setCommentCount(String count_comment) {
        this.count_comment = count_comment;
    }


    public String getTimeCount() {
        return time;
    }

    public void setTimeCount(String time) {
        this.time = time;
    }

    public String getLikeCount() {
        return count_like;
    }

    public void setLikeCount(String count_like) {
        this.count_like = count_like;
    }

}
