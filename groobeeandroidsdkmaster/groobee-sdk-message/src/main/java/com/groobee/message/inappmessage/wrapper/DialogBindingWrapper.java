package com.groobee.message.inappmessage.wrapper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.groobee.message.R;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.MessageType;
import com.groobee.message.inappmessage.interfaces.InAppMessageScope;
import com.groobee.message.inappmessage.layout.GroobeeLinearLayout;
import com.groobee.message.inappmessage.model.InAppMessage;

import java.util.Map;

import javax.inject.Inject;

@InAppMessageScope
public class DialogBindingWrapper extends BindingWrapper {

    private GroobeeLinearLayout layoutRoot;

    private ImageView imgDialogContents;

    private TextView txtDialogTitle;
    private TextView txtDialogBody;

    private Button btnDialogNegative;
    private Button btnDialogPositive;

    private View.OnClickListener dismissListener;

    private ViewTreeObserver.OnGlobalLayoutListener layoutListener = new ScrollViewAdjustableListener();

    @Inject
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public DialogBindingWrapper (InAppMessage inAppMessage, LayoutInflater inflater) {
        super(inAppMessage, inflater);
    }

    @NonNull
    @Override
    public ViewTreeObserver.OnGlobalLayoutListener inflate(Map<ButtonType, View.OnClickListener> buttonClickListener, View.OnClickListener dismissClickListener) {
        View root = inflater.inflate(R.layout.dialog, null);

        layoutRoot = root.findViewById(R.id.layoutRoot);

        imgDialogContents = root.findViewById(R.id.imgDialogContents);

        txtDialogTitle = root.findViewById(R.id.txtDialogTitle);
        txtDialogBody = root.findViewById(R.id.txtDialogBody);

        btnDialogNegative = root.findViewById(R.id.btnDialogNegative);
        btnDialogPositive = root.findViewById(R.id.btnDialogPositive);

        if(inAppMessage.getMessageType().equals(MessageType.DIALOG)) {
            setLruImage(imgDialogContents, inAppMessage.getimageUrl());

            setTextStyleAppearance(txtDialogTitle, inAppMessage.getTitle());
            setTextStyleAppearance(txtDialogBody, inAppMessage.getBody());

            setButtonStyleAppearance(btnDialogPositive, inAppMessage.getMessageButton().get(ButtonType.POSITIVE), buttonClickListener.get(ButtonType.POSITIVE));
            setButtonStyleAppearance(btnDialogNegative, inAppMessage.getMessageButton().get(ButtonType.NEGATIVE), buttonClickListener.get(ButtonType.NEGATIVE));
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
