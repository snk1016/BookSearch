package com.groobee.message.inappmessage.layout.functions;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

public class HandlerBackPressOnDown {

    private ViewGroup viewGroup;
    private View.OnClickListener onBackPress;

    public HandlerBackPressOnDown(ViewGroup viewGroup, View.OnClickListener onBackPress) {
        this.viewGroup = viewGroup;
        this.onBackPress = onBackPress;
    }

    public Boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onBackPress != null) {
                onBackPress.onClick(viewGroup);
                return true;
            }
            return false;
        }
        return null;
    }

}
