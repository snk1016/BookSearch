package com.groobee.message.inappmessage.inflaters;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;

import com.groobee.message.inappmessage.interfaces.InAppMessageScope;
import com.groobee.message.inappmessage.model.InAppMessage;

import dagger.Module;
import dagger.Provides;

@Module
public class InflaterModule {
    private final InAppMessage inAppMessage;
    private final Context context;

    public InflaterModule(InAppMessage inAppMessage, Context context) {
        this.inAppMessage = inAppMessage;
        this.context = context;
    }

    @Provides
    @InAppMessageScope
    public LayoutInflater providesInflaterService() {
        return (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Provides
    public InAppMessage providesBannerMessage() { return inAppMessage; }
}
