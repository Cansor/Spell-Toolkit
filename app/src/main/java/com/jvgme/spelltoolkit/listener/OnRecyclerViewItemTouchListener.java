package com.jvgme.spelltoolkit.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 文件列表的触摸监听器
 */
public class OnRecyclerViewItemTouchListener extends GestureDetector.SimpleOnGestureListener implements RecyclerView.OnItemTouchListener{
    private final RecyclerView recyclerView;
    private final GestureDetector gestureDetector;

    public OnRecyclerViewItemTouchListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        gestureDetector = new GestureDetector(recyclerView.getContext(),this);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child!=null) {
            RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
            onItemClick(vh,recyclerView.getChildAdapterPosition(child));
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child!=null) {
            RecyclerView.ViewHolder vh = recyclerView.getChildViewHolder(child);
            onItemLongClick(vh,recyclerView.getChildAdapterPosition(child));
        }
    }

    /**
     * 点击事件
     *
     * @param vh 被点击的 item 对应的 ViewHolder
     * @param position 被点击的 item 在 Adapter 中的位置
     */
    public void onItemClick(RecyclerView.ViewHolder vh,int position){}

    /**
     * 长按事件
     * @param vh 被长按的 item 对应的 ViewHolder
     * @param position 被长按的 item 在 Adapter 中的位置
     */
    public void onItemLongClick(RecyclerView.ViewHolder vh, int position){}
}
