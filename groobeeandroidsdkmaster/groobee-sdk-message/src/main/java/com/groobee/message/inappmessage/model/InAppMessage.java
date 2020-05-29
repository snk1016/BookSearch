package com.groobee.message.inappmessage.model;

import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.MessageType;

import java.util.Map;

public class InAppMessage {

    private MessageText title;
    private MessageText body;
    private String imageUrl;

    private Map<ButtonType, MessageButton> messageButton;

    private Map<String, String> data;

    private MessageType messageType;

    public InAppMessage(MessageText title, MessageText body, String imageUrl, Map<ButtonType, MessageButton> messageButton, Map<String, String> data, MessageType messageType) {
        this.title = title;
        this.body = body;
        this.imageUrl = imageUrl;
        this.messageButton = messageButton;
        this.data = data;
        this.messageType = messageType;
    }

    public MessageText getTitle() {
        return title;
    }

    public MessageText getBody() {
        return body;
    }

    public String getimageUrl() {
        return imageUrl;
    }

    public Map<ButtonType, MessageButton> getMessageButton() {
        return messageButton;
    }

    public Map<String, String> getData() {
        return data;
    }

    public MessageType getMessageType() {
        return messageType;
    }
}
