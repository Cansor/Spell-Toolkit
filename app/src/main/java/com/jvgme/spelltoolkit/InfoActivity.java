package com.jvgme.spelltoolkit;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.widget.Toolbar;

import java.nio.charset.StandardCharsets;

/**
 * 描述界面
 */
public class InfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int setContentViewLayout() {
        return R.layout.activity_info;
    }

    @Override
    protected void handlerToolbar(Toolbar toolbar) {
        toolbar.setTitle(getIntent().getStringExtra("title"));
        toolbar.setNavigationIcon(R.drawable.back);
        // 导航按钮事件
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initView() {
        // 获取传递过来的数据
        String text = getIntent().getStringExtra("info");

        WebView webView = findViewById(R.id.tv_plugins_description);
        webView.loadDataWithBaseURL(null, text, "text/html", StandardCharsets.UTF_8.name(), null);

        /*TextView textView = findViewById(R.id.tv_plugins_description);
        // 添加文本，解析html
        textView.setText(Html.fromHtml(text));
        // 设置可以滚动
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());*/
    }
}