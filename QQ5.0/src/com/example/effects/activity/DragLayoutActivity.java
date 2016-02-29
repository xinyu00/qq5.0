package com.example.effects.activity;

import com.example.effects.R;
import com.example.effects.bean.Cheeses;
import com.example.effects.utils.ToastUtil;
import com.example.effects.view.DragLayout;
import com.example.effects.view.DragLayout.OnDragStatusChangeListener;
import com.example.effects.view.MyLinearLayout;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DragLayoutActivity extends Activity {

	private ImageView iv_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.draglayout);
		
		DragLayout dragLayout = (DragLayout) findViewById(R.id.draglayout);
		dragLayout.setOnDragStatusChangeListener(new OnDragStatusChangeListener() {
			
			@Override
			public void onOpened() {
				ToastUtil.showToast(getApplication(), "onOpened");
			}
			
			@Override
			public void onDraging(float percent) {
				ToastUtil.showToast(getApplication(), "onDraging,percent="+percent);
				// 展开面板的同时，修改主面板头像的透明度
				ViewHelper.setAlpha(iv_header, 1 - percent);
			}

			@Override
			public void onClosed() {
				ToastUtil.showToast(getApplication(), "onClosed");
				// 关闭面板时，抖动主界面的用户头像
				ObjectAnimator animator = ObjectAnimator.ofFloat(iv_header, "translationX", 5f);
				animator.setDuration(500);
				animator.setInterpolator(new CycleInterpolator(4));
				animator.start();
			}
		});
		
		ListView lv_left = (ListView) findViewById(R.id.lv_left);
		lv_left.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		
		ListView lv_content = (ListView) findViewById(R.id.lv_content);
		lv_content.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
		
		iv_header = (ImageView) findViewById(R.id.iv_content_header);
		
		MyLinearLayout layout_content = (MyLinearLayout) findViewById(R.id.layout_content);
		layout_content.setDragLayout(dragLayout);
	}

}
