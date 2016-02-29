package com.example.effects.activity;

import com.example.effects.R;
import com.example.effects.adapter.SwipeViewAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class SwipeViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swipe_view);
		
		ListView listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(new SwipeViewAdapter());
	}

}
