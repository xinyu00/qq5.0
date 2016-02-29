package com.example.effects.activity;


import com.example.effects.R;
import com.example.effects.utils.ToastUtil;
import com.example.effects.view.GooView;
import com.example.effects.view.GooView.OnDragChangeListener;

import android.app.Activity;
import android.os.Bundle;

public class GooViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gooview_layout);
		
		GooView gooView = (GooView) findViewById(R.id.gooview);
		gooView.setOnDragChangeListener(new OnDragChangeListener() {
			
			@Override
			public void onReset(boolean isBrokenConnect) {
				ToastUtil.showToast(getApplication(), "onReset,isBrokenConnect="+isBrokenConnect);
			}
			
			@Override
			public void onDisapear() {
				ToastUtil.showToast(getApplication(), "onDisapear");
			}
		});
	}

}
