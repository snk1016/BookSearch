package com.groobee.message.push.actions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.groobee.message.common.Channel;
import com.groobee.message.utils.StringUtils;

public class ActionFactory {

  /**
   * Convenience method for creating {@link UriAction} instances. Returns null if the supplied url
   * is null, blank, or can not be parsed into a valid Uri.
   */
  public static UriAction createUriActionFromUrlString(String url, Bundle extras, Channel channel) {
    if (!StringUtils.isNullOrBlank(url)) {
      Uri uri = Uri.parse(url);
      return createUriActionFromUri(uri, extras, channel);
    }
    return null;
  }

  /**
   * Convenience method for creating {@link UriAction} instances. Returns null if the supplied uri
   * is null.
   */
  public static UriAction createUriActionFromUri(Uri uri, Bundle extras, Channel channel) {
    if (uri != null) {
      return new UriAction(uri, extras, channel);
    }
    return null;
  }

  public static UriAction createUriActionFromActMove(Intent intent, boolean isActMoveEnabled, Channel channel) {
    if (isActMoveEnabled) {
      return new UriAction(isActMoveEnabled, intent, channel);
    }
    return null;
  }
}
