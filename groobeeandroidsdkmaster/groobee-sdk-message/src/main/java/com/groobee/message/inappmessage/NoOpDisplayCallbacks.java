package com.groobee.message.inappmessage;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.groobee.message.inappmessage.interfaces.GroobeeInAppMessagingDisplayCallbacks;
import com.groobee.message.inappmessage.model.MessageButton;

public class NoOpDisplayCallbacks implements GroobeeInAppMessagingDisplayCallbacks {
    @NonNull
    @Override
    public Task<Void> impressionDetected() {
        return new TaskCompletionSource<Void>().getTask();
    }

    @NonNull
    @Override
    public Task<Void> messageDismissed(@NonNull InAppMessagingDismissType dismissType) {
        return new TaskCompletionSource<Void>().getTask();
    }

    @NonNull
    @Override
    public Task<Void> messageClicked(@NonNull MessageButton messageButton) {
        return new TaskCompletionSource<Void>().getTask();
    }

    @NonNull
    @Override
    public Task<Void> displayErrorEncountered(@NonNull InAppMessagingErrorReason inAppMessagingErrorReason) {
        return new TaskCompletionSource<Void>().getTask();
    }
}
