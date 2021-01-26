/**
 *	 文件名：SettingPreference.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：负责将某些参数写入到preference中 如 文档打开方式、文档路径等
 */
package cn.wps.moffice.demo.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingPreference 
{
	public static final String PREFS_NAME = "MyPrefsFile";			//用于存取参数的文件名

//	Context context;
	SharedPreferences settings;
	SharedPreferences sharedPreferences;
	SharedPreferences.Editor editor;

	public SettingPreference(Context context) 
	{
//		this.context = context;
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		sharedPreferences = context.getSharedPreferences(PREFS_NAME,  Context.MODE_PRIVATE);
		editor = settings.edit();
	}
	
	/**
	 * 向文件中写入参数
	 * @return
	 */
	public boolean setSettingParam(String key, boolean value)
	{
		 editor.putBoolean(key, value);
		 editor.commit();
		 return false;
	}
	
	public boolean setSettingParam(String key, String value)
	{
		 editor.putString(key, value);
		 editor.commit();
		 return false;
	}

	public boolean setSettingParam(String key, int value)
	{
		 editor.putInt(key, value);
		 editor.commit();
		 return false;
	}
	
	public boolean setSettingParam(String key, float value)
	{
		 editor.putFloat(key, value);
		 editor.commit();
		 return false;
	}
	
	/**
	 * 获得设置的参数
	 * @param key
	 * @param defValue
	 * @return
	 */
	public boolean getSettingParam(String key, boolean defValue)
	{
		return sharedPreferences.getBoolean(key ,defValue);
	}
	
	public String getSettingParam(String key, String defValue)
	{
		return sharedPreferences.getString(key ,defValue);
	}
	
	public float getSettingParam(String key, float defValue)
	{
		return sharedPreferences.getFloat(key ,defValue);
	}
	
	public int getSettingParam(String key, int defValue)
	{
		return sharedPreferences.getInt(key ,defValue);
	}
	
}
