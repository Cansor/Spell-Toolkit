package com.jvgme.spelltoolkit.core.android.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jvgme.spelltoolkit.R;
import com.jvgme.spelltoolkit.core.widget.LogWindow;

/**
 * 日志窗口控件的安卓实现
 */
public class LogWindowImpl implements LogWindow {
    private final Activity activity;
    private final PopupWindow popupWindow;

    private final TextView textView;

    public LogWindowImpl(Activity activity) {
        this.activity = activity;

        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.log_plugin_window, null);

        // 获取宽高
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);

        // 创建弹出层窗口
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                point.y / 2, true);
        // 设置动画
        popupWindow.setAnimationStyle(R.style.log_window);
        // 添加背景
//        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
//        popupWindow.setBackgroundDrawable(dw);

        textView = view.findViewById(R.id.tv_plugin_log);
    }

    @Override
    public void show() {
        popupWindow.showAtLocation(activity.getLayoutInflater()
                .inflate(R.layout.activity_file_list, null), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 检测此弹窗是否正在显示，正在显示返回 true, 否则返回 false
     *
     * @return boolean
     */
    @Override
    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    /**
     * 打印插件运行的日志信息
     * @param log 输出到日志界面的信息
     */
    @Override
    public void print(String log) {
        // 从 Android N 开始，Html.fromHtml(String) 被费弃
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            textView.append(Html.fromHtml(log, Html.FROM_HTML_MODE_COMPACT));
        else
            textView.append(Html.fromHtml(log));
    }

    /**
     * 使用指定颜色打印插件运行的日志信息
     *
     * @param log   输出到日志界面的信息
     * @param color 十六进制颜色代码或颜色名（html）
     */
    @Override
    public void print(String log, String color) {
        print("<font color='" + color + "'>" + log + "</font>");
    }

    /**
     * 换行打印插件运行的日志信息
     * @param log 输出到日志界面的信息
     */
    @Override
    public void println(String log) {
        print(log + "<br>");
    }

    /**
     * 使用指定颜色换行打印插件运行的日志信息
     *
     * @param log   输出到日志界面的信息
     * @param color 十六进制颜色代码或颜色名（html）
     */
    @Override
    public void println(String log, String color) {
        print("<font color='" + color + "'>" + log + "</font><br>");
    }

    /**
     * 清除所有内容
     */
    @Override
    public void clear() {
        textView.setText("");
    }
}
