package com.jvgme.spelltoolkit.core.android.widget;

import android.app.AlertDialog;
import android.content.Context;

import com.jvgme.spelltoolkit.core.widget.Dialog;

public class DialogImpl implements Dialog {
    private final Context context;

    public DialogImpl(Context context) {
        this.context = context;
    }

    /**
     * 弹出一个确认/取消的对话框
     *
     * @param title 标题
     * @param msg   消息文本
     */
    @Override
    public void confirm(String title, String msg, String btn1, String btn2,
                        OnClickListener btn1Listener, OnClickListener btn2Listener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setPositiveButton(btn1, (dialog, which) -> btn1Listener.onClick(which))
                .setNegativeButton(btn2, ((dialog, which) -> btn2Listener.onClick(which)));
    }

    /**
     * 弹出一个可选择的列表项
     *
     * @param title           标题
     * @param items           列表项目录
     * @param onClickListener 监听器
     */
    @Override
    public void select(String title, String[] items, OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setItems(items, (dialog, which) -> onClickListener.onClick(which))
                .show();
    }
}
