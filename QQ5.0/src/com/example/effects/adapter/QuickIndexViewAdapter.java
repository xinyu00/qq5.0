package com.example.effects.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.effects.R;
import com.example.effects.bean.Cheeses;
import com.example.effects.bean.Man;
import com.example.effects.utils.StringUtils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuickIndexViewAdapter extends BaseAdapter {

	List<Man> mans;
	
	public QuickIndexViewAdapter() {
		mans = new ArrayList<Man>();
		
		for (int i = 0; i < Cheeses.NAMES.length; i++) {
			String name = Cheeses.NAMES[i];
			char letter = StringUtils.getPinyin(name).charAt(0);
			Man man = new Man(name, letter);
			mans.add(man);
		}
		
		Collections.sort(mans);
	}
	
	@Override
	public int getCount() {
		return mans.size();
	}

	@Override
	public Object getItem(int position) {
		return mans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(parent.getContext(), R.layout.item_man, null);
		}
		// 从初始化控件
		TextView tv_index = (TextView) convertView.findViewById(R.id.tv_index);
		TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
		
		// 填充数据
		Man man = (Man) getItem(position);		
		tv_index.setText(String.valueOf(man.getLetter()));
		tv_name.setText(man.getName());
		
		// 处理字母栏的显示/隐藏
		if (position != 0) {
			Man preMan = (Man) getItem(position - 1);
			if (man.getLetter() == preMan.getLetter()) {
				tv_index.setVisibility(View.GONE);
			}else {
				tv_index.setVisibility(View.VISIBLE);
			}
		}else {
			tv_index.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

}
