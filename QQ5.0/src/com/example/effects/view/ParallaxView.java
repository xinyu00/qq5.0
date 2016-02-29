package com.example.effects.view;

import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ListView;

public class ParallaxView extends ListView {
	
	private ImageView iv_header;

	private int maxH;

	private int originH;

	public ParallaxView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ParallaxView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ParallaxView(Context context) {
		super(context);
	}

	/** 接受 要修改大小的ImageView */
	public void setHeaderImage(ImageView iv_header) {
		this.iv_header = iv_header;
		maxH = (int) (iv_header.getDrawable().getIntrinsicHeight() * 0.7);
		originH = iv_header.getHeight();
	}
	
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		
		if (deltaY < 0) {
			int newH = iv_header.getHeight() + Math.abs(deltaY);
			if (newH <=  maxH) {
				iv_header.getLayoutParams().height = newH;
				iv_header.requestLayout();
			}
		}
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (ev.getAction()==MotionEvent.ACTION_UP) {
			final int tmpH = iv_header.getHeight();
			// 回弹
			ValueAnimator animator = ValueAnimator.ofInt(1);
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					float percent = animator.getAnimatedFraction();
					int newH = evaluate(percent, tmpH , originH);
					iv_header.getLayoutParams().height = newH;
					iv_header.requestLayout();
				}
			});
			animator.setDuration(500);
			animator.start();
		}
		return super.onTouchEvent(ev);
	}

	/** 根据百分比，在最大值和最小值之间算出一个过渡值 */
	public int evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return (int) (startFloat + fraction * (endValue.floatValue() - startFloat));
    }
}
