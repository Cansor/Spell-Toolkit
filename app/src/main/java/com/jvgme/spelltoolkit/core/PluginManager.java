package com.jvgme.spelltoolkit.core;

import com.jvgme.spelltoolkit.util.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插件管理器
 */
public class PluginManager {
    public static final String PLUGIN_PATH = "/SpellToolkit/Plugins";

    private static PluginManager pluginManager;

    private final PluginLoader pluginLoader;
    private Map<String, Plugin> pluginMap;

    private PluginManager(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
        initialize();
    }

    // 初始化，加载插件
    private void initialize() {
        if (pluginLoader != null) {
            try {
                pluginMap = pluginLoader.load();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static PluginManager getPluginsManager(PluginLoader pluginLoader) {
        if (pluginManager == null)
            pluginManager = new PluginManager(pluginLoader);
        return pluginManager;
    }

    /**
     * 获取所有插件
     */
    public List<Plugin> getAllPlugins() {
        List<Plugin> plugins = new ArrayList<>();

        Set<String> keys = pluginMap.keySet();
        for (String key : keys) {
            Plugin p = pluginMap.get(key);
            if (p != null)
                plugins.add(p);
        }

        return plugins;
    }

    /**
     * 反回插件的数量
     */
    public int getPluginQuantity() {
        return pluginMap.size();
    }

    /**
     * 重新加载插件，并反回插件数量
     */
    public int reload() {
        try {
            pluginMap = pluginLoader.load();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return pluginMap.size();
    }

    /**
     * 根据插件ID执行相应插件
     *
     * @param pluginsId 插件ID，根据该ID区分插件
     * @param file 当前选择的File对象，给插件开发者处理
     */
    public void execute(String pluginsId, PluginLog pluginLog, File file) throws InstantiationException, IllegalAccessException {
        Plugin plugin = pluginMap.get(pluginsId);
        if (plugin == null) {
            Tools.logInfo("没有该插件，ID: " + pluginsId);
            return;
        }

        // 创建插件实例
        Class<?> aClass = plugin.getMainClass();
        PluginsServer pluginsServer = (PluginsServer) aClass.newInstance();

        // 执行插件
        pluginsServer.before(pluginLog);
        pluginsServer.service(file);
        pluginsServer.after();
    }
}
