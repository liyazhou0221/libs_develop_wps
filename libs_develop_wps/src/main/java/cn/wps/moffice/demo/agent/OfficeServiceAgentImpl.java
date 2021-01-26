package cn.wps.moffice.demo.agent;

import cn.wps.moffice.agent.OfficeServiceAgent;
import cn.wps.moffice.demo.client.MOfficeClientService;
import cn.wps.moffice.demo.test.AutoTestParam;
import android.os.RemoteException;

public class OfficeServiceAgentImpl extends OfficeServiceAgent.Stub 
{
	private static final String JSON_DATA =
		"[" +
		"{ \"name\" : \""+ MOfficeClientService.CLASS_CLINT+"\"," +
		" \"type\" : \"Package-ID\",\"id\" : \"cn.wps.moffice.client\", " +
		"\"Security-Level\" : \"Full-access\", \"Authorization\"  : \"abxxdsewrwsds3232ss\" }," +
		"]";
	private static final String JSON_DATA_EMPTY = "["  + "]";
	protected AgentMessageService service = null;
	private boolean mIsValidPackageName = true;
	
	public OfficeServiceAgentImpl( AgentMessageService service ) 
	{
		this.service = service;
	}
	
	/**
	 * 该方法的结果，根据isValidPackage来决定
	 */
	public int getClients( String[] clients, int[] expiredDays ) throws RemoteException 
	{
		clients[ 0 ] = mIsValidPackageName ? JSON_DATA : JSON_DATA_EMPTY;
		expiredDays[ 0 ] = 1;
		setAutoTestParam();			//加解密自动化测试的初始化
		return 0;
	}

	/**
	 * 判断可变的第三方应用包名,在wps内部，每次调用getclients之前，都会调用该方法，以确保验证包名
	 * @param originalPackage 根据渠道写入到wps中的定制包名
	 * @param realThirdPackage 可变的第三方应用包名
	 * @return
	 * @throws RemoteException
	 */
	public boolean isValidPackage(String originalPackage, String realThirdPackage) throws RemoteException
	{//此段代码是某些企业的特殊需求，可以忽略
//		mIsValidPackageName = false;
//		if (originalPackage.equals(service.getPackageName()) && realThirdPackage.equals("cn.wps.moffice"))
//		{
//			mIsValidPackageName = true;
//			return true;
//		}
		return false;
	}

	/**
	 * 用来设置加解密测试的参数的初始化
	 */
	private void setAutoTestParam()
	{
		AutoTestParam.IsOpenCorrect = false;		
	}
}
