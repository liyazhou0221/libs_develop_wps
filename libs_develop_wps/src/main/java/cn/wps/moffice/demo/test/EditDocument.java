package cn.wps.moffice.demo.test;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.doc.Document;

public class EditDocument extends Service
{

	private OfficeService mService;
	private Document mDoc = null;
	
	public EditDocument ()
	{
		
	}
	
	@Override
	public void onCreate() 
	{
		super.onCreate();
		bindOfficeService();
	}
	
	public boolean openFile(String path)
	{
		if (mService == null) 
		{
			if (!bindOfficeService())
				return false;
		}
		
		try 
		{
			mDoc = mService.openWordDocument(path, "",null);
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
			mDoc = null;
			return false;
		}
		return true;
	}
	
	public void closeFile()
	{
		if(mDoc != null)
		{
			try 
			{
				mDoc.closeHandWriteComment();
				
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * connection of binding
	 */
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = OfficeService.Stub.asInterface(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
	};
	private static final String OFFICE_SERVICE_ACTION = "cn.wps.moffice.service.OfficeService";
	private boolean bindOfficeService() {
		// bind service
		final Intent intent = new Intent(OFFICE_SERVICE_ACTION);
		intent.putExtra("DisplayView", true);
		if (!bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
			unbindService(connection);
			return false;
		}
		return true;
	}


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
