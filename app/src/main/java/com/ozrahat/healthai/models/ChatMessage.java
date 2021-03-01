package com.ozrahat.healthai.models;

public class ChatMessage {

    public Integer sender;
    public String message;
    public Long date;
    public Boolean showProfile;
    public Boolean showTime;

    public ChatMessage(int sender, String message, long date, boolean showProfile, boolean showTime) {
        this.sender = sender;
        this.message = message;
        this.date = date;
        this.showProfile = showProfile;
        this.showTime = showTime;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Boolean getShowProfile() {
        return showProfile;
    }

    public void setShowProfile(Boolean showProfile) {
        this.showProfile = showProfile;
    }

    public Boolean getShowTime() {
        return showTime;
    }

    public void setShowTime(Boolean showTime) {
        this.showTime = showTime;
    }
}
