package cn.wps.moffice.demo.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;

import cn.wps.moffice.demo.bean.WpsOpenBean;
import cn.wps.moffice.demo.client.MOfficeClientService;
import cn.wps.moffice.demo.service.WpsAIDLService;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.demo.util.WpsLogger;

/**
 * 提供第三方调用的插件
 * 1、先判断传入的文件路径是否是WPS可打开文件
 * 2、判断是否安装WPS专业版客户端（判断提前，在调用插件时就判断，否则文件会多下载一次）
 * 3、添加打开参数
 * 4、注册接收器，监听wps服务是否启动，以及WPS事件监听
 * 5、服务启动并连接WPS成功，打开文件
 */
public class WpsPlugin implements IPlugin {

    private String TAG = WpsPlugin.class.getSimpleName();
    private final Handler handler;

    /**
     * 接受器action
     */
    public static final String ACTION_WPS_SERVICE_CONNECT = "action.cn.wps.moffice.service.connect";
    public static final String ACTION_WPS_SERVICE_REBIND = "action.cn.wps.moffice.service.rebind";
    public static final String ACTION_WPS_SERVICE_UNBIND = "action.cn.wps.moffice.service.unbind";
    public static final String ACTION_WPS_SERVICE_DISCONNECT = "action.cn.wps.moffice.service.disconnect";
    public static final String ACTION_WPS_SERVICE_DESTROY = "action.cn.wps.moffice.service.destroy";
    public static final String ACTION_WPS_SERVICE_OPEN_FILE = "action.cn.wps.moffice.service.open.file";
    public static final String ACTION_WPS_SERVICE_SAVE_FILE = "action.cn.wps.moffice.service.save.file";
    public static final String ACTION_WPS_SERVICE_SAVE_FILE_FAILED = "action.cn.wps.moffice.service.save.file.failed";
    public static final String ACTION_WPS_SERVICE_CLOSE_WINDOW = "action.cn.wps.moffice.service.close.window";

    private IWpsPluginInterface listener;
    private static WeakReference<Context> context;
    private String wpsPackageName;
    public static final String FILE_PATH = "filePath";
    public static final String OPEN_MODE = "openMode";
    public static final String BOOK_MARKS = "bookMarks";
    public static final String ENTER_REVISE_MODE = "enterReviseMode";
    public static final String USER_NAME = "userName";
    public static final String OPEN_TYPE = "openType";
    public static final String SERIAL_NUMBER_OTHER = "serialNumberOther";

    public static final String OPEN_TYPE_AIDL = "AIDL";
    public static final String OPEN_TYPE_THIRD_PARTY = "THIRD_PARTY";

    private boolean wpsServerIsRunning = false;
    private BroadcastReceiver receiver;

    private String filePath = "";
    private WpsOpenBean openBean;

    public WpsPlugin(Context context, IWpsPluginInterface listener) {
        this.context = new WeakReference<>(context);
        this.listener = listener;
        this.handler = new Handler();
        registerReceiver();
    }

    private void registerReceiver() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String fileName = intent.getStringExtra(WpsPlugin.FILE_PATH);
                if (TextUtils.isEmpty(fileName)) {
                    fileName = "";
                }
                WpsLogger.e(TAG, "action = " + action);
                if (action.equals(ACTION_WPS_SERVICE_CONNECT)) {
                    WpsLogger.e(TAG, "服务连接成功！");
                    wpsServerIsRunning = true;
                } else if (action.equals(ACTION_WPS_SERVICE_REBIND)) {
                    // 重新连接
                    wpsServerIsRunning = false;
                } else if (action.equals(ACTION_WPS_SERVICE_DISCONNECT)) {
                    // 服务连接失败
                    if (!TextUtils.isEmpty(filePath) && !wpsServerIsRunning) {
                        if (listener != null) {
                            listener.onOpenFailed(filePath, "WPS服务连接失败！");
                        }
                    }
                    wpsServerIsRunning = false;
                } else if (action.equals(ACTION_WPS_SERVICE_UNBIND)) {
                    // 断开连接，可以视为用户关闭了WPS编辑框（这里可以作为PDF文件保存的入口）
                    if (listener != null && !TextUtils.isEmpty(filePath)) {
                        listener.unbind(filePath);
                    }
                } else if (action.equals(ACTION_WPS_SERVICE_DESTROY)) {
                    // 服务关闭 这里可以作为离开编辑页面的判断
                    wpsServerIsRunning = false;
                    if (listener != null && !TextUtils.isEmpty(filePath)) {
                        listener.onClosedWindow(filePath);
                    }
                } else if (action.equals(ACTION_WPS_SERVICE_OPEN_FILE)) {
                    // WPS做了打开操作
                    WpsLogger.e(TAG, "WPS做了打开操作  fileName = " + fileName);
                    if (listener != null && !TextUtils.isEmpty(filePath)) {
                        listener.onOpenFile(filePath);
                    }
                } else if (action.equals(ACTION_WPS_SERVICE_SAVE_FILE)) {
                    // WPS做了保存操作
                    WpsLogger.e(TAG, "WPS做了保存操作  fileName = " + fileName);
                    if (listener != null && !TextUtils.isEmpty(filePath)) {
                        listener.onSaveFile(filePath);
                    }
                } else if (action.equals(ACTION_WPS_SERVICE_CLOSE_WINDOW)) {
                    // WPS关闭了编辑窗口(旧方法，可能获取不到)
                } else if (action.equals(ACTION_WPS_SERVICE_SAVE_FILE_FAILED)) {
                    // 文件保存失败
                    WpsLogger.e(TAG, "WPS文件保存失败  fileName = " + fileName);
                    if (listener != null && !TextUtils.isEmpty(filePath)) {
                        listener.onOpenFailed(filePath, "文件保存失败");
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_WPS_SERVICE_CONNECT);
        filter.addAction(ACTION_WPS_SERVICE_REBIND);
        filter.addAction(ACTION_WPS_SERVICE_UNBIND);
        filter.addAction(ACTION_WPS_SERVICE_DISCONNECT);
        filter.addAction(ACTION_WPS_SERVICE_DESTROY);
        filter.addAction(ACTION_WPS_SERVICE_SAVE_FILE);
        filter.addAction(ACTION_WPS_SERVICE_OPEN_FILE);
        filter.addAction(ACTION_WPS_SERVICE_CLOSE_WINDOW);
        context.get().registerReceiver(receiver, filter);
    }

    public void openFile(WpsOpenBean wpsOpenBean) {
        try {
            filePath = wpsOpenBean.getFilePath();
            if (initFileParams(wpsOpenBean)) {
                this.openBean = wpsOpenBean;
                openWpsService(true);
            }
        } catch (Exception e) {
            WpsLogger.e("WPS plugin", "openFile initFileParams 异常！");
            e.printStackTrace();
            if (listener != null) {
                listener.onOpenFailed(filePath, "打开文件出现错误，请重试！");
            }
        }
    }

    /**
     * 校验传入参数
     *
     * @return
     */
    private boolean initFileParams(WpsOpenBean wpsOpenBean) {
        if (listener == null) {
            WpsLogger.e("WPS plugin", "IWpsPluginInterface 不能为空！");
            // throw new Exception("文件打开失败 IWpsPluginInterface 不能为空！");
            return false;
        }
        String _filePath = wpsOpenBean.getFilePath();
        if (wpsOpenBean != null) {
            // 直接打开时文件不能为空
            if (TextUtils.isEmpty(_filePath)) {
                listener.onOpenFailed(_filePath, "文件打开失败，文件地址不能为空");
                return false;
            }
            File file = new File(_filePath);
            if (file == null || !file.exists()) {
                listener.onOpenFailed(_filePath, "文件打开失败，文件不存在！");
                return false;
            }
            if (!file.isFile()) {
                listener.onOpenFailed(_filePath, "文件打开失败，不是文件路径！");
                return false;
            }
            if (!Util.IsWPSFile(file)) {
                listener.onOpenFailed(_filePath, "文件打开失败，该文件不能使用WPS打开，请使用其它打开方式！");
                return false;
            }
        } else {
            listener.onOpenFailed(_filePath, "文件打开失败，传入参数为空！");
            return false;
        }

        wpsPackageName = Util.getWPSPackageName(context.get());
        if (wpsPackageName == null) {
            listener.onOpenFailed(_filePath, "文件打开失败，请安装WPS Office专业版");
            return false;
        }
        return true;
    }

    private void openFileDelay() {
        Intent intent = new Intent(context.get(), MOfficeClientService.class);
        context.get().startService(intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 调用WPS页面打开文件
                openWpsService(false);
            }
        }, 500);
    }

    /**
     * 1、第三方打开方式，不支持插入书签
     * 2、AIDL 方式打开，可以插入书签，并对文档DOCUMENT直接操作
     */
    private void openWpsService(boolean isFirst) {
        //启动service
        if (!wpsServerIsRunning && isFirst) {
            // 需要等待一会，启动服务后打开文件
            openFileDelay();
        } else {
            //启动service
            if (OPEN_TYPE_THIRD_PARTY.equals(openBean.getOpenType())) {
                // 1、第三方打开方式，不支持插入书签
                Intent intent;
                if (Util.isPDFFile(openBean.getFilePath())){
                    intent = Util.getPDFOpenIntent(context.get(),openBean.getFilePath(),true);
                }else{
                    intent = Util.getOpenIntent(context.get(),openBean);
                }
                context.get().startActivity(intent);
            } else {
                // 2、AIDL 方式打开，可以插入书签，并对文档DOCUMENT直接操作
                Intent intent = WpsAIDLService.getIntent(context.get(),openBean);
                context.get().startService(intent);
            }
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {
        if (receiver != null && context.get() != null) {
            context.get().unregisterReceiver(receiver);
        }
    }

    public interface IWpsPluginInterface {
        void onSaveFile(String filePath);

        void onClosedWindow(String filePath);

        void onOpenFile(String filePath);

        void onOpenFailed(String filePath, String msg);

        void unbind(String filePath);
    }
}
