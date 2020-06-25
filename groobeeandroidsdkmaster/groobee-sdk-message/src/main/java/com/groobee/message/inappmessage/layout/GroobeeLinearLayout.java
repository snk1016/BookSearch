package com.groobee.message.inappmessage.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.groobee.message.inappmessage.interfaces.BackPressOnDown;
import com.groobee.message.inappmessage.layout.functions.HandlerBackPressOnDown;

public class GroobeeLinearLayout extends LinearLayout implements BackPressOnDown {
    private HandlerBackPressOnDown handlerBackPressOnDown;

    public GroobeeLinearLayout(Context context) {
        super(context);
    }

    public GroobeeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GroobeeLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDismissListener(OnClickListener onBackPress) {
        handlerBackPressOnDown = new HandlerBackPressOnDown(this, onBackPress);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Boolean handled = handlerBackPressOnDown.dispatchKeyEvent(event);

        if(handled != null)
            return handled;
        else
            return super.dispatchKeyEvent(event);
    }
}
