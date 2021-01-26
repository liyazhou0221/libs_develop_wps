package cn.wps.moffice.demo.broad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.demo.util.WpsLogger;

public class PremiumAidlReceiver extends BroadcastReceiver {


    private final String TAG = PremiumAidlReceiver.class.getSimpleName();
    private static String PremiumResultCode = "PremiumResultCode";

    private static String ACTION_PREMIUM_RESULT = "cn.wps.moffice.premium.result";
    private static final int CODE_RESULT_OK = 1; //成功
    private static final int CODE_NOT_SUPPORT_FILE_TYPE = 2; //不支持这种格式
    private static final int CODE_NOT_ACTIVATE = 3; //没有激活
    private static final int CODE_PHONE_ONLY = 4;//仅支持手机
    private static final int CODE_IMAGE_ONLY = 5; //仅支持图片类型
    private static final int CODE_PDF_ONLY = 6; //仅支持pdf类型
    private static final int CODE_PPT_ONLY = 7; //仅支持PPT类型

    @Override
    public void onReceive(Context context, Intent intent) {
       WpsLogger.e(TAG, "onReceive");
        if (intent != null) {
            int result = intent.getIntExtra(PremiumResultCode, -1);
            switch (result) {
                case CODE_RESULT_OK:
                    WpsLogger.e(TAG, "CODE_RESULT_OK");
                    break;
                case CODE_NOT_SUPPORT_FILE_TYPE:
                    Util.showToast(context, "暂不支持该类型文件");
                    break;
                case CODE_NOT_ACTIVATE:
                    Util.showToast(context, "请激活wps后使用该功能");
                    break;
                case CODE_PHONE_ONLY:
                    Util.showToast(context, "抱歉，该功能仅支持手机");
                    break;
                case CODE_IMAGE_ONLY:
                    Util.showToast(context, "请选择图片类型文件");
                    break;
                case CODE_PDF_ONLY:
                    Util.showToast(context, "请选择pdf类型文件");
                    break;

                case CODE_PPT_ONLY:
                    Util.showToast(context, "请选择ppt类型文件");
                    break;
                default:
                    break;
            }
        }
    }
}
