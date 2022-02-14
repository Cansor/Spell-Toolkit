package com.jvgme.spelltoolkit.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 插件管理器
 * 该类设计为单例模式
 */
public class PluginManager {
    private static PluginManager pluginManager;

    private PluginLoader pluginLoader;
    private final Map<String, Plugin> pluginMap;

    private PluginManager() {
        pluginMap = new HashMap<>();
    }

    /**
     * 获取一个未指定插件加载器的插件管理器实例。
     * 这是一个单例的对象，反回的总是同一个实例。
     *
     * @return PluginsManager
     */
    public static PluginManager instance() {
        if (pluginManager == null) {
            pluginManager = new PluginManager();
        }
        return pluginManager;
    }

    /**
     * 获取一个插件管理器实例，并指定一个插件加载器
     * 这是一个单例的对象，反回的总是同一个实例（但是插件加载器会被重新指定，即等价于
     * 调用 instance() 后再调用 setPluginLoader()）。
     *
     * @param pluginLoader 插件加载器
     * @return PluginsManager
     */
    public static PluginManager instance(PluginLoader pluginLoader) {
        instance().setPluginLoader(pluginLoader);
        return pluginManager;
    }

    /**
     * 指定一个插件加载器
     *
     * @param pluginLoader PluginLoader
     */
    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = pluginLoader;
    }

    /**
     * 加载插件，并反回插件数量
     *
     * @return 加载的插件的数量
     */
    public int load() {
        pluginMap.clear();
        pluginMap.putAll(pluginLoader.load());
        return pluginMap.size();
    }

    /**
     * 获取所有插件，反回一个包含了所有插件对象 Plugin 的 List 集合
     *
     * @return 包含所有 Plugin 对象的 List 集合
     */
    public List<Plugin> getAllPlugins() {
        List<Plugin> pluginList = new ArrayList<>();

        Set<String> keys = pluginMap.keySet();
        for (String key : keys) {
            Plugin p = pluginMap.get(key);
            if (p != null)
                pluginList.add(p);
        }

        return pluginList;
    }

    /**
     * 根据插件 ID 获取插件的实例对象
     *
     * @param pluginID ID
     * @return Plugin
     */
    public Plugin getPlugin(String pluginID) {
        return pluginMap.get(pluginID);
    }

    /**
     * 反回已加载的插件的数量
     *
     * @return 已加载的插件的数量
     */
    public int getPluginQuantity() {
        return pluginMap.size();
    }

}
