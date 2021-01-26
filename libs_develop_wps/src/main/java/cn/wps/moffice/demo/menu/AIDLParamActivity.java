/**
 *	 文件名：AIDLParamActivity.java
 * 	创建者:linyuancong & fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：设置以AIDL远程调用方式参数
 */
package cn.wps.moffice.demo.menu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;


public class AIDLParamActivity extends Activity {

	private ListView lv;
	MyAdapter mAdapter;
	/* 定义一个动态数组 */
	ArrayList<HashMap<String, Object>> listItem;
	SettingPreference settingPreference;
	//设置参数和打开模式的常量信息
	private final String[] SETTING = 
	{
			Define.FAIR_COPY,
			Define.BACK_KEY_DOWN,
			Define.HOME_KEY_DOWN,
			Define.IS_SHOW_VIEW
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
		map1.put("ItemTitle", "清稿");
		map1.put("ItemText", "是否使用清稿功能");
		listItem.add(map1);
		
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", j++);
		map2.put("ItemTitle", "监听BackKey");
		map2.put("ItemText", "是否监听BackKey并发广播");
		listItem.add(map2);
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("id", j++);
		map3.put("ItemTitle", "监听HomeKey");
		map3.put("ItemText", "是否监听HomeKey并发广播");
		listItem.add(map3);
		
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("id", j++);
		map4.put("ItemTitle", "显示文档界面");
		map4.put("ItemText", "是否显示wps界面来操作");
		listItem.add(map4);
		
		HashMap<String, Object> map0 = new HashMap<String, Object>();
		map0.put("id", j++);
		map0.put("ItemTitle", "文件参数设置");
		map0.put("ItemText", "设置批注作者。");
		listItem.add(map0);
		
		return listItem;
	}

	
	/*
	 * 新建一个类继承BaseAdapter，实现视图与数据的绑定
	 */
	private class MyAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
		private boolean IsSelected[] = new boolean[5]; 
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
				holder.bt.setVisibility(View.VISIBLE);
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
			IsSelected[0] = preference.getSettingParam(Define.FAIR_COPY, true);
			IsSelected[1] = preference.getSettingParam(Define.BACK_KEY_DOWN, false);
			IsSelected[2] = preference.getSettingParam(Define.HOME_KEY_DOWN, false);
			IsSelected[3] = preference.getSettingParam(Define.IS_SHOW_VIEW, true);
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
        LayoutInflater inflater = LayoutInflater.from(AIDLParamActivity.this);  
        switch (id) 
        {
        case 1:
        	
        	final View ParamDialogView = inflater.inflate(R.layout.param_aidl_dialog, null);  
        	EditText CommentAuthor  = (EditText)ParamDialogView.findViewById(R.id.CommentAuthor);
        	CommentAuthor.setText(settingPreference.getSettingParam(Define.USER_NAME,""));
		    AlertDialog paramAlertDialog = new AlertDialog.Builder(this)  
		    	.setTitle("设置文件参数").setView(ParamDialogView)
		               .setPositiveButton("确定",  
		               new DialogInterface.OnClickListener() {  
		                   @Override                    
		                   public void onClick(DialogInterface dialog, int which) 
		                   {  
		                	   EditText CommentAuthor  = (EditText)ParamDialogView.findViewById(R.id.CommentAuthor);
		                	   if ( CommentAuthor.getText().toString().length() == 0)
		           				{
		                		   Toast.makeText(AIDLParamActivity.this,"请输入参数", Toast.LENGTH_SHORT).show();
		                		   return;
		           				}

		   					   settingPreference.setSettingParam(Define.USER_NAME, 
		   							   CommentAuthor.getText().toString());
	                		   Toast.makeText(AIDLParamActivity.this,"保存成功！", Toast.LENGTH_SHORT).show();
		   					   return;
		                   }  
		               }).setNegativeButton("取消",  
		               new DialogInterface.OnClickListener() {  
		                   @Override  
		                   public void onClick(DialogInterface dialog, int which) 
		                   {
		                   }  
		               }).create();
		    paramAlertDialog.show();  
		    break;
     }
        return dialog;
    }
	
}