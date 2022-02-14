package com.jvgme.spelltoolkit.core.widget;

import java.io.InputStream;

/**
 * 对话框
 */
public interface Dialog extends Widget{
    String ID = "Dialog";

    /**
     * 点击监听器
     */
    interface OnClickListener {
        /**
         * 对话框被点击时的回调方法
         *
         * @param dialog 被点击的 Dialog 实例
         * @param which 点击的项目下标
         */
        void onClick(Dialog dialog, int which);
    }

    /**
     * 多选监听器
     */
    interface OnMultiChoiceClickListener {
        /**
         * 被点击时回调
         * @param which 点击的项目的下标
         * @param isChecked 当前项目的状态，选中返回 true, 否则返回 false
         */
        void onClick(int which, boolean isChecked);
    }

    /**
     * 设置对话框的标题
     *
     * @param title 标题
     */
    Dialog setTitle(String title);

    /**
     * 设置消息文本
     *
     * @param message 消息文本
     */
    Dialog setMessage(String message);

    /**
     * 设置确认按钮
     *
     * @param buttonText 按钮显示文本
     * @param onClickListener 点击监听器
     */
    Dialog setPositiveButton(String buttonText, OnClickListener onClickListener);

    /**
     * 设置否定按钮
     *
     * @param buttonText 按钮显示文本
     * @param onClickListener 点击监听器
     */
    Dialog setNegativeButton(String buttonText, OnClickListener onClickListener);

    /**
     * 设置选项列表
     *
     * @param items 选项列表数组
     * @param onClickListener 点击监听器，在选中时调用
     */
    Dialog setItem(String[] items, OnClickListener onClickListener);

    /**
     * 设置单选项
     *
     * @param items 单选列表项
     * @param i 默认选中项的下标，-1 表示无选中项
     * @param onClickListener 监听器，在选中时调用
     */
    Dialog setSingleChoiceItems(String[] items, int i, OnClickListener onClickListener);

    /**
     * 设置复选项
     *
     * @param items 复选列表项
     * @param checkedItems 复选列表项的状态，true 表示选中，false 表示未选中
     * @param listener 监听器，在选中时调用
     */
    Dialog setMultiChoiceItems(String[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener);

    /**
     * 设置对话框的 Icon
     *
     * @param is InputStream
     * @param srcName 资源名称
     */
    Dialog setIcon(InputStream is, String srcName);

    /**
     * 显示对话框
     */
    void show();
}
