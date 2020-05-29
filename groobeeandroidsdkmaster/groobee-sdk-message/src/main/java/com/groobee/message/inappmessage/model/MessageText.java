package com.groobee.message.inappmessage.model;

import android.text.TextUtils;

import androidx.annotation.NonNull;

public class MessageText {

    private String text;
    private String textColor = null;
    private Integer textSize = null;

    public MessageText(@NonNull String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getTextColor() {
        return textColor;
    }

    public Integer getTextSize() {
        return textSize;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String textColor;
        private int textSize;

        public Builder setText(String text) {
            if(!TextUtils.isEmpty(text))
                this.text = text;
            return this;
        }

        public Builder setTextColor(String textColor) {
            if(!TextUtils.isEmpty(textColor))
                this.textColor = textColor;
            return this;
        }

        public Builder setTextSize(Integer textSize) {
            if (textSize != null)
                this.textSize = textSize;
            return this;
        }

        public MessageText build() {
            return new MessageText(text);
        }
    }
}
