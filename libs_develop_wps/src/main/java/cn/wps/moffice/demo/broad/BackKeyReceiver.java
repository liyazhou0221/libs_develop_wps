package cn.wps.moffice.demo.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import cn.wps.moffice.demo.util.WpsLogger;

/**
 * 当按返回键时传来的广播信息
 * @author kingsoft
 *
 */
public class BackKeyReceiver extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		//告诉用户收到Back键的广播
		// Toast.makeText(context, "监听Back键", Toast.LENGTH_SHORT).show();
        WpsLogger.e("CloseReceiver","告诉用户收到Back键的广播");
	}

}
