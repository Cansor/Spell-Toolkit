package com.jvgme.spelltoolkit.core.android.widget;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.jvgme.spelltoolkit.core.widget.Message;

/**
 * 消息提示的安卓实现
 */
public class MessageImpl implements Message {
    private final Context context;

    public MessageImpl(Context context) {
        this.context = context;
    }

    /**
     * 弹出一条消息，持续指定时间后自动消失
     *
     * @param msg      消息内容
     * @param duration 持续时间，大于 2000 或小于 2000
     */
    @Override
    public void toast(String msg, int duration) {
        Toast.makeText(context, msg, duration>2000 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}
