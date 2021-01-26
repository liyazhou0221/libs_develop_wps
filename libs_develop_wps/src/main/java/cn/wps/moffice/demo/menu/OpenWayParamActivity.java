/**
 *	 文件名：OpenWayparamActivity.java
 * 	创建者:linyuancong & fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：设置打开方式参数
 */
package cn.wps.moffice.demo.menu;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.floatingview.service.FloatServiceTest;
import cn.wps.moffice.demo.floatingview.service.FloatingServiceHideView;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;


public class OpenWayParamActivity extends Activity {

	private ListView lv;
	MyAdapter mAdapter;
	/* 定义一个动态数组 */
	ArrayList<HashMap<String, Object>> listItem;
	SettingPreference settingPreference;
	private String wps_configString = "";
	//设置参数和打开模式的常量信息
	private final String[] SETTING = 
	{
			Define.FAIR_COPY
	};
	private final String[] WPS_OEPNMODE = 
		{
				Define.WPS_OPEN_AIDL,
				Define.WPS_OPEN_THIRD
		};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_manager);

		lv = (ListView) findViewById(R.id.lv);
		mAdapter = new MyAdapter(this);
		lv.setAdapter(mAdapter);
        
		//读取preference中的内容
		settingPreference = new SettingPreference(this);
		wps_configString = settingPreference.getSettingParam(Define.WPS_OPEN_MODE, "默认");
		for (int i = 0; i < WPS_OEPNMODE.length; i++)
        	if (wps_configString.equals(WPS_OEPNMODE[i]))
        	{
        		wps_configString = getResources().getStringArray(R.array.wpsmode)[i];
        				break;
        	}
	}

	/* 添加一个得到数据的方法，方便使用
	 *  id:
	 *  ItemTitle:
	 *  ItemText:
	 *  value:
	*/
	private ArrayList<HashMap<String, Object>> getDate() {

		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		int j = 1;
		/* 添加设置列表信息 */

		
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("id", j++);
		map1.put("ItemTitle", "打开WPS的方式");
		map1.put("ItemText", wps_configString);
		listItem.add(map1);
		
		return listItem;
	}

	
	/*
	 * 新建一个类继承BaseAdapter，实现视图与数据的绑定
	 */
	private class MyAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
		private boolean IsSelected[] = new boolean[8]; 
		SettingPreference preference;

		/* 构造函数 */
		public MyAdapter(Context context) 
		{
			this.mInflater = LayoutInflater.from(context);
			preference = new SettingPreference(context);
			setIsSelected();			//读取之前设置信息，显示到界面
		}

		@Override
		public int getCount() 
		{
			return getDate().size();// 返回数组的长度
		}

		@Override
		public Object getItem(int position) 
		{
			return getDate().get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return Long.valueOf(getDate().get(position).get("id").toString());
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{
			final ViewHolder holder;
			convertView = mInflater.inflate(R.layout.item, null);
			holder = new ViewHolder();
			/* 得到各个控件的对象 */
			holder.title = (TextView) convertView.findViewById(R.id.ItemTitle);
			holder.text = (TextView) convertView.findViewById(R.id.ItemText);
			holder.bt = (CheckBox) convertView.findViewById(R.id.ItemButton);
			convertView.setTag(holder);// 绑定ViewHolder对象
			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
			holder.title.setText(getDate().get(position).get("ItemTitle").toString());
			holder.text.setText(getDate().get(position).get("ItemText").toString());
			if ( position < getCount() - 1)
			{
				holder.bt.setVisibility(1);
				holder.bt.setChecked(IsSelected[position]);//显示之前的设置信息
			}
			/* 为CheckBox添加点击事件 */
			convertView.setOnClickListener(new OnClickListener() 
			{
				@SuppressWarnings("deprecation")
				@Override
				public void onClick(View v) 
				{
					if (position < getCount() - 1)
					{
						holder.bt.performClick();
						preference.setSettingParam(SETTING[position], holder.bt.isChecked());
					}
					else
					{
						showDialog(1);
					}
				}
			});
			return convertView;
		}
		
		/**
		 * 读取preference的信息来判定参数是否设定
		 */
		private void setIsSelected() 
		{
			IsSelected[0] = preference.getSettingParam(Define.FAIR_COPY, false);
		}
	}

	/* 存放控件 */
	public final class ViewHolder 
	{
		public TextView title;
		public TextView text;
		public CheckBox bt;
	}
	
	/**
     * 创建单选按钮对话框
     */
	@Override
    protected Dialog onCreateDialog(int id) 
	{
        Dialog dialog=null;
        switch (id) 
        {
        case 1:
        	
        	int i = 0;  //获取之前的打开模式
        	String wps_openMode = settingPreference.getSettingParam(Define.WPS_OPEN_MODE, Define.WPS_OPEN_THIRD);
        	for (i = 0; i < WPS_OEPNMODE.length && !wps_openMode.equals(WPS_OEPNMODE[i]); i++);
        	
            Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("wps打开模式选择");
            builder1.setSingleChoiceItems(R.array.wpsmode, i, new DialogInterface.OnClickListener() 
            {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					wps_configString = getResources().getStringArray(R.array.wpsmode)[which];
					mAdapter.notifyDataSetChanged();
					settingPreference.setSettingParam(Define.WPS_OPEN_MODE, WPS_OEPNMODE[which]);
					if (WPS_OEPNMODE[which].equals(Define.WPS_OPEN_THIRD))
					{//取消绑定的floatservice
						  Intent service = new Intent();
						  service.setClass(getApplicationContext(), FloatServiceTest.class);
						  stopService(service);
						  
						//取消绑定的floatserviceHideView
						  Intent service2 = new Intent();
						  service2.setClass(getApplicationContext(), FloatingServiceHideView.class);
						  stopService(service2);
					}
					dialog.dismiss();
					if (WPS_OEPNMODE[which].equals(Define.WPS_OPEN_AIDL) && !isFloatWindowOpAllowed(getApplicationContext())) {
						AlertDialog opAlertDialog = new AlertDialog.Builder(OpenWayParamActivity.this)
						.setTitle("注意")
						.setMessage("检测到系统禁止弹出悬浮窗，将导致无法正常使用AIDL模式，请到系统设置中开启应用的悬浮窗权限")
						.setPositiveButton("OK", null)
						.create();
						opAlertDialog.show();
					}
				}
			});

            //创建一个单选按钮对话框
            dialog=builder1.create();
            break;
     }
        return dialog;
    }
	
	/**
	 * 判断demo是否获得悬浮窗权限，4.4以下系统判断方法不同
	 * @param context
	 * @return
	 */
	public static boolean isFloatWindowOpAllowed(Context context) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, 24);//在AppOpsManager中24表示悬浮窗权限
        } else {
            //如果&第28位所得值为1则该位置被置为1，悬浮窗打开
        	//MIUI系统开启悬浮窗后需要将demo进程杀掉重启才生效，否则返回值一直为false
        	return (context.getApplicationInfo().flags & 1<<27) == 1<<27;
        }
    }
    
    protected static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService("appops");
            try {
                Object object = invokeMethod(manager, "checkOp", op, Binder.getCallingUid(), context.getPackageName());
                return AppOpsManager.MODE_ALLOWED == (Integer) object;
            } catch (Exception e) {
                Log.e("CheckAppOps", e.toString());
            }
        } else {
            Log.e("CheckAppOps", "Below API 19 cannot invoke!");
        }
        return false;
    }
    
    public static Object invokeMethod(AppOpsManager manager, String method, int op, int uid, String packageName){
        Class<?> c = manager.getClass();
        try {
            Class<?>[] classes = new Class[] {int.class, int.class, String.class};
            Object[] x2 = {op, uid, packageName};
            Method m = c.getDeclaredMethod(method, classes);
            return m.invoke(manager, x2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
	
}