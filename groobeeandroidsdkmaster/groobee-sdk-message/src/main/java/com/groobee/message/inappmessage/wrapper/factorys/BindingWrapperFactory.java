package com.groobee.message.inappmessage.wrapper.factorys;

import android.app.Application;
import android.content.Context;

import com.groobee.message.inappmessage.inflaters.DaggerInAppMessageComponent;
import com.groobee.message.inappmessage.inflaters.InflaterModule;
import com.groobee.message.inappmessage.interfaces.InAppMessageComponent;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.wrapper.BindingWrapper;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BindingWrapperFactory {

    private final Application application;

    @Inject
    public BindingWrapperFactory(Application application) {
        this.application = application;
    }

    public BindingWrapper createDialogBindingWrapper(InAppMessage inAppMessage) {
        InAppMessageComponent inAppMessageComponent = DaggerInAppMessageComponent.builder()
                .inflaterModule(new InflaterModule(inAppMessage, application))
                .build();
        return inAppMessageComponent.dialogBindingWrapper();

    }

    public BindingWrapper createHtmlModalBindingWrapper(InAppMessage inAppMessage) {
        InAppMessageComponent inAppMessageComponent = DaggerInAppMessageComponent.builder()
                .inflaterModule(new InflaterModule(inAppMessage, application))
                .build();
        return inAppMessageComponent.htmlModalBindingWrapper();

    }
}
