package com.example.effects.view;

import com.example.effects.view.DragLayout.Status;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {
	
	private DragLayout dragLayout;
	
	public void setDragLayout(DragLayout dragLayout) {
		this.dragLayout = dragLayout;
	}

	@SuppressLint("NewApi")
	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (dragLayout.getStatus() == Status.CLOSED) {
			// 面板处于关闭状态，按原先的系统逻辑处理
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (dragLayout.getStatus() == Status.CLOSED) {
			// 面板处于关闭状态，按原先的系统逻辑处理
			return super.onTouchEvent(event);
		}else {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				dragLayout.close();
			}
			return true;
		}
	}

}
