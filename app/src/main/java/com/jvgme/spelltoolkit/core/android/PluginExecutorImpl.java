package com.jvgme.spelltoolkit.core.android;

import com.jvgme.spelltoolkit.core.Plugin;
import com.jvgme.spelltoolkit.core.PluginExecutor;
import com.jvgme.spelltoolkit.core.PluginManager;
import com.jvgme.spelltoolkit.core.android.widget.LogWindowImpl;
import com.jvgme.spelltoolkit.core.server.PluginServer;
import com.jvgme.spelltoolkit.core.widget.LogWindow;
import com.jvgme.spelltoolkit.core.widget.WidgetManager;
import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;

/**
 * 插件执行器的安卓实现
 */
public class PluginExecutorImpl implements PluginExecutor {
    private final PluginManager pluginManager;
    private final WidgetManager widgetManager;

    public PluginExecutorImpl(PluginManager pluginManager, WidgetManager widgetManager) {
        this.pluginManager = pluginManager;
        this.widgetManager = widgetManager;
    }

    /**
     * 执行插件
     *
     * @param pluginId 插件ID
     * @param file 要处理的文件
     */
    @Override
    public void execute(String pluginId, File file) {
        LogWindow logWindow = (LogWindow) widgetManager.getWidget(LogWindowImpl.ID);
        try {
            // 执行插件
            Plugin plugin = pluginManager.getPlugin(pluginId);
            if (plugin == null) {
                Tools.logInfo("没有该插件，ID: " + pluginId);
                return;
            }

            // 创建插件实例
            Class<?> aClass = plugin.getMainClass();
            PluginServer pluginServer = (PluginServer) aClass.newInstance();

            // 执行插件
            pluginServer.before(widgetManager);
            pluginServer.service(file);
            pluginServer.after();
        } catch (Exception e) {
            e.printStackTrace();
            StringBuilder log = new StringBuilder();
            log.append(e).append("<br>");
            StackTraceElement[] stackTrace = e.getStackTrace();
            for (StackTraceElement ste : stackTrace) {
                log.append("\tat ").append(ste).append("<br>");
            }
            logWindow.println(log.toString(), LogWindow.COLOR_RED);
        }

    }
}
