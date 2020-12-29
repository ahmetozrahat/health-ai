package com.ozrahat.healthai.models;

public class ChatMessage {
    public Integer sender;
    public String message;
    public Long date;

    public ChatMessage(int sender, String message, long date) {
        this.sender = sender;
        this.message = message;
        this.date = date;
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
}
