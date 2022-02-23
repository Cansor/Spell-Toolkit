package com.jvgme.spelltoolkit.core.android.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jvgme.spelltoolkit.core.widget.EditTextDialog;

/**
 * 文本编辑对话框的安卓实现
 */
public class EditTextDialogImpl extends DialogImpl implements EditTextDialog {
    private final EditText editText;

    public EditTextDialogImpl(Context context) {
        super(context);
        editText = new EditText(context);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        alertDialog.setView(editText);
    }

    /**
     * 设置文本的颜色
     *
     * @param color 十六进制颜色值
     */
    @Override
    public void setTextColor(String color) {
        editText.setTextColor(Color.parseColor(color));
    }

    /**
     * 设置编辑框的背景颜色
     *
     * @param color 十六进制颜色值
     */
    @Override
    public void setBackgroundColor(String color) {
        editText.setBackgroundColor(Color.parseColor(color));
    }

    /**
     * 设置编辑框的高度为精确的行数
     *
     * @param lines 行数
     */
    @Override
    public void setLines(int lines) {
        editText.setLines(lines);
    }

    /**
     * 设置编辑框的内容
     *
     * @param text 文本
     */
    @Override
    public void setText(String text) {
        editText.setText(text);
    }

    /**
     * 设置光标的位置
     *
     * @param i 字符的索引
     */
    @Override
    public void setCursor(int i) {
        editText.setSelection(i);
    }

    /**
     * 设置光标选择的范围
     *
     * @param start 开始的索引
     * @param stop  结束的索引
     */
    @Override
    public void setCursor(int start, int stop) {
        editText.setSelection(start, stop);
    }

    /**
     * 使编辑框获得焦点
     */
    @Override
    public void requestFocus() {
        editText.requestFocus();
    }

    /**
     * 返回编辑框的文本
     *
     * @return String
     */
    @Override
    public String getText() {
        return editText.getText().toString();
    }

    /**
     * 显示对话框
     */
    @Override
    public void show() {
        // 一个 Child 只能有一个 Parent, 所以在第二次使用（调用 show()）时需要先 remove, 否则会报错
        ViewGroup parent = (ViewGroup) editText.getParent();
        if (parent != null)
            parent.removeView(editText);
        super.show();
    }
}
