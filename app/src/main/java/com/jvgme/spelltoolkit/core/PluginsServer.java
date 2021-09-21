package com.jvgme.spelltoolkit.core;

import java.io.File;

public interface PluginsServer {
    /**
     * 插件执行之前
     */
    void before(PluginLog pluginLog);

    /**
     * 插件执行
     */
    void service(File file);

    /**
     * 插件执行之后
     */
    void after();
}
