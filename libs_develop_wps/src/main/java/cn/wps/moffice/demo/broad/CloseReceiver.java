/*
 * 	 文件名：CloseReceiver.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：负责接收wps关闭时发送的广播，解析并保存起来
 */
package cn.wps.moffice.demo.broad;

import cn.wps.moffice.demo.floatingview.service.FloatServiceTest;
import cn.wps.moffice.demo.service.WpsAIDLService;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.demo.util.WpsLogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class CloseReceiver extends BroadcastReceiver
{
	
	SettingPreference settingPreference;
	/**
	 * 接收wps关闭时发送的广播，同时记录关闭时的参数，以便下一次打开
	 */
    public void onReceive(Context context, Intent intent) 
    {
    	settingPreference = new SettingPreference(context);
    	
        String 	name = intent.getExtras().getString("CurrentPath");
        String	packageName = intent.getExtras().getString("ThirdPartyPackage");
        float 	ViewProgress = intent.getExtras().getFloat("ViewProgress");
        float 	ViewScale = intent.getExtras().getFloat("ViewScale");
        int 	ViewScrollX = intent.getExtras().getInt("ViewScrollX");
        int 	ViewScrollY = intent.getExtras().getInt("ViewScrollY");

        settingPreference.setSettingParam(Define.VIEW_PROGRESS, ViewProgress);
        settingPreference.setSettingParam(Define.VIEW_SCALE, ViewScale);
        settingPreference.setSettingParam(Define.VIEW_SCROLL_X, ViewScrollX);
        settingPreference.setSettingParam(Define.VIEW_SCROLL_Y, ViewScrollY);
        settingPreference.setSettingParam(Define.CLOSE_FILE, name);
        settingPreference.setSettingParam(Define.THIRD_PACKAGE, packageName);
        String msg = "文件路径: " + name + "\n第三方包名: " + packageName
                + "\n文件查看的进度: " + ViewProgress + "\n上次查看的视图的缩放: " + ViewScale
                + "\nX坐标: " + ViewScrollX + "\nY坐标: " + ViewScrollY;
        WpsLogger.e("CloseReceiver",msg);
        // Util.showToast(context, msg);
        
        //关闭service
        if (FloatServiceTest.isBound){
            FloatServiceTest.stopService();
        }
        if (WpsAIDLService.isBound){
            WpsAIDLService.getInstance().stopSelf();
        }
    }
}