package com.dbvertex.company.h1bq;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "postpolllist")
public class User
{
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public String getPostidRoom() {
        return postidRoom;
    }

    public void setPostidRoom( String postidRoom) {
        this.postidRoom = postidRoom;
    }


    public String getImageRoom() {
        return imageRoom;
    }

    public void setImageRoom( String imageRoom) {
        this.imageRoom = imageRoom;
    }


    public String getUserNameRoom() {
        return userNameRoom;
    }

    public void setUserNameRoom( String userNameRoom) {
        this.userNameRoom = userNameRoom;
    }

//    public String getAnswersRoom() {
//        return answersRoom;
//    }
//
//    public void setAnswersRoom(@NonNull String answersRoom) {
//        this.answersRoom = answersRoom;
//    }

    public String getFavouriteRoom() {
        return favouriteRoom;
    }

    public void setFavouriteRoom( String favouriteRoom) {
        this.favouriteRoom = favouriteRoom;
    }

    public String getTitleRoom() {
        return titleRoom;
    }

    public void setTitleRoom( String titleRoom) {
        this.titleRoom = titleRoom;
    }

    public String getDescriptionRoom() {
        return descriptionRoom;
    }

    public void setDescriptionRoom( String descriptionRoom) {
        this.descriptionRoom = descriptionRoom;
    }

    public String getVotesCountRoom() {
        return votesCountRoom;
    }

    public void setVotesCountRoom(String votesCountRoom) {
        this.votesCountRoom = votesCountRoom;
    }


    public String getViewCountRoom() {
        return viewCountRoom;
    }

    public void setViewCountRoom(String viewCountRoom) {
        this.viewCountRoom = viewCountRoom;
    }

    public String getLikeCountRoom() {
        return likeCountRoom;
    }

    public void setLikeCountRoom( String likeCountRoom) {
        this.likeCountRoom = likeCountRoom;
    }


    public String getCommentCountRoom() {
        return commentCountRoom;
    }

    public void setCommentCountRoom( String commentCountRoom) {
        this.commentCountRoom = commentCountRoom;
    }

    public String getNoOfDaysCountRoom() {
        return noOfDaysCountRoom;
    }

    public void setNoOfDaysCountRoom( String noOfDaysCountRoom) {
        this.noOfDaysCountRoom = noOfDaysCountRoom;
    }

    public String getUserIdRoom() {
        return userIdRoom;
    }

    public void setUserIdRoom( String userIdRoom) {
        this.userIdRoom = userIdRoom;
    }

    public String getPosttypeRoom() {
        return posttypeRoom;
    }

    public void setPosttypeRoom( String posttypeRoom) {
        this.posttypeRoom = posttypeRoom;
    }

    public String getAnsslecetiontypeRoom() {
        return ansslecetiontypeRoom;
    }

    public void setAnsslecetiontypeRoom( String ansslecetiontypeRoom) {
        this.ansslecetiontypeRoom = ansslecetiontypeRoom;
    }

    public String getBookmarkstatusRoom() {
        return bookmarkstatusRoom;
    }

    public void setBookmarkstatusRoom( String bookmarkstatusRoom) {
        this.bookmarkstatusRoom = bookmarkstatusRoom;
    }

    public String getLikestatusRoom() {
        return likestatusRoom;
    }

    public void setLikestatusRoom( String likestatusRoom) {
        this.likestatusRoom = likestatusRoom;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

//
//    public String getDeleteStatusRoom() {
//        return deleteStatusRoom;
//    }
//
//    public void setDeleteStatusRoom( String deleteStatusRoom) {
//        this.deleteStatusRoom = deleteStatusRoom;
//    }
//
//    public String getReportabusestatusRoom() {
//        return reportabusestatusRoom;
//    }
//
//    public void setReportabusestatusRoom( String reportabusestatusRoom) {
//        this.reportabusestatusRoom = reportabusestatusRoom;
//    }
//
//    public String getViewstatusRoom() {
//        return viewstatusRoom;
//    }
//
//    public void setViewstatusRoom( String viewstatusRoom) {
//        this.viewstatusRoom = viewstatusRoom;
//    }

    public String getBucketIdRoom() {
        return bucketIdRoom;
    }

    public void setBucketIdRoom(String bucketIdRoom) {
        this.bucketIdRoom = bucketIdRoom;
    }

    @PrimaryKey(autoGenerate = true)
    private  int id;

    @ColumnInfo(name = "bucketid")
    private String bucketIdRoom;



    @ColumnInfo(name = "postid")
    private String postidRoom;

    @ColumnInfo(name = "image")
    private String imageRoom;

    @ColumnInfo(name = "username")
    private String userNameRoom;

    @ColumnInfo(name = "favorite")

    private String favouriteRoom;
    @ColumnInfo(name = "title")
    private String titleRoom;
    @ColumnInfo(name = "description")
    private String descriptionRoom;
    @ColumnInfo(name = "votescount")

    private String votesCountRoom;
    @ColumnInfo(name = "viewcount")

    private String viewCountRoom;
    @ColumnInfo(name = "likecount")

    private String likeCountRoom;
    @ColumnInfo(name = "commentcount")

    private String commentCountRoom;
    @ColumnInfo(name = "noofdays")

    private String noOfDaysCountRoom;
    @ColumnInfo(name = "userid")

    private String userIdRoom;

    @ColumnInfo(name = "posttype")

    private String posttypeRoom;
    @ColumnInfo(name = "ansselectiontype")

    private String ansslecetiontypeRoom;
    @ColumnInfo(name = "bookmarkstatus")

    private String bookmarkstatusRoom;
    @ColumnInfo(name = "likestatus")
    private String likestatusRoom;

    @ColumnInfo(name = "answers")
    private String answers;

//    @ColumnInfo(name = "deletestatus")
//
//    private String deleteStatusRoom;
//    @ColumnInfo(name = "reportabusestatus")
//
//    private String reportabusestatusRoom;
//    @ColumnInfo(name = "viewstatus")
//
//    private String viewstatusRoom;





}
