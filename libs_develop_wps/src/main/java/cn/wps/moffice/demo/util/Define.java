/**
 *	 文件名：Define.java
 * 	创建者:fanguangcheng
 * 	创建时间:2013.7.18
 * 	作用：保存一些基本的常用字符串
 */
package cn.wps.moffice.demo.util;

public class Define 
{
	public static final String PREFS_NAME = "MyPrefsFile";			//用于存取参数的文件名
	public static final String KEY = "PackageName";					//第三方包名
	public static final String READ_ONLY = "ReadOnly";				//只读模式
	public static final String NORMAL = "Normal";					//正常模式
	public static final String READ_MODE = "ReadMode";		//打开文件直接进入阅读器模式
	public static final String EDIT_MODE = "EditMode";		//打开文件直接进入编辑模式(文档上面添加空格，是错误的，之后修改)
	public static final String SAVE_ONLY = "SaveOnly";			//仅仅用来另存文件
	public static final String VIEW_SCALE = "ViewScale";			//视图比例
	public static final String VIEW_PROGRESS = "ViewProgress";		//查看进度百分比
	public static final String VIEW_SCROLL_X = "ViewScrollX";		//显示的x坐标
	public static final String VIEW_SCROLL_Y = "ViewScrollY";		//显示的y坐标
	public static final String CLOSE_FILE = "CloseFile";			//关闭的文件
	public static final String OPEN_FILE = "OpenFile";				//打开的文件
	public static final String THIRD_PACKAGE = "ThirdPackage";		//第三方的包名
	public static final String SAVE_PATH = "SavePath";				//文件保存的路径
	public static final String CLEAR_BUFFER = "ClearBuffer";		//清除缓冲区,默认为true
	public static final String CLEAR_TRACE = "ClearTrace";			//清除使用痕迹,默认为false
	public static final String CLEAR_FILE = "ClearFile";			//删除文件自身,默认为false
	public static final String CHECK_PACKAGE_NAME = "CheckPackageName";		//企业版华为不固定的应用包名
	public static final String IS_SCREEN_SHOTFORBID = "isScreenshotForbid"; // 禁止截屏
	
	// 以下是自己重新定义的
	public static final String USER_NAME = "UserName";
	public static final String SEND_CLOSE_BROAD = "SendCloseBroad";	//关闭文件时是否发送广播,默认不发送
	public static final String SEND_SAVE_BROAD = "SendSaveBroad";		//关闭保存时是否发送广播,默认不发送
	public static final String IS_VIEW_SCALE = "IsViewScale";		//view scale
	public static final String OPEN_MODE = "OpenMode";				//阅读器模式
	public static final String AUTO_JUMP = "AutoJump";				//第三方打开文件时是否自动跳转
	public static final String IS_CLEAR_BUFFER = "IsClearBuffer";		//清除缓冲区,默认为true
	public static final String IS_CLEAR_TRACE = "IsClearTrace";			//清除使用痕迹,默认为false
	public static final String IS_CLEAR_FILE = "IsClearFile";			//删除文件自身,默认为false	
	public static final String HOME_KEY_DOWN = "HomeKeyDown";		//Home 按钮
	public static final String BACK_KEY_DOWN = "BackKeyDown";		//Back 按钮
	public static final String CACHE_FILE_INVISIBLE = "CacheFileInvisible";		//缓存文件是否可见，默认可见
	public static final String ENTER_REVISE_MODE = "EnterReviseMode";		//以修订模式打开文档
	public static final String ENCRYPT_FILE = "EncrptFile";		//加密方式操作文档
	public static final String MENU_XML = "MenuXML";			//xml菜单
	public static final String REVISION_NOMARKUP = "RevisionNoMarkup";//修订模式的无标记
	public static final String DISPLAY_OPEN_FILE_NAME = "DisplayOpenFileName"; // 第三方设置文档显示名称
	public static final String SHOW_REVIEWING_PANE_RIGHT_DEFAULT = "ShowReviewingPaneRightDefault";		//打开文档默认侧边栏不显示/显示 默认false
	public static final String WATERMASK_TEXT = "WaterMaskText";//水印文字
	public static final String WATERMASK_COLOR = "WaterMaskColor";//水印文字颜色
	public static final String PACKAGENAME_KING_PRO = "com.kingsoft.moffice_pro";
	public static final String PACKAGENAME_PRO_DEBUG = "cn.wps.moffice";
	public static final String PACKAGENAME_ENG = "cn.wps.moffice_eng";
	public static final String PACKAGENAME_K_ENG = "cn.kingsoft.moffice_eng";
	public static final String PACKAGENAME_KING_PRO_HW = "com.kingsoft.moffice_pro_hw";		//华为定制包名
	
