package com.groobee.message;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;

import com.groobee.message.common.GroobeeImageLoader;
import com.groobee.message.common.thread.HandlerUncaughtException;
import com.groobee.message.common.thread.WorkingThreadFactory;
import com.groobee.message.common.thread.WorkingThreadSmall;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.MessageType;
import com.groobee.message.inappmessage.NoOpDisplayCallbacks;
import com.groobee.message.inappmessage.displays.GroobeeInAppMessageDisplay;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.model.MessageButton;
import com.groobee.message.inappmessage.model.MessageText;
import com.groobee.message.inappmessage.utils.AnimatorUtils;
import com.groobee.message.inappmessage.utils.RenewableTimer;
import com.groobee.message.inappmessage.wrapper.factorys.BindingWrapperFactory;
import com.groobee.message.providers.GroobeeConfigProvider;
import com.groobee.message.providers.RuntimeConfigProvider;
import com.groobee.message.push.interfaces.InterfaceGroobeeNotificationFactory;
import com.groobee.message.utils.LoggerUtils;
import com.groobee.message.utils.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Singleton;

@Singleton
public class Groobee {
    private static final String TAG = LoggerUtils.getClassLogTag(Groobee.class);

    //    private static volatile Groobee groobee = null;
    private static volatile InterfaceGroobeeNotificationFactory interfaceGroobeeNotificationFactory;

    private Context context;

    private GroobeeImageLoader groobeeImageLoader;

    private HandlerUncaughtException handlerUncaughtException;

    private WorkingThreadSmall workingThreadSmall;

//    private GroobeeInAppMessageDisplay groobeeInAppMessageDisplay;

    private RenewableTimer impressionTimer;
    private RenewableTimer autoDismissTimer;

    private BindingWrapperFactory bindingWrapperFactory;

    private AnimatorUtils animatorUtils;

    private GroobeeConfigProvider groobeeConfigProvider;

    private static class LazyHolder {
        private static final Groobee INSTANCE = new Groobee();
    }

    //    public static Groobee getInstance(Context context) {
//        if (groobee == null) {
//            groobee = new Groobee(context);
//            synchronized (Groobee.class) {
//                groobee = new Groobee(context);
//                return groobee;
//            }
//        }
//
//        return groobee;
//    }
    public static Groobee getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Groobee() {
        handlerUncaughtException = new HandlerUncaughtException();

        WorkingThreadFactory workingThreadFactory = new WorkingThreadFactory("Groobee-Working-Pool-Thread");
        workingThreadFactory.setUncaughtExceptionHandler(handlerUncaughtException);

        workingThreadSmall = new WorkingThreadSmall("identifier", workingThreadFactory);

        impressionTimer = new RenewableTimer();
        autoDismissTimer = new RenewableTimer();

        animatorUtils = new AnimatorUtils();
    }

//    public Groobee(Context context) {
//        this.context = context;
//        groobeeImageLoader = new GroobeeImageLoader(context);
//
//
//
//        bindingWrapperFactory = new BindingWrapperFactory(context);
//
//        groobeeInAppMessageDisplay = new GroobeeInAppMessageDisplay(impressionTimer, autoDismissTimer, context, bindingWrapperFactory, animatorUtils);
//    }

    public static boolean configure(Context context, GroobeeConfig config) {
        LoggerUtils.d(TAG, "configure() called with configuration: " + config);

        if (getInstance() == null) {
            synchronized (Groobee.class) {
                if (getInstance() == null) {
                    RuntimeConfigProvider runtimeConfigProvider = new RuntimeConfigProvider(context);

                    if(config != null) {
                        runtimeConfigProvider.setRuntimeConfig(config);
                        return true;
                    }

                    runtimeConfigProvider.clean();
                    return true;
                }
            }
        }

        getInstance().context = context.getApplicationContext();
        getInstance().groobeeImageLoader = new GroobeeImageLoader(context);
//        getInstance().bindingWrapperFactory = new BindingWrapperFactory(context.);
//        getInstance().groobeeInAppMessageDisplay = new GroobeeInAppMessageDisplay(getInstance().impressionTimer
//                , getInstance().autoDismissTimer
//                , context, getInstance().bindingWrapperFactory, getInstance().animatorUtils);
        getInstance().groobeeConfigProvider = new GroobeeConfigProvider(context);

//        List<ComponentRegistrar> registrars = ComponentDiscovery.forContext(context, ComponentDiscoveryService.class).discover();
//
//        getInstance().componentRuntime = new ComponentRuntime(UI_EXECUTOR, registrars, Component.of(context, Context.class), Component.of(getInstance(), Groobee.class));

        return false;
    }

//    public static boolean configure(Application application, Context context, GroobeeConfig config) {
//        LoggerUtils.d(TAG, "configure() called with configuration: " + config);
//
//        if (groobee == null) {
//            synchronized (Groobee.class) {
//                if (groobee == null) {
//                    RuntimeConfigProvider runtimeConfigProvider = new RuntimeConfigProvider(context);
//
//                    if(config != null) {
//                        runtimeConfigProvider.setRuntimeConfig(config);
//                        return true;
//                    }
//
//                    runtimeConfigProvider.clean();
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

//    @KeepForSdk
//    public <T> T get(Class<T> anInterface) {
//        checkNotDeleted();
//        return componentRuntime.get(anInterface);
//    }
//
//    private void checkNotDeleted() {
//        Preconditions.checkState(!deleted.get(), "Groobee was deleted");
//    }

