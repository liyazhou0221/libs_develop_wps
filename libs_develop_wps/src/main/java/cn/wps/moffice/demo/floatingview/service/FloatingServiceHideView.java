package cn.wps.moffice.demo.floatingview.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.floatingview.FloatingFunc;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.SettingPreference;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.doc.Document;
import cn.wps.moffice.service.doc.Page;
import cn.wps.moffice.service.doc.SaveFormat;
import cn.wps.moffice.service.pdf.PDFReader;
import cn.wps.moffice.service.presentation.Presentation;
import cn.wps.moffice.service.spreadsheet.Workbook;

public class FloatingServiceHideView extends Service implements OnClickListener {

	private OfficeService mService;
	private static Document mDoc = null;
	private static Presentation mPresentation = null;
	private static Workbook mWorkBook = null;
	private static PDFReader mPdfReader = null;

	private Page page;
	private View view;
	
	private Button btnStopWindow;
	private Button btnOpen;
	private Button btnSaveAs;
	private Button btnGetPageCount;
	private Button btnGetPage;
	private Button btnGetPath;
	private Button btnGetName;
	private Button btngetText;
//	private Button btnIsModi;
	
	private static TextView txt_fileName;
	private int delaytime = 500;
	private static String docPath = "/storage/sdcard0/DCIM/文档9.doc";
	private static Context mContext;     //上一级Context 为了关闭浮动窗口
	private static Context myContext;   //自身Context 为了关闭service
	public static boolean isBound = false; //是否绑定,为了在关闭wps接收到广播后解绑
	private static boolean isLoadOk = false;//判断文档是否加载完毕

	private SettingPreference settingPreference;

	@Override
	public void onCreate() {
		Log.d("FloatingServiceHideView", "onCreate");
		super.onCreate();
		mContext = getApplicationContext();
		myContext = this;
		view = LayoutInflater.from(this).inflate(R.layout.floating_hideview, null);
		
		btnStopWindow = (Button) view.findViewById(R.id.btnStopWindow);
		btnOpen = (Button) view.findViewById(R.id.btnOpen);
		btnSaveAs = (Button) view.findViewById(R.id.btnSaveAs);
//		btnIsModi = (Button) view.findViewById(R.id.btnIsModi);
		btnGetPageCount = (Button) view.findViewById(R.id.btnGetPageCount);
		btnGetName = (Button) view.findViewById(R.id.btnGetName);
		btnGetPage = (Button) view.findViewById(R.id.btnGetPage);
		btnGetPath = (Button) view.findViewById(R.id.btnGetPath);
		btngetText = (Button) view.findViewById(R.id.btngetText);
		txt_fileName = (TextView) view.findViewById(R.id.filename);
//		txt_fileName.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		
		createView();
		//初始化的时候就刷新视图上按钮显示状态。
		handler.postDelayed(task, 0);

		this.settingPreference = new SettingPreference(this);
		bindOfficeService();
		

	}

