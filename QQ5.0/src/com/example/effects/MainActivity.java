package com.example.effects;

import com.example.effects.activity.DragLayoutActivity;
import com.example.effects.activity.GooViewActivity;
import com.example.effects.activity.PrallaxViewActivity;
import com.example.effects.activity.QuickIndexViewActivity;
import com.example.effects.activity.SwipeViewActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	private Button bt_dragLayout, bt_gooView, bt_prallaxView, bt_quickIndexView, bt_swipeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		bt_dragLayout = (Button) findViewById(R.id.bt_dragLayout);
		bt_gooView = (Button) findViewById(R.id.bt_gooView);
		bt_prallaxView = (Button) findViewById(R.id.bt_prallaxView);
		bt_quickIndexView = (Button) findViewById(R.id.bt_quickIndexView);
		bt_swipeView = (Button) findViewById(R.id.bt_swipeView);
		bt_dragLayout.setOnClickListener(this);
		bt_gooView.setOnClickListener(this);
		bt_prallaxView.setOnClickListener(this);
		bt_quickIndexView.setOnClickListener(this);
		bt_swipeView.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_dragLayout:
			startActivity(new Intent(this, DragLayoutActivity.class));
			break;
		case R.id.bt_gooView:
			startActivity(new Intent(this, GooViewActivity.class));
			break;
		case R.id.bt_prallaxView:
			startActivity(new Intent(this, PrallaxViewActivity.class));
			break;
		case R.id.bt_quickIndexView:
			startActivity(new Intent(this, QuickIndexViewActivity.class));
			break;
		case R.id.bt_swipeView:
			startActivity(new Intent(this, SwipeViewActivity.class));
			break;
		}

	}
}