    public GroobeeInAppMessageDisplay getActivityLifecycleCallbacks() {
        return GroobeeInAppMessageDisplay.getInstance();
    }

    public GroobeeImageLoader getImageLoader() {
        if (groobeeImageLoader == null) {
            LoggerUtils.d(TAG, "The Image Loader was null. Creating a new Image Loader and returning it.");
            groobeeImageLoader = new GroobeeImageLoader(context);
        }

        return groobeeImageLoader;
    }

    public static void setCustomGroobeeNotificationFactory(InterfaceGroobeeNotificationFactory customGroobeeNotificationFactory) {
        LoggerUtils.d(TAG, "Custom Braze notification factory set");
        interfaceGroobeeNotificationFactory = customGroobeeNotificationFactory;
    }

    public static InterfaceGroobeeNotificationFactory getCustomGroobeeNotificationFactory() {
        return interfaceGroobeeNotificationFactory;
    }

//    public void onActivityPaused(Activity activity) {
//        if(groobeeInAppMessageDisplay != null)
//            groobeeInAppMessageDisplay.onActivityPaused(activity);
//    }
//
//    public void onActivityDestroyed(Activity activity) {
//        if(groobeeInAppMessageDisplay != null)
//            groobeeInAppMessageDisplay.onActivityDestroyed(activity);
//    }
//
//    public void onActivityResumed(Activity activity) {
//        if(groobeeInAppMessageDisplay != null)
//            groobeeInAppMessageDisplay.onActivityResumed(activity);
//    }

    public void logPushNotificationActionClicked(final String campaignId, final String actionId, final String actionType) {
        LoggerUtils.d(TAG, "call method logPushNotificationActionClicked");

        /*
        Push 클릭한 내용 서버 전송
        * */
//        if (!j()) {
//            this.d.execute(new Runnable() {
//                public void run() {
//                    try {
//                        if (StringUtils.isNullOrBlank(campaignId)) {
//                            AppboyLogger.w(Appboy.l, "Campaign ID cannot be null or blank. Not logging push notification action clicked.");
//                            return;
//                        }
//
//                        if (StringUtils.isNullOrBlank(actionId)) {
//                            AppboyLogger.w(Appboy.l, "Action ID cannot be null or blank");
//                            return;
//                        }
//
//                        Appboy.this.i.a(cq.b(campaignId, actionId, actionType));
//                    } catch (Exception var2) {
//                        AppboyLogger.w(Appboy.l, "Failed to log push notification action clicked.", var2);
//                        Appboy.this.a((Throwable)var2);
//                    }
//
//                }
//            });
//        }
    }

    public void logPushNotificationOpened(final Intent intent) {
        LoggerUtils.d(TAG, "call method logPushNotificationOpened");
        /*
         * push open 일때 서버 전송
         * */
        workingThreadSmall.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (intent.hasExtra("cid")) {
                        String cid = intent.getStringExtra("cid");
                        if (!StringUtils.isNullOrBlank(cid)) {
                            LoggerUtils.i(TAG, "Logging push click to notification. Campaign Id: " + cid);
                        } else {
                            LoggerUtils.i(TAG, "Not Found campaign Id with this notification");
                        }
                    } else {

                    }
                } catch (Exception e) {
                    LoggerUtils.w(TAG, "Error logging push notification", e);
                }
            }
        });

