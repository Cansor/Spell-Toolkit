package com.jvgme.spelltoolkit.core;


import java.io.File;

/**
 * 插件执行器
 *
 * 执行插件功能，并对插件提供资源（如控件）
 */
public interface PluginExecutor {

    /**
     * 执行插件
     *
     * @param PluginId 插件ID
     * @param file 要处理的文件
     */
    void execute(String PluginId, File file);
}
