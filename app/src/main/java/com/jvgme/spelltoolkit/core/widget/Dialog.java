package com.jvgme.spelltoolkit.core.widget;

/**
 * 对话框
 */
public interface Dialog extends Widget{
    String ID = "Dialog";

    /**
     * 监听器
     */
    interface OnClickListener {
        /**
         * 对话框被点击时的回调方法
         *
         * @param which 点击的项目编号
         */
        void onClick(int which);
    }

    /**
     * 弹出一个确认/取消的对话框
     *
     * @param title 标题
     * @param msg 消息
     * @param btn1 确认按钮
     * @param btn2 取消按钮
     * @param btn1Listener 确认按钮的监听事件
     * @param btn2Listener 取消按钮的监听事件
     */
    void confirm(String title, String msg, String btn1, String btn2,
                 OnClickListener btn1Listener, OnClickListener btn2Listener);

    /**
     * 弹出一个可选择的列表项
     *
     * @param title 标题
     * @param items 列表项目录
     * @param onClickListener 监听器
     */
    void select(String title, String[] items, OnClickListener onClickListener);
}
