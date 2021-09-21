package com.jvgme.spelltoolkit.core;

public interface PluginLog {

    /**
     * 打印插件运行的日志信息
     * @param log 日志
     */
    void print(String log);

    // 换行打印
    void println(String log);

    /**
     * 清除所有内容
     */
    void clear();
}
