package com.example.effects.view;

import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class DragLayout extends FrameLayout{
	
	
	// 拖拽状态
	public enum Status{
		CLOSED,OPENED,DRAGING;
	}
	
	private Status status = Status.CLOSED;
	
	public Status getStatus() {
		return status;
	}
	
	// 拖拽状态监听器
	public interface OnDragStatusChangeListener{
		void onClosed();
		void onOpened();
		void onDraging(float percent);
	}

	private OnDragStatusChangeListener onDragStatusChangeListener;
	public void setOnDragStatusChangeListener(OnDragStatusChangeListener onDragStatusChangeListener) {
		this.onDragStatusChangeListener = onDragStatusChangeListener;
	}
	
	private ViewDragHelper dragHelper;

	private ViewGroup mLeftView;

	private ViewGroup mContentView;

	private int mWidth;

	private int mRange;

	private int mHeight;

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DragLayout(Context context) {
		super(context);
		initView();
	}

	/** 初始化view将要使用到的相关对象 */
	private void initView() {
		dragHelper = ViewDragHelper.create(this, new MyCallback());
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return dragHelper.shouldInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dragHelper.processTouchEvent(event);
		return true;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		// 健壮性检查
		if (getChildCount()!=2) {
			throw new RuntimeException("DragLayout只能有两个子布局");
		}
		
		if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1) instanceof ViewGroup)) {
			throw new RuntimeException("DragLayout的子布局只能是ViewGroup");
		}
		
		// 获取两个子布局
		mLeftView = (ViewGroup) getChildAt(0);
		mContentView = (ViewGroup) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
		mRange = (int) (mWidth * 0.6);
	}

	/** 拖拽事件的回调 */
	private final class MyCallback extends Callback {
		
		@Override
		/** 返回child在水平方向上的拖拽可移动范围 */
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}
		
		@Override
		/** 返回true则说明child可以被拖拽 */
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}
		
		@Override
		/** 返回当child被拖拽时移动到的水平位置<br>
		 * left 系统推荐移动到的位置
		 * dx 从上一次移动到新位置的偏移量 
		 */
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			
			if (child == mContentView) {
				left = fixContentLeft(left);
			}
			
			return left	;
		}
		
		@Override
		/** 当被拖拽的view移动位置后，会调用此方法。可以用于处理View之间的联动 */
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			// 当拖拽侧拉菜单时保持不动，转而偏移主内容面板
			if (changedView == mLeftView) {
				mLeftView.layout(0, 0, mWidth, mHeight);
				
				int newLeft = dx + mContentView.getLeft();
				newLeft = fixContentLeft(newLeft);
				mContentView.layout(newLeft, 0, newLeft + mWidth, mHeight);
			}	
			
			// 根据拖拽位置处理view间的联动
			processDragEvent();
			
			// 在2.3版本上修正位置后并不会主动刷新界面，需要强制处理
			invalidate();
		}
		
		@Override
		/** 当被拖拽的view被释放的时候会回调此方法 */
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 根据用户放手时的位置，自动打开或关闭面板
			
			// 向右移动
			boolean isRightMove = xvel > 0;
			// 静止，但是面板已打开超过一半
			boolean isOpenHalfMore = (xvel == 0 && mContentView.getLeft() > mRange / 2);
			
			if (isRightMove || isOpenHalfMore) {
				open();
			}else {
				close();
			}
		}
	}

	@Override
	public void computeScroll() {
		super.computeScroll();
		
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	/** 根据当前主面板的位置，协调整个界面的联动 */
	public void processDragEvent() {
		// 计算面板展开的百分比
		float percent = mContentView.getLeft() / (float) mRange;
		
		// 随着拖拽，修正界面间的位置
		animChange(percent);
		
		// 处理监听回调
		status = updateStatus();
		
		if (onDragStatusChangeListener != null) {
			onDragStatusChangeListener.onDraging(percent);
			
			if (status == Status.CLOSED) {
				onDragStatusChangeListener.onClosed();
			}else if (status == Status.OPENED) {
				onDragStatusChangeListener.onOpened();
			}
		}
	}

	/** 根据当前面板的位置，计算展开状态 */
	private Status updateStatus() {
		if (mContentView.getLeft() == 0) {
			return Status.CLOSED;
		}
		
		if (mContentView.getLeft() == mRange) {
			return Status.OPENED;
		}
		
		return Status.DRAGING;
	}

	private void animChange(float percent) {
		// 根据面板展开的百分比,处理主面板的缩放
//		float maxContentScale = 1.0f;
//		float minContentScale = 0.8f;
//		float offsetContentScale = (maxContentScale - minContentScale) * percent;
//		float finalContentScale = maxContentScale - offsetContentScale;
//		mContentView.setScaleX(finalContentScale);
//		mContentView.setScaleY(finalContentScale); 
//		ViewHelper.setScaleX(mContentView, finalContentScale);
//		ViewHelper.setScaleY(mContentView, finalContentScale);
		ViewHelper.setScaleX(mContentView, evaluate(percent, 1.0, 0.8));
		ViewHelper.setScaleY(mContentView, evaluate(percent, 1.0, 0.8));
		
		// 根据面板展开的百分比，平移侧滑菜单
		float translationX = evaluate(percent, -mRange, 0);
		ViewHelper.setTranslationX(mLeftView, translationX);
		
		// 根据面板展开的百分比，缩放侧滑菜单
		float scaleLeft = evaluate(percent, 0.5f, 1.0f);
		ViewHelper.setScaleX(mLeftView, scaleLeft);
		ViewHelper.setScaleY(mLeftView, scaleLeft);
		
		// 根据面板展开的百分比，修改侧滑菜单透明度
		float alphaLeft = evaluate(percent, 0.5, 1.0);
		ViewHelper.setAlpha(mLeftView, alphaLeft);
		
		// 根据面板展开的百分比，修改背景图的颜色
		int color = (Integer) evaluateColor(percent, Color.BLACK, Color.TRANSPARENT);
		getBackground().setColorFilter(color, Mode.SRC_OVER);
	}

	/** 根据百分比，在起始颜色和最终颜色之间计算出一个过渡颜色 */
	public Object evaluateColor(float fraction, Object startValue, Object endValue) {
        int startInt = (Integer) startValue;
        int startA = (startInt >> 24) & 0xff;
        int startR = (startInt >> 16) & 0xff;
        int startG = (startInt >> 8) & 0xff;
        int startB = startInt & 0xff;

        int endInt = (Integer) endValue;
        int endA = (endInt >> 24) & 0xff;
        int endR = (endInt >> 16) & 0xff;
        int endG = (endInt >> 8) & 0xff;
        int endB = endInt & 0xff;

        return (int)((startA + (int)(fraction * (endA - startA))) << 24) |
                (int)((startR + (int)(fraction * (endR - startR))) << 16) |
                (int)((startG + (int)(fraction * (endG - startG))) << 8) |
                (int)((startB + (int)(fraction * (endB - startB))));
    }
	
	/** 根据百分比，在最大值和最小值之间算出一个过渡值 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
	
	/** 打开面板 */
	private void open() {
		int left = mRange;
//		mContentView.layout(left, 0, left + mWidth, mHeight);
		if(dragHelper.smoothSlideViewTo(mContentView, left, 0)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/** 关闭面板 */
	void close() {
		int left = 0;
//		mContentView.layout(left, 0, left + mWidth, mHeight);
		if(dragHelper.smoothSlideViewTo(mContentView, left, 0)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}

	/** 修正正文的左侧位置只能在 [0,mRange]区间内 */
	private int fixContentLeft(int left) {
		if (left < 0) {
			left = 0;
		}
		
		if (left > mRange) {
			left = mRange;
		}
		return left;
	}
}