	private void createView() {
		File file = new File(docPath);
		txt_fileName.setText(file.getName());
		
		btnOpen.setOnClickListener(this);;
		btnSaveAs.setOnClickListener(this);
		btnStopWindow.setOnClickListener(this);
//		btnIsModi.setOnClickListener(this);
		btnGetPageCount.setOnClickListener(this);
		btnGetName.setOnClickListener(this);
		btnGetPage.setOnClickListener(this);
		btnGetPath.setOnClickListener(this);
		btngetText.setOnClickListener(this);
		
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				FloatingFunc.onTouchEvent(arg1, view);
				return true;
			}
		});
	}
	 int xh_count = 0;
	// 声明进度条对话框
	 ProgressDialog xh_pDialog;
	@Override
	public void onClick(View v) {
		// 如果按钮不是打开，新建文档，mDoc 为 null的话，不处理
		if (!(v.getId() == R.id.btnOpen || v.getId() == R.id.btnStopWindow ) && mDoc == null &&
				mWorkBook != null && mPresentation != null) {
			Toast.makeText(getApplicationContext(), "请先打开文件", Toast.LENGTH_LONG)
					.show();
			return;
		}
		try {
			int id = v.getId();
			if (id == R.id.btnStopWindow) {
				this.stopSelf();
				closeFile();
			} else if (id == R.id.btnOpen) {// 打开文档
				if (mService == null) {
					if (!bindOfficeService())
						return;
				}
				openFile();
				Toast.makeText(mContext, "正在加载,请稍后!!", Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnGetName) {
				Toast.makeText(this, "文档名称: " + mDoc.getName(), Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnGetPath) {
				Toast.makeText(this, "文档路径: " + mDoc.getPath(), Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnGetPageCount) {
				Toast.makeText(this, "总页数: " + mDoc.getPageCount(), Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnGetPage) {
				page = mDoc.getPage(0);
				Toast.makeText(this, "第 0 页 : 宽:" + page.getWidth() + "  高:" + page.getHeight(), Toast.LENGTH_SHORT).show();
			} else if (id == R.id.btnSaveAs) {//另存功能实现
				saveAsDocment();
			} else if (id == R.id.btngetText) {
				getText();
			} else if (id == R.id.btnIsModi) {
				if (mDoc.isModified()) {
					Toast.makeText(mContext, "文档已修改", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mContext, "文档未修改", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	
	private void openFile() {
		if (Util.isPptFile(docPath)) {
			LoadPresentationThread loadThread = new LoadPresentationThread(docPath);
			loadThread.start();
		}else if (Util.isExcelFile(docPath)) {
			LoadWorkBookThread loadThread = new LoadWorkBookThread(docPath);
			loadThread.start();
		} else if (Util.isPDFFile(docPath)) {
			LoadPDFDocThread mythread = new LoadPDFDocThread(docPath);
			mythread.start();
		}else {
			LoadDocThread mythread = new LoadDocThread(docPath);
			mythread.start();
		}
	}
	
	private void closeFile() {
		try {
			if (Util.isPptFile(docPath) && mPresentation != null) {
				mPresentation.close();
			}else if (Util.isExcelFile(docPath)) {
				mWorkBook.close();
			} else if (Util.isPDFFile(docPath)) {
				mPdfReader.close();
			}else {
				mDoc.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭文档
	 */
//	private void closeDoc() {
//		try {
//			mDoc.close();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//		mDoc = null;
//		isLoadOk = false;
//		handler.removeCallbacks(task);
//		System.out.println(mDoc+"----------------+++");
//		FloatingFunc.close(getApplicationContext());
//	}
	/**
	 * 另存为文件
	 */
	private void saveAsDocment() {
		Log.i("FloatingService", "btnSaveAs");
		//打开输入框
		inputPathDialog();
	
	}
	
	/**
	  * 获取word文档中指定区域的内容（过滤页面页脚文本）
	  */ 
	 public void getText() {
//	  if (mDoc != null) {
//	   Util.showToast(mContext, "文档未打开，请先打开或者新建文档");
//	   return;
//	  }
	  try {

	   // 获取文档中前0~100直接的文本内容
	   Util.showToast(mContext, "DOC文档摘要：" + mDoc.range(0, 100).getText());
	  } catch (RemoteException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  
	 }  



	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d("FloatingService", "onStart");
		mDoc = null;
		isLoadOk = false;
		//setForeground(true);
		FloatingFunc.show(this.getApplicationContext(), view);
		File file = new File(docPath);
		txt_fileName.setText(file.getName());
	
	
		super.onStart(intent, startId);
	}
	/**
	 * 停止服务调用
	 */
	public static  void stopService(){
		Log.i("FloatingService", "btnCloseWindow");
		FloatingFunc.close(mContext);
		isBound = false;
		mDoc = null;
		((Service) myContext).stopSelf();//关闭自身service
	}
	@Override
	public void onDestroy() {
		//关闭线程
		handler.removeCallbacks(task);
		Log.d("FloatingService", "onDestroy");
		
		FloatingFunc.close(this.getApplicationContext());
		if (mService != null)
			unbindService(connection);
		isLoadOk = false;
		mDoc = null;
		this.stopSelf();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * connection of binding
	 */
	private  ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = OfficeService.Stub.asInterface(service);
			isBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			isBound = false;
		}
	};

	private boolean bindOfficeService() {
		// bind service
		final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
		intent.putExtra("DisplayView", true);
		if (!bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
			// bind failed, maybe wps office is not installd yet.
			unbindService(connection);
			
			return false;
		}
		return true;
	}


	/**
	 * 设置文档路径
	 * @param path
	 */
	public static void setDocPath(String path) {
		docPath = path;

	}

	class LoadDocThread extends Thread// 内部类
	{
		String path;

		public LoadDocThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) {
				if (!bindOfficeService())
					return;
			}

			try {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// bundle.putBoolean(Define.HOME_KEY_DOWN, true);
				// bundle.putBoolean(Define.BACK_KEY_DOWN, true);
//				bundle.putString(Define.OPEN_MODE, settingPreference.getSettingParam(Define.OPEN_MODE, Define.NORMAL));	 //打开模式
//				bundle.putString(Define.THIRD_PACKAGE, settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName()));
//				bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
//				bundle.putBoolean(Define.FAIR_COPY, settingPreference.getSettingParam(Define.FAIR_COPY, false));
//				bundle.putString(Define.USER_NAME,settingPreference.getSettingParam(Define.USER_NAME, ""));
				intent.putExtras(bundle);
//				mService.setFileId("asd");
				mDoc = mService.openWordDocument(path, "",intent);
			} catch (RemoteException e) {
				e.printStackTrace();
				mDoc = null;
			}
		}
	}
	
	class LoadWorkBookThread extends Thread// 内部类
	{
		String path;

		public LoadWorkBookThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) {
				if (!bindOfficeService())
					return;
			}

			try {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// bundle.putBoolean(Define.HOME_KEY_DOWN, true);
				// bundle.putBoolean(Define.BACK_KEY_DOWN, true);
				bundle.putString(Define.OPEN_MODE, settingPreference.getSettingParam(Define.OPEN_MODE, Define.NORMAL));	 //打开模式
				bundle.putString(Define.THIRD_PACKAGE, settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName()));
				bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
				bundle.putBoolean(Define.FAIR_COPY, settingPreference.getSettingParam(Define.FAIR_COPY, false));
				bundle.putString(Define.USER_NAME,settingPreference.getSettingParam(Define.USER_NAME, ""));
				intent.putExtras(bundle);
				mWorkBook = mService.getWorkbooks().openBookEx(path, "", intent);
			} catch (RemoteException e) {
				e.printStackTrace();
				mWorkBook = null;
			}
		}
	}
	
	class LoadPresentationThread extends Thread// 内部类
	{
		String path;

		public LoadPresentationThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) {
				if (!bindOfficeService())
					return;
			}

			try {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// bundle.putBoolean(Define.HOME_KEY_DOWN, true);
				// bundle.putBoolean(Define.BACK_KEY_DOWN, true);
				bundle.putString(Define.OPEN_MODE, settingPreference.getSettingParam(Define.OPEN_MODE, Define.NORMAL));	 //打开模式
				bundle.putString(Define.THIRD_PACKAGE, settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName()));
				bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
				bundle.putBoolean(Define.FAIR_COPY, settingPreference.getSettingParam(Define.FAIR_COPY, false));
				bundle.putString(Define.USER_NAME,settingPreference.getSettingParam(Define.USER_NAME, ""));
				bundle.putBoolean("PagePlay",true);
				intent.putExtras(bundle);

				mPresentation = mService.openPresentation(path, "", intent);
//				mPresentation.showThumbView(true);
			} catch (RemoteException e) {
				e.printStackTrace();
				mPresentation = null;
			}
		}
	}
	
	class LoadPDFDocThread extends Thread// 内部类
	{
		String path;

		public LoadPDFDocThread(String path) {
			this.path = path;
		}

		public void run() {
			// 打开文档
			if (mService == null) {
				if (!bindOfficeService())
					return;
			}

			try {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				// bundle.putBoolean(Define.HOME_KEY_DOWN, true);
				// bundle.putBoolean(Define.BACK_KEY_DOWN, true);
				bundle.putString(Define.OPEN_MODE, settingPreference.getSettingParam(Define.OPEN_MODE, Define.NORMAL));	 //打开模式
				bundle.putString(Define.THIRD_PACKAGE, settingPreference.getSettingParam(Define.THIRD_PACKAGE, getPackageName()));
				bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
				bundle.putBoolean(Define.FAIR_COPY, settingPreference.getSettingParam(Define.FAIR_COPY, false));
				bundle.putString(Define.USER_NAME,settingPreference.getSettingParam(Define.USER_NAME, ""));
				intent.putExtras(bundle);
				mPdfReader = mService.openPDFReader(path, "", intent);
			} catch (RemoteException e) {
				e.printStackTrace();
				mPdfReader = null;
			}
		}
	}

	private  Handler handler = new Handler();

	private Runnable task = new Runnable() {
		
		public void run() {
			
//			try 
//			{
//				isLoadOk = refreshView();
//			}
//			catch (NullPointerException ee)
//			{
//				ee.printStackTrace();
//				mDoc = null;
//			}
//			catch(RemoteException e)
//			{
//				e.printStackTrace();
//				mDoc = null;
//			}
			if (!isLoadOk){
				handler.postDelayed(this, delaytime);
			}
		}
	};

	public void refreshView() throws RemoteException {
		
		if (mDoc != null) {
//			if (mDoc.isLoadOK()) {
				Toast.makeText(this, "已加载完毕!!", Toast.LENGTH_SHORT).show();
				btnOpen.setVisibility(View.GONE);
				btnStopWindow.setVisibility(View.VISIBLE);
				btnSaveAs.setVisibility(View.VISIBLE);
				btnGetPage.setVisibility(View.VISIBLE);
				btnGetPath.setVisibility(View.VISIBLE);
				btnGetName.setVisibility(View.VISIBLE);
				btnGetPage.setVisibility(View.VISIBLE);
//				btnIsModi.setVisibility(View.VISIBLE);
				btnGetPageCount.setVisibility(View.VISIBLE);
				handler.removeCallbacks(task);
//				return true; 
//			} else {
//				Toast.makeText(this, "load fail!!", Toast.LENGTH_SHORT).show();
//				btnOpen.setVisibility(View.VISIBLE);
//				btnOpen.setClickable(false);
////				btnIsModi.setVisibility(View.GONE);
//				btnSaveAs.setVisibility(View.GONE);
//				btnStopWindow.setVisibility(View.GONE);;
////				btnIsModi.setVisibility(View.GONE);
//				btnGetPageCount.setVisibility(View.GONE);
//				btnGetName.setVisibility(View.GONE);
//				btnGetPage.setVisibility(View.GONE);
//				btnGetPath.setVisibility(View.GONE);
			}
			
		 else {
			btnStopWindow.setVisibility(View.VISIBLE);
			btnOpen.setVisibility(View.VISIBLE);
			btnOpen.setClickable(true);

			btnSaveAs.setVisibility(View.GONE);
//			btnIsModi.setVisibility(View.GONE);
			btnGetPageCount.setVisibility(View.GONE);
			btnGetName.setVisibility(View.GONE);
			btnGetPath.setVisibility(View.GONE);
			btnGetPage.setVisibility(View.GONE);
		}

	}

	/**
	 * 弹出输入另存为文件的路径对话框
	 */
	private void inputPathDialog() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		final View ParamDialogView = inflater.inflate(R.layout.path_dialog,null);
		EditText saveAsPath = (EditText) ParamDialogView.findViewById(R.id.SaveAsPath);
		String fileName = docPath.substring(0,
				docPath.lastIndexOf("."));
		saveAsPath.setText(fileName + "-副本");
		AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
				.setTitle("设置保存路径")
				.setView(ParamDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText SavePath = (EditText) ParamDialogView
								.findViewById(R.id.SaveAsPath);
						//另存为文件
						//根据ID找到RadioGroup实例
						RadioGroup group = (RadioGroup)ParamDialogView.findViewById(R.id.saveAsFormat);
						RadioButton rb = (RadioButton)ParamDialogView.findViewById(group.getCheckedRadioButtonId());
						Log.i("SaveAsFormat",rb.getText().toString());
						
						if (SavePath.getText().toString().length() == 0) {
							Toast.makeText(mContext, "请输入参数",
									Toast.LENGTH_SHORT).show();
							return ;
						} else {
							try {
								SaveFormat saveFormat = SaveFormat.valueOf(rb.getText().toString());
								mDoc.saveAs(SavePath.getText().toString()+"."+rb.getText().toString().toLowerCase(), saveFormat, "", "");
							} catch (RemoteException e) {
								Toast.makeText(mContext, "保存失败",
										Toast.LENGTH_SHORT).show();
								e.printStackTrace();
							}
							Toast.makeText(mContext, "保存成功！路径为:"+SavePath.getText().toString()+"."+rb.getText().toString().toLowerCase(),
									Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create();
		paramAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		paramAlertDialog.show();
	}

	
}
