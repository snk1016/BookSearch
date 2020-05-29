package com.groobee.message.inappmessage.inflaters;

import android.view.LayoutInflater;

import com.groobee.message.inappmessage.inflaters.factorys.InflaterModuleProvidesBannerMessageFactory;
import com.groobee.message.inappmessage.inflaters.factorys.InflaterModuleProvidesInflaterServiceFactory;
import com.groobee.message.inappmessage.interfaces.InAppMessageComponent;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.wrapper.DialogBindingWrapper;
import com.groobee.message.inappmessage.wrapper.HtmlModalBindingWrapper;
import com.groobee.message.inappmessage.wrapper.factorys.DialogBindingWrapperFactory;
import com.groobee.message.inappmessage.wrapper.factorys.HtmlModalBindingWrapperFactory;

import javax.inject.Provider;

import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;

public class DaggerInAppMessageComponent implements InAppMessageComponent {

    private Provider<LayoutInflater> providesInflaterServiceProvider;
    private Provider<InAppMessage> providesBannerMessageProvider;

    private Provider<DialogBindingWrapper> dialogBindingWrapperProvider;
    private Provider<HtmlModalBindingWrapper> htmlModalBindingWrapperProvider;

    private DaggerInAppMessageComponent (DaggerInAppMessageComponent.Builder builder) {
        providesInflaterServiceProvider = DoubleCheck.provider(InflaterModuleProvidesInflaterServiceFactory.create(builder.inflaterModule));
        providesBannerMessageProvider = InflaterModuleProvidesBannerMessageFactory.create(builder.inflaterModule);

        dialogBindingWrapperProvider = DoubleCheck.provider(DialogBindingWrapperFactory.create(providesInflaterServiceProvider, providesBannerMessageProvider));
        htmlModalBindingWrapperProvider = DoubleCheck.provider(HtmlModalBindingWrapperFactory.create(providesInflaterServiceProvider, providesBannerMessageProvider));
    }

    public static DaggerInAppMessageComponent.Builder builder() {
        return new DaggerInAppMessageComponent.Builder();
    }

    public DialogBindingWrapper dialogBindingWrapper() {
        return dialogBindingWrapperProvider.get();
    }

    public HtmlModalBindingWrapper htmlModalBindingWrapper() {
        return htmlModalBindingWrapperProvider.get();
    }

    public static final class Builder {
        private InflaterModule inflaterModule;

        private Builder() {
        }

        public InAppMessageComponent build() {
            if (inflaterModule == null) {
                throw new IllegalStateException(InflaterModule.class.getCanonicalName() + " must be set");
            } else {
                return new DaggerInAppMessageComponent(this);
            }
        }

        public DaggerInAppMessageComponent.Builder inflaterModule(InflaterModule inflaterModule) {
            this.inflaterModule = Preconditions.checkNotNull(inflaterModule);
            return this;
        }
    }
}
