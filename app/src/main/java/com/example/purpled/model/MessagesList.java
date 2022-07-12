package com.example.purpled.model;

public class MessagesList {

    private String username, uid, lastMessage, userProfile, chatKey;

    private final int unseenMessages;

    public MessagesList(String username, String uid, String lastMessage, int unseenMessages, String userProfile, String chatKey) {
        this.username = username;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
        this.userProfile = userProfile;
        this.chatKey = chatKey;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUID() {
        return uid;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getUnseenMessages() {
        return unseenMessages;
    }

    public String getChatKey() {
        return chatKey;
    }
}
