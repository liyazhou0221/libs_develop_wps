/**
 * 文件名：ListFile.java
 * 创建者:fanguangcheng
 * 创建时间:2013.7.18
 * 作用：文件列表显示，并响应打开wps文件等一系列动作
 */
package cn.wps.moffice.demo.fileManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.wps.moffice.demo.bean.WpsOpenBean;
import cn.wps.moffice.demo.client.MOfficeClientService;
import cn.wps.moffice.demo.floatingview.service.FloatServiceTest;
import cn.wps.moffice.demo.floatingview.service.FloatingServiceHideView;
import cn.wps.moffice.demo.menu.AIDLParamActivity;
import cn.wps.moffice.demo.menu.ATEditParamActivity;
import cn.wps.moffice.demo.menu.ListViewParamActivity;
import cn.wps.moffice.demo.menu.OpenWayParamActivity;
import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.test.AutoTest;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.demo.util.Util;

public class ListFileActivity extends ListActivity {

    File currentParent;                    // 记录当前的父文件夹
    File[] currentFiles;                // 记录当前路径下的所有文件夹的文件数组
    private final static String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private final String LOG_TAG = ListFileActivity.class.getName();
    File root = new File(rootPath);
    SortByName sort = new SortByName();    //文件排序

    private final int REQUEST_CODE_PERMISSION_STORAGE = 10001;
    //存入preference的操作对象
    SettingPreference settingPreference;

    WpsPlugin wpsPlugin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //启动service
        Intent intent = new Intent(this, MOfficeClientService.class);
        startService(intent);
        if (hasStoragePermissions()) {
            refreshFileList();
        }

