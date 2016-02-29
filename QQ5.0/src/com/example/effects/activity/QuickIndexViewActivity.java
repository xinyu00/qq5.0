package com.example.effects.activity;

import com.example.effects.R;
import com.example.effects.adapter.QuickIndexViewAdapter;
import com.example.effects.bean.Man;
import com.example.effects.utils.ToastUtil;
import com.example.effects.view.QuickIndexView;
import com.example.effects.view.QuickIndexView.OnLetterChangedListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


public class QuickIndexViewActivity extends Activity {
	
	private static final String TAG = "itcast_MainActivity";

	private ListView listView;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.quick_index_view);
		
		// 注册快速检索监听
		QuickIndexView quickIndexView = (QuickIndexView) findViewById(R.id.quickindex);
		quickIndexView.setOnLetterChangedListener(new OnLetterChangedListener() {
			
			@Override
			public void onLetterChanged(char lettter) {
				ToastUtil.showToast(getBaseContext(), "字符为："+lettter);
				/** 根据当前选择的字符，查找列表中对应的item位置 */
				for (int i = 0; i < listView.getCount(); i++) {
					Man man = (Man) listView.getItemAtPosition(i);
					if (man.getLetter() == lettter) {
						listView.setSelection(i);
						break;
					}
				}
				
				mHandler.removeCallbacksAndMessages(null);
				final TextView tv_pop = (TextView) findViewById(R.id.tv_pop);
				tv_pop.setText(String.valueOf(lettter));
				tv_pop.setVisibility(View.VISIBLE);
				mHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						tv_pop.setVisibility(View.GONE);
					}
				}, 1000);
				
			}

		});
		
		// 初始化好友列表
		listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new QuickIndexViewAdapter());
	}

}
