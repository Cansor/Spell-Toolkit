package com.jvgme.spelltoolkit.core.widget;

/**
 * 控件管理器
 *
 * 所有控件都由该接口的实现类注册、提供。
 * 插件开发者通过该接口获取已注册的控件。
 */
public interface WidgetManager {

    /**
     * 注册一个控件，ID 必需是唯一的
     *
     * @param id 唯一 ID
     * @param widget 控件类
     */
    void registerWidget(String id, Widget widget);

    /**
     * 返回一个已注册的控件实例
     *
     * @param id 控件注册时定义的 ID
     * @return Widget
     */
    Widget getWidget(String id);

    /**
     * 返回一个已注册的控件的 ID 名单，以数组形式返回
     *
     * @return 已注册的控件的 ID 的数组
     */
    String[] getRegisteredWidget();
}
