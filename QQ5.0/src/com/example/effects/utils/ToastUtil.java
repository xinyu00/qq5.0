package com.example.effects.utils;

import android.content.Context;
import android.widget.Toast;
/**
 * Toast的工具类
 * @author Administrator
 *
 */
public class ToastUtil {

	
	private static Toast toast;

	public static void showToast(Context context, String msg){
		if(toast == null){
			toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		}
		
		toast.setText(msg);
		toast.show();
	}
	
}
