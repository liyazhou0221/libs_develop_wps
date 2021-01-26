package cn.wps.moffice.demo.client;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import cn.wps.moffice.client.ActionType;
import cn.wps.moffice.client.AllowChangeCallBack;
import cn.wps.moffice.client.OfficeEventListener;
import cn.wps.moffice.client.OfficeInputStream;
import cn.wps.moffice.client.OfficeOutputStream;
import cn.wps.moffice.client.ViewType;
import cn.wps.moffice.demo.floatingview.service.FloatServiceTest;
import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.service.BaseService;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.EncryptClass;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.doc.Document;

//import cn.wps.moffice.demo.floatingview.service.FloatingService;

public class OfficeEventListenerImpl extends OfficeEventListener.Stub {
    protected MOfficeClientService service = null;

    private boolean mIsValidPackage = true;

    public OfficeEventListenerImpl(MOfficeClientService service) {
        this.service = service;
    }

    @Override
    public int onOpenFile(String path, OfficeOutputStream output)
            throws RemoteException {
        Log.d("OfficeEventListener", "onOpenFile");
        Intent intent = new Intent(WpsPlugin.ACTION_WPS_SERVICE_OPEN_FILE);
        intent.putExtra(WpsPlugin.FILE_PATH, path);
        service.sendBroadcast(intent);
        if (!mIsValidPackage)
            return -1;

        SettingPreference settingPreference;
        settingPreference = new SettingPreference(this.service.getApplicationContext());
        // 这里不能默认加密，加密后H5页面打开乱码，客户端不做加密处理即可
        boolean isEncrypt = settingPreference.getSettingParam(Define.ENCRYPT_FILE, false);
        if (isEncrypt)
            return EncryptClass.encryptOpenFile(path, output);
        else
            return EncryptClass.normalOpenFile(path, output);

    }

    @Override
    public int onSaveFile(OfficeInputStream input, String path) throws RemoteException {
        Log.d("OfficeEventListener", "onSaveFile");

        SettingPreference settingPreference;
        settingPreference = new SettingPreference(this.service.getApplicationContext());
        // 这里不能默认加密，加密后H5页面打开乱码，客户端不做加密处理即可
        boolean isEncrypt = settingPreference.getSettingParam(Define.ENCRYPT_FILE, false);
        int result;
        if (isEncrypt) {
            result = EncryptClass.encryptSaveFile(input, path);
        } else {
            result = EncryptClass.normalSaveFile(input, path);
        }
        // Client对path所对应的文件解密，将解密结果写入到output对象中，写入完成之后，返回0。解密失败返回-1.
        Intent intent;
        if (result == 0) {
            // 完成保存操作后发送消息通知，客户端可以开始上传保存后的文件了
            intent = new Intent(WpsPlugin.ACTION_WPS_SERVICE_SAVE_FILE);
        } else {
            // 返回值为-1时，Server将告诉用户，文件无法打开。
            intent = new Intent(WpsPlugin.ACTION_WPS_SERVICE_SAVE_FILE_FAILED);
        }
        intent.putExtra(WpsPlugin.FILE_PATH, path);
        service.sendBroadcast(intent);
        return result;
    }

    @Override
    public int onCloseFile() throws RemoteException {
        Log.d("OfficeEventListener", "onCloseFile");
        return 0;
    }

    @Override
    public boolean isActionAllowed(String path, ActionType type) throws RemoteException {
        Log.d("OfficeEventListener", "isActionAllowed " + type.toString());

        SettingPreference settingPreference;
        settingPreference = new SettingPreference(this.service.getApplicationContext());

        //光标输入模式，进行特殊处理
//		if (type.equals(ActionType.AT_CURSOR_MODEL)
//		    && settingPreference.getSettingParam(type.toString(), true))
//		{
//			return isCursorMode(path, type);
//		}
//
//		if (type.equals(ActionType.AT_EDIT_REVISION)) 	//如果是接受或拒绝某条修订的事件,做特殊处理
//		{
//			return isRevisionMode(path, type, settingPreference);
//		}
//		if (type.equals(ActionType.AT_SAVE))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_SAVEAS))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_COPY))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_CUT))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_PASTE))
//		{
//			return false;
//		}
        if (type.equals(ActionType.AT_SHARE)) {
            return false;
        }
//		if (type.equals(ActionType.AT_PRINT))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_SPELLCHECK))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_MULTIDOCCHANGE))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_QUICK_CLOSE_REVISEMODE))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_EDIT_REVISION))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_CHANGE_COMMENT_USER))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_SHARE_PLAY))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_GRID_BACKBOARD))
//		{
//			return false;
//		}
//		if (type.equals(ActionType.AT_EXPORT_AS_PDF))
//		{
//			return false;
//		}
//
//		if (type.equals(ActionType.AT_MENU_FIND))
//		{
//			return false;
//		}


        boolean result = true;
        boolean typeAT = settingPreference.getSettingParam(type.toString(), true);
        String pathAT = settingPreference.getSettingParam(Define.AT_PATH, "/");
        boolean isExist = path.startsWith(pathAT) || path.equals("");  //有部分事件传过来路径为"",
        if (!typeAT && isExist)
            result = false;

        return result;
    }

