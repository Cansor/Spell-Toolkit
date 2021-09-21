package com.jvgme.spelltoolkit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

/**
 * 封装一些通用方法的 BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewLayout());

        // 设为 ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        handlerToolbar(toolbar);
    }

    /**
     * 设置当前 Activity 的布局
     */
    protected abstract int setContentViewLayout();

    /**
     * 定制 Toolbar
     */
    protected void handlerToolbar(Toolbar toolbar) {}

}