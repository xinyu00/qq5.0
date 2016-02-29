package com.example.effects.adapter;

import java.util.HashSet;
import java.util.Set;

import com.example.effects.R;
import com.example.effects.bean.Cheeses;
import com.example.effects.view.SwipeView;
import com.example.effects.view.SwipeView.OnSwipeListener;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SwipeViewAdapter extends BaseAdapter {

	private String[] NAMES = Cheeses.NAMES;

	private Set<SwipeView> openedSwipeViews = new HashSet<SwipeView>();

	private OnSwipeListener onSwipeListener = new OnSwipeListener() {

		@Override
		public void onStartOpen(SwipeView swipeView) {
			closeAll();
		}

		@Override
		public void onStartClosed(SwipeView swipeView) {
		}

		@Override
		public void onOpened(SwipeView swipeView) {
			openedSwipeViews.add(swipeView);
		}

		@Override
		public void onDraging(SwipeView swipeView) {
		}

		@Override
		public void onClosed(SwipeView swipeView) {
			openedSwipeViews.remove(swipeView);
		}
	};

	@Override
	public int getCount() {
		return NAMES.length;
	}

	protected void closeAll() {
		for (SwipeView swipeView : openedSwipeViews) {
			swipeView.close();
		}
		openedSwipeViews.clear();
	}

	@Override
	public Object getItem(int position) {
		return NAMES[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.item, null);
		}

		SwipeView swipeView = (SwipeView) convertView;
		swipeView.setOnSwipeListener(onSwipeListener);

		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		tv_name.setText(NAMES[position]);

		return convertView;
	}

}
