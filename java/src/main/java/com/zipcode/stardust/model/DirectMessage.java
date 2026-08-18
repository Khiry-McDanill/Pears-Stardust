package com.zipcode.stardust.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class DirectMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    private String content;

    private LocalDateTime sentAt;

    public DirectMessage() {
    }
    public Long getId() {
    return id;
}

public User getSender() {
    return sender;
}

public void setSender(User sender) {
    this.sender = sender;
}

public User getRecipient() {
    return recipient;
}

public void setRecipient(User recipient) {
    this.recipient = recipient;
}

public String getContent() {
    return content;
}

public void setContent(String content) {
    this.content = content;
}

public LocalDateTime getSentAt() {
    return sentAt;
}

public void setSentAt(LocalDateTime sentAt) {
    this.sentAt = sentAt;
}

    public DirectMessage(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.sentAt = LocalDateTime.now();
    }
}