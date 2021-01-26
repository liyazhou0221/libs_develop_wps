package cn.wps.moffice.demo.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cn.wps.moffice.demo.bean.WpsOpenBean;
import cn.wps.moffice.demo.floatingview.service.FloatServiceTest;
import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.JsonUtil;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.demo.util.WpsLogger;
import cn.wps.moffice.service.doc.Bookmark;
import cn.wps.moffice.service.doc.Bookmarks;
import cn.wps.moffice.service.doc.Document;
import cn.wps.moffice.service.pdf.PDFReader;

/**
 * 以AIDL的方式打开WPS服务
 */
public class WpsAIDLService extends BaseService {

    private static Document mDocument;
    private final static int NEW_DOCUMENT = 0;
    private final static int OPEN_DOCUMENT = 1;
    private Timer scheduleTimer;
    private WpsOpenBean wpsOpenBean;
    private PDFReader mPdfReader;

    public static Intent getIntent(Context context, WpsOpenBean openBean) {
        Intent intent = new Intent(context, WpsAIDLService.class);
        intent.putExtra("WpsOpenBean",openBean);
        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取传入参数
        Bundle extras = intent.getExtras();
        if (extras != null){
            wpsOpenBean = (WpsOpenBean)extras.getSerializable("WpsOpenBean");
            // 若服务未启动，则等待启动完成后打开
            if (scheduleTimer == null ){
                scheduleTimer = new Timer();
            }else{
                scheduleTimer.cancel();
                scheduleTimer = new Timer();
            }
            // 修改：每次重新打开都启动服务，connection这里好像没有回调到服务断开，导致后台关闭Wps时，无法再次打开
            startWpsService();
            scheduleTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mService != null){
                        scheduleTimer.cancel();
                        // 每次cancel后scheduleTimer都相当于废弃，不可重复使用
                        scheduleTimer = null;
                        openDocument();
                    }else{
                        WpsLogger.e(TAG, "mService == null scheduleTimer.schedule 等待500ms...");
                    }
                }
            },100,500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WpsLogger.e(TAG, "onDestroy");
        if (mService != null){
            unbindService(connection);
        }
        mDocument = null;
        if (scheduleTimer != null){
            scheduleTimer.cancel();
            scheduleTimer = null;
        }
    }

    protected void startWpsService(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!bindOfficeService()){
                    closeService();
                }
            }
        }).start();
    }

    /**
     * 停止服务调用
     */
    protected void stopService() {
        super.stopService();
        isBound = false;
        wpsOpenBean = null;
    }

    @Override
    public synchronized boolean bindOfficeService() {
        if (mService  != null) {
            // 之前已经连接过，先解除绑定，再尝试连接
            WpsLogger.e(TAG, "bindOfficeService 之前已经连接过，先解除绑定，再尝试连接>>>");
            try {
                // 这里的service可能会断开，解绑时会报错闪退
                unbindService(connection);
            }catch (Exception e){
                e.printStackTrace();
                WpsLogger.e(TAG, "unbindService(connection) 解绑异常！！！");
            }
        }
        // 关闭自启动后，先启动moffice进程
        final Intent startAppIntent = new Intent();
        startAppIntent.setClassName(packageName, Define.START_APP_ACTIVITY);
        startAppIntent.setAction(Define.START_APP);
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startAppIntent);
        WpsLogger.e(TAG, "bindOfficeService 关闭自启动后，先启动moffice进程>>>");
        // 先唤醒moffice进程，再绑定service
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Define.OFFICE_READY_ACTION.equals(intent.getAction())) {
                    synchronized (this) {
                        this.notifyAll();
                    }
                }
            }
        };
        WpsLogger.e(TAG, "bindOfficeService 先唤醒moffice进程，再绑定service>>>");
        registerReceiver(receiver, new IntentFilter(Define.OFFICE_READY_ACTION));
        final Intent wakeAppIntent = new Intent();
        wakeAppIntent.setClassName(packageName, Define.OFFICE_ACTIVITY_NAME);
        wakeAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(wakeAppIntent);
        synchronized (this) {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(receiver);
        WpsLogger.e(TAG, "bindOfficeService unregisterReceiver(receiver)>>>");
        final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
        intent.setPackage(packageName);
        intent.putExtra("DisplayView", true);
        startService(intent);
        WpsLogger.e(TAG, "bindOfficeService startService>>>");
        boolean bindOk = false;
        for (int i = 0; i < 10; ++i) {
            bindOk = bindService(intent, connection, Service.BIND_AUTO_CREATE);
            if (bindOk)
                break;
            else {
                try {
                    WpsLogger.e(TAG, "bindOfficeService Thread.sleep(500)>>>");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        // bind failed, maybe wps office is not installd yet.
        if (!bindOk) {
            unbindService(connection);
            return false;
        }
        WpsLogger.e(TAG, "bindOfficeService bindOk>>>");
        return true;
    }

    @Override
    protected void onChildServiceDisconnected() {
    }

    @Override
    protected void onChildServiceConnected() {
        try {
            WpsLogger.e(TAG, "onChildServiceConnected");
            mService.getApps().openApp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static Document getDocument() {
        return mDocument;
    }

    // 打开文档
    private void openDocument() {
        WpsLogger.e(TAG, "openDocument filePath = " + wpsOpenBean.getFilePath());
        if (mService == null) return;
        if (Util.isPDFFile(wpsOpenBean.getFilePath())){
            new LoadPDFDocThread(wpsOpenBean.getFilePath()).start();
        }else {
            new LoadDocThread(wpsOpenBean.getFilePath(), OPEN_DOCUMENT).start();
        }
    }

    class LoadDocThread extends Thread// 内部类
    {
        String path;
        int flag;
        boolean isShow = true;

        public LoadDocThread(String path, int openFlag) {
            this.path = path;
            this.flag = openFlag;
        }

        public LoadDocThread(String path, int openFlag, boolean isShow) {
            this.path = path;
            this.flag = openFlag;
            this.isShow = isShow;
        }

        public void run() {
            try {
                Intent intent = Util.getOpenIntent(mContext,wpsOpenBean);
//				mService.setFileId("sss");
                mService.setSecurityKey("123465".getBytes());
                if (OPEN_DOCUMENT == flag) {
                    Intent i = mService.getDocuments().getDocumentIntent(path, "", intent);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    mDocument = mService.getDocuments().waitForDocument(path);
                    if (mDocument != null){
                        for (int k = 0; k < 10; ++k) {
                            if (!mDocument.isLoadOK()) {
                                try {
                                    WpsLogger.e(TAG, "openDocument !mDocument.isLoadOK() Thread.sleep(500)>>>");
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                //Util.showToast(mContext, "文档已就绪，可以开始进行AIDL调用了");
                                /*如需在文档打开后，立即自动完成文档某些操作，可以在这里添加相关代码，如下面这句，开启手写
                                mDocument.toggleInkFinger();*/
                                WpsLogger.e(TAG, "openDocument mDocument.isLoadOK()>>>");
                                if (!TextUtils.isEmpty(wpsOpenBean.getBookMarks())) {
                                    JSONArray bookMarksArr = JsonUtil.string2JsonArray(wpsOpenBean.getBookMarks());
                                    for (int j = 0; j < bookMarksArr.length(); j++) {
                                        JSONObject bookMark = JsonUtil.getJson2JsonArray(bookMarksArr, j);
                                        if (bookMark != null) {
                                            String name = JsonUtil.getString2Json(bookMark,"name","书签");
                                            int rangeEnd = JsonUtil.getInt2Json(bookMark,"rangeEnd",0);
                                            int rangeStart = JsonUtil.getInt2Json(bookMark,"rangeStart",0);
                                            mDocument.getBookmarks().add(name, mDocument.range(rangeStart, rangeEnd));
                                        }
                                    }
                                    WpsLogger.e(TAG, "openDocument mDocument.getBookmarks().add 添加书签成功>>>");
                                }
                                break;
                            }
                        }
                    }else{
                        WpsLogger.e(TAG, "openDocument mDocument == null 文档对象为空！！！");
                    }
                } else if (NEW_DOCUMENT == flag) {
                    mDocument = mService.newDocument(path, intent);
                }
                if (mDocument != null && mDocument.getApplication() != null) {
                    WpsLogger.e(TAG, "Window.get_Version : " + mDocument.getApplication().getVersion());
                }
            } catch (RemoteException e) {
                WpsLogger.e(TAG, "openDocument RemoteException>>>");
                e.printStackTrace();
                mDocument = null;
                closeService();
            }
        }
    }

    class LoadPDFDocThread extends Thread// 内部类
    {
        String path;

        public LoadPDFDocThread(String path) {
            this.path = path;
        }

        public void run() {
            try {
                mService.setSecurityKey("123456".getBytes());
                Intent intent = Util.getPDFOpenIntent(mContext, this.path, true);
                Intent i = mService.getPDFReaders().getPDFReaderIntent(path, "", intent);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                mPdfReader = mService.getPDFReaders().waitForPDFReader(path);
                if (mPdfReader == null) {
                    WpsLogger.e(TAG, "PDF对象获取失败");
                }else{
                    WpsLogger.e(TAG, "PDF对象获取成功");
                    Intent intent1 = new Intent(WpsPlugin.ACTION_WPS_SERVICE_OPEN_FILE);
                    intent1.putExtra(WpsPlugin.FILE_PATH, path);
                    getApplicationContext().sendBroadcast(intent1);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                mPdfReader = null;
                closeService();
            }
        }
    }

    private void closeService(){
        WpsLogger.e(TAG, "closeService");
        try {
            if(connection != null){
                unbindService(connection);
            }
            sendBroadcast(new Intent(WpsPlugin.ACTION_WPS_SERVICE_DISCONNECT));
            stopService();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
