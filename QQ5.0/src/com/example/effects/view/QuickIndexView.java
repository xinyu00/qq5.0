package com.example.effects.view;

import com.example.effects.bean.Cheeses;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class QuickIndexView extends View {

	private static final char[] LETTER = Cheeses.LETTERS;
	
	/** 字符变更的监听器 */
	public interface OnLetterChangedListener {
		void onLetterChanged(char lettter);
	}
	
	private OnLetterChangedListener onLetterChangedListener;
	
	public void setOnLetterChangedListener(OnLetterChangedListener onLetterChangedListener) {
		this.onLetterChangedListener = onLetterChangedListener;
	}
	
	public OnLetterChangedListener getOnLetterChangedListener() {
		return onLetterChangedListener;
	}
	
	private Paint paint;
	// view的宽度
	private int mWidth;

	private int lineH;

	private int index = -1;

	public QuickIndexView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public QuickIndexView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public QuickIndexView(Context context) {
		super(context);
		initView();
	}

	/** 初始化自定义View使用到的相关参数 */
	private void initView() {
		paint = new Paint();
		paint.setTextSize(20);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);// 开启抗锯齿
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w; 
		lineH = h / LETTER.length;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		for (int i = 0; i < LETTER.length; i++) {
			// 如果是当前选中的字符，则加深颜色
			if (i == index) {
				paint.setColor(Color.BLACK);
			}else {
				paint.setColor(Color.WHITE);
			}
			
			// 绘制一个字母
			String text = String.valueOf(LETTER[i]);
			
			// 获取文本的宽高
			Rect bounds = new Rect();
			paint.getTextBounds(text, 0, 1, bounds);
			
			// 在不同的高度上水平居中绘制文本
			float drawX = (mWidth - bounds.width()) / 2;
			float drawY = (lineH + bounds.height()) / 2 + lineH * i;
			canvas.drawText(text, drawX, drawY, paint);
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			float y = event.getY();
			
			int preIndex = index;
			getIndex(y);
			if (index == preIndex) {
				// 字母位置没有变化,不需要回调监听
				break;
			}
			
			// 获取到对应位置的字符
			char c = LETTER[index];
			
			// 字符发生变更，回调监听接口
			if (onLetterChangedListener != null) {
				onLetterChangedListener.onLetterChanged(c);
			}
			break;
		case MotionEvent.ACTION_UP:
			index = -1;
			break;
		}

		// 刷新高亮字符
		invalidate();
		return true;
	}

	private void getIndex(float y) {
		// 计算触摸位置的字符
		index = (int) (y / lineH);
		// 越界处理
		if (index<0) {
			index = 0;
		}else if (index > LETTER.length - 1) {
			index = LETTER.length - 1;
		}
	}

	private void drawSingleChar(Canvas canvas) {
		// 绘制一个字母
		String text = "A";
		
		// 获取文本的宽高
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		
		// 水平居中绘制文本
		float drawX = (mWidth - bounds.width()) / 2;
		float drawY = bounds.height();
		canvas.drawText(text, drawX, drawY, paint);
	}

}
