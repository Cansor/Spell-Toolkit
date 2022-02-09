package com.jvgme.spelltoolkit.core.widget;

/**
 * 消息提示
 */
public interface Message extends Widget{
    String ID = "Message";

    /**
     * 弹出一条消息，持续指定时间后自动消失
     *
     * @param msg 消息内容
     * @param duration 持续时间，毫秒
     */
    void toast(String msg, int duration);
}
