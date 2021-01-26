package cn.wps.moffice.demo.client;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;

import cn.wps.moffice.client.OfficeServiceClient;
import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.WpsLogger;
import cn.wps.moffice.service.OfficeService;

public class MOfficeClientService extends Service {

    public static final String CLASS_AGENT = "cn.wps.moffice.agent.OfficeServiceAgent";
    public static final String CLASS_CLINT = "cn.wps.moffice.client.OfficeServiceClient";

    protected static final String TAG = MOfficeClientService.class.getSimpleName();

    public static final String BROADCAST_ACTION = "cn.wps.moffice.broadcast.action.serviceevent";

    protected final Handler handler = new Handler();
    protected final Intent intent = new Intent(BROADCAST_ACTION);
	// 重要：监听WPS事件
    protected final OfficeServiceClient.Stub mBinder = new OfficeServiceClientImpl(this);

    public OfficeService mService;

    public MOfficeClientService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		WpsLogger.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        WpsLogger.e(TAG, "onCreate(): " + this.hashCode());
    }

    @Override
    public void onRebind(Intent intent) {
        WpsLogger.e(TAG, "onRebind(): " + this.hashCode() + ", " + intent.toString());
        super.onRebind(intent);
        bindOfficeService(getApplicationContext());
        sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_REBIND));
    }

    @Override
    public IBinder onBind(Intent intent) {
        WpsLogger.e(TAG, "onBind(): " + this.hashCode() + ", " + intent.toString());
        bindOfficeService(getApplicationContext());
        sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_REBIND));
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // 断开连接时通知插件
		sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_UNBIND));
        WpsLogger.e(TAG, "onUnbind(): " + this.hashCode() + ", " + intent.toString());
        try {
            getApplicationContext().unbindService(connection);
        } catch (Exception e) {
            WpsLogger.e(TAG, "onUnbind() e： " + e.getMessage());
        }
        super.onUnbind(intent);
        return true;
    }

    @Override
    public void onStart(Intent intent, int startId) {
		WpsLogger.e(TAG, "onStart");
        // handler.removeCallbacks(sendUpdatesToUI);
        // handler.postDelayed(sendUpdatesToUI, 1000);
    }

    /*protected Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            displayServiceStatus();
            handler.postDelayed(sendUpdatesToUI, 1000);
        }
    };*/

    public OfficeService getOfficeService() {
        return mService;
    }

    private boolean bindOfficeService(Context context) {
        final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
        intent.setPackage(Define.PACKAGENAME_KING_PRO);
        intent.putExtra("DisplayView", true);
        if (!context.bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
            // bind failed, maybe wps office is not installd yet.
            context.unbindService(connection);
            sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DISCONNECT));
            return false;
        }

        return true;
    }

    /**
     * connection of binding
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
			WpsLogger.e(TAG, "onServiceConnected");
			mService = OfficeService.Stub.asInterface(service);
			if (mService != null) {
				sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_CONNECT));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
			WpsLogger.e(TAG, "onServiceDisconnected");
            mService = null;
            sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DISCONNECT));
        }
    };

    private void displayServiceStatus() {
        // sendBroadcast( intent );
        // Intent intent = new Intent( this, MOfficeClientActivity.class );
        // intent.setAction( Intent.ACTION_VIEW );
        // intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        // intent.setFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
        // startActivity( intent );
    }

    @Override
    public void onDestroy() {
        WpsLogger.e(TAG, "onDestroy(): " + this.hashCode());
        // handler.removeCallbacks(sendUpdatesToUI);
        // 销毁时通知插件
        sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DESTROY));
    }
}
