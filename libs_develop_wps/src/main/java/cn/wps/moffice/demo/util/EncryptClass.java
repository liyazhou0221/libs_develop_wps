package cn.wps.moffice.demo.util;

/**
 * creator:fanguangcheng
 * date:2013.10.16
 * comment:主要是对文件数据的简单加密，使用异或对数据加密，
 * 同时可以识别某路径下的文件是否加密
 */
import android.os.RemoteException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.wps.moffice.client.OfficeInputStream;
import cn.wps.moffice.client.OfficeOutputStream;

public class EncryptClass
{
	final static private int HEAD_SIZE = 20;
	final static private int HEAD_FLAG = -1;
	final static private byte  KEY = 'z';
	private final static int	BUFFER_SIZE = 64 * 1024;

	/**
	 * 解密打开（如果文件加密）指定路径的文件，并将数据返回给output流
	 * @param path
	 * @param output
	 * @return
	 * @throws RemoteException
	 */
	
	public static int normalOpenFile(String path, OfficeOutputStream output) throws RemoteException
	{
		byte[] buffer = new byte[BUFFER_SIZE];
		FileInputStream fin = null;
		try
		{
			int count = 0;
			fin = new FileInputStream(new File(path));
			
	        while ((count = fin.read(buffer)) > 0)
	        {
	        	output.write(buffer, 0, count);
	        }
	        fin.close();
	        output.close();
        }
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
        }
			
		return 0;
	}
	
	/**
	 * 加密保存文件
	 * @param input
	 * @param path
	 * @return
	 * @throws RemoteException
	 */
	public static int normalSaveFile(OfficeInputStream input, String path) throws RemoteException
	{
		if (input == null)
			return -1;
		
		final byte[] buffer = new byte[BUFFER_SIZE];
		
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(path);

			int[] read = new int[1];
			while (input.read(buffer, read) >= 0)
			{
				if (read[0] <= 0)
					break;
				fos.write(buffer, 0, read[0]);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}
	
	
	/**解密方法
	 * 解密指定数据块的数据
	 * 其实加解密都是异或操作
	 * @param buffer
	 * @param length
	 */
	private static void encrypt(byte[] buffer, int length)
	{
		if (buffer == null || length <= 0)
			return;
		
		for (int i = 0; i < length; i++)
			buffer[i] = (byte) (buffer[i] ^ KEY);
	}
	
	
	/**加密方法
	 * 加密指定数据块的数据
	 * @param buffer
	 * @param length
	 */
	private static void decrypt(byte[] buffer, int length)
	{
		if (buffer == null || length <= 0)
			return;
		
		for (int i = 0; i < length; i++)
			buffer[i] = (byte) (buffer[i] ^ KEY);
	}
	
	public static boolean isEncryptFile(String path)
	{
		if (path == null)
			return false;
		File file = new File(path);
		if (!file.exists() || file.length() < HEAD_SIZE)
			return false;
		
		byte[] buf = new byte[HEAD_SIZE]; 
		try
		{
			FileInputStream fin = new FileInputStream(file);
			fin.read(buf);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		for (int i = 0; i < HEAD_SIZE; i++)
		{
			if(buf[i] != HEAD_FLAG)
				return false;
		}
		return true;
	}
	
	/**
	 * 解密打开（如果文件加密）指定路径的文件，并将数据返回给output流
	 * @param path
	 * @param output
	 * @return
	 * @throws RemoteException
	 */
	
	public static int encryptOpenFile(String path, OfficeOutputStream output) throws RemoteException
	{
		final boolean isEncrypt = isEncryptFile(path);

		byte[] buffer = new byte[1024 * 64];
		FileInputStream fin = null;
			try
			{
				int count = 0;
				fin = new FileInputStream(new File(path));
				if (isEncrypt)								//如果加密，则跳过文件头
					fin.read(new byte[HEAD_SIZE]);
				
		        while ((count = fin.read(buffer)) > 0)
		        {
		        	if (isEncrypt)
		        		decrypt(buffer, count);
		        	output.write(buffer, 0, count);
		        }
		        fin.close();
		        output.close();
	        }
			catch (IOException e)
			{
				e.printStackTrace();
				return -1;
	        }
			
		return 0;
	}
	
	/**
	 * 加密保存文件
	 * @param input
	 * @param path
	 * @return
	 * @throws RemoteException
	 */
	public static int encryptSaveFile(OfficeInputStream input, String path) throws RemoteException
	{
		if (input == null)
			return -1;
		
		final byte[] buffer = new byte[1024 * 64];
		
		final byte[] head_buf = new byte[HEAD_SIZE];
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(path);

			for (int i = 0; i < HEAD_SIZE; i++)	//写入文件头
				head_buf[i] = HEAD_FLAG;
			fos.write(head_buf);
			
			int[] read = new int[1];
			while (input.read(buffer, read) >= 0)
			{
				if (read[0] <= 0)
					break;
				encrypt(buffer, read[0]);		//加密数据块
				fos.write(buffer, 0, read[0]);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return -1;
		}
		
		return 0;
	}
}
