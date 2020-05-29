package com.groobee.message.inappmessage.interfaces;

import com.groobee.message.inappmessage.inflaters.InflaterModule;
import com.groobee.message.inappmessage.wrapper.DialogBindingWrapper;
import com.groobee.message.inappmessage.wrapper.HtmlModalBindingWrapper;

import dagger.Component;

@InAppMessageScope
@Component(modules = {InflaterModule.class})
public interface InAppMessageComponent {
    @InAppMessageScope
    DialogBindingWrapper dialogBindingWrapper();

    @InAppMessageScope
    HtmlModalBindingWrapper htmlModalBindingWrapper();
}
