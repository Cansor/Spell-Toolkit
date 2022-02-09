package com.jvgme.spelltoolkit.core.server;

import com.jvgme.spelltoolkit.core.widget.WidgetManager;

import java.io.File;

/**
 * 插件服务接口，执行插件时调用该接口的实现类。
 */
public interface PluginServer {
    /**
     * 插件执行之前，一般用来准备资源
     * @param widgetManager 控件管理器的实例，插件可以通过此对象来获得控件
     */
    void before(WidgetManager widgetManager);

    /**
     * 插件执行之时，一般为正式要执行的插件代码
     * @param file 文件和目录路径的实例，由插件处理。
     */
    void service(File file);

    /**
     * 插件执行之后，一般为资源释放
     */
    void after();
}
