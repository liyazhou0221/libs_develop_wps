/**
 *	 文件名：MyListViewBase.java
 * 	创建者:linyuancong & fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：用户设置操作
 */
package cn.wps.moffice.demo.menu;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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


public class ListViewParamActivity extends Activity {

	private ListView lv;
	private String open_configString = "";
	MyAdapter mAdapter;
	/* 定义一个动态数组 */
	ArrayList<HashMap<String, Object>> listItem;
	SettingPreference settingPreference;
	//设置参数和打开模式的常量信息
	private final String[] SETTING = 
	{
			Define.SEND_SAVE_BROAD, 
			Define.SEND_CLOSE_BROAD, 
			Define.IS_CLEAR_TRACE, 
			Define.IS_CLEAR_FILE, 
			Define.AUTO_JUMP,	
			Define.IS_VIEW_SCALE,
			Define.ENTER_REVISE_MODE,
			Define.CACHE_FILE_INVISIBLE,
			Define.ENCRYPT_FILE
	};
	private final String[] OPEN_MODE = 
	{
			Define.READ_ONLY, 
			Define.NORMAL, 
			Define.READ_MODE,
			Define.SAVE_ONLY,
			Define.EDIT_MODE
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
        open_configString = settingPreference.getSettingParam(Define.OPEN_MODE, "自动选择");
        for (int i = 0; i < OPEN_MODE.length; i++)
        	if (open_configString.equals(OPEN_MODE[i]))
        	{
        		open_configString = getResources().getStringArray(R.array.readmode)[i];
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
		map1.put("ItemTitle", "文件保存广播");
		map1.put("ItemText", "保存文件时是否发送广播");
		listItem.add(map1);
		
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", j++);
		map2.put("ItemTitle", "关闭文件发送广播");
		map2.put("ItemText", "关闭文件时是否发送广播");
		listItem.add(map2);
		
//		HashMap<String, Object> map3 = new HashMap<String, Object>();
//		map3.put("id", j++);
//		map3.put("ItemTitle", "清空临时文件");
//		map3.put("ItemText", "关闭文件时是否请空临时文件");
//		listItem.add(map3);
		
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("id", j++);
		map4.put("ItemTitle", "删除使用记录");
		map4.put("ItemText", "关闭文件时是否删除使用记录");
		listItem.add(map4);
		
		HashMap<String, Object> map5 = new HashMap<String, Object>();
		map5.put("id", j++);
		map5.put("ItemTitle", "删除打开文件");
		map5.put("ItemText", "关闭文件时是否删除打开的文件");
		listItem.add(map5);
		
		HashMap<String, Object> map6 = new HashMap<String, Object>();
		map6.put("id", j++);
		map6.put("ItemTitle", "自动跳转");
		map6.put("ItemText", "是否自动跳转到上次查看的进度");
		listItem.add(map6);
		
		HashMap<String, Object> map7 = new HashMap<String, Object>();
		map7.put("id", j++);
		map7.put("ItemTitle", "使用上次缩放大小");
		map7.put("ItemText", "是否使用文件上次查看的视图的缩放");
		listItem.add(map7);
		
		
		HashMap<String, Object> map_Revise = new HashMap<String, Object>();
		map_Revise.put("id", j++);
		map_Revise.put("ItemTitle", "以修订模式打开");
		map_Revise.put("ItemText", "是否打开文档即进入修订模式");
		listItem.add(map_Revise);
		
		
		HashMap<String, Object> map_CacheInvisible = new HashMap<String, Object>();
		map_CacheInvisible.put("id", j++);
		map_CacheInvisible.put("ItemTitle", "缓存文件不可见");
		map_CacheInvisible.put("ItemText", "打开文档生成的缓存文件不可见");
		listItem.add(map_CacheInvisible);
		
		HashMap<String, Object> map_EncryptFile = new HashMap<String, Object>();
		map_EncryptFile.put("id", j++);
		map_EncryptFile.put("ItemTitle", "加密方式操作文档");
		map_EncryptFile.put("ItemText", "加密保存文件，可以打开加密的文件");
		listItem.add(map_EncryptFile);
		
		HashMap<String, Object> map8 = new HashMap<String, Object>();
		map8.put("id", j++);
		map8.put("ItemTitle", "文件参数设置");
		map8.put("ItemText", "第三方包名、文件保存路径、批注作者。");
		listItem.add(map8);
		
		HashMap<String, Object> map9 = new HashMap<String, Object>();
		map9.put("id", j++);
		map9.put("ItemTitle", "自定义展示参数设置");
		map9.put("ItemText", "点击设置缩放、跳转、横纵坐标等参数。");
		listItem.add(map9);
		
		
		//最后一个
		HashMap<String, Object> map0 = new HashMap<String, Object>();
		map0.put("id", j++);
		map0.put("ItemTitle", "打开文件的模式");
		map0.put("ItemText", open_configString);
		listItem.add(map0);
		
		return listItem;
	}

	
	/*
	 * 新建一个类继承BaseAdapter，实现视图与数据的绑定
	 */
	private class MyAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
		private boolean IsSelected[] = new boolean[9]; 
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
			if ( position < getCount() - 3)
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
					if (position < getCount() - 3)
					{
						holder.bt.performClick();
						preference.setSettingParam(SETTING[position], holder.bt.isChecked());
					}
					else
					{
						showDialog(getCount() - position);
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
			IsSelected[0] = preference.getSettingParam(Define.SEND_SAVE_BROAD, false);
			IsSelected[1] = preference.getSettingParam(Define.SEND_CLOSE_BROAD, false);
			IsSelected[2] = preference.getSettingParam(Define.IS_CLEAR_TRACE, false);
			IsSelected[3] = preference.getSettingParam(Define.IS_CLEAR_FILE, false);
			IsSelected[4] = preference.getSettingParam(Define.AUTO_JUMP, false);
			IsSelected[5] = preference.getSettingParam(Define.IS_VIEW_SCALE, false);
			IsSelected[6] = preference.getSettingParam(Define.ENTER_REVISE_MODE, false);
			IsSelected[7] = preference.getSettingParam(Define.CACHE_FILE_INVISIBLE, false);
			IsSelected[8] = preference.getSettingParam(Define.ENCRYPT_FILE, false);
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
        LayoutInflater inflater = LayoutInflater.from(ListViewParamActivity.this);  
        switch (id) 
        {
        
        case 1:
        	int j = 0;  //获取之前的打开模式
        	String openMode = settingPreference.getSettingParam(Define.OPEN_MODE, Define.NORMAL);
        	for (j = 0; j < OPEN_MODE.length && !openMode.equals(OPEN_MODE[j]); j++);
        	
            Builder builder2 = new AlertDialog.Builder(this);
            builder2.setTitle("模式选择");
            builder2.setSingleChoiceItems(R.array.readmode, j, new DialogInterface.OnClickListener() 
            {
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
					String hoddy = getResources().getStringArray(R.array.readmode)[which];
					open_configString = hoddy;
					mAdapter.notifyDataSetChanged();
					settingPreference.setSettingParam(Define.OPEN_MODE, OPEN_MODE[which]);
					dialog.dismiss();
				}
			});

            //创建一个单选按钮对话框
            dialog=builder2.create();
            break;
        case 2:
        	
		    final View DialogView = inflater.inflate(R.layout.dialog, null);  
		    
		    EditText ViewProgress = (EditText)DialogView.findViewById(R.id.ViewProgress);
     	   EditText ViewScale 	 = (EditText)DialogView.findViewById(R.id.ViewScale);
     	   EditText ViewScrollX  = (EditText)DialogView.findViewById(R.id.ViewScrollX);
     	   EditText ViewScrollY  = (EditText)DialogView.findViewById(R.id.ViewScrollY);
     	  
     	  ViewProgress.setText(settingPreference.getSettingParam(Define.VIEW_PROGRESS, 0.0f)+"");
     	  ViewScale.setText(settingPreference.getSettingParam(Define.VIEW_SCALE, 1.0f)+"");
     	  ViewScrollX.setText(settingPreference.getSettingParam(Define.VIEW_SCROLL_X,0)+"");
     	 ViewScrollY.setText(settingPreference.getSettingParam(Define.VIEW_SCROLL_Y,0)+"");
     	   
		    AlertDialog alertDialog = new AlertDialog.Builder(this)  
		    	.setTitle("设置展示参数").setView(DialogView)
		               .setPositiveButton("确定",  
		               new DialogInterface.OnClickListener() {  
		                   @Override  
		                   public void onClick(DialogInterface dialog, int which) 
		                   {  

		                	   EditText ViewProgress = (EditText)DialogView.findViewById(R.id.ViewProgress);
		                	   EditText ViewScale 	 = (EditText)DialogView.findViewById(R.id.ViewScale);
		                	   EditText ViewScrollX  = (EditText)DialogView.findViewById(R.id.ViewScrollX);
		                	   EditText ViewScrollY  = (EditText)DialogView.findViewById(R.id.ViewScrollY);

		                	   if (ViewProgress.getText().toString().length() == 0
		                			   || ViewScale.getText().toString().length() == 0
		                			   || ViewScrollX.getText().toString().length() == 0
		                			   || ViewScrollY.getText().toString().length() == 0)
		           				{
		                		   Toast.makeText(ListViewParamActivity.this,"请输入参数", Toast.LENGTH_SHORT).show();
		                		   return;
		           				}
		   					   settingPreference.setSettingParam(Define.VIEW_PROGRESS, 
		   							   Float.parseFloat(ViewProgress.getText().toString()));
		   					   settingPreference.setSettingParam(Define.VIEW_SCALE, 
		   							   Float.parseFloat(ViewScale.getText().toString()));
		   					   settingPreference.setSettingParam(Define.VIEW_SCROLL_X, 
		   							   Integer.parseInt(ViewScrollX.getText().toString()));
		   					   settingPreference.setSettingParam(Define.VIEW_SCROLL_Y, 
		   							Integer.parseInt(ViewScrollY.getText().toString()));
		   					   return;
		                   }  
		               }).setNegativeButton("取消",  
		               new DialogInterface.OnClickListener() {  
		                   @Override  
		                   public void onClick(DialogInterface dialog, int which) 
		                   {
		                   }  
		               }).create();// 创建   
		       alertDialog.show();  
		    break;
		    
        case 3:
        	final View ParamDialogView = inflater.inflate(R.layout.param_dialog, null);  
       	 EditText ThirdPackage = (EditText)ParamDialogView.findViewById(R.id.ThirdPackage);
       	 ThirdPackage.setText(settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName()));
       	EditText SavePath 	 = (EditText)ParamDialogView.findViewById(R.id.SavePath);
       	SavePath.setText(settingPreference.getSettingParam(Define.SAVE_PATH, ""));
       	 EditText CommentAuthor  = (EditText)ParamDialogView.findViewById(R.id.CommentAuthor);
    	CommentAuthor.setText(settingPreference.getSettingParam(Define.USER_NAME,""));
		    AlertDialog paramAlertDialog = new AlertDialog.Builder(this)  
		    	.setTitle("设置文件参数").setView(ParamDialogView)
		               .setPositiveButton("确定",  
		               new DialogInterface.OnClickListener() {  
		                   @Override                    
		                   public void onClick(DialogInterface dialog, int which) 
		                   {  
		                	   EditText ThirdPackage = (EditText)ParamDialogView.findViewById(R.id.ThirdPackage);
		                	   EditText SavePath 	 = (EditText)ParamDialogView.findViewById(R.id.SavePath);
		                	   EditText CommentAuthor  = (EditText)ParamDialogView.findViewById(R.id.CommentAuthor);
		                	   if (ThirdPackage.getText().toString().length() == 0
		                			 
		                			   && CommentAuthor.getText().toString().length() == 0)
		           				{
		                		   Toast.makeText(ListViewParamActivity.this,"请输入参数", Toast.LENGTH_SHORT).show();
		                		   return;
		           				}
		   					   settingPreference.setSettingParam(Define.THIRD_PACKAGE, 
		   							   ThirdPackage.getText().toString());
		   					   settingPreference.setSettingParam(Define.SAVE_PATH, 
		   							   SavePath.getText().toString());
		   					   settingPreference.setSettingParam(Define.USER_NAME, 
		   							   CommentAuthor.getText().toString());
	                		   Toast.makeText(ListViewParamActivity.this,"保存成功！", Toast.LENGTH_SHORT).show();
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