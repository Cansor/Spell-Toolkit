package com.jvgme.spelltoolkit.core.widget;

/**
 * 日志窗口
 * 插件执行中的日志将通过此窗口展示给用户
 */
public interface LogWindow extends Widget {
    String ID = "LogWindow";

    // 一些常用颜色
    String COLOR_RED = "#FF0000";
    String COLOR_BLUE = "#0000FF";
    String COLOR_GREEN = "#00FF00";
    String COLOR_YELLOW = "#FFFF00";
    String COLOR_PURPLE = "#800080";
    String COLOR_ORANGE = "#FFA500";

    /**
     * 显示窗口
     */
    void show();

    /**
     * 检测此弹窗是否正在显示，正在显示返回 true, 否则返回 false
     * @return boolean
     */
    boolean isShowing();

    /**
     * 打印插件运行的日志信息
     * @param log 输出到日志界面的信息
     */
    void print(String log);

    /**
     * 使用指定颜色打印插件运行的日志信息
     *
     * @param log 输出到日志界面的信息
     * @param color 十六进制颜色代码
     */
    void print(String log, String color);

    /**
     * 换行打印插件运行的日志信息
     * @param log 输出到日志界面的信息
     */
    void println(String log);

    /**
     * 使用指定颜色换行打印插件运行的日志信息
     * @param log 输出到日志界面的信息
     * @param color 十六进制颜色代码
     */
    void println(String log, String color);

    /**
     * 清除所有日志内容
     */
    void clear();
}
