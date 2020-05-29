package com.groobee.message.inappmessage.wrapper.factorys;

import android.view.LayoutInflater;

import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.wrapper.DialogBindingWrapper;

import javax.inject.Provider;

import dagger.internal.Factory;

public class DialogBindingWrapperFactory implements Factory<DialogBindingWrapper> {
    private final Provider<LayoutInflater> inflaterProvider;
    private final Provider<InAppMessage> messageProvider;

    public DialogBindingWrapperFactory(Provider<LayoutInflater> inflaterProvider, Provider<InAppMessage> messageProvider) {
        this.inflaterProvider = inflaterProvider;
        this.messageProvider = messageProvider;
    }

    public DialogBindingWrapper get() {
        return new DialogBindingWrapper(messageProvider.get(), inflaterProvider.get());
    }

    public static Factory<DialogBindingWrapper> create(Provider<LayoutInflater> inflaterProvider, Provider<InAppMessage> messageProvider) {
        return new DialogBindingWrapperFactory(inflaterProvider, messageProvider);
    }
}
