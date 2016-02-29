package com.example.effects.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipeView extends FrameLayout {

	
	public enum Status{
		CLOSED,OPENED,DRAGING;
	}
	
	public interface OnSwipeListener {
		// 面板已关闭
		void onClosed(SwipeView swipeView);
		// 面板已打开
		void onOpened(SwipeView  swipeView);
		// 面板正在被拖拽
		void onDraging(SwipeView swipeView);
		// 将要关闭面板
		void onStartClosed(SwipeView swipeView);
		// 将要打开面板
		void onStartOpen(SwipeView swipeView);
	}
	
	private Status status = Status.CLOSED;
	private OnSwipeListener onSwipeListener;
	
	public Status getStatus() {
		return status;
	}
	
	public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
		this.onSwipeListener = onSwipeListener;
	}
	
	private ViewDragHelper dragHelper;
	private ViewGroup mButtonView;
	private ViewGroup mContentView;
	private int mRange;

	private int mWidth;

	private int mHeight;

	public SwipeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public SwipeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public SwipeView(Context context) {
		super(context);
		initView();
	}

	/** 初始化要使用到的相关对象 */
	private void initView() {
		dragHelper = ViewDragHelper.create(this, 1.0f, new MyCallback());
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
		// 获取包含的两个子布局
		if (getChildCount()!=2) {
			throw new RuntimeException("SwipeView必须包含两个子布局");
		}
		if (!(getChildAt(0) instanceof ViewGroup) || !(getChildAt(1)  instanceof ViewGroup)) {
			throw new RuntimeException("SwipeView的子布局必须是ViewGroup");
		}
		
		mButtonView = (ViewGroup) getChildAt(0);
		mContentView = (ViewGroup) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		mRange = mButtonView.getMeasuredWidth();
		mWidth = w;
		mHeight = h;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		// 修正按钮布局的位置
		mButtonView.layout(right, 0, right + mButtonView.getWidth(), bottom);
	}
	
	private final class MyCallback extends Callback {

		@Override
		/** 返回true则允许child被拖拽 */
		public boolean tryCaptureView(View child, int pointerId) {
			return true;
		}
		
		@Override
		/** 说明view可以拖拽的最大值 */
		public int getViewHorizontalDragRange(View child) {
			return mRange;
		}
		
		@Override
		/** 返回值表示当拖拽是child将要移动到的位置 */
		public int clampViewPositionHorizontal(View child, int left, int dx) {
//			logE("SwipeView.clampViewPositionHorizontal.left="+left+";mRange="+mRange);
			if (child == mContentView) {
				// 限制主面板的拖拽位置
				if (left < -mRange) {
					left = -mRange;
				}else if (left > 0) {
					left = 0;
				}
			}else {
				// 限制按钮面板的拖拽位置
				if (left < mWidth - mRange) {
					left = mWidth - mRange;
				}else if (left > mWidth) {
					left = mWidth;
				}
			}
			
			return left;
		}
		
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			if (changedView == mContentView) {
				mButtonView.offsetLeftAndRight(dx);
			}else {
				mContentView.offsetLeftAndRight(dx);
			}
			
			// 分配监听事件
			processDragEvent();
			
			// 2.3以前的旧版本在clampViewPositionHorizontal后，只偏移位置，不会刷新界面，需要手动处理一下
			invalidate();
		}
		
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			if (xvel > 0) {
				// 向右移动
				close();
			}else if (xvel < 0) {
				// 向左移动
				open();
			}else {
				// 静止
				if (mContentView.getLeft() < -mRange / 2) {
					open();
				}else {
					close();
				}
			}
		}
	}
	

	/** 分配监听事件 */
	public void processDragEvent() {
		
		Status preStatus = status;
		status = updateStatus();
		
		if (preStatus != status && onSwipeListener!=null) {
			if (status == Status.CLOSED) {
				onSwipeListener.onClosed(this);
			}else if (status== Status.OPENED) {
				onSwipeListener.onOpened(this);
			}else {
				onSwipeListener.onDraging(this);
				if (preStatus == Status.CLOSED) {
					// 拖拽之前是关闭状态，说明用户想要打开面板
					onSwipeListener.onStartOpen(this);
				}else if (preStatus == Status.OPENED) {
					// 拖拽之前是打开状态，说明用户想要关闭
					onSwipeListener.onStartClosed(this);
				}
			}
		}
	}

	/** 根据当前面板的位置，确定打开状态 */
	private Status updateStatus() {
		if (mContentView.getLeft() == 0) {
			return Status.CLOSED;
		}
		if (mContentView.getLeft() == -mRange) {
			return Status.OPENED;
		}
		return Status.DRAGING;
	}

	/** 打开面板 */
	public void open() {
		int left = -mRange;
		layoutContent(left);
	}

	/** 关闭面板 */
	public void close() {
		int left = 0;
		layoutContent(left);
	}

	/** 根据left位置，更新子布局的位置 */
	private void layoutContent(int left) {
		if (dragHelper.smoothSlideViewTo(mContentView, left, 0)) {
			ViewCompat.postInvalidateOnAnimation(this);
		};
	}
	
	@Override
	public void computeScroll() {
		super.computeScroll();
		
		if (dragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
}
