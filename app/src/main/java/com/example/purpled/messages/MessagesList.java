package com.example.purpled.messages;

public class MessagesList {

    private String username, uid, lastMessage, userProfile, chatKey, date, time, userone, usertwo;
    private boolean online;
    private final int unseenMessages;

    public MessagesList(String username, String uid, String lastMessage, int unseenMessages, String userProfile, String chatKey, String date, String time, boolean online,String userone,String usertwo) {
        this.username = username;
        this.uid = uid;
        this.lastMessage = lastMessage;
        this.unseenMessages = unseenMessages;
        this.userProfile = userProfile;
        this.chatKey = chatKey;
        this.date = date;
        this.time = time;
        this.online = online;
        this.userone = userone;
        this.usertwo = usertwo;

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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public boolean isOnline() {
        return online;
    }

    public String getUserone() {
        return userone;
    }

    public String getUsertwo() {
        return usertwo;
    }
}
