import com.jvgme.spelltoolkit.core.server.PluginServer;
import com.jvgme.spelltoolkit.core.widget.*;

import java.io.File;

public class Main implements PluginServer {
    private LogWindow logWindow;
    private Message message;
    private Dialog dialog;
    private EditTextDialog editTextDialog;

    @Override
    public void before(WidgetManager widgetManager) {
        // 获取控件
        logWindow = (LogWindow) widgetManager.getWidget(LogWindow.ID);
        message = (Message) widgetManager.getWidget(Message.ID);
        dialog = (Dialog) widgetManager.getWidget(Dialog.ID);
        editTextDialog = (EditTextDialog) widgetManager.getWidget(EditTextDialog.ID);
    }

    @Override
    public void service(File file) {
        String[] menu = {
                "弹出一条消息",
                "显示日志窗口",
                "弹出文本编辑框"
        };

        // 选项弹窗
        dialog.setTitle("测试菜单")
                .setItem(menu, (dialog, i) -> {
                    if (i == 0) {
                        message.toast("Hello!", 3000);
                    }
                    else if (i == 1) {
                        if (!logWindow.isShowing())
                            logWindow.show();
                        logWindow.println("Hello!", LogWindow.COLOR_GREEN);
                    }
                    else if (i == 2) {
                        editTextDialog.setTitle("写点什么吧：");
                        editTextDialog.setTextColor("#333333");
                        editTextDialog.setPositiveButton("确定", (dialog1, i1) -> {
                            if (!logWindow.isShowing())
                                logWindow.show();
                            logWindow.println(editTextDialog.getText(), LogWindow.COLOR_ORANGE);
                        });
                        editTextDialog.show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public void after() {}
}
