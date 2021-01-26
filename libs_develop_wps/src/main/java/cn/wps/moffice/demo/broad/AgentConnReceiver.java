package cn.wps.moffice.demo.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import cn.wps.moffice.demo.util.WpsLogger;

public class AgentConnReceiver extends BroadcastReceiver {
	public static final String BROAD_THIRD_AGENT_CONN = "cn.wps.moffice.agent.connected"; //actionAgent连接成功广播
	public static final String BROAD_THIRD_AGENT_STATE = "AgentState";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// 告诉用户收到链接状态的广播
		boolean connected = intent.getExtras().getBoolean(BROAD_THIRD_AGENT_STATE, false);
		// Toast.makeText(context, "agent connected: " + connected, Toast.LENGTH_SHORT).show();
        WpsLogger.e("CloseReceiver","告诉用户收到链接状态的广播 connected = " + connected);
	}
}
