package cn.wps.moffice.demo.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import cn.wps.moffice.demo.menu.ListViewParamActivity;

public class MyGestureListener extends SimpleOnGestureListener { 
    private Context mContext; 

   public MyGestureListener(Context context) 
   { 
        mContext = context; 
   } 

    @Override 
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
    { 
    	float dex_X = e2.getX() - e1.getX();
    	float dex_Y = e2.getY() - e1.getY();
    	if (dex_X > 0 && dex_X > Math.abs(dex_Y))
    	{
    		Intent intent = new Intent();
    		intent.setClass(mContext, ListViewParamActivity.class);
    		mContext.startActivity(intent);//无返回值的调用,启动一个明确的activity
    	}
		Log.d("sort", "手势判断调用");

        return false; 
    } 

} 