package cn.wps.moffice.demo.test;

import java.io.File;
/**
 * 暂时还没有用到
 * 功能:主要是返回目录下的WPS文件
 * @author kingsoft
 *
 */
public class AutoWPSFiles {
	static File[] tempFiles = null;
	static File[] tfiles = null;
	public static File[] getWPSFiles(File[] files){
		if (null == files || files.length == 0)
			return null;
		
		int num = 0;
		tempFiles = new File[files.length];
		for(int i = 0; i < files.length; i++)
		{
			if (IsWPSFile(files[i])){
				tempFiles[num] = files[i];
				num++;
			}
		}
		return tempFiles;
	}
  	/**
  	 * 判断是否是wps能打开的文件
  	 * @param file
  	 * @return
  	 */
  	private static boolean IsWPSFile(File file)
  	{
  		String end = file.getName().substring(file.getName().lastIndexOf(".") + 1,  
        		file.getName().length()).toLowerCase();  
  		
 		if (end.equals("doc") || end.equals("docx") || end.equals("wps") || end.equals("dot") || end.equals("wpt"))
  			return true;
  		
  		return false;
  	}

}
