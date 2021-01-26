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

public class ATEditParamActivity extends Activity{
	
	private ListView lv;
	MyAdapter mAdapter;
	/* 定义一个动态数组 */
	ArrayList<HashMap<String, Object>> listItem;
	SettingPreference settingPreference;
	
	//设置参数常量信息
		private final String[] SETTING = 
		{
				Define.AT_SAVE,
				Define.AT_SAVEAS,
				Define.AT_COPY,
				Define.AT_CUT,
				Define.AT_PASTE,
				Define.AT_SHARE,
				Define.AT_PRINT,
				Define.AT_SPELLCHECK,
				Define.AT_MULTIDOCCHANGE,
				Define.AT_QUICK_CLOSE_REVISEMODE,
				Define.AT_EDIT_REVISION,
				Define.AT_CURSOR_MODEL,
				Define.AT_CHANGE_COMMENT_USER,
				Define.AT_SHARE_PLAY,
				Define.AT_GRID_BACKBOARD,
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
		map1.put("ItemTitle", "保存");
		map1.put("ItemText", "是否允许保存");
		listItem.add(map1);
		
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		map2.put("id", j++);
		map2.put("ItemTitle", "另存为");
		map2.put("ItemText", "是否允许另存为");
		listItem.add(map2);
		
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		map3.put("id", j++);
		map3.put("ItemTitle", "复制");
		map3.put("ItemText", "是否允许复制");
		listItem.add(map3);
		
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		map4.put("id", j++);
		map4.put("ItemTitle", "剪切");
		map4.put("ItemText", "是否允许剪切");
		listItem.add(map4);
		
		HashMap<String, Object> map5 = new HashMap<String, Object>();
		map5.put("id", j++);
		map5.put("ItemTitle", "粘贴");
		map5.put("ItemText", "是否允许粘贴");
		listItem.add(map5);
		
		HashMap<String, Object> map10 = new HashMap<String, Object>();
		map10.put("id", j++);
		map10.put("ItemTitle", "共享");
		map10.put("ItemText", "是否允许共享");
		listItem.add(map10);
		
		HashMap<String, Object> map11 = new HashMap<String, Object>();
		map11.put("id", j++);
		map11.put("ItemTitle", "打印");
		map11.put("ItemText", "是否允许打印");
		listItem.add(map11);
		
		HashMap<String, Object> map12 = new HashMap<String, Object>();
		map12.put("id", j++);
		map12.put("ItemTitle", "拼写检查");
		map12.put("ItemText", "是否允许进行拼写检查");
		listItem.add(map12);
		
		HashMap<String, Object> map13 = new HashMap<String, Object>();
		map13.put("id", j++);
		map13.put("ItemTitle", "多文档切换");
		map13.put("ItemText", "是否允许多文档切换");
		listItem.add(map13);
		
		HashMap<String, Object> map14 = new HashMap<String, Object>();
		map14.put("id", j++);
		map14.put("ItemTitle", "快速关闭修订");
		map14.put("ItemText", "是否允许快速关闭修订");
		listItem.add(map14);
				
		HashMap<String, Object> map15 = new HashMap<String, Object>();
		map15.put("id", j++);
		map15.put("ItemTitle", "不限制接受他人修订");
		map15.put("ItemText", "是否限制修改不同名的修订记录");
		listItem.add(map15);		
		
		HashMap<String, Object> map16 = new HashMap<String, Object>();
		map16.put("id", j++);
		map16.put("ItemTitle", "光标输入模式（中广核需求）");
		map16.put("ItemText", "是否允许收到该事件后，设置字体属性");
		listItem.add(map16);	
		
		HashMap<String, Object> map17 = new HashMap<String, Object>();
		map17.put("id", j++);
		map17.put("ItemTitle", "允许修改批注作者名");
		map17.put("ItemText", "是否允许修改批注修订的作者名（即菜单栏上修改作者名按钮是否可用）");
		listItem.add(map17);	

		HashMap<String, Object> map18 = new HashMap<String, Object>();
		map18.put("id", j++);
		map18.put("ItemTitle", "共享播放");
		map18.put("ItemText", "允许共享播放");
		listItem.add(map18);
		
		HashMap<String, Object> map19 = new HashMap<String, Object>();
		map19.put("id", j++);
		map19.put("ItemTitle", "表格背板");
		map19.put("ItemText", "允许表格背板");
		listItem.add(map19);

		HashMap<String, Object> map0 = new HashMap<String, Object>();
		map0.put("id", j++);
		map0.put("ItemTitle", "文件路径");
		map0.put("ItemText", "设置编辑的文件路径");
		listItem.add(map0);
		
		return listItem;
	}
	
	
	/*
	 * 新建一个类继承BaseAdapter，实现视图与数据的绑定
	 */
	private class MyAdapter extends BaseAdapter 
	{
		private LayoutInflater mInflater;// 得到一个LayoutInfalter对象用来导入布局
		private boolean IsSelected[] = new boolean[16]; 
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
			IsSelected[0] = preference.getSettingParam(Define.AT_SAVE, true);
			IsSelected[1] = preference.getSettingParam(Define.AT_SAVEAS, true);
			IsSelected[2] = preference.getSettingParam(Define.AT_COPY, true);
			IsSelected[3] = preference.getSettingParam(Define.AT_CUT, true);
			IsSelected[4] = preference.getSettingParam(Define.AT_PASTE, true);
			IsSelected[5] = preference.getSettingParam(Define.AT_SHARE, true);
			IsSelected[6] = preference.getSettingParam(Define.AT_PRINT, true);
			IsSelected[7] = preference.getSettingParam(Define.AT_SPELLCHECK, true);
			IsSelected[8] = preference.getSettingParam(Define.AT_MULTIDOCCHANGE, true);
			IsSelected[9] = preference.getSettingParam(Define.AT_QUICK_CLOSE_REVISEMODE, true);
			IsSelected[10] = preference.getSettingParam(Define.AT_EDIT_REVISION, true);
			IsSelected[11] = preference.getSettingParam(Define.AT_CURSOR_MODEL, true);
			IsSelected[12] = preference.getSettingParam(Define.AT_CHANGE_COMMENT_USER, true);
			IsSelected[13] = preference.getSettingParam(Define.AT_SHARE_PLAY, true);
			IsSelected[14] = preference.getSettingParam(Define.AT_GRID_BACKBOARD, true);
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
     * 创建对话框
     */
	@Override
    protected Dialog onCreateDialog(int id) 
	{
        Dialog dialog=null;
        LayoutInflater inflater = LayoutInflater.from(ATEditParamActivity.this);  
        switch (id) 
        {
		    
        case 1:
        	final View ParamDialogView = inflater.inflate(R.layout.path_edit_dialog, null);  
       	 EditText editPath = (EditText)ParamDialogView.findViewById(R.id.editPath);
       	editPath.setText(settingPreference.getSettingParam(Define.AT_PATH, "/storage/sdcard0/"));

		    AlertDialog paramAlertDialog = new AlertDialog.Builder(this)  
		    	.setTitle("设置编辑的文件路径").setView(ParamDialogView)
		               .setPositiveButton("确定",  
		               new DialogInterface.OnClickListener() {  
		                   @Override                    
		                   public void onClick(DialogInterface dialog, int which) 
		                   {  
		                	   EditText editPath 	 = (EditText)ParamDialogView.findViewById(R.id.editPath);
		                	   if (editPath.getText().toString().length() == 0)
		           				{
		                		   Toast.makeText(ATEditParamActivity.this,"请输入参数", Toast.LENGTH_SHORT).show();
		                		   return;
		           				}
		   					   settingPreference.setSettingParam(Define.AT_PATH, 
		   							editPath.getText().toString());
	                		   Toast.makeText(ATEditParamActivity.this,"保存成功！", Toast.LENGTH_SHORT).show();
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
