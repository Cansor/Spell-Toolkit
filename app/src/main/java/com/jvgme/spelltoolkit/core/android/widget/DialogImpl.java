package com.jvgme.spelltoolkit.core.android.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AlertDialog;

import com.jvgme.spelltoolkit.core.widget.Dialog;

import java.io.InputStream;

/**
 * 对话框的安卓实现
 */
public class DialogImpl implements Dialog {
    protected final AlertDialog.Builder alertDialog;

    protected DialogImpl(Context context) {
        alertDialog = new AlertDialog.Builder(context);
    }

    /**
     * 返回该类的实例
     *
     * @param context Context
     * @return 该类的实例
     */
    public static Dialog create(Context context) {
        return new DialogImpl(context);
    }

    /**
     * 设置对话框的标题
     *
     * @param title 标题
     */
    @Override
    public Dialog setTitle(String title) {
        alertDialog.setTitle(title);
        return this;
    }

    /**
     * 设置消息文本
     * 注意，当设置了消息文本后，选项将不可用。
     *
     * @param message 消息文本
     */
    @Override
    public Dialog setMessage(String message) {
        alertDialog.setMessage(message);
        return this;
    }

    /**
     * 设置确认按钮
     *
     * @param buttonText      按钮显示文本
     * @param onClickListener 点击监听器
     */
    @Override
    public Dialog setPositiveButton(String buttonText, OnClickListener onClickListener) {
        alertDialog.setPositiveButton(buttonText, (dialog, which) -> {
            if (onClickListener != null)
                onClickListener.onClick(this, which);
        });
        return this;
    }

    /**
     * 设置否定按钮
     *
     * @param buttonText      按钮显示文本
     * @param onClickListener 点击监听器
     */
    @Override
    public Dialog setNegativeButton(String buttonText, OnClickListener onClickListener) {
        alertDialog.setNegativeButton(buttonText, (dialog, which) -> {
            if (onClickListener != null)
                onClickListener.onClick(this, which);
        });
        return this;
    }

    /**
     * 设置选项列表
     *
     * @param items           选项列表数组
     * @param onClickListener 点击监听器，在选中时调用
     */
    @Override
    public Dialog setItem(String[] items, OnClickListener onClickListener) {
        alertDialog.setItems(items, (dialog, which) -> {
            if (onClickListener != null)
                onClickListener.onClick(this, which);
        });
        return this;
    }

    /**
     * 设置单选项
     *
     * @param items           单选列表项
     * @param i               默认选中项的下标，-1 表示无选中项
     * @param onClickListener 监听器，在选中时调用
     */
    @Override
    public Dialog setSingleChoiceItems(String[] items, int i, OnClickListener onClickListener) {
        alertDialog.setSingleChoiceItems(items, i, (dialog, which) -> {
            if (onClickListener != null)
                onClickListener.onClick(this, which);
        });
        return this;
    }

    /**
     * 设置复选项
     *
     * @param items        复选列表项
     * @param checkedItems 复选列表项的状态，true 表示选中，false 表示未选中
     * @param listener     监听器，在选中时调用
     */
    @Override
    public Dialog setMultiChoiceItems(String[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
        alertDialog.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            if (listener != null)
                listener.onClick(which, isChecked);
        });
        return this;
    }

    /**
     * 设置对话框的 Icon
     * 注意，只有在设置了标题的情况下才会显示
     *
     * @param is InputStream
     * @param srcName 资源名称
     */
    @Override
    public Dialog setIcon(InputStream is, String srcName) {
        Drawable drawable = BitmapDrawable.createFromStream(is, srcName);
        alertDialog.setIcon(drawable);
        return this;
    }

    /**
     * 显示对话框
     */
    @Override
    public void show() {
        alertDialog.show();
    }

}
