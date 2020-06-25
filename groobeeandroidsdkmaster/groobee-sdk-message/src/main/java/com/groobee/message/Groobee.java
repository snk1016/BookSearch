package com.groobee.message;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.groobee.message.common.GroobeeImageLoader;
import com.groobee.message.common.GroobeeServiceManager;
import com.groobee.message.common.ServicesConstants;
import com.groobee.message.common.thread.HandlerUncaughtException;
import com.groobee.message.common.thread.WorkingThreadFactory;
import com.groobee.message.common.thread.WorkingThreadLarge;
import com.groobee.message.common.thread.WorkingThreadSmall;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.MessageType;
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
import com.groobee.message.utils.WebViewUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

    private WorkingThreadLarge workingThreadLarge;

//    private GroobeeInAppMessageDisplay groobeeInAppMessageDisplay;

//    private RenewableTimer impressionTimer;
//    private RenewableTimer autoDismissTimer;

//    private BindingWrapperFactory bindingWrapperFactory;

//    private AnimatorUtils animatorUtils;

    private GroobeeConfigProvider groobeeConfigProvider;

    private RuntimeConfigProvider runtimeConfigProvider;

    private GroobeeServiceManager groobeeServiceManager;

    private SharedPreferences sharedPreferences;

    private static class LazyHolder {
        private static final Groobee INSTANCE = new Groobee();
    }

    public static Groobee getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Groobee() {
        handlerUncaughtException = new HandlerUncaughtException();

        WorkingThreadFactory workingThreadFactory = new WorkingThreadFactory("Groobee-Working-Pool-Thread");
        workingThreadFactory.setUncaughtExceptionHandler(handlerUncaughtException);

        workingThreadSmall = new WorkingThreadSmall("identifier", workingThreadFactory);

        workingThreadLarge = new WorkingThreadLarge("identifier-large", workingThreadFactory);

//        impressionTimer = new RenewableTimer();
//        autoDismissTimer = new RenewableTimer();
//
//        animatorUtils = new AnimatorUtils();
    }

    public static boolean configure(Context context, GroobeeConfig config) {
        LoggerUtils.d(TAG, "configure() called with configuration: " + config);

        getInstance().runtimeConfigProvider = new RuntimeConfigProvider(context);
        getInstance().context = context.getApplicationContext();
        getInstance().groobeeImageLoader = new GroobeeImageLoader(context);
        getInstance().groobeeConfigProvider = new GroobeeConfigProvider(context);
        getInstance().sharedPreferences = context.getSharedPreferences(RuntimeConfigProvider.PREFERENCES_NAME, Context.MODE_PRIVATE);
        getInstance().groobeeServiceManager = new GroobeeServiceManager(context);

        if(config != null) {
            getInstance().runtimeConfigProvider.setRuntimeConfig(config);
            return true;
        }

        getInstance().runtimeConfigProvider.clean();

        return false;
    }

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

    public void logPushNotificationActionClicked(final String campaignId, final String actionId, final String actionType) {
        LoggerUtils.d(TAG, "call method logPushNotificationActionClicked");

        /*
        Push 클릭한 내용 서버 전송
        * */
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
                    String key_cid = context.getString(R.string.KEY_VARIABLE_CAMPAIGN_ID);

                    if (intent.hasExtra(key_cid)) {
                        String cid = intent.getStringExtra(key_cid);
                        if (!StringUtils.isNullOrBlank(cid)) {
                            LoggerUtils.i(TAG, "Logging push click to notification. Campaign Id: " + cid);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            runtimeConfigProvider.addData(editor, RuntimeConfigProvider.PREFERENCES_PUSH_UUID, cid);
                            editor.apply();

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
    }

    public void logPushDeliveryEvent(final String campaignId) {
        LoggerUtils.d(TAG, "call method logPushDeliveryEvent");
        /*
         * push를 받았을 때에 대한 통신
         * delivery!
         * */
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
        LoggerUtils.d(TAG, "call showDialog " + context.getString(R.string.txt_test));

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

//                    InAppMessage inAppMessage = new InAppMessage(title, body, "https://static.hubzum.zumst.com/hubzum/2018/03/21/10/b4af8da309e846cc87927e8e6f939b23.jpg"
                    InAppMessage inAppMessage = new InAppMessage(title, body, "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcS_boWRLNjwhmPe_XoV2Ed72Ad5z1cRhFt100de4oV9sxbj4gJW&usqp=CAU"
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

    public void setWebViewLogger(final String url) {
        LoggerUtils.d(TAG, "call setWebViewLogger");

        workingThreadSmall.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String grb_ck = WebViewUtils.getCookieById(url, context.getString(R.string.KEY_VARIABLE_GROOBEE_CK));
                    String grb_ui = WebViewUtils.getCookieById(url, context.getString(R.string.KEY_VARIABLE_GROOBEE_UI));
                    Log.d("nh", "grb_ck : " + grb_ck + " grb_ui : " + grb_ui);
                } catch (Exception e) {
                    LoggerUtils.w(TAG, "Error logging webview cookie", e);
                }
            }
        });
    }

    public void logTestService(final Activity activity) {
        LoggerUtils.d(TAG, "call setWebViewLogger");

        workingThreadSmall.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject request = new JSONObject();
                    request.put("test_id", 1);
                    request.put("test_body", "가나다라");
                    request.put("test_title", "제목!!");

                    groobeeServiceManager.sendRequest(activity, ServicesConstants.API_TEST, request);
                } catch (Exception e) {
                    LoggerUtils.w(TAG, "Error logging webview cookie", e);
                }
            }
        });
    }

//    groobeeServiceManager

//    private static class UiExecutor implements Executor {
//        private static final Handler HANDLER = new Handler(Looper.getMainLooper());
//
//        @Override
//        public void execute(@NonNull Runnable command) {
//            HANDLER.post(command);
//        }
//    }
}
/*
 * api통신
 * 사용자 정의 메소드 작성
 * custom event 메소드 작성
 * */