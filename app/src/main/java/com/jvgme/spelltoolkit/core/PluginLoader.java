package com.jvgme.spelltoolkit.core;

import java.io.IOException;
import java.util.Map;

public interface PluginLoader {

    /**
     * 获取插件对象的Map集合，插件ID为key，插件实体对象为value
     *
     * @return 插件对象的Map集合
     */
    Map<String, Plugin> load() throws IOException, ClassNotFoundException;
}
