package com.intheloop.chat.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "messages")
public class Message {
    public enum Type {
        TEXT,
        IMAGE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated
    @Column(nullable = false)
    private Type type;

    @Column(nullable = false)
    private String content;

    @OneToOne
    private User user;

    @ManyToOne
    private Conversation conversation;

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
