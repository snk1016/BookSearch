package com.groobee.message.inappmessage.wrapper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.groobee.message.Groobee;
import com.groobee.message.inappmessage.ButtonType;
import com.groobee.message.inappmessage.model.InAppMessage;
import com.groobee.message.inappmessage.model.MessageButton;
import com.groobee.message.inappmessage.model.MessageText;
import com.groobee.message.utils.StringUtils;

import java.util.Map;

public abstract class BindingWrapper {
    protected final InAppMessage inAppMessage;
    final LayoutInflater inflater;

    protected BindingWrapper(InAppMessage inAppMessage, LayoutInflater inflater) {
        this.inAppMessage = inAppMessage;
        this.inflater = inflater;
    }

    public abstract ViewTreeObserver.OnGlobalLayoutListener inflate(
            Map<ButtonType, View.OnClickListener> buttonClickListener, View.OnClickListener dismissClickListener);

    protected void setTextStyleAppearance(TextView textView, MessageText messageText) {
        if(!StringUtils.isNullOrBlank(messageText.getText()))
            textView.setText(messageText.getText());

        if(!StringUtils.isNullOrBlank(messageText.getTextColor()))
            textView.setTextColor(Color.parseColor(messageText.getTextColor()));

        if(messageText.getTextSize() != null && messageText.getTextSize() > 0)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, messageText.getTextSize());
    }

    protected void setButtonStyleAppearance(Button button, MessageButton messageButton, View.OnClickListener buttonClickListener) {
        if(messageButton != null) {
            if (!StringUtils.isNullOrBlank(messageButton.getText())) {
                String text = messageButton.getText();
                button.setText(text);
                setButtonClickListener(button, buttonClickListener);
            }

            if (!StringUtils.isNullOrBlank(messageButton.getTextColor()))
                button.setTextColor(Color.parseColor(messageButton.getTextColor()));

            if (messageButton.getTextSize() != null && messageButton.getTextSize() > 0)
                button.setTextSize(TypedValue.COMPLEX_UNIT_SP, messageButton.getTextSize());

            /*button background 추가 필요!*/
        }
    }

    protected void setLruImage(ImageView imageView, String imageUrl) {
        if(!StringUtils.isNullOrBlank(imageUrl)) {
            Bitmap bitmap = Groobee.getInstance().getImageLoader().getBitmap(imageUrl);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void setButtonClickListener(Button button, View.OnClickListener buttonClickListener) {
        if (buttonClickListener == null) {
            button.setVisibility(View.GONE);
        } else {
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(buttonClickListener);
        }
    }

    @NonNull
    public abstract ViewGroup getRootView();

    public boolean canSwipeToDismiss() {
        return false;
    }

    @Nullable
    public View.OnClickListener getDismissListener() {
        return null;
    }
}
