package com.groobee.message.inappmessage.inflaters.factorys;

import android.view.LayoutInflater;

import com.groobee.message.inappmessage.inflaters.InflaterModule;

import dagger.internal.Factory;
import dagger.internal.Preconditions;

public class InflaterModuleProvidesInflaterServiceFactory implements Factory<LayoutInflater> {

    private final InflaterModule module;

    public InflaterModuleProvidesInflaterServiceFactory(InflaterModule module) {
        this.module = module;
    }

    @Override
    public LayoutInflater get() {
        return Preconditions.checkNotNull(module.providesInflaterService(), "Cannot return null from a non-@Nullable @Provides method");
    }

    public static Factory<LayoutInflater> create(InflaterModule module) {
        return new InflaterModuleProvidesInflaterServiceFactory(module);
    }
}
