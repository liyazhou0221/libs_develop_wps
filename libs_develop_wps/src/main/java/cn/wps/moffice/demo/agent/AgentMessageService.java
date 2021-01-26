package cn.wps.moffice.demo.agent;

import cn.wps.moffice.agent.OfficeServiceAgent;
import cn.wps.moffice.demo.agent.OfficeServiceAgentImpl;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class AgentMessageService extends Service
{
	private final static String TAG = AgentMessageService.class.getSimpleName();
	
	protected final OfficeServiceAgent.Stub mBinder = new OfficeServiceAgentImpl(this);
	protected final static String ACTION = "cn.wps.moffice.agent.OfficeServiceAgent";
	
	@Override
	public void onCreate()
	{
		Log.i(TAG, "onCreate(): " + this.hashCode());
	}
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		Log.i(TAG, "onBind(): " + this.hashCode() + ", " + intent.toString());
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent)
	{
		Log.i(TAG, "onUnbind(): " + this.hashCode() + ", " + intent.toString());
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy()
	{
		Log.i(TAG, "onDestroy(): " + this.hashCode());
	}
	
}