    @Override
    public boolean isViewForbidden(String arg0, ViewType arg1) throws RemoteException {
//		if (arg1 == ViewType.VT_FILE_PRINT)
//		{
//			return true;
//		}
//		else if (arg1 == ViewType.VT_FILE_SAVE)
//		{
//			return true;
//		}
        return false;
    }

    @Override
    public boolean isViewInVisible(String arg0, ViewType arg1) throws RemoteException {
//		if (arg1 == ViewType.VT_MENU_REVIEW || arg1 == ViewType.VT_MENU_PEN)
//		{
//			return true;
//		}
        return false;
    }

    @Override
    public void onMenuAtion(String path, String id) throws RemoteException {
        //TODO　自定义菜单，path为文档路径，id为传入的菜单按钮id
        Document mDoc = service.mService.getActiveDocument();
        if (mDoc == null) {
            Util.showToast(this.service.getApplicationContext(), "服务已经断开，请重启程序");
        } else {
            if (id.equals("menu_id_comment_pad")) {
                mDoc.enterReviseMode();
            } else if (id.equals("menu_id_exit_comment")) {
                mDoc.exitReviseMode();
            } else if (id.equals("menu_id_exit_pdf") || id.equals("menu_id_exit_wps")){
                mDoc.close();
            }
        }
    }

    @Override
    public String getMenuText(String path, String id) throws RemoteException {
        //TODO　自定义菜单，可更新菜单文字，path为文档路径，id为传入的菜单按钮id
        if ("menu_id_comment".equals(id)) {
            return "退出修订";
        }
        return null;
    }

    @Override
    public void setText(CharSequence charSequence) throws RemoteException {

    }

    @Override
    public CharSequence getText() throws RemoteException {
        return null;
    }

    @Override
    public boolean hasText() throws RemoteException {
        return false;
    }

    @Override
    public int printFileSave(String s, String s1) throws RemoteException {
        return 0;
    }

    //是否可以操作他人修订（作者名不同的修订）
    private boolean isRevisionMode(String path, ActionType type, SettingPreference settingPreference) {
        String docUserName = settingPreference.getSettingParam(Define.USER_NAME, "");
        boolean typeAT = settingPreference.getSettingParam(type.toString(), true);
        boolean isSameOne = docUserName.equals(path);    //在此事件中，path中存放是是作者批注名
        if (!typeAT && !isSameOne) {
            return false;
        }

        return true;
    }

    //中广核特殊需求
    private boolean isCursorMode(String path, ActionType type) throws RemoteException {

        boolean flag = null != FloatServiceTest.getDocument() && FloatServiceTest.getDocument().getSelection().getStart() == FloatServiceTest.getDocument().getSelection().getEnd();

        if (!flag)
            return false;

        if (FloatServiceTest.getDocument().isProtectOn())
            return false;

        FloatServiceTest.getDocument().getSelection().getFont().setBold(true);
        FloatServiceTest.getDocument().getSelection().getFont().setItalic(true);
        FloatServiceTest.getDocument().getSelection().getFont().setName("宋体");
        FloatServiceTest.getDocument().getSelection().getFont().setStrikeThrough();
        FloatServiceTest.getDocument().getSelection().getFont().setSize((float) 15.0);
        FloatServiceTest.getDocument().getSelection().getFont().setTextColor(0x00ff00);

        return true;
    }

    /**
     * 实现多个可变包名的验证
     * originalPackage是最原始的第三方包名，华为渠道为“com.huawei.svn.hiwork”
     * thirdPackage为可变动的应用包名，具体有企业资金定制
     */
    @Override
    public boolean isValidPackage(String originalPackage, String thirdPackage)
            throws RemoteException {
//此处是某些企业的特殊需求，可以忽略
//		mIsValidPackage = false;
//		if (originalPackage.equals(service.getPackageName()) && thirdPackage.equals("cn.wps.moffice"))
//		{
//			mIsValidPackage = true;
//			return true;
//		}
        return false;
    }


    @Override
    public void setAllowChangeCallBack(AllowChangeCallBack allowChangeCallBack) throws RemoteException {
        FloatServiceTest.setAllowCallBack(allowChangeCallBack);
    }

    @Override
    public int invoke(String actionID, String args) throws RemoteException {
        // TODO Auto-generated method stub
        if ("addPicture".equals(actionID)) {
            OfficeService officeService = service.getOfficeService();
//			officeService.getActiveDocument().getShapes().addPicture(args, false, false, 100, 100, 100, 100, 0, WrapType.None);
        } else if ("showDialog".equals(actionID)) {
            Message msg = Message.obtain();
            showDialogHandler.sendMessage(msg);
        }
        return 0;
    }

    private Handler showDialogHandler = new Handler() {
        public void handleMessage(Message msg) {
            showSystemDialog(OfficeEventListenerImpl.this.service.getApplicationContext());
        }
    };

    private void showSystemDialog(Context context) {
        TextView t = new TextView(context);
        t.setText("showSystemDialog");
        AlertDialog d = new AlertDialog.Builder(context).create();
        d.setView(t, 0, 0, 0, 0);
        d.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        d.show();
    }
}
