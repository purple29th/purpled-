package com.example.purpled.model;

public class MessageModel {
    private String msgId;
    private String senderId;
    private String message;
    private String receiverId;

    public MessageModel(String msgId, String senderId, String message, String receiverId) {
        this.msgId = msgId;
        this.senderId = senderId;
        this.message = message;
        this.receiverId = receiverId;
    }

    public MessageModel(){

    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