//        if (!j()) {
//            this.d.execute(new Runnable() {
//                public void run() {
//                    try {
//                        String var1 = intent.getStringExtra("cid");
//                        if (!StringUtils.isNullOrBlank(var1)) {
//                            AppboyLogger.i(Appboy.l, "Logging push click to Appboy. Campaign Id: " + var1);
//                            Appboy.this.logPushNotificationOpened(var1);
//                        } else {
//                            AppboyLogger.i(Appboy.l, "No campaign Id associated with this notification. Not logging push click to Appboy.");
//                        }
//
//                        Appboy.a((Intent)intent, (bu)Appboy.this.i);
//                    } catch (Exception var2) {
//                        AppboyLogger.w(Appboy.l, "Error logging push notification", var2);
//                    }
//
//                }
//            });
//        }
    }

    public void logPushDeliveryEvent(final String campaignId) {
        LoggerUtils.d(TAG, "call method logPushDeliveryEvent");
        /*
         * push를 받았을 때에 대한 통신
         * delivery!
         * */
//        if (!j()) {
//            this.d.execute(new Runnable() {
//                public void run() {
//                    try {
//                        if (!Appboy.this.f.m()) {
//                            AppboyLogger.v(Appboy.l, "Push delivery events are disabled via server configuration. Not logging event.");
//                            return;
//                        }
//
//                        if (StringUtils.isNullOrBlank(campaignId)) {
//                            AppboyLogger.w(Appboy.l, "Campaign ID cannot be null or blank for push delivery event.");
//                            return;
//                        }
//
//                        Appboy.this.i.a(cp.i(campaignId));
//                    } catch (Exception var2) {
//                        AppboyLogger.w(Appboy.l, "Failed to log push delivery event.", var2);
//                        Appboy.this.a((Throwable)var2);
//                    }
//
//                }
//            });
//        }
    }

    public void showDialog(final Activity activity) {
        LoggerUtils.d(TAG, "call showDialog");



        workingThreadSmall.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageText title = new MessageText.Builder().setText("Title TEST!!").build();
                    MessageText body = new MessageText.Builder().setText("Body TEST!!").build();

                    MessageButton messageButton1 = new MessageButton.Builder()
                            .setText("Button1")
                            .setEventUrl("https://naver.com")
                            .build();
                    Map<ButtonType, MessageButton> messageButton = new HashMap<>();
                    messageButton.put(ButtonType.POSITIVE, messageButton1);

                    Map<String, String> data = new HashMap<>();

                    InAppMessage inAppMessage = new InAppMessage(title, body, null, messageButton, data, MessageType.DIALOG);

//                    getInstance().showMessagePopup(activity, inAppMessage, new NoOpDisplayCallbacks());
//                    GroobeeInAppMessageDisplay.getInstance().showMessagePopup(activity, inAppMessage, new NoOpDisplayCallbacks());

                } catch (Exception e) {
                    LoggerUtils.w(TAG, "Error logging push notification", e);
                }
            }
        });
    }

    public void showDialog(final Activity activity, final View.OnClickListener onClickListener) {
        LoggerUtils.d(TAG, "call showDialog");

        workingThreadSmall.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MessageText title = new MessageText.Builder().setText("Title TEST!!").build();
                    MessageText body = new MessageText.Builder().setText("Body TEST!! " + groobeeConfigProvider.getGroobeeApiKey()).build();
//                    MessageText body = new MessageText.Builder()
//                            .setText("<div id=\"imgMapGrid\"><img name=\"selectedTemplateImage\" id=\"pcSelectedTemplateImageA\"" +
//                                    " src=\"https://s3.ap-northeast-2.amazonaws.com/static.groobee.io/banner/2020/02/05/323cfbef17bd471eb5a4e4013a1b6f86/af7b30739d5c404b818ed965175860db.png\"" +
//                                    " style=\"width: 250px; height: 350px;\"></div>").build();

                    MessageButton messageButton1 = new MessageButton.Builder()
                            .setText("Button1")
                            .setEventUrl("https://naver.com")
                            .build();

                    MessageButton messageButton2 = new MessageButton.Builder()
                            .setText("Button2")
//                            .setActivityName(activity.getClass().getName())
//                            .setActivityName("com.example.test.groobee2.ActivityScreen3")
                            .setOnClickListener(onClickListener)
                            .build();

                    Map<ButtonType, MessageButton> messageButton = new HashMap<>();

//                    if(messageButton.containsKey(ButtonType.POSITIVE))
                    messageButton.put(ButtonType.NEGATIVE, messageButton2);
//                    else
                    messageButton.put(ButtonType.POSITIVE, messageButton1);

                    Map<String, String> data = new HashMap<>();

                    InAppMessage inAppMessage = new InAppMessage(title, body, "https://static.hubzum.zumst.com/hubzum/2018/03/21/10/b4af8da309e846cc87927e8e6f939b23.jpg"
                            , messageButton, data, MessageType.DIALOG);
//                            , messageButton, data, MessageType.HTML_MODAL);

//                    groobeeInAppMessageDisplay.showMessagePopup(activity, inAppMessage, new NoOpDisplayCallbacks());
//                    groobeeInAppMessageDisplay.showMessagePopup(activity, inAppMessage, null);
                    GroobeeInAppMessageDisplay.getInstance().showMessagePopup(activity, inAppMessage, null);

                } catch (Exception e) {
                    LoggerUtils.w(TAG, "Error logging push notification", e);
                }
            }
        });
    }

    private static class UiExecutor implements Executor {
        private static final Handler HANDLER = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            HANDLER.post(command);
        }
    }
}
/*
 * api통신
 * 사용자 정의 메소드 작성
 * custom event 메소드 작성
 * */