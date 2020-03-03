package com.ansbob.practice;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private Date mDate;
    private String mTitle;
    private boolean mSolved;
    private UUID id;
    private String suspect;

    public String getPhotoFileName() {
        return "IMG_"+getId().toString()+".jpg";
    }

    public String getSuspect() {
        return suspect;
    }

    public void setSuspect(String suspect) {
        this.suspect = suspect;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public UUID getId() {
        return id;
    }

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        this.id = id;
        mDate = new Date();
    }
}
