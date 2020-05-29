package com.groobee.message.inappmessage.interfaces;

import androidx.annotation.Keep;

import com.groobee.message.inappmessage.model.InAppMessage;

@Keep
public interface GroobeeInAppMessagingDisplay {
    @Keep
    public void onDisplayMessage(InAppMessage inAppMessage, GroobeeInAppMessagingDisplayCallbacks callbacks);
}
