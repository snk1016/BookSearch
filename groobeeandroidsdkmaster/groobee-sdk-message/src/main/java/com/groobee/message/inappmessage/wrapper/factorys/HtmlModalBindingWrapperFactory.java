package com.groobee.message.inappmessage.wrapper.factorys;

import android.view.LayoutInflater;

import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.wrapper.HtmlModalBindingWrapper;

import javax.inject.Provider;

import dagger.internal.Factory;

public class HtmlModalBindingWrapperFactory implements Factory<HtmlModalBindingWrapper> {
    private final Provider<LayoutInflater> inflaterProvider;
    private final Provider<InAppMessage> messageProvider;

    public HtmlModalBindingWrapperFactory(Provider<LayoutInflater> inflaterProvider, Provider<InAppMessage> messageProvider) {
        this.inflaterProvider = inflaterProvider;
        this.messageProvider = messageProvider;
    }

    public HtmlModalBindingWrapper get() {
        return new HtmlModalBindingWrapper(messageProvider.get(), inflaterProvider.get());
    }

    public static Factory<HtmlModalBindingWrapper> create(Provider<LayoutInflater> inflaterProvider, Provider<InAppMessage> messageProvider) {
        return new HtmlModalBindingWrapperFactory(inflaterProvider, messageProvider);
    }
}
