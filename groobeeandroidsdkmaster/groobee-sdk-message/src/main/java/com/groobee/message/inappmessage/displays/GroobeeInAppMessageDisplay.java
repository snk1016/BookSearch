package com.groobee.message.inappmessage.displays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.groobee.message.R;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.GroobeeActivityLifecycleCallbacks;
import com.groobee.message.inappmessage.MessageType;
import com.groobee.message.inappmessage.interfaces.DialogListener;
import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplayCallbacks;
import com.groobee.message.inappmessage.interfaces.InAppMessageScope;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.model.MessageButton;
import com.groobee.message.inappmessage.utils.AnimatorUtils;
import com.groobee.message.inappmessage.utils.RenewableTimer;
import com.groobee.message.inappmessage.wrapper.BindingWrapper;
import com.groobee.message.inappmessage.wrapper.HtmlModalBindingWrapper;
import com.groobee.message.inappmessage.wrapper.factorys.BindingWrapperFactory;
import com.groobee.message.utils.IntentUtils;
import com.groobee.message.utils.LoggerUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import static com.groobee.message.inappmessage.utils.AnimatorUtils.Position.BOTTOM;
import static com.groobee.message.inappmessage.utils.AnimatorUtils.Position.LEFT;
import static com.groobee.message.inappmessage.utils.AnimatorUtils.Position.TOP;

@Keep
@InAppMessageScope
public class GroobeeInAppMessageDisplay extends GroobeeActivityLifecycleCallbacks {

    private static final String TAG = LoggerUtils.getClassLogTag(GroobeeInAppMessageDisplay.class);

    static final long IMPRESSION_THRESHOLD_MILLIS = 5 * 1000; // 5 seconds is a valid impression
    static final long DISMISS_THRESHOLD_MILLIS = 20 * 1000; // auto dismiss after 20 seconds for banner
    static final long INTERVAL_MILLIS = 1000;

    private static final int DEFAULT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG;

    public static int DISABLED_BG_FLAG = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

    public static int DISMISSIBLE_DIALOG_FLAG = WindowManager.LayoutParams.FLAG_DIM_BEHIND
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;

    private int ENABLED_BG_FLAG = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

    private final RenewableTimer impressionTimer;
    private final RenewableTimer autoDismissTimer;

    private final GroobeeWindowManager windowManager;
    private BindingWrapperFactory bindingWrapperFactory;
//    private final Context context;
    private final AnimatorUtils animator;

    private DialogListener dialogListener;
    private InAppMessage inAppMessage;
    private GroobeeInAppMessagingDisplayCallbacks callbacks;

    private BindingWrapper bindingWrapper;

    private static class LazyHolder {
        private static final GroobeeInAppMessageDisplay INSTANCE = new GroobeeInAppMessageDisplay();
    }

    @Keep
    public static GroobeeInAppMessageDisplay getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Inject
    public GroobeeInAppMessageDisplay() {
        super();
        this.impressionTimer = new RenewableTimer();
        this.autoDismissTimer = new RenewableTimer();
        this.windowManager = new GroobeeWindowManager();
        this.animator = new AnimatorUtils();
    }