	public static final String CLASSNAME = "cn.wps.moffice.documentmanager.PreStartActivity2";		//wps类名，标准版本
//	public static final String CLASSNAME = "cn.wps.moffice.emm.EmmOpenFileActivity";		//wps类名，EMM版本
	public static final String OFFICE_SERVICE_ACTION = "cn.wps.moffice.service.OfficeService";
	public static final String PRO_OFFICE_SERVICE_ACTION = "cn.wps.moffice.service.ProOfficeService";
	public static final String OFFICE_ACTIVITY_NAME = "cn.wps.moffice.service.MOfficeWakeActivity";
	public static final String OFFICE_READY_ACTION = "cn.wps.moffice.service.startup";

	public static final String START_APP_ACTIVITY = "cn.wps.moffice.second_dev.StartAppActivity";
	public static final String START_APP = "cn.wps.moffice.second_dev.StartApp";

	public static final String WPS_OPEN_MODE = "WPSOPENMODE";				
	public static final String WPS_OPEN_AIDL = "AIDL";
	public static final String WPS_OPEN_THIRD = "THIRD";
	
	public static final String FAIR_COPY = "FairCopy";		//清稿
	public static final String FAIR_COPY_PW = "FairCopyPw";		//清稿密码
	
	public static final String IS_SHOW_VIEW = "IsShowView";   //是否显示wps界面

	//自动播放控制
	public static final String AUTO_PLAY = "AutoPlay";      // PPT直接进入自动播放
	public static final String AUTO_PLAY_INTERNAL = "AutoPlayInternal";  // PPT自动播放间隔
	
	//编辑
	public static final int INVALID_EDITPARAM = -1;
	public static final String AT_SAVE = "AT_SAVE";                   //保存
	public static final String AT_SAVEAS = "AT_SAVEAS";               //另存为
	public static final String AT_COPY = "AT_COPY";                   //复制
	public static final String AT_CUT = "AT_CUT";                      //剪切
	public static final String AT_PASTE = "AT_PASTE";                  //粘贴
//	public static final String AT_EDIT_TEXT = "AT_EDIT_TEXT";          //插入文字
//	public static final String AT_EDIT_PICTURE = "AT_EDIT_PICTURE";      //插入图片
//	public static final String AT_EDIT_SHAPE = "AT_EDIT_SHAPE";         //插入浮动图片
//	public static final String AT_EDIT_CHART = "AT_EDIT_CHART";         //编辑图表
	public static final String AT_SHARE = "AT_SHARE";                    //分享
	public static final String AT_PRINT = "AT_PRINT";                    //输出
	public static final String AT_SPELLCHECK = "AT_SPELLCHECK";          //拼写检查
	public static final String AT_QUICK_CLOSE_REVISEMODE = "AT_QUICK_CLOSE_REVISEMODE";          //快速关闭修订
	public static final String AT_MULTIDOCCHANGE = "AT_MULTIDOCCHANGE";          //多文档切换
	public static final String AT_EDIT_REVISION = "AT_EDIT_REVISION";
	public static final String AT_CURSOR_MODEL = "AT_CURSOR_MODEL";
	public static final String AT_PATH = "at_path";                             //编辑路径
	public static final String AT_CHANGE_COMMENT_USER = "AT_CHANGE_COMMENT_USER";
	public static final String AT_SHARE_PLAY = "AT_SHARE_PLAY";
	public static final String AT_GRID_BACKBOARD = "AT_GRID_BACKBOARD";
	public static final String SERIAL_NUMBER_OTHER = "SerialNumberOther"; // android 激活外部传入序列号
	public static final String SERIAL_NUMBER_OTHERPC = "SerialNumberOtherPc"; // PC 激活外部传入序列号
	public static final String SCREEN_ORIENTATION = "ScreenOrientation";
	public static final String ZOOM_OTHER = "zoom";
	public static final String BROAD_WPS_ONPAUSE = "cn.wps.moffice.activity.onPause";	//讯飞分支切换到后台发送广播
	public static final String BROAD_WPS_ONRESUME = "cn.wps.moffice.activity.onResume"; //讯飞分支切回到前台发送广播
	public static final String BROAD_WPS_VIEWMODE = "cn.wps.moffice.viewMode";	//讯飞分支切换播放模式的广播
	public static final String BROAD_WPS_VIEWMODE_PLAY = "cn.wps.moffice.viewMode.play";
//	boolean enter = extra.getBoolean(BROAD_WPS_VIEWMODE_PLAY, false);

public static final String AGENT_CLASS_NAME = "agentClassName";

	public static final String JSON_DATA =
			"[" +
			"{ \"name\" : \"cn.wps.moffice.client.OfficeServiceClient\"," +
			" \"type\" : \"Package-ID\",\"id\" : \"cn.wps.moffice.client\", " +
			"\"Security-Level\" : \"Full-access\", \"Authorization\"  : \"abxxdsewrwsds3232ss\" }," +
			"]";
	
}
