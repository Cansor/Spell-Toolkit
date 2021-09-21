package com.jvgme.spelltoolkit.core.android;

import android.os.Build;
import android.text.Html;
import android.widget.TextView;

import com.jvgme.spelltoolkit.core.PluginLog;

/**
 * 插件日志类
 */
public class PluginLogImpl implements PluginLog {
    TextView textView;

    public PluginLogImpl(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void print(String log) {
        // 从 Android N 开始，Html.fromHtml(String) 被费弃
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            textView.append(Html.fromHtml(log, Html.FROM_HTML_MODE_COMPACT));
        else
            textView.append(Html.fromHtml(log));
    }

    @Override
    public void println(String log) {
        print(log + "<br>");
    }

    /**
     * 清除所有内容
     */
    @Override
    public void clear() {
        textView.setText("");
    }
}
