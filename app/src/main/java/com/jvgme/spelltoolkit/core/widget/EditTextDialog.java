package com.jvgme.spelltoolkit.core.widget;

/**
 * 文本编辑对话框
 */
public interface EditTextDialog extends Widget, Dialog{
    String ID = "EditTextDialog";
    /**
     * 设置文本的颜色
     *
     * @param color 十六进制颜色值
     */
    void setTextColor(String color);

    /**
     * 设置编辑框的背景颜色
     *
     * @param color 十六进制颜色值
     */
    void setBackgroundColor(String color);

    /**
     * 设置编辑框的高度为精确的行数
     *
     * @param lines 行数
     */
    void setLines(int lines);

    /**
     * 设置编辑框的内容
     *
     * @param text 文本
     */
    void setText(String text);

    /**
     * 设置光标的位置
     *
     * @param i 字符的索引
     */
    void setCursor(int i);

    /**
     * 设置光标选择的范围
     *
     * @param start 开始的索引
     * @param stop 结束的索引
     */
    void setCursor(int start, int stop);

    /**
     * 使编辑框获得焦点
     */
    void requestFocus();

    /**
     * 返回编辑框的文本
     *
     * @return String
     */
    String getText();
}
