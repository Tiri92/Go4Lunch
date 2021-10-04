package com.go4lunch.model.firestore;

import java.util.Date;
import java.util.List;

public class Message {

    private String from, to, message, urlPicFrom;
    private Date date;
    private List<String> between;

    public Message() {
    }

    public Message(String from, String to, String message, String urlPicFrom, Date date, List<String> between) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.urlPicFrom = urlPicFrom;
        this.date = date;
        this.between = between;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrlPicFrom() {
        return urlPicFrom;
    }

    public void setUrlPicFrom(String urlPicFrom) {
        this.urlPicFrom = urlPicFrom;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getBetween() {
        return between;
    }

    public void setBetween(List<String> between) {
        this.between = between;
    }


}
