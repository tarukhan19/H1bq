package com.dbvertex.company.h1bq.model;

import java.util.List;

public class HomeDTO {

    private static boolean  isLikeSelected , isBookmarkSelected= false,isViewSelected;


    private String image,userName,answers,favourite,title,description,votesCount,viewCount,likeCount,commentCount,noOfDaysCount,userId,postid,
            posttype,ansslecetiontype,bookmarkstatus,likestatus,deleteStatus,reportabusestatus,viewstatus;

    int length;


    public String getReportabuseStatus() {
        return reportabusestatus;
    }

    public void setReportabusestatus(String reportabusestatus) {
        this.reportabusestatus = reportabusestatus;
    }

    public int getDescLength() {
        return length;
    }

    public void setDescLength(int length) {
        this.length = length;
    }

    public String getViewStatus() {
        return viewstatus;
    }

    public void setViewstatus(String viewstatus) {
        this.viewstatus = viewstatus;
    }

    public void setViewSelected(boolean isViewSelected)
    {
        isViewSelected = isViewSelected;
    }


    public static boolean isViewSelected()
    {
        return isViewSelected;
    }


    public String getDeleteStatus()
    {
        return deleteStatus;
    }

    public void setDeleteStatus(String deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public void setLikeSelected(boolean isLikeSelected) {
        isLikeSelected = isLikeSelected;
    }


    public static boolean isLikeSelected() {
        return isLikeSelected;
    }


    public void setBookmarkSelected(boolean isBookmarkSelected) {
        isBookmarkSelected = isBookmarkSelected;
    }


    public static boolean isBookmarkSelected() {
        return isBookmarkSelected;
    }



    public String getBookmarkStatus() {
        return bookmarkstatus;
    }

    public void setBookmarkstatus(String bookmarkstatus) {
        this.bookmarkstatus = bookmarkstatus;
    }



    public String getLikeStatus() {
        return likestatus;
    }

    public void setLikestatus(String likestatus) {
        this.likestatus = likestatus;
    }

    private List<AnsListDTO> ansListDTOS;

    public HomeDTO() {
    }


    public List<AnsListDTO> getAnswers() {
        return ansListDTOS;
    }

    public void setAnswers(List<AnsListDTO> ansListDTOS) {
        this.ansListDTOS = ansListDTOS;
    }

    public String getAnsSlectType() {
        return ansslecetiontype;
    }

    public void setAnsSlectType(String ansslecetiontype) {
        this.ansslecetiontype = ansslecetiontype;
    }

    public String getPostType() {
        return posttype;
    }

    public void setPostType(String posttype) {
        this.posttype = posttype;
    }

    public String getPostId() {
        return postid;
    }

    public void setPostId(String postId) {
        this.postid = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
       this.userName  = userName ;
    }

    public String getFavourite() {
        return favourite;
    }

    public void setFavourite(String favourite) {
        this.favourite = favourite ;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
         this.title=title ;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
       this.description  =description ;
    }

    public String getVotesCount() {
        return votesCount+" Votes";
    }

    public void setVotesCount(String votesCount) {
        this.votesCount =votesCount ;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount ) {
       this.viewCount  =viewCount ;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount =commentCount ;
    }



    public String getDaysCount() {
        return noOfDaysCount;
    }

    public void setDaysCount(String noOfDaysCount) {
        this.noOfDaysCount =noOfDaysCount ;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount =likeCount ;
    }

}
