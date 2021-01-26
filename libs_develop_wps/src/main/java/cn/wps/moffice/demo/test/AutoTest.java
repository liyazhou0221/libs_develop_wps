/**
 *	 文件名：AutoTest.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.8.5
 * 	作用：实现加解密的自动化测试
 */
package cn.wps.moffice.demo.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.doc.Document;
import cn.wps.moffice.service.doc.SaveFormat;
import cn.wps.moffice.service.doc.WrapType;


public class AutoTest extends Activity implements OnClickListener
{
	private Button btnOpenDoc;
	private Button btnSaveAsDoc;
	private Button btnAddText;
	private Button btnAddPicToInline;
	private Button btnAddShapPic;
	private Button btnHandWriter;
	private Button btnPasteText;
	private Button btnTestOpen;
	private TextView txtTestPath;
	private TextView txtPicPath;
	private TextView txtSaveAsPath;
	private TextView txtResultPath;
	
	private Map<Integer, String> errorLog = null;
	private final static String LOGP_ATH = "/mnt/sdcard/测试缓存加密/original";
	private final static String SAVE_PATH = "/mnt/sdcard/测试缓存加密/save";
	private final static String PIC_PATH = "/storage/sdcard0/DCIM/ico.png";
	private final static String RESULT_PATH = "/mnt/sdcard/测试缓存加密/result";
	private Bitmap bitmap = null;
	private OfficeService mService;
	private Document mDoc = null;
	private static boolean isLoadOk = false;//判断文档是否加载完毕
	private int testId = 0;
	private String saveName = "";
	private SettingPreference settingPreference;
	File[] wpsFiles;				// 记录当前路径下的所有wps的文件数组
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.autotest);
		this.settingPreference = new SettingPreference(this);
        bindOfficeService();
        initView();
	
		
	}

	private void initView(){
		btnOpenDoc = (Button)findViewById(R.id.btnOpenDoc);
		btnSaveAsDoc = (Button)findViewById(R.id.btnSaveAsDoc);
		btnAddPicToInline = (Button)findViewById(R.id.btnAddPicToInline);
		btnAddShapPic = (Button) findViewById(R.id.btnAddShapPic);
		btnAddText = (Button) findViewById(R.id.btnAddText);
		btnHandWriter = (Button) findViewById(R.id.btnHandWrite);
		btnPasteText = (Button) findViewById(R.id.btnPasteText);
		txtPicPath = (TextView) findViewById(R.id.txtPicPath);
		txtSaveAsPath = (TextView) findViewById(R.id.txtSaveAsPath);
		txtTestPath = (TextView) findViewById(R.id.txtTestPath);
		txtResultPath = (TextView) findViewById(R.id.txtResultPath);
		btnTestOpen = (Button)findViewById(R.id.btnTestOpen);
		
		btnTestOpen.setOnClickListener(this);
		btnOpenDoc.setOnClickListener(this);
		btnAddText.setOnClickListener(this);
		btnAddPicToInline.setOnClickListener(this);
		btnAddShapPic.setOnClickListener(this);
		btnHandWriter.setOnClickListener(this);
		btnPasteText.setOnClickListener(this);
		btnSaveAsDoc.setOnClickListener(this);
		
		txtPicPath.setText(PIC_PATH);
		txtSaveAsPath.setText(SAVE_PATH);
		txtTestPath.setText(LOGP_ATH);
		txtResultPath.setText(RESULT_PATH);
		
		bitmap = BitmapFactory.decodeFile(txtPicPath.getText().toString());
		errorLog = new HashMap<Integer, String>();
	}
	
	@Override
	public void onClick(View v) {
		File file = new File(txtTestPath.getText().toString());
		File[] files = file.listFiles();
		//获得是否是wps能打开的文件组
	
		try {
			int id = v.getId();
			if (id == R.id.btnOpenDoc) {
				Log.i("AutoTest", "btnOpenDoc");

				showDialog(0);
			} else if (id == R.id.btnAddText) {
				Log.i("AutoTest", "btnAddText");
				testId = R.id.btnAddText;
				saveName = "测试--添加文字到选区";

//					files = AutoWPSFiles.getWPSFiles(files);
				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnAddPicToInline) {
				Log.i("AutoTest", "btnAddPicToInline");
				testId = R.id.btnAddPicToInline;
				saveName = "测试--添加图片到选区";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnAddShapPic) {
				Log.i("AutoTest", "btnAddShapPic");
				testId = R.id.btnAddShapPic;
				saveName = "测试--添加浮动图片";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnHandWrite) {
				Log.i("AutoTest", "btnHandWrite");
				testId = R.id.btnHandWrite;
				saveName = "测试--打开和关闭手绘批注";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnPasteText) {
				Log.i("AutoTest", "btnPasteText");
				testId = R.id.btnPasteText;
				saveName = "测试--粘贴文字";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnSaveAsDoc) {
				Log.i("AutoTest", "btnSaveAsDoc");
				testId = R.id.btnSaveAsDoc;
				saveName = "测试--另存为文档";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnTestOpen) {
				Log.i("AutoTest", "btnTestOpen");
				testId = R.id.btnTestOpen;
				saveName = "测试--打开文档";

				autoOpenTest(files);
				Toast.makeText(AutoTest.this, "开始测试", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
     * 创建单选按钮对话框
     */
	@Override
    protected Dialog onCreateDialog(int id) 
	{
        Dialog dialog=null;
        LayoutInflater inflater = LayoutInflater.from(AutoTest.this);  
        switch (id) 
        {
        case 0:
     	final View OpenView = inflater.inflate(R.layout.autotest_dialog, null);  
     	EditText editLoadPath = (EditText)OpenView.findViewById(R.id.editLoadPath);
     	editLoadPath.setText(txtTestPath.getText().toString());
     	EditText editPicPath = (EditText)OpenView.findViewById(R.id.editPicPath);
     	editPicPath.setText(txtPicPath.getText().toString());
     	EditText editResultPath = (EditText)OpenView.findViewById(R.id.editResultPath);
     	editResultPath.setText(txtResultPath.getText().toString());
 	  	EditText editSaveAsPath = (EditText)OpenView.findViewById(R.id.editSaveAsPath);
 	  	editSaveAsPath.setText(txtSaveAsPath.getText().toString());
 	  	
 	  	
		    AlertDialog OpenDialog = new AlertDialog.Builder(this)  
		    	.setTitle("设置打开测试目录").setView(OpenView)
		               .setPositiveButton("确定",  
		               new DialogInterface.OnClickListener() {  
		                   @Override                    
		                   public void onClick(DialogInterface dialog, int which) 
		                   {  
		                	   EditText editLoadPath = (EditText)OpenView.findViewById(R.id.editLoadPath);
		                	   EditText editPicPath = (EditText)OpenView.findViewById(R.id.editPicPath);
		                	   EditText editSaveAsPath = (EditText)OpenView.findViewById(R.id.editSaveAsPath);
		                	   EditText editResultPath = (EditText)OpenView.findViewById(R.id.editResultPath);
		                	   
		                	   String path =  editLoadPath.getText().toString();
		                	   String pic = editPicPath.getText().toString();
		                	   String save = editSaveAsPath.getText().toString();
		                	   String result = editResultPath.getText().toString();
		                	   
		                	   //判断路径是否为空
		                	   if (path.length() == 0 || pic.length() == 0
		                			   || save.length() == 0 || result.length() == 0)
		           				{
		                		   Toast.makeText(AutoTest.this,"请输入目录路径", Toast.LENGTH_SHORT).show();
		                		   return;
		           				}
		                	   
		                	   //判断该路径是否存在
		                	   File file = new File(path);
		                	   if(!file.exists()){
		                		   file.mkdir();
		                	   }
		                	   file = new File(save);
		                	   if(!file.exists()){
		                		   file.mkdir();
		                	   }
		                	   file = new File(result);
		                	   if(!file.exists()){
		                		   file.mkdir();
		                	   }
		                	   file = new File(pic);
		                	   if(!file.exists()){
		                		   file.mkdir();
		                	   }
		                	   txtPicPath.setText(pic);
		               			txtSaveAsPath.setText(save);
		               			txtTestPath.setText(path);
		               			txtResultPath.setText(result);
		                	   Toast.makeText(AutoTest.this,"设置成功!!", Toast.LENGTH_SHORT).show();
		                	   
		                	   
		   					   return;
		                   }  
		               }).setNegativeButton("取消",  
		               new DialogInterface.OnClickListener() {  
		                   @Override  
		                   public void onClick(DialogInterface dialog, int which) 
		                   {
		                   }  
		               }).create();
		    OpenDialog.show();  
		    break;
     }
        return dialog;
    }
	
	
	
	/**
	 * 打开多个文件
	 * @param paths
	 */
	public void autoOpenTest(File[] paths)
	{
		OpenThread myThread = new OpenThread(paths);
		myThread.start();
		
	}
	
	
	/** 
  	 * 将解密打开文件的结果写入
  	 */
  	private void writeTestLog()
  	{
  		if (errorLog == null)
  			return ;
  		Log.d("sort", "测试结果写入");
  		File logFile = new File(txtResultPath.getText().toString() + File.separator + saveName+".txt");
  		if (logFile.exists() == false)
			try 
  			{
				logFile.createNewFile();
			}
  			catch (IOException e1) 
  			{
				e1.printStackTrace();
			}
  		
  		int size = errorLog.size();
		try 
		{
			PrintWriter fos = new PrintWriter(logFile);
			for (int i = 0; i < size; i++)
			{
				fos.write(errorLog.get(i) + "\n");
			}
			fos.close();
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
  	}	
	
	

	public boolean openFile(String path)
	{
		if (path.equals("doc") || path.equals("docx") || path.equals("wps") || path.equals("dot") || path.equals("wpt"))
			return true;
		return false;
	}
	
	public void closeFile()
	{
		if(mDoc != null)
		{
			try 
			{
				mDoc.close();
			} 
			catch (RemoteException e) 
			{
				e.printStackTrace();
			}
			mDoc = null;
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
	private boolean bindOfficeService() {
		// bind service
		final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
		intent.putExtra("DisplayView", true);
		if (!bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
			unbindService(connection);
			return false;
		}
		return true;
	}
	
	class OpenThread extends Thread// 内部类 实现用多线程方式连接AIDL打开wps
	{
		private File[] path;
		public OpenThread(File[] paths)
		{
			path = paths; 
		}
		public void run()
		{
			if (mService == null || path == null) 
			{
				if (!bindOfficeService())
					return ;
			}
			//循环打开目录下所有
			openFile[] open = new openFile[path.length];
			int num = 0;			//用来标记文档在有限的时间内是否能打开，打不开默认失败
			for(int i = 0;i < path.length;i++)
			{
				num = 0;
				open[i] = new openFile(path[i]);
				open[i].start();
				while (open[i].getFlag() == false && num < 1000) {
					num++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				errorLog.put(i, path[i].getName() + ": " + 
				(open[i].getFlag() && AutoTestParam.IsOpenCorrect));
				
				Log.d("sort","第  " + i + " 次测试结果统计  " + (open[i].getFlag()
						&& AutoTestParam.IsOpenCorrect)	+ "     " + path[i].getName());
				open[i].setFlag(false);
			}
			
			writeTestLog();
			return ;
		}
	}
	
	
	class openFile extends Thread
	{
		private File path;
		public boolean mFlag = false;
		public openFile(File paths)
		{
			path = paths; 
		}
		public void run()
		{
			if (mService == null || path == null) 
			{
				if (!bindOfficeService())
					return ;
			}
				try 
				{	isLoadOk = false;
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString(Define.THIRD_PACKAGE, getPackageName());
					bundle.putString(Define.USER_NAME, settingPreference.getSettingParam(Define.USER_NAME, ""));
					intent.putExtras(bundle);
					mDoc = mService.openWordDocument(path.getAbsolutePath(), "", intent);
					
					while(!isLoadOk){
						isLoadOk = refreshView();
					}
					if (testId == R.id.btnAddText) {
						mDoc.getSelection().typeText("测试文字!!!");
					} else if (testId == R.id.btnAddPicToInline) {
						mDoc.getSelection().getInlineShapes().addPicture(txtPicPath.getText().toString());
					} else if (testId == R.id.btnAddShapPic) {//							mDoc.getShapes().addPicture(txtPicPath.getText().toString(), false, false, 10, 10, bitmap.getWidth(), bitmap.getHeight(), 0, WrapType.fromValue(7));
					} else if (testId == R.id.btnHandWrite) {
						mDoc.showHandWriteComment();
						Thread.sleep(1000);
						mDoc.closeHandWriteComment();
					} else if (testId == R.id.btnPasteText) {
						mDoc.getSelection().paste();
					} else if (testId == R.id.btnSaveAsDoc) {
						SaveFormat saveFormat = SaveFormat.DOC;
						String fileName = path.getName().substring(0,
								path.getName().lastIndexOf("."));
						mDoc.saveAs(SAVE_PATH + File.separator + fileName + "-副本.doc", saveFormat, null, null);
					} else if (testId == R.id.btnTestOpen) {//if open doc auto,do nothing!
					}
					
					
					Thread.sleep(2000);//间隔
					if (mDoc.isModified()){
						mDoc.save(true);
					}
					
					
					if(mDoc != null)
					{
						mDoc.close();
						setFlag(true);			//设置标记，标明文档已经成功关闭
						mDoc = null;
					}
					Thread.sleep(500);//关闭和打开之间的间隔
				} 
				catch (NullPointerException e) 
				{
					e.printStackTrace();
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
				catch (RemoteException e) 
				{
					e.printStackTrace();
					return ;
				}
				catch (Exception e) 
				{
					Log.d("sort", "打开文档线程崩溃！");
					setFlag(false);
					if (mDoc != null){
						try {
							mDoc.close();
						} catch (RemoteException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
					return ;
				}
				setFlag(true);
			return ;
		}
		public void setFlag(boolean flag)
		{
			mFlag = flag;
		}
		public boolean getFlag()
		{
			return mFlag;
		}
		
	}

	public boolean refreshView() throws RemoteException{

		if (mDoc != null && mDoc.isLoadOK()) {
			return true;
		}
		return false;
	}
	


}
