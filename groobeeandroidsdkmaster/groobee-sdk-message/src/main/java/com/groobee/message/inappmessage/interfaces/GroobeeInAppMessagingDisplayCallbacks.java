package com.groobee.message.inappmessage.interfaces;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.groobee.message.inappmessage.model.MessageButton;

public interface GroobeeInAppMessagingDisplayCallbacks {

    // log the campaign impression:
    @NonNull
    Task<Void> impressionDetected();

    // log when a message is dismissed, and specify dismiss type
    @NonNull
    Task<Void> messageDismissed(@NonNull InAppMessagingDismissType dismissType);

    // log when a message is tap (ie: button, in the modal view)  with the Action followed
    @NonNull
    Task<Void> messageClicked(@NonNull MessageButton messageButton);

    // log when there is an issue rendering the content (ie, image_url is invalid
    // or file_type is unsupported
    @NonNull
    Task<Void> displayErrorEncountered(@NonNull InAppMessagingErrorReason inAppMessagingErrorReason);

    enum InAppMessagingDismissType {
        // Unspecified dismiss type
        UNKNOWN_DISMISS_TYPE,

        // Message was dismissed automatically after a timeout
        AUTO,

        // Message was dismissed by clicking on cancel button or outside the message
        CLICK,

        // Message was swiped
        SWIPE
    }

    enum InAppMessagingErrorReason {
        // Generic error
        UNSPECIFIED_RENDER_ERROR,

        // Failure to fetch the image
        IMAGE_FETCH_ERROR,

        // Failure to display the image
        IMAGE_DISPLAY_ERROR,

        // Image has an unsupported format
        IMAGE_UNSUPPORTED_FORMAT
    }
}
