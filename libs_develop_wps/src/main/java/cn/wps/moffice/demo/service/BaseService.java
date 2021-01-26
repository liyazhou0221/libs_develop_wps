package cn.wps.moffice.demo.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.demo.util.WpsLogger;
import cn.wps.moffice.service.OfficeService;

/**
 * wps service的基类，用来扩展不同的需求使用
 */
public abstract class BaseService extends Service {

    protected String TAG ;
    public String packageName;
    public static OfficeService mService;
    protected Context mContext;
    public static BaseService mInstance;
    public static boolean isBound;

    public static Service getInstance(){
        return mInstance;
    }

    /**
     * 对外暴露的获取方法，用来直接操纵文档服务
     * @return
     */
    public OfficeService getOfficeService() {
        return mService;
    }

    @Override
    public void onCreate() {
        TAG = this.getClass().getSimpleName();
        WpsLogger.e(TAG, "onCreate(): " + this.hashCode());
        mContext = getApplicationContext();
        mInstance = this;
        // 获取WPS应用包名（默认为专业版）；
        String name = Util.getWPSPackageName(this);
        if (name != null){
            packageName = name;
        }else{
            packageName = Define.PACKAGENAME_KING_PRO;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WpsLogger.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        WpsLogger.e(TAG, "onBind(): " + this.hashCode() + ", " + intent.toString());
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        WpsLogger.e(TAG, "onRebind(): " + this.hashCode() + ", " + intent.toString());
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        WpsLogger.e(TAG, "onUnbind(): " + this.hashCode() + ", " + intent.toString());
        return true;
    }
    @Override
    public void onDestroy() {
        WpsLogger.e(TAG, "onDestroy(): " + this.hashCode());
        // 销毁时通知插件
        // sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DESTROY));
    }

    protected abstract boolean bindOfficeService();

    /**
     * connection of binding
     */
    protected ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WpsLogger.e(TAG, "onServiceConnected");
            mService = OfficeService.Stub.asInterface(service);
            if (mService != null) {
                isBound = true;
                onChildServiceConnected();
                sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_CONNECT));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            WpsLogger.e(TAG, "onServiceDisconnected");
            mService = null;
            isBound = false;
            onChildServiceDisconnected();
            sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DISCONNECT));
        }
    };

    protected abstract void onChildServiceDisconnected();

    // 根据需求处理连接成功后的操作
    protected abstract void onChildServiceConnected();

    /**
     * 停止服务调用
     */
    protected void stopService() {
        WpsLogger.e(TAG, "stopService");
        if (mInstance != null){
            mInstance.stopSelf();//关闭自身service
        }
    }

}