        initWpsPlugin();
    }

    private void initWpsPlugin() {
        wpsPlugin = new WpsPlugin(this, new WpsPlugin.IWpsPluginInterface() {
            @Override
            public void onSaveFile(String filePath) {
                Toast.makeText(ListFileActivity.this, "onSaveFile:"  + filePath, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onClosedWindow(String filePath) {
                Toast.makeText(ListFileActivity.this, "onClosedWindow :" + filePath, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onOpenFile(String filePath) {
                Toast.makeText(ListFileActivity.this, "onOpenFile = " + filePath, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onOpenFailed(String filePath, String msg) {
                Toast.makeText(ListFileActivity.this, msg +":"+ filePath, Toast.LENGTH_LONG).show();
            }

            @Override
            public void unbind(String filePath) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkStoragePer();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    private void updateUI(Intent intent) {
        Log.d(LOG_TAG, "Service ...... ");
    }

    private long exitTime = 0;                        //实现“再按一次退出程序”的时间检测
    private long waitTime = 2000;

    @Override
    public void onBackPressed() {
        try {
            if (!currentParent.getCanonicalPath().equals(rootPath)) {
                currentParent = currentParent.getParentFile();
                currentFiles = currentParent.listFiles();
                currentFiles = sort.sort(currentFiles);
                setListAdapter(new EfficientAdapter(this, currentFiles));
            } else {
                if ((System.currentTimeMillis() - exitTime) > waitTime) {
                    Toast.makeText(getApplicationContext(), "再按一次退出程序",
                            Toast.LENGTH_SHORT).show();
                    exitTime = System.currentTimeMillis();
                } else {
                    finish();
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (currentFiles[position].isFile()) {
            // openFileOld(position);
            WpsOpenBean wpsOpenBean = new WpsOpenBean(currentFiles[position].getAbsolutePath(),true,Define.READ_ONLY);
            wpsPlugin.openFile(wpsOpenBean);
            return;
        }
        // 如果是目录，获取用户点击的文件夹 下的所有文件
        File[] tem = currentFiles[position].listFiles();
        if (tem == null || tem.length == 0) {
            Toast.makeText(this, "当前路径下没有文件", Toast.LENGTH_LONG).show();
            return;
        } else {
            if (sort.hideFileNum(tem) == tem.length) {//如果目录下都是隐藏文件就返回
                Toast.makeText(this, "当前路径下没有文件", Toast.LENGTH_LONG).show();
                return;
            }
            currentParent = currentFiles[position];
            currentFiles = tem;
            currentFiles = sort.sort(currentFiles);
            setListAdapter(new EfficientAdapter(this, currentFiles));
        }
    }

    private void openFileOld(int position){
        if (Util.IsWPSFile(currentFiles[position])) {
            //如果是wps能打开的文件，则打开
            if (Define.WPS_OPEN_AIDL.equals(settingPreference.getSettingParam(Define.WPS_OPEN_MODE, ""))) {
                if (settingPreference.getSettingParam(Define.IS_SHOW_VIEW, true)) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (!Settings.canDrawOverlays(this)) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent);
                            return;
                        }
                    }
                    //显示wps界面操作
                    FloatServiceTest.setDocPath(currentFiles[position].getAbsolutePath());
                    Intent service = new Intent();
                    service.setClass(ListFileActivity.this, FloatServiceTest.class);
                    startService(service);
                } else {
                    //不显示wps界面操作
                    FloatingServiceHideView.setDocPath(currentFiles[position].getAbsolutePath());
                    Intent service2 = new Intent();
                    service2.setClass(ListFileActivity.this, FloatingServiceHideView.class);
                    startService(service2);
                }
            } else {
                //以第三方方式打开
                openFile(currentFiles[position].getAbsolutePath());
            }

        } else {//不是wps文件则让用户选择
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            String type = Util.getMIMEType(currentFiles[position]);
            intent.setDataAndType(Uri.fromFile(currentFiles[position]), type);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 0, 0, "        设置常用参数");
        menu.add(0, 1, 1, "        设置打开方式参数");
        menu.add(0, 2, 2, "        设置AIDL调用参数");
        menu.add(0, 3, 3, "        设置编辑参数");
        menu.add(0, 4, 4, "        加解密自动化测试");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent();
                intent.setClass(this, ListViewParamActivity.class);
                startActivity(intent);//无返回值的调用,启动一个明确的activity
                break;
            case 1:
                Intent intent1 = new Intent();
                intent1.setClass(this, OpenWayParamActivity.class);
                startActivity(intent1);//无返回值的调用,启动一个明确的activity
                break;
            case 2:
                Intent intent2 = new Intent();
                intent2.setClass(this, AIDLParamActivity.class);
                startActivity(intent2);//无返回值的调用,启动一个明确的activity
                break;
            case 3:
                Intent intent3 = new Intent();
                intent3.setClass(this, ATEditParamActivity.class);
                startActivity(intent3);//无返回值的调用,启动一个明确的activity
                break;
            case 4:
                Intent autotestIntent = new Intent();
                autotestIntent.setClass(this, AutoTest.class);
                startActivity(autotestIntent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }


    /**
     * 识别文件的类型
     * @param f
     * @return
     */

    /**
     * 如果是wps文件，则用wps打开，并对其设置一下参数
     * @param path
     * @return
     */
    private boolean openFile(String path) {
        String wps_open_mode = settingPreference.getSettingParam(Define.WPS_OPEN_MODE, "null");
        if (wps_open_mode.equals(Define.WPS_OPEN_AIDL)) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(intent);
                    return false;
                }
            }
            //AIDL打开方式
            FloatServiceTest.setDocPath(path);
            Intent service = new Intent();
            service.setClass(ListFileActivity.this, FloatServiceTest.class);
            startService(service);
            Log.d("sort", "AIDL方式启动office");
            return true;
        }

        Intent intent = Util.getOpenIntent(getApplicationContext(), new WpsOpenBean(path, true, Define.READ_ONLY));
        if (null == intent) {
            return false;
        }

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("stop", "stop");
        // createView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("restart", "restart");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wpsPlugin != null){
            wpsPlugin.onDestroy();
        }
    }

    private static final String ENT_WRITER_KEY_BACK_ACTION = "com.kingsoft.writer.back.key.down";
    IntentFilter filterBackKey = new IntentFilter(ENT_WRITER_KEY_BACK_ACTION);
    private BroadcastReceiver writerBackKeyDownListerner = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MOfficeDemoActivity", "writerBackKeyDownListerner");
        }

    };

    private static final String ENT_WRITER_KEY_HOME_ACTION = "com.kingsoft.writer.home.key.down";
    IntentFilter filterHomeKey = new IntentFilter(ENT_WRITER_KEY_HOME_ACTION);
    private BroadcastReceiver writerHomeKeyDownListerner = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MOfficeDemoActivity", "writerHomeKeyDownListerner");
        }

    };


    @TargetApi(Build.VERSION_CODES.M)
    private void checkStoragePer() {
        if (!hasStoragePermissions()) {
            String[] PERMISSIONS_STORAGE = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};
            this.requestPermissions(PERMISSIONS_STORAGE, REQUEST_CODE_PERMISSION_STORAGE);
        }
    }

    private boolean hasStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION_STORAGE) {
            if (hasStoragePermissions()) {
                refreshFileList();
            }
        }
    }

    private void refreshFileList() {
        currentParent = root;
        currentFiles = root.listFiles();
        if (sort.hideFileNum(currentFiles) == currentFiles.length) {//如果目录下都是隐藏文件就返回
            Toast.makeText(this, "当前路径下没有文件", Toast.LENGTH_LONG).show();
            return;
        }
        currentFiles = sort.sort(currentFiles);
        setListAdapter(new EfficientAdapter(this, currentFiles));

        //实现将第三方包名写入文件，以便wps读取
        settingPreference = new SettingPreference(this);
        settingPreference.setSettingParam(Define.KEY, getPackageName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
