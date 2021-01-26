/**
 *	 文件名：SortByName.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：将文件名按照常用文件浏览器展示文件的格式排序
 */
package cn.wps.moffice.demo.fileManager;
import java.io.File;

public class SortByName 
{

	File[] tempFiles;
	File[] sortedFiles;
	/**
	 * 排序文件，文件夹在前
	 * @param files
	 * @return
	 */
	public File[] sort(File[] files)
	{
		if (null == files || files.length == 0)
			return null;
		
		int i = 0, j = 0;
		tempFiles = noHideFile(files);
		int length = tempFiles.length;
		sortedFiles = new File[length];         
		File sortedName,tempName;
		
		sortedFiles[0] = tempFiles[0];
		for(i = 0; i < length; i++)
		{
			tempName = tempFiles[i];
			for (j = 0; j < i ; j++)
			{
				sortedName = sortedFiles[j];
				if (!compareStr(sortedName.getName(), tempName.getName()))
				{
					continue;
				}
				else
					break;
			}
			for (int flag = i; flag > j ; flag--)
			{
				sortedFiles[flag] = sortedFiles[flag - 1];
			}
			
			sortedFiles[j] = tempFiles[i];
		}
			sortedFiles	= sortFile(sortedFiles);
		return sortedFiles;
	}

	/**
	 * 排序文件
	 * @param files
	 * @return
	 */
	public File[] sortFile(File[] files)
	{
		if (files == null)
			return null;
		int length = files.length;
		File[] sortedFile = new File[length];
		int num = 0;
		for (int i = 0; i < length; i++)
		{	
			if (files[i].isDirectory())
			{
				sortedFile[num] = files[i];
				num++;
			}
		}
		for (int i = 0; i < length; i++)
		{	
			if (files[i].isFile())
			{
				sortedFile[num] = files[i];
				num++;
			}
		}
		return sortedFile;
	}
		
	/**
	 * 比较str1和str的大小，字符串的关系依次是：数字<汉字<字母
	 * @param str1
	 * @param str2
	 * @return 如果str1大就返回true，否则返回false
	 */
	private boolean compareStr(String str1, String str2)
	{
		if (str1 == null || str1.length() == 0 )
			return false;
		if (str2 == null || str2.length() == 0)
			return true;
		boolean str1_Letter,str1_Num;
		boolean str2_Letter,str2_Num;
		str1_Letter = matchLetter(str1);
		str1_Num = matchNum(str1);
		str2_Letter = matchLetter(str2);
		str2_Num = matchNum(str2);

		if (str1_Num)//str1为数字开头
		{
			if (!str2_Num)//str2不是数字开头
				return false;
			else		  //str2也是数字开头
			{
				if (str1.charAt(0) == str2.charAt(0))
				{
					String str1_cut = str1.substring(1, str1.length());
					String str2_cut = str2.substring(1, str2.length());
					return compareStr(str1_cut,str2_cut);
				}
				return str1.charAt(0) > str2.charAt(0);
			}
		}
		else if(!str1_Letter)//如果str既不是数字开头也不是字母开头，那应该是汉字开头
		{
			if (str2_Num)
				return true;
			if (str2_Letter)
				return false;
			if (str1.charAt(0) == str2.charAt(0))
			{//如果都是汉字开头
				String str1_cut = str1.substring(1, str1.length());
				String str2_cut = str2.substring(1, str2.length());
				return compareStr(str1_cut,str2_cut);
			}
			return str1.charAt(0) > str2.charAt(0);
		}
		else if(str1_Letter)
		{
			if (str2_Num)
				return true;
			if (!str2_Letter)
				return true;
			if (str1.charAt(0) == str2.charAt(0))
			{
				String str1_cut = str1.substring(1, str1.length());
				String str2_cut = str2.substring(1, str2.length());
				if (str1_cut == null || str1_cut.length() == 0 )
					return false;
				if (str2_cut == null || str2_cut.length() == 0)
					return true;
				return compareStr(str1_cut,str2_cut);
			}
			return str1.toLowerCase().charAt(0) > str2.toLowerCase().charAt(0);
		}
		return false;
	}
	
	/**
	 * 判断该字符串是否以字母开头
	 */
	public boolean matchLetter(String str)
	{

		if (str == null || str.length() == 0)
			return false;

		char c = str.charAt(0);
		if (c >= 'A' && c <= 'z')
			return true;
		return false;
	}
	
	/**
	 * 判断字符串是否匹配数字开头
	 */
	public boolean matchNum(String str)
	{
		if (str == null || str.length() == 0)
			return false;
		
		char c = str.charAt(0);
		if (c >= '0' && c <= '9')
			return true;
		
		return false;
	}
	
	/**
	 * 从文件列表中去除隐藏的文件
	 * @param files
	 * @return
	 */
	public File[] noHideFile(File[] files)
	{
		if (files == null || files.length == 0)
			return null;
		int length = files.length;
		int hideNum = hideFileNum(files); 
		File[] noHideFile = new File[length - hideNum];
		int num = 0;
		for (int i = 0; i < length; i++)
		{
			if (files[i].getName().startsWith("."))
				continue;
			else
			{
				noHideFile[num] = files[i];
				num++;
			}
		}
		return noHideFile;
	}
			
	/**
	 * 检测文件列表中有多少隐藏文件及文件夹
	 * @param files
	 * @return
	 */
	public int hideFileNum(File[] files)
	{
		if (files == null)
			return 0;
		int num = 0;
		int length = files.length;
		for (int i = 0; i < length; i++)
		{
			if (files[i].getName().startsWith("."))
				num++;
		}

		return num;
	}
}
