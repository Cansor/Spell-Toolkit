package com.jvgme.spelltoolkit.core.android.widget;

import com.jvgme.spelltoolkit.core.widget.Widget;
import com.jvgme.spelltoolkit.core.widget.WidgetManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 控件管理器的安卓实现
 * 单例模式
 */
public class WidgetManagerImpl implements WidgetManager {
    private static WidgetManagerImpl widgetManager;
    private final Map<String, Widget> widgetMap;

    private WidgetManagerImpl() {
        widgetMap = new HashMap<>();
    }

    /**
     * 返回一个控件管理器的实例，返回的总是同一个实例。
     *
     * @return WidgetManagerImpl
     */
    public static synchronized WidgetManagerImpl instance() {
        if (widgetManager == null) {
            widgetManager = new WidgetManagerImpl();
        }
        return widgetManager;
    }


    /**
     * 注册一个控件，ID 必需是唯一的，否则将被覆盖
     *
     * @param id ID
     * @param widget 控件实例
     */
    @Override
    public void registerWidget(String id, Widget widget) {
        widgetMap.put(id, widget);
    }

    /**
     * 返回一个已注册的控件实例
     * @param id 控件注册时定义的 ID
     * @return Widget
     */
    @Override
    public Widget getWidget(String id) {
        return widgetMap.get(id);
    }

    /**
     * 返回一个已注册的控件的 ID 名单，以数组形式返回
     *
     * @return 已注册的控件的 ID 的数组
     */
    @Override
    public String[] getRegisteredWidget() {
        return (String[]) widgetMap.keySet().toArray();
    }
}
