package com.jvgme.spelltoolkit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

public class PluginRunLogWindow {
    private final Activity activity;
    private final PopupWindow popupWindow;

    private final TextView textView;

    public PluginRunLogWindow(Activity activity) {
        this.activity = activity;

        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.log_plugin_window, null);

        // 获取宽高
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);

        popupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT,
                point.y / 2, true);
        popupWindow.setAnimationStyle(R.style.log_window);

        textView = view.findViewById(R.id.tv_plugin_log);
    }

    public TextView getTextView() {
        return textView;
    }

    @SuppressLint("InflateParams")
    public void showWindow() {
        popupWindow.showAtLocation(activity.getLayoutInflater()
                .inflate(R.layout.activity_file_list, null), Gravity.BOTTOM, 0, 0);
    }

}
