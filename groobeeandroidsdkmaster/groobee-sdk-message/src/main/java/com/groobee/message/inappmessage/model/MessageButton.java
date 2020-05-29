package com.groobee.message.inappmessage.model;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.groobee.message.inappmessage.ButtonType;

public class MessageButton {

    private MessageButton.Builder builder = null;

    public MessageButton(@NonNull MessageButton.Builder builder) {
        this.builder = builder;
    }

    public String getText() {
        return builder.text;
    }

    public String getTextColor() { return builder.textColor; }

    public Integer getTextSize() {
        return builder.textSize;
    }

    public String getActivityName() {
        return builder.activityName;
    }

    public String getEventUrl() {
        return builder.eventUrl;
    }

    public View.OnClickListener getClickListener() {
        return builder.onClickListener;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String text;
        private String textColor;
        private int textSize;

        private String eventUrl;

        private String activityName;

        private ButtonType buttonType = ButtonType.POSITIVE;

        private View.OnClickListener onClickListener;

//        public Builder(@NonNull String text, String eventUrl) {
//            this.text = text;
//            this.eventUrl = eventUrl;
//        }

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

        public Builder setActivityName(String activityName) {
            if(!TextUtils.isEmpty(activityName))
                this.activityName = activityName;
            return this;
        }

        public Builder setEventUrl(String eventUrl) {
            if(!TextUtils.isEmpty(eventUrl))
                this.eventUrl = eventUrl;
            return this;
        }

        public Builder setOnClickListener(View.OnClickListener onClickListener) {
            if(onClickListener != null)
                this.onClickListener = onClickListener;
            return this;
        }

        public MessageButton build() {
                return new MessageButton(this);
        }
    }
}