    @Override
    public void onActivityStarted(final Activity activity) {
        super.onActivityStarted(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        removeDisplayed(activity);
        notifyDismiss();
        super.onActivityPaused(activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        removeDisplayed(activity);
        notifyDismiss();
        super.onActivityDestroyed(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (inAppMessage != null) {
            showActive(activity);
//            showMessagePopup(activity, inAppMessage, callbacks);
        }
        super.onActivityResumed(activity);
    }

    public void testMessage(Activity activity, InAppMessage inAppMessage, GroobeeInAppMessagingDisplayCallbacks callbacks) {
        this.inAppMessage = inAppMessage;
        this.callbacks = callbacks;
        showActive(activity);
    }

    public void showMessagePopup(Activity activity, InAppMessage inAppMessage, GroobeeInAppMessagingDisplayCallbacks callbacks) {
        this.inAppMessage = inAppMessage;
        this.callbacks = callbacks;
        showActive(activity);
    }

    private void showActive(@NonNull final Activity activity) {
        if (inAppMessage == null) {
            LoggerUtils.e(TAG, activity.getString(R.string.GROOBEE_IN_APP_MESSAGE_DISPLAY_SHOW_ACTIVE_MESSAGE_IS_NULL));
            return;
        }

//        if (inAppMessage.getMessageType().equals(MessageType.UNSUPPORTED)) {
//            LoggerUtils.e(TAG, "The message being triggered is not supported by this version of the sdk.");
//            return;
//        }
        notifyTrigger();

        if(bindingWrapperFactory == null)
            bindingWrapperFactory = new BindingWrapperFactory(activity.getApplication());

        final MessageType messageType = inAppMessage.getMessageType();

        switch (messageType) {
            case DIALOG:
                bindingWrapper = bindingWrapperFactory.createDialogBindingWrapper(inAppMessage);
                break;

            case HTML_MODAL:
                bindingWrapper = bindingWrapperFactory.createHtmlModalBindingWrapper(inAppMessage);
                break;
            default:
                LoggerUtils.e(TAG, activity.getString(R.string.GROOBEE_IN_APP_MESSAGE_DISPLAY_SHOW_ACTIVE_NOT_FOUND_MESSAGE_TYPE));
                // so we should break out completely and not attempt to show anything
                return;
        }

        // The WindowManager LayoutParams.TYPE_APPLICATION_PANEL requires tokens from the activity
        // which does not become available until after all lifecycle methods are complete.
        activity.findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                inflateBinding(activity, bindingWrapper, messageType);
            }
        });
    }

    private void dismiss(Activity activity) {
        notifyDismiss();
        removeDisplayed(activity);
        inAppMessage = null;
        callbacks = null;
    }

    private void removeDisplayed(Activity activity) {
        if (windowManager.isDisplayed()) {
            windowManager.dismiss(activity);
            cancelTimers();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inflateBinding(final Activity activity, final BindingWrapper bindingWrapper, final MessageType messageType) {
        // On click listener when X button or collapse button is clicked
        final View.OnClickListener dismissListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callbacks != null) {
                    callbacks.messageDismissed(GroobeeInAppMessagingDisplayCallbacks.InAppMessagingDismissType.CLICK);
                }
                dismiss(activity);
            }
        };

        Map<ButtonType, View.OnClickListener> actionListeners = new HashMap<>();

        final Map<ButtonType, MessageButton> mapMessage = inAppMessage.getMessageButton();

        for(final ButtonType buttonType : mapMessage.keySet()) {
            final View.OnClickListener actionListener;

            if(buttonType != null && !TextUtils.isEmpty(mapMessage.get(buttonType).getEventUrl())) {
                actionListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callbacks != null) {
                            callbacks.messageClicked(mapMessage.get(buttonType));
                        }

                        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().setShowTitle(true).build();
                        customTabsIntent.launchUrl(activity, Uri.parse(mapMessage.get(buttonType).getEventUrl()));

                        notifyClick();

                        removeDisplayed(activity);
                        inAppMessage = null;
                        callbacks = null;
                    }
                };
            } else {
                if(buttonType != null && mapMessage.get(buttonType).getClickListener() != null) {
                    actionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callbacks != null) {
                                callbacks.messageClicked(mapMessage.get(buttonType));
                            }

                            mapMessage.get(buttonType).getClickListener().onClick(v);

                            notifyClick();

                            removeDisplayed(activity);
                            inAppMessage = null;
                            callbacks = null;
                        }
                    };
                } else if(buttonType != null && !TextUtils.isEmpty(mapMessage.get(buttonType).getActivityName())) {
                    actionListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (callbacks != null) {
                                callbacks.messageClicked(mapMessage.get(buttonType));
                            }

                            notifyClick();

                            removeDisplayed(activity);
                            inAppMessage = null;
                            callbacks = null;

                            Intent it = IntentUtils.setActivity(activity, mapMessage.get(buttonType).getActivityName(), new Bundle());
                            activity.startActivity(it);
                        }
                    };
                } else {
                    LoggerUtils.e(TAG, activity.getString(R.string.GROOBEE_IN_APP_MESSAGE_DISPLAY_SHOW_ACTIVE_NOT_FOUND_ACTION));
                    actionListener = dismissListener;
                }
            }

            actionListeners.put(buttonType, actionListener);
        }

        final ViewTreeObserver.OnGlobalLayoutListener layoutListener = bindingWrapper.inflate(actionListeners, dismissListener);
        if (layoutListener != null) {
            bindingWrapper.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
        }

        bindingWrapper.getRootView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (callbacks != null) {
                        callbacks.messageDismissed(GroobeeInAppMessagingDisplayCallbacks.InAppMessagingDismissType.UNKNOWN_DISMISS_TYPE);
                    }
                    dismiss(activity);
                    return true;
                }
                return false;
            }
        });

