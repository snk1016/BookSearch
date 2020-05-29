package com.groobee.message.inappmessage.wrapper;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.groobee.message.R;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.MessageType;
import com.groobee.message.inappmessage.interfaces.InAppMessageScope;
import com.groobee.message.inappmessage.layout.GroobeeLinearLayout;
import com.groobee.message.inappmessage.model.InAppMessage;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

@InAppMessageScope
public class HtmlModalBindingWrapper extends BindingWrapper {

    private GroobeeLinearLayout layoutRoot;

    private LinearLayout layoutWebModalContents;

    private WebView webView;

    private Button btnWebModalNegative;
    private Button btnWebModalPositive;

    private View.OnClickListener dismissListener;

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ScrollViewAdjustableListener();

    @Inject
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public HtmlModalBindingWrapper(InAppMessage inAppMessage, LayoutInflater inflater) {
        super(inAppMessage, inflater);
    }

    @NonNull
    @Override
    public ViewTreeObserver.OnGlobalLayoutListener inflate(Map<ButtonType, View.OnClickListener> buttonClickListener, View.OnClickListener dismissClickListener) {
        View root = inflater.inflate(R.layout.html_modal, null);

        layoutRoot = root.findViewById(R.id.layoutRoot);

        layoutWebModalContents = root.findViewById(R.id.layoutWebModalContents);

        btnWebModalNegative = root.findViewById(R.id.btnWebModalNegative);
        btnWebModalPositive = root.findViewById(R.id.btnWebModalPositive);

        webView = new WebView(root.getContext().getApplicationContext());

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setDomStorageEnabled(true);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setBackgroundColor(Color.TRANSPARENT);

        layoutWebModalContents.addView(webView);

        if(inAppMessage.getMessageType().equals(MessageType.HTML_MODAL)) {
            webView.loadData(inAppMessage.getBody().getText(), "text/html", "utf-8");

            setButtonStyleAppearance(btnWebModalPositive, inAppMessage.getMessageButton().get(ButtonType.POSITIVE), buttonClickListener.get(ButtonType.POSITIVE));
            setButtonStyleAppearance(btnWebModalNegative, inAppMessage.getMessageButton().get(ButtonType.NEGATIVE), buttonClickListener.get(ButtonType.NEGATIVE));
        }

        setDismissListener(dismissClickListener);

        return layoutListener;
    }

    @NonNull
    @Override
    public ViewGroup getRootView() {
        return layoutRoot;
    }

    @NonNull
    @Override
    public View.OnClickListener getDismissListener() {
        return dismissListener;
    }

    private void setDismissListener(View.OnClickListener dismissListener) {
        this.dismissListener = dismissListener;
        layoutRoot.onDismissListener(dismissListener);
    }

    public void setLayoutListener(ViewTreeObserver.OnGlobalLayoutListener listener) {
        this.layoutListener = listener;
    }

    public class ScrollViewAdjustableListener implements ViewTreeObserver.OnGlobalLayoutListener {
        public ScrollViewAdjustableListener() {
        }

        public void onGlobalLayout() {
            layoutRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    }
}
