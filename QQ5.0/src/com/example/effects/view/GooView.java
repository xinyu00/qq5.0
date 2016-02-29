package com.example.effects.view;

import com.example.effects.utils.GeometryUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GooView extends View {
	public interface OnDragChangeListener{
		// 当控件消失时被回调
		void onDisapear();
		// 当控件恢复原位的时候被回调，如果isBrokenConnect为true则说明之前已经断开连接
		void onReset(boolean isBrokenConnect);
	}
	
	private OnDragChangeListener onDragChangeListener;
	
	public void setOnDragChangeListener(OnDragChangeListener onDragChangeListener) {
		this.onDragChangeListener = onDragChangeListener;
	}
	
	private Paint paint;

	// 创建两个圆的圆心
	private PointF mStickCenter = new PointF(150, 150);
	private PointF mDragCenter = new PointF(80, 80);
	
	// 创建两个圆的半径
	private static final float MAX_DISTANCE = 80;
	private static final float STICK_RADIUS = 10;
	private static final float DRAG_RADIUS = 16;

	private Path path;

	// 如果为true则说明不需要绘制连接部分
	private boolean isBrokenConnet = false;

	private boolean isDisapear;

	public GooView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public GooView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public GooView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setAntiAlias(true);

		path = new Path();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 消失不再绘制任何画面
		if (isDisapear) {
			return;
		}
		
		// 绘制之前根据拖拽圆的距离，重新计算固定圆的半径
		float stickRadius = fixRadius();
		
		// 计算斜率
		float offsetY = mStickCenter.y - mDragCenter.y ;
		float offsetX = mStickCenter.x - mDragCenter.x;
		// (mStickCenter.y - mDragCenter.y) / (mStickCenter.x - mDragCenter.x)
		// -(mStickCenter.y - mDragCenter.y) / -(mStickCenter.x - mDragCenter.x)
		
		double lineK = 0;
		if (offsetX != 0) {
			lineK = offsetY / offsetX;
		}
		
		// 创建两个圆的附着点
//		PointF[] mStickPoints = {new PointF(250, 250),new PointF(250, 350)};
//		PointF[] mDragPoints = {new PointF(50, 250),new PointF(50, 350)};
		PointF[] mStickPoints = GeometryUtil.getIntersectionPoints(mStickCenter, stickRadius, lineK);
		PointF[] mDragPoints = GeometryUtil.getIntersectionPoints(mDragCenter, DRAG_RADIUS, lineK);
		
		// 创建控制点
//		PointF mControlor = new PointF(150, 300);
		PointF mControlor = GeometryUtil.getMiddlePoint(mDragCenter, mStickCenter);
		
		// 绘制一个大圆
		canvas.drawCircle(mDragCenter.x, mDragCenter.y, DRAG_RADIUS, paint);
		
		if (!isBrokenConnet) {
			// 绘制一个小圆
			canvas.drawCircle(mStickCenter.x, mStickCenter.y, stickRadius, paint);
			
			// 绘制连接曲线
			path.reset();
			path.moveTo(mStickPoints[0].x, mStickPoints[0].y);// 起始点
			path.quadTo(mControlor.x, mControlor.y, mDragPoints[0].x, mDragPoints[0].y);// 绘制曲线
			path.lineTo(mDragPoints[1].x, mDragPoints[1].y);// 绘制直线
			path.quadTo(mControlor.x, mControlor.y, mStickPoints[1].x, mStickPoints[1].y);//绘制曲线
			path.close();
			canvas.drawPath(path, paint);
		}
		
		// 绘制所有的附着点和控制点
		paint.setColor(Color.BLUE);
		canvas.drawCircle(mControlor.x, mControlor.y, 2, paint);
		canvas.drawCircle(mStickPoints[0].x, mStickPoints[0].y, 2, paint);
		canvas.drawCircle(mStickPoints[1].x, mStickPoints[1].y, 2, paint);
		canvas.drawCircle(mDragPoints[0].x, mDragPoints[0].y, 2, paint);
		canvas.drawCircle(mDragPoints[1].x, mDragPoints[1].y, 2, paint);
		paint.setColor(Color.RED);
		
		// 绘制拖拽可变化的范围
		paint.setStyle(Style.STROKE);
		canvas.drawCircle(mStickCenter.x, mStickCenter.y, MAX_DISTANCE, paint);
		paint.setStyle(Style.FILL);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			isBrokenConnet = false;// 每次重新点击的时候，允许再次绘制连接部分
			isDisapear = false;// 每次重新点击的时候，允许再次绘制画面
			updateDragCenter(event.getX(),event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			// 更新拖拽圆的位置 
			updateDragCenter(event.getX(),event.getY());
			
			// 如果拖拽超出可变化范围则断开连接部分
			float distance = GeometryUtil.getDistanceBetween2Points(mStickCenter, mDragCenter);
			if (distance > MAX_DISTANCE) {
				isBrokenConnet = true;
				invalidate();
			}
			break;
		case MotionEvent.ACTION_UP:
			onUpEvent();
			
			break;
		}
		
		return true;
	}

	/** 发生up事件时的处理 */
	private void onUpEvent() {
		// 处理拖拽圆的消失、恢复或回弹
		if (isBrokenConnet) {
			// 连接已断开
			float dis = GeometryUtil.getDistanceBetween2Points(mStickCenter, mDragCenter);
			if (dis > MAX_DISTANCE) {
				// 松手时仍然在可变化范围为之外，则消失
				isDisapear = true;
				if (onDragChangeListener != null) {
					onDragChangeListener.onDisapear();
				}
				invalidate();
			}else {
				// 松手时已回到可变化范围之内，则恢复到原位置
				updateDragCenter(mStickCenter.x, mStickCenter.y);
				if (onDragChangeListener != null) {
					onDragChangeListener.onReset(true);
				}
			}
		}else {
			// 松手时连接一直没有断开，使用回弹动画
			ValueAnimator animator = ValueAnimator.ofInt(1);
			final PointF tmpPointF = mDragCenter;
			animator.addUpdateListener(new AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					
					float percent = animator.getAnimatedFraction();
					PointF p = GeometryUtil.getPointByPercent(tmpPointF, mStickCenter, percent);
					updateDragCenter(p.x, p.y);
				}
			});
			animator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator arg0) {
				}
				
				@Override
				public void onAnimationRepeat(Animator arg0) {
				}
				
				@Override
				public void onAnimationEnd(Animator arg0) {
					if (onDragChangeListener != null) {
						onDragChangeListener.onReset(false);
					}
				}
				
				@Override
				public void onAnimationCancel(Animator arg0) {
				}
			});
//			animator.setInterpolator(new BounceInterpolator());
			animator.setDuration(500);
			animator.start();
		}
	}

	/** 更新拖拽圆的位置 */
	private void updateDragCenter(float x, float y) {
		mDragCenter.x = x;
		mDragCenter.y = y;
		
		invalidate();
	}

	/** 随着固定圆到拖拽圆距离的远近而缩放 */
	private float fixRadius() {
		float distance = GeometryUtil.getDistanceBetween2Points(mStickCenter, mDragCenter);
		float percent = distance / MAX_DISTANCE;
		// 当拖拽距离超过最大距离的时候需要修正
		if (percent > 1) {
			percent = 1;
		}
		
		return evaluate(percent, DRAG_RADIUS, STICK_RADIUS * 0.5);
	}
	
	/** 根据百分比，在最大值和最小值之间算出一个过渡值 */
	public Float evaluate(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }
	
}
