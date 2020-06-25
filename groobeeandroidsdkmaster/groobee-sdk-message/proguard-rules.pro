# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.groobee.message.Groobee {
    getInstance();
    configure(android.content.Context, com.groobee.message.GroobeeConfig);
    getActivityLifecycleCallbacks();
    showDialog(android.app.Activity);
    showDialog(android.app.Activity, android.view.View$OnClickListener);
    setWebViewLogger(java.lang.String);
    logTestService(android.app.Activity);
}


-keep class com.groobee.message.GroobeeConfig {
    GroobeeConfig(GroobeeConfig.Builder);
}

-keep class com.groobee.message.GroobeeConfig$Builder {
    Builder();
    build();
    setApiKey(java.lang.String);
    setHandlePushDeepLinks(boolean);
    setPushMoveActivityEnabled(boolean);
    setPushMoveActivityClassName(java.lang.Class);
    setSmallNotificationIcon(java.lang.String);
    setLargeNotificationIcon(java.lang.String);
}

-keep class com.groobee.message.utils.LoggerUtils {
    setLogLevel(int);
}

-keep class com.groobee.message.GroobeeFirebaseMessagingService {
    handleRemoteMessage(android.content.Context, com.google.firebase.messaging.RemoteMessage);
}

-keep class com.android.okhttp.* { *;}