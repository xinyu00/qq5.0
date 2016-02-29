package com.example.effects.activity;


import com.example.effects.R;
import com.example.effects.bean.Cheeses;
import com.example.effects.view.ParallaxView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class PrallaxViewActivity extends Activity {

	private ParallaxView parallaxView;
	private ImageView iv_header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prallax_layout);
		
		parallaxView = (ParallaxView) findViewById(R.id.listview);
		
		// 创建HeaderView
		View header = View.inflate(this, R.layout.header, null);
		parallaxView.addHeaderView(header);
		
		// 将要修改大小的ImageView告知给列表
		iv_header = (ImageView) header.findViewById(R.id.iv_header);
		iv_header.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				parallaxView.setHeaderImage(iv_header);	
				
				iv_header.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		parallaxView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Cheeses.NAMES));
	}

}
