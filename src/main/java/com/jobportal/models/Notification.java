package com.jobportal.models;

import java.util.Date;

public class Notification {
    private String recipient;
    private String content;
    private Date timestamp;

    public Notification(String recipient, String content, Date timestamp) {
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public Date getTimestamp() { return timestamp; }
}
