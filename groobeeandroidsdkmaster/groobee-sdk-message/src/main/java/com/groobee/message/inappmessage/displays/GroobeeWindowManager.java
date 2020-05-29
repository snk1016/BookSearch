package com.groobee.message.inappmessage.displays;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.groobee.message.inappmessage.utils.SwipeDismissTouchListener;
import com.groobee.message.inappmessage.wrapper.BindingWrapper;
import com.groobee.message.utils.LoggerUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GroobeeWindowManager {
    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeWindowManager.class);

    private BindingWrapper bindingWrapper;

    @Inject
    GroobeeWindowManager() { }

    public void show(@NonNull final BindingWrapper bindingWrapper, @NonNull Activity activity, WindowManager.LayoutParams params) {
        if (isDisplayed()) {
            LoggerUtils.d(TAG, "Fiam already active. Cannot show new Fiam.");
            return;
        }

        final WindowManager windowManager = getWindowManager(activity);
        windowManager.addView(bindingWrapper.getRootView(), params);

        Log.d("nh", "isFocusable : " + bindingWrapper.getRootView().isFocusable());
        if(!bindingWrapper.getRootView().isFocusable()) {
            bindingWrapper.getRootView().setFocusable(true);
            bindingWrapper.getRootView().setFocusableInTouchMode(true);
            bindingWrapper.getRootView().requestFocus();
        }

        Log.d("nh", "isFocusable : " + bindingWrapper.getRootView().isFocusable());

        if (bindingWrapper.canSwipeToDismiss()) {
            SwipeDismissTouchListener listener = getSwipeListener(bindingWrapper, windowManager, params);
            bindingWrapper.getRootView().setOnTouchListener(listener);
        }

        this.bindingWrapper = bindingWrapper;
    }

    public boolean isDisplayed() {
        if (bindingWrapper == null) {
            return false;
        }
        return bindingWrapper.getRootView().isShown();
    }

    public void dismiss(@NonNull Activity activity) {
        if (isDisplayed()) {
            getWindowManager(activity).removeViewImmediate(bindingWrapper.getRootView());
            bindingWrapper = null;
        }
    }

    private WindowManager getWindowManager(@NonNull Activity activity) {
        return (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
    }

    private SwipeDismissTouchListener getSwipeListener(final BindingWrapper bindingWrapper, final WindowManager windowManager,
            final WindowManager.LayoutParams layoutParams) {

        // The dismiss callbacks are the same in any case.
        SwipeDismissTouchListener.DismissCallbacks callbacks = new SwipeDismissTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(Object token) {
                return true;
            }

            @Override
            public void onDismiss(View view, Object token) {
                if (bindingWrapper.getDismissListener() != null) {
                    bindingWrapper.getDismissListener().onClick(view);
                }
            }
        };

        if (layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            // When we are using the entire view width we can use the default behavior
            return new SwipeDismissTouchListener(bindingWrapper.getRootView(), null, callbacks);
        } else {
            // When we are not using the entire view width we need to use the WindowManager to animate.
            return new SwipeDismissTouchListener(bindingWrapper.getRootView(), null, callbacks) {
                @Override
                protected float getTranslationX() {
                    return layoutParams.x;
                }

                @Override
                protected void setTranslationX(float translationX) {
                    layoutParams.x = (int) translationX;
                    windowManager.updateViewLayout(bindingWrapper.getRootView(), layoutParams);
                }
            };
        }
    }
}
