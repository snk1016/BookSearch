package com.groobee.message;

import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

public class GroobeeConfig {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeConfig.class);

    private static GroobeeConfig.Builder builder = null;

    public static class Builder {

        public static String apiKey = null;

        private boolean handlePushDeepLinks;

        private boolean moveActivityEnabled;

        private String ActivityClass;

        private String smallNotificationIconName;

        private String largeNotificationIconName;

        public Builder() {}

        public GroobeeConfig build() {
            return new GroobeeConfig(this);
        }

        public GroobeeConfig.Builder setApiKey(String apiKey) {
            if(!StringUtils.isNullOrBlank(apiKey))
                this.apiKey = apiKey;
            else
                LoggerUtils.e(TAG, "Api Key is Empty. Please register api key.");
            return this;
        }

        public GroobeeConfig.Builder setHandlePushDeepLinks(boolean handlePushDeepLinks) {
            this.handlePushDeepLinks = handlePushDeepLinks;
            return this;
        }

        public GroobeeConfig.Builder setPushMoveActivityEnabled(boolean moveActivityEnabled) {
            this.moveActivityEnabled = moveActivityEnabled;
            return this;
        }

        public GroobeeConfig.Builder setPushMoveActivityClassName(Class ActivityClass) {
            this.ActivityClass = ActivityClass.getName();
            return this;
        }

        public GroobeeConfig.Builder setSmallNotificationIcon(String smallNotificationIconName) {
            this.smallNotificationIconName = smallNotificationIconName;
            return this;
        }

        public GroobeeConfig.Builder setLargeNotificationIcon(String largeNotificationIconName) {
            this.largeNotificationIconName = largeNotificationIconName;
            return this;
        }

    }

    private GroobeeConfig(GroobeeConfig.Builder builder) {
        this.builder = builder;
    }

    public String getApiKey() { return this.builder.apiKey; }

    public Boolean getHandlePushDeepLinks() { return this.builder.handlePushDeepLinks; }

    public Boolean getPushMoveActivityEnabled() { return this.builder.moveActivityEnabled; }

    public String getPushMoveActivityClassName() { return this.builder.ActivityClass; }

    public String getSmallNotificationIcon() { return this.builder.smallNotificationIconName; }

    public String getLargeNotificationIcon() { return this.builder.largeNotificationIconName; }
}
