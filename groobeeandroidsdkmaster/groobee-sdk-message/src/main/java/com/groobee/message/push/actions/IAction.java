package com.groobee.message.push.actions;

import android.content.Context;

import com.groobee.message.common.Channel;

public interface IAction {

  void execute(Context context);

  Channel getChannel();
}