//        if(inAppMessage.getMessageType().equals(MessageType.HTML_MODAL)) {
            // Setup impression timer
            impressionTimer.start(new RenewableTimer.Callback() {
                @Override
                public void onFinish() {
                    if (inAppMessage != null && callbacks != null) {
//                            LoggerUtils.i(TAG, "Impression timer onFinish for: " + inAppMessage.getCampaignMetadata().getCampaignId());
//                        LoggerUtils.i(TAG, "Impression timer onFinish for: " + inAppMessage.getTitle().getText());

                        callbacks.impressionDetected();
                    }
                }
            }, IMPRESSION_THRESHOLD_MILLIS, INTERVAL_MILLIS);
//        }

        if(inAppMessage.getMessageType().equals(MessageType.HTML_MODAL)) {
            // Setup auto dismiss timer
            autoDismissTimer.start(new RenewableTimer.Callback() {
                @Override
                public void onFinish() {
                    if (inAppMessage != null && callbacks != null) {
                        callbacks.messageDismissed(GroobeeInAppMessagingDisplayCallbacks.InAppMessagingDismissType.AUTO);
                    }

                    dismiss(activity);
                }
            }, DISMISS_THRESHOLD_MILLIS, INTERVAL_MILLIS);
        }

        Handler mainLooperHandler = new Handler(activity.getMainLooper());
        mainLooperHandler.post(new Runnable() {
            @Override
            public void run() {
                windowManager.show(bindingWrapper, activity, getLayoutParams(messageType));
//                animator.slideIntoView(context, bindingWrapper.getRootView(), TOP);
//                animator.slideIntoView(context, bindingWrapper.getRootView(), BOTTOM);
            }
        });
    }

    private void cancelTimers() {
        impressionTimer.cancel();
        autoDismissTimer.cancel();
    }

    private void notifyTrigger() {
        if (dialogListener != null) {
            dialogListener.onTrigger();
        }
    }

    private void notifyClick() {
        if (dialogListener != null) {
            dialogListener.onClick();
        }
    }

    private void notifyDismiss() {
        if (dialogListener != null) {
            dialogListener.onDismiss();
        }
    }

    private WindowManager.LayoutParams getLayoutParams(@NonNull MessageType messageType) {
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        switch (messageType) {
            case DIALOG:
            case HTML_MODAL:
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.type = DEFAULT_TYPE;
                layoutParams.flags = DISABLED_BG_FLAG;
                layoutParams.format = PixelFormat.TRANSLUCENT;
                break;

//            case HTML_MODAL:
//                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                layoutParams.type = DEFAULT_TYPE;
//                layoutParams.flags = DISABLED_BG_FLAG;
//                layoutParams.format = PixelFormat.TRANSLUCENT;
//                break;
        }

        layoutParams.dimAmount = 0.3f;
//        layoutParams.gravity = layoutConfig.viewWindowGravity();
        layoutParams.windowAnimations = 0;

        return layoutParams;
    }
}
