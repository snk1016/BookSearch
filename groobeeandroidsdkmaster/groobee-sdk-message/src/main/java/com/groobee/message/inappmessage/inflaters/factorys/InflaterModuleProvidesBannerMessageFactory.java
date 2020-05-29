package com.groobee.message.inappmessage.inflaters.factorys;

import com.groobee.message.inappmessage.inflaters.InflaterModule;
import com.groobee.message.inappmessage.model.InAppMessage;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

public class InflaterModuleProvidesBannerMessageFactory implements Factory<InAppMessage> {
    private final InflaterModule module;

    public InflaterModuleProvidesBannerMessageFactory(InflaterModule module) {
        this.module = module;
    }

    public InAppMessage get() {
        return Preconditions.checkNotNull(module.providesBannerMessage(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<InAppMessage> create(InflaterModule module) {
        return new InflaterModuleProvidesBannerMessageFactory(module);
    }

    public static InAppMessage proxyProvidesBannerMessage(InflaterModule instance) {
        return instance.providesBannerMessage();
    }
}
