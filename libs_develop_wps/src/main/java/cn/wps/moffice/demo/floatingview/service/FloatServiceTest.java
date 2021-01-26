package cn.wps.moffice.demo.floatingview.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.List;

import cn.wps.moffice.client.AllowChangeCallBack;
import cn.wps.moffice.demo.R;
import cn.wps.moffice.demo.bean.WpsOpenBean;
import cn.wps.moffice.demo.floatingview.FloatingFunc;
import cn.wps.moffice.demo.util.Define;
import cn.wps.moffice.demo.util.PremiumUtils;
import cn.wps.moffice.demo.util.Util;
import cn.wps.moffice.service.OfficeService;
import cn.wps.moffice.service.Variant;
import cn.wps.moffice.service.base.print.PrintOutItem;
import cn.wps.moffice.service.base.print.PrintProgress;
import cn.wps.moffice.service.doc.Document;
import cn.wps.moffice.service.doc.DocumentProperties;
import cn.wps.moffice.service.doc.MsoAutoShapeType;
import cn.wps.moffice.service.doc.MsoTextOrientation;
import cn.wps.moffice.service.doc.MsoTriState;
import cn.wps.moffice.service.doc.Page;
import cn.wps.moffice.service.doc.PictureFormat;
import cn.wps.moffice.service.doc.ProtectionType;
import cn.wps.moffice.service.doc.Revision;
import cn.wps.moffice.service.doc.SaveFormat;
import cn.wps.moffice.service.doc.WdBool;
import cn.wps.moffice.service.doc.WdCollapseDirection;
import cn.wps.moffice.service.doc.WdInformation;
import cn.wps.moffice.service.doc.WdOrientation;
import cn.wps.moffice.service.doc.WdOriginalFormat;
import cn.wps.moffice.service.doc.WdProtectionType;
import cn.wps.moffice.service.doc.WdRevisionsMode;
import cn.wps.moffice.service.doc.WdRevisionsView;
import cn.wps.moffice.service.doc.WdSaveOptions;
import cn.wps.moffice.service.doc.WdUnderline;
import cn.wps.moffice.service.doc.WdUnits;
import cn.wps.moffice.service.doc.WrapType;
import cn.wps.moffice.service.pdf.PDFReader;
import cn.wps.moffice.service.presentation.Presentation;
import cn.wps.moffice.service.spreadsheet.Range;
import cn.wps.moffice.service.spreadsheet.Workbook;
import cn.wps.moffice.service.spreadsheet.Worksheet;

public class FloatServiceTest extends Service implements OnClickListener {

    private final static String WINDOW_NOTE = "按住该按钮可以拖动窗口\n文件名为：";
    final int QUALITY = 100;
    final int DPI = 64;
    final int PRINT_ITEM = PrintOutItem.wdPrintContent;

    private static Context mContext;     //上一级Context 为了关闭浮动窗口
    private static Context myContext;   //自身Context 为了关闭service
    public static boolean isBound = false; //是否绑定,为了在关闭wps接收到广播后解绑
    public static boolean isChangedFlag = false;
    public boolean isProtected = false;
    private static AllowChangeCallBack mAllow;
    private static String docPath = "/sdcard/DCIM/文档9.doc";

    private OfficeService mService;
    private static Document mDoc = null;
    private static PDFReader mPdfReader = null;
    private static Presentation mPresentation = null;
    private static Workbook mWorkBook = null;

    private View view;

    private static TextView txt_fileName;
    private Button btnHideAll;
    private View allBtnGroup;
    private Button btnCloseWindow;
    private Button btnNewDocument;
    private Button btnOpenFile;
    private Button btnCloseFile;
    private Button btnGetPath;
    private Button btnGetPageCount;
    private Button btnSave;
    private Button btnSaveAs;
    private Button btnHiddenToolBar;
    private Button btnHiddenMenuBar;
    private Button btnIsModified;
    private Button btnHandWriteComment;
    private Button btnChangeReviseMode;
    private Button btnChangeMarkupMode;
    private Button btnAcceptAllRevision;
    private Button btnUndo;
    private Button btnRedo;
    private Button btnClearAllComments;
    private Button btnGetCurrentPageNum;
    private Button btnSaveCurrentPageToImage;
    private Button btnSaveAllPageToImage;
    private Button btnGetLength;
    private Button btnGetScrollY;
    private Button btnGetScrollX;
    private Button btnAddDocumentVariable;
    private Button btnGetDocumentVariable;
    private Button btnReadOnlyProtect;
    private Button btnCommentProtect;
    private Button btnTrackedProtect;
    private Button btnUnProtect;
    private Button btnIsProtectOn;
    private Button btnGetProtectionType;
    private Button btnToggleInk;
    private Button btnToggleInkFinger;
    private Button btnToggleToPen;
    private Button btnToggleToHighLightPen;
    private Button btnToggleToEraser;
    private Button btnSetInkColor;
    private Button btnGetInkColor;
    private Button btnGetInkPenThick;
    private Button btnGetInkHighLightThick;
    private Button btnSetInkThick;
    private Button btnDeleteShape;
    private Button btnGetPage;
    private Button btnDeleteComment;
    private Button btnAddComment;
    private Button btnAddBookmark;
    private Button btnDeleteBookmark;

    //以下是选区的aidl按钮
    private Button btnCopy;
    private Button btnCut;
    private Button btnPaste;
    private Button btnTypeText;
    private Button btnGetText;
    private Button btnInsertParagraph;
    private Button btnGetLeft;
    private Button btnGetTop;
    private Button btnGetStart;
    private Button btnGetEnd;
    private Button btnSetSelection;
    private Button btngetFont;

    //InlineShape
    private Button btnAddInlinePicture;
    //Shapes
    private Button btnAddPicture;
    private Button btnAddTextBox;
    private Button btnAddNoneTextBox;

    //选区字体相关设置
    private Button btnRemoveStrikeThrough;
    private Button btnSetStrikeThrough;

    // 上下翻页
    private Button btnPrePage;
    private Button btnNextPage;
    private Button btnGetShapeLeft;
    private Button btnGetShapeTop;
    private Button btnGetShapeHeight;
    private Button btnGetShapeWidth;
    private Button btnGetTextBoxLineColor;
    private Button btnGetTextBoxFLine;
    private Button btnGetDocumentProp;
    private Button btnGetShowRevisions;
    private Button btnGetForbittenInk;
    private Button btnSetPageOrientation;
    private Button btnSetPageWidth;
    private Button btnSetPageHeight;
    private Button btnSetPageLeft;
    private Button btnSetPageRight;
    private Button btnSetPageTop;
    private Button btnSetPageBottom;
    private Button btnSavePDF;

    //	houjing补充按钮
    private Button btngetBookmarkText;
    private Button btnsetBookmarkText;
    private Button btngetPageOrientation;
    private Button btngetPageWidth;
    private Button btngetPageHeight;
    private Button btngetLeftMargin;
    private Button btngetRightMargin;
    private Button btngetTopMargin;
    private Button btngetBottomMargin;
    private Button btngetActiveDocument;
    private Button btngetSelection;
    private Button btnrangecopy;
    private Button btnrangepaste;
    private Button btnrangecollapse;
    private Button btngetUnderline;
    private Button btnsetUnderline;
    private Button btnsetStyle;
    private Button btngetStyle;
    private Button btnsetItalic;
    private Button btnisItalic;
    private Button btnsetBold;
    private Button btnisBold;
    private Button btnrangegetFont;
    private Button btnrangesetEnd;
    private Button btnrangegetEnd;
    private Button btnrangesetStart;
    private Button btnrangegetStart;
    private Button btnrangesetText;
    private Button btnrangegetText;
    private Button btnrangedelete;
    private Button btnrangeexpand;

    private Button font_setBold;
    private Button font_setItalic;
    private Button font_setTextColor;
    private Button font_part_setName;
    private Button font_setSize;
    private Button font_setStrikeThrough;
    private Button font_setDoubleStrikeThrough;
    private Button font_part_setNoneStrikeThrough;
    private Button font_getSuperscript;
    private Button font_setSuperscript;
    private Button font_getSubscript;
    private Button font_setSubscript;
    private Button font_getBoldBi;
    private Button font_setBoldBi;
    private Button font_getSmallCaps;
    private Button font_setSmallCaps;
    private Button font_getAllCaps;
    private Button font_setAllCaps;
    private Button font_setNoneStrikeThrough;
    private Button font_getDoubleStrikeThrough2;
    private Button font_setDoubleStrikeThrough2;
    private Button font_setStrikeThrough2;
    private Button font_shrink;
    private Button font_grow;
    private Button font_getSize;
    private Button font2_setSize;
    private Button font_getNameFarEast;
    private Button font_setNameFarEast;
    private Button font_getNameAscii;
    private Button font_setName;
    private Button font_getTextColor;
    private Button font2_setTextColor;
    private Button font_getUnderlineColor;
    private Button font_setUnderlineColor;
    private Button font_getUnderline2;
    private Button font_setUnderline2;
    private Button font_getItalic2;
    private Button font_setItalic2;
    private Button font_setBold2;
    private Button font_getItalic;
    private Button SelectionaddComment;
    private Button SelectiongetShapeRange;
    private Button SelectiongetInformation;
    private Button SelectiongetRightOnScreen;
    private Button SelectiongetBottomOnScreen;
    private Button InlineShapesgetWidth;
    private Button InlineShapesgetHeight;
    private Button InlineShapessetScaleWidth;
    private Button InlineShapessetScaleHeight;
    private Button InlineShapessetHeight;
    private Button InlineShapessetWidth;
    private Button InlineShapesgetScaleWidth;
    private Button InlineShapesgetScaleHeight;
    private Button InlineShapesgetOLE;
    private Button InlineShapesgetCount;
    private Button InlineShapessetAlternativeText;
    private Button InlineShapesgetAlternativeText;
    private Button InlineShapesaddOLEControl;
    //	Document
    private Button document_getName;
    private Button document_getPage;
    private Button document_getPageCount;
    private Button document_getSelection;
    private Button document_print;
    private Button document_save;
    private Button document_acceptAllRevisions;
    private Button document_acceptAllRevise;
    private Button document_pageUp;
    private Button document_pageDown;
    private Button document_getCommentNumber;
    private Button document_isInRevisionMode;
    private Button document_getUser;
    private Button document_isInHandWriterMode;
    private Button document_getBuiltInDocumentProperties;
    private Button document_saveToImage;
    private Button document_changeRevisionState;
    private Button document_close2;
    private Button document_delAllComments;
    private Button document_denyAllRevision;
    private Button document_getProtectionType2;
    private Button document_setSaved;
    private Button document_getViewProgress;
    private Button document_getViewScale;
    private Button document_getViewScrollX;
    private Button document_getViewScrollY;
    private Button document_isLoadOK;
    private Button document_protect2;
    private Button document_range;
    private Button document_redo2;
    private Button document_rejectAllRevisions;
    private Button document2_save;
    private Button document_setUser;
    private Button document_undo2;
    private Button document_undoClear;
    private Button document_unProtectDocument;
    private Button document_getContent;

    //	Shapes
    private Button Shapes_addPicture;
    private Button Shapes_addPicture2;
    private Button Shapes_addTextbox2;
    private Button Shapes_addOLEControl;
    private Button Shapes_addShape;


    //	shape
    private Button shape_getIPictureFormat;
    private Button shape_getOLE;
    private Button shape_select;
    private Button shape_getWrapFormat;
    private Button shape_hasInk;
    private Button shape_incrementTop;
    private Button shape_incrementLeft;
    private Button shape_getWidth;
    private Button shape_setVisible;
    private Button shape_getVisible;
    private Button shape_getType;
    private Button shape_getTop;
    private Button shape_getTextFrame;
    private Button shape_getLine;
    private Button shape_getLeft;
    private Button shape_getHeight;
    private Button shape_getGroupItems;
    private Button shape_getFill;
    private Button shape_getAlternativeText;
    private Button shape_setAlternativeText;

    //	View
    private Button View_putRevisionsView;
    private Button View_putShowRevisionsAndComments;
    private Button View_getShowRevisionsAndComments;
    private Button View_getRevisionsView;

    //	Other
    private Button setVisibleFill;
    private Button setVisibleLine;
    private Button getGroupItemsItem;
    private Button getGroup;
    private Button getTextRange;
    private Button groupCount;
    private Button getOpenDocument;
    private Button getRevision;
    private Button btnReadMode;
    private Button btnWorkbookgetName;

    //	截止以上houjing补充按钮
    private Button btnfairCopy;

    private boolean isSharedEnable = true;

    private boolean mIsHindeAll = false;
    private boolean mShowHandWriteComment = false;
    private Boolean mEnterRevisionMode = false;
    private final static String NEW_DOCUMENT_PATH = "/sdcard/专业版新建文档.doc";
    private final static String SAVE_AS_PATH = "/sdcard/专业版另存文档.doc";
    private final static String SAVE_CURRUNT_PAGE = "/sdcard/专业版截图.png";//截图保存路径
    private final static String SAVE_IMAGE_PATH = "/sdcard/pic";
    private final static String INLINE_PIC_PATH = "/sdcard/DCIM/ico.png";

    private final static int NEW_DOCUMENT = 0;
    private final static int OPEN_DOCUMENT = 1;

    private final static String FAIL_NOTE = "操作失败！";
    private final static String VARIABLE_EXIST = "该属性已经存在，不能重复添加！";
    private final static String INSERT_STR = "这是插入的内容！";        //插入到选区的内容

    //文档保护类型
    public final static short TRACKEDCHANGES = 0;
    public final static short COMMENTS = 1;
    public final static short READONLY = 3;
    public final static short NONE = 7;

    private final static String PROTECT_PASSWORD = "123";

    @Override
    public void onCreate() {
        Log.d("FloatingService", "onCreate");
        super.onCreate();
        mContext = getApplicationContext();
        myContext = this;
        view = LayoutInflater.from(this).inflate(R.layout.float_test_view, null);

        findViewByID();
        createView();

        new Thread(new Runnable() {

            @Override
            public void run() {
                bindOfficeService();
            }
        }).start();
    }


    private void findViewByID() {
        txt_fileName = (TextView) view.findViewById(R.id.filename);
        btnHideAll = (Button) view.findViewById(R.id.btnHideAll);
        allBtnGroup = view.findViewById(R.id.allbtngroup);

        btnCloseWindow = (Button) view.findViewById(R.id.btnCloseWindow);
        btnNewDocument = (Button) view.findViewById(R.id.btnNewDocument);
        btnOpenFile = (Button) view.findViewById(R.id.btnOpenFile);
        btnCloseFile = (Button) view.findViewById(R.id.btnCloseFile);
        btnGetPath = (Button) view.findViewById(R.id.btnGetPath);
        btnGetPageCount = (Button) view.findViewById(R.id.btnGetPageCount);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSaveAs = (Button) view.findViewById(R.id.btnSaveAs);
        btnHiddenToolBar = (Button) view.findViewById(R.id.btnHiddenToolBar);
        btnHiddenMenuBar = (Button) view.findViewById(R.id.btnHiddenMenuBar);
        btnIsModified = (Button) view.findViewById(R.id.btnIsModified);
        btnHandWriteComment = (Button) view.findViewById(R.id.btnHandWriteComment);
        btnChangeReviseMode = (Button) view.findViewById(R.id.btnChangeReviseMode);
        btnChangeMarkupMode = (Button) view.findViewById(R.id.btnChangeMarkupMode);
        btnAcceptAllRevision = (Button) view.findViewById(R.id.btnAcceptAllRevision);
        btnUndo = (Button) view.findViewById(R.id.btnUndo);
        btnRedo = (Button) view.findViewById(R.id.btnRedo);
        btnClearAllComments = (Button) view.findViewById(R.id.btnClearAllComments);
        btnGetCurrentPageNum = (Button) view.findViewById(R.id.btnGetCurrentPageNum);
        btnSaveCurrentPageToImage = (Button) view.findViewById(R.id.btnSaveCurrentPageToImage);
        btnSaveAllPageToImage = (Button) view.findViewById(R.id.btnSaveAllPageToImage);
        btnGetLength = (Button) view.findViewById(R.id.btnGetLength);
        btnGetScrollY = (Button) view.findViewById(R.id.btnGetScrollY);
        btnGetScrollX = (Button) view.findViewById(R.id.btnGetScrollX);
        btnAddDocumentVariable = (Button) view.findViewById(R.id.btnAddDocumentVariable);
        btnGetDocumentVariable = (Button) view.findViewById(R.id.btnGetDocumentVariable);
        btnReadOnlyProtect = (Button) view.findViewById(R.id.btnReadOnlyProtect);
        btnCommentProtect = (Button) view.findViewById(R.id.btnCommentProtect);
        btnTrackedProtect = (Button) view.findViewById(R.id.btnTrackedProtect);
        btnUnProtect = (Button) view.findViewById(R.id.btnUnProtect);
        btnIsProtectOn = (Button) view.findViewById(R.id.btnIsProtectOn);
        btnGetProtectionType = (Button) view.findViewById(R.id.btnGetProtectionType);
        btnToggleInkFinger = (Button) view.findViewById(R.id.btnToggleInkFinger);
        btnToggleInk = (Button) view.findViewById(R.id.btnToggleInk);
        btnToggleToPen = (Button) view.findViewById(R.id.btnToggleToPen);
        btnToggleToHighLightPen = (Button) view.findViewById(R.id.btnToggleToHighLightPen);
        btnToggleToEraser = (Button) view.findViewById(R.id.btnToggleToEraser);
        btnSetInkColor = (Button) view.findViewById(R.id.btnSetInkColor);
        btnGetInkColor = (Button) view.findViewById(R.id.btnGetInkColor);
        btnGetInkPenThick = (Button) view.findViewById(R.id.btnGetInkPenThick);
        btnGetInkHighLightThick = (Button) view.findViewById(R.id.btnGetInkHighLightThick);
        btnSetInkThick = (Button) view.findViewById(R.id.btnSetInkThick);
        btnDeleteShape = (Button) view.findViewById(R.id.btnDeletShape);
        btnGetPage = (Button) view.findViewById(R.id.btnGetPage);
        btnDeleteComment = (Button) view.findViewById(R.id.btnDeleteComment);
        btnAddComment = (Button) view.findViewById(R.id.btnAddComment);
        btnAddBookmark = (Button) view.findViewById(R.id.btnAddBookmark);
        btnDeleteBookmark = (Button) view.findViewById(R.id.btnDeleteBookmark);

        //以下是选区aidl操作的按钮
        btnCopy = (Button) view.findViewById(R.id.btnCopy);
        btnCut = (Button) view.findViewById(R.id.btnCut);
        btnPaste = (Button) view.findViewById(R.id.btnPaste);
        btnTypeText = (Button) view.findViewById(R.id.btnTypeText);
        btnGetText = (Button) view.findViewById(R.id.btnGetText);
        btnInsertParagraph = (Button) view.findViewById(R.id.btnInsertParagraph);
        btnGetLeft = (Button) view.findViewById(R.id.btnGetLeft);
        btnGetTop = (Button) view.findViewById(R.id.btnGetTop);
        btnGetStart = (Button) view.findViewById(R.id.btnGetStart);
        btnGetEnd = (Button) view.findViewById(R.id.btnGetEnd);
        btnSetSelection = (Button) view.findViewById(R.id.btnSetSelection);
        btngetFont = (Button) view.findViewById(R.id.btngetFont);

        //InlineShape
        btnAddInlinePicture = (Button) view.findViewById(R.id.btnAddInlinePicture);
        //Shapes
        btnAddPicture = (Button) view.findViewById(R.id.btnAddPicture);
        btnAddTextBox = (Button) view.findViewById(R.id.btnAddTextBox);
        btnAddNoneTextBox = (Button) view.findViewById(R.id.btnAddNoneTextBox);

        //font
        btnRemoveStrikeThrough = (Button) view.findViewById(R.id.btnRemoveStrikeThrough);
        btnSetStrikeThrough = (Button) view.findViewById(R.id.btnSetStrikeThrough);

        // turn page
        btnPrePage = (Button) view.findViewById(R.id.btnPrePage);
        btnNextPage = (Button) view.findViewById(R.id.btnNextPage);

        //shape 相关属性
        btnGetShapeLeft = (Button) view.findViewById(R.id.btnGetShapeLeft);
        btnGetShapeTop = (Button) view.findViewById(R.id.btnGetShapeTop);
        btnGetShapeHeight = (Button) view.findViewById(R.id.btnGetShapeHeight);
        btnGetShapeWidth = (Button) view.findViewById(R.id.btnGetShapeWidth);
        btnGetTextBoxLineColor = (Button) view.findViewById(R.id.btnGetTextBoxLineColor);
        btnGetTextBoxFLine = (Button) view.findViewById(R.id.btnGetTextBoxFLine);

        btnGetDocumentProp = (Button) view.findViewById(R.id.btnGetDocProp);
        btnGetForbittenInk = (Button) view.findViewById(R.id.btnGetForbittenInk);
        btnGetShowRevisions = (Button) view.findViewById(R.id.btnGetShowRevisions);
        btnSetPageOrientation = (Button) view.findViewById(R.id.btnSetPageOrientation);
        btnSetPageWidth = (Button) view.findViewById(R.id.btnSetPageWidth);
        btnSetPageHeight = (Button) view.findViewById(R.id.btnSetPageHeight);
        btnSetPageLeft = (Button) view.findViewById(R.id.btnSetPageLeft);
        btnSetPageRight = (Button) view.findViewById(R.id.btnSetPageRight);
        btnSetPageTop = (Button) view.findViewById(R.id.btnSetPageTop);
        btnSetPageBottom = (Button) view.findViewById(R.id.btnSetPageBottom);
        btnSavePDF = (Button) view.findViewById(R.id.btnSavePDF);

//		houjing补充案例
        btngetBookmarkText = (Button) view.findViewById(R.id.btngetBookmarkText);
        btnsetBookmarkText = (Button) view.findViewById(R.id.btnsetBookmarkText);
        btngetPageOrientation = (Button) view.findViewById(R.id.btngetPageOrientation);
        btngetPageWidth = (Button) view.findViewById(R.id.btngetPageWidth);
        btngetPageHeight = (Button) view.findViewById(R.id.btngetPageHeight);
        btngetLeftMargin = (Button) view.findViewById(R.id.btngetLeftMargin);
        btngetRightMargin = (Button) view.findViewById(R.id.btngetRightMargin);
        btngetTopMargin = (Button) view.findViewById(R.id.btngetTopMargin);
        btngetBottomMargin = (Button) view.findViewById(R.id.btngetBottomMargin);
        btngetActiveDocument = (Button) view.findViewById(R.id.btngetActiveDocument);
        btngetSelection = (Button) view.findViewById(R.id.btngetSelection);
        btnrangecopy = (Button) view.findViewById(R.id.btnrangecopy);
        btnrangepaste = (Button) view.findViewById(R.id.btnrangepaste);
        btnrangecollapse = (Button) view.findViewById(R.id.btnrangecollapse);
        btngetUnderline = (Button) view.findViewById(R.id.btngetUnderline);
        btnsetUnderline = (Button) view.findViewById(R.id.btnsetUnderline);
        btnsetStyle = (Button) view.findViewById(R.id.btnsetStyle);
        btngetStyle = (Button) view.findViewById(R.id.btngetStyle);
        btnsetItalic = (Button) view.findViewById(R.id.btnsetItalic);
        btnisItalic = (Button) view.findViewById(R.id.btnisItalic);
        btnsetBold = (Button) view.findViewById(R.id.btnsetBold);
        btnisBold = (Button) view.findViewById(R.id.btnisBold);
        btnrangegetFont = (Button) view.findViewById(R.id.btnrangegetFont);
        btnrangesetEnd = (Button) view.findViewById(R.id.btnrangesetEnd);
        btnrangegetEnd = (Button) view.findViewById(R.id.btnrangegetEnd);
        btnrangesetStart = (Button) view.findViewById(R.id.btnrangesetStart);
        btnrangegetStart = (Button) view.findViewById(R.id.btnrangegetStart);
        btnrangesetText = (Button) view.findViewById(R.id.btnrangesetText);
        btnrangegetText = (Button) view.findViewById(R.id.btnrangegetText);
        btnrangedelete = (Button) view.findViewById(R.id.btnrangedelete);
        btnrangeexpand = (Button) view.findViewById(R.id.btnrangeexpand);

        font_setBold = (Button) view.findViewById(R.id.font_setBold);
        font_setItalic = (Button) view.findViewById(R.id.font_setItalic);
        font_setTextColor = (Button) view.findViewById(R.id.font_setTextColor);
        font_part_setName = (Button) view.findViewById(R.id.font_part_setName);
        font_setSize = (Button) view.findViewById(R.id.font_setSize);
        font_setStrikeThrough = (Button) view.findViewById(R.id.font_setStrikeThrough);
        font_setDoubleStrikeThrough = (Button) view.findViewById(R.id.font_setDoubleStrikeThrough);
        font_part_setNoneStrikeThrough = (Button) view.findViewById(R.id.font_part_setNoneStrikeThrough);
        font_getSuperscript = (Button) view.findViewById(R.id.font_getSuperscript);
        font_setSuperscript = (Button) view.findViewById(R.id.font_setSuperscript);
        font_getSubscript = (Button) view.findViewById(R.id.font_getSubscript);
        font_setSubscript = (Button) view.findViewById(R.id.font_setSubscript);
        font_getBoldBi = (Button) view.findViewById(R.id.font_getBoldBi);
        font_setBoldBi = (Button) view.findViewById(R.id.font_setBoldBi);
        font_getSmallCaps = (Button) view.findViewById(R.id.font_getSmallCaps);
        font_setSmallCaps = (Button) view.findViewById(R.id.font_setSmallCaps);
        font_getAllCaps = (Button) view.findViewById(R.id.font_getAllCaps);
        font_setAllCaps = (Button) view.findViewById(R.id.font_setAllCaps);
        font_setNoneStrikeThrough = (Button) view.findViewById(R.id.font_setNoneStrikeThrough);
        font_getDoubleStrikeThrough2 = (Button) view.findViewById(R.id.font_getDoubleStrikeThrough2);
        font_setDoubleStrikeThrough2 = (Button) view.findViewById(R.id.font_setDoubleStrikeThrough2);
        font_setStrikeThrough2 = (Button) view.findViewById(R.id.font_setStrikeThrough2);
        font_shrink = (Button) view.findViewById(R.id.font_shrink);
        font_grow = (Button) view.findViewById(R.id.font_grow);
        font_getSize = (Button) view.findViewById(R.id.font_getSize);
        font2_setSize = (Button) view.findViewById(R.id.font2_setSize);
        font_getNameFarEast = (Button) view.findViewById(R.id.font_getNameFarEast);
        font_setNameFarEast = (Button) view.findViewById(R.id.font_setNameFarEast);
        font_getNameAscii = (Button) view.findViewById(R.id.font_getNameAscii);
        font_setName = (Button) view.findViewById(R.id.font_setName);
        font_getTextColor = (Button) view.findViewById(R.id.font_getTextColor);
        font2_setTextColor = (Button) view.findViewById(R.id.font2_setTextColor);
        font_getUnderlineColor = (Button) view.findViewById(R.id.font_getUnderlineColor);
        font_setUnderlineColor = (Button) view.findViewById(R.id.font_setUnderlineColor);
        font_getUnderline2 = (Button) view.findViewById(R.id.font_getUnderline2);
        font_setUnderline2 = (Button) view.findViewById(R.id.font_setUnderline2);
        font_getItalic2 = (Button) view.findViewById(R.id.font_getItalic2);
        font_setItalic2 = (Button) view.findViewById(R.id.font_setItalic2);
        font_setBold2 = (Button) view.findViewById(R.id.font_setBold2);
        font_getItalic = (Button) view.findViewById(R.id.font_getItalic);
        SelectionaddComment = (Button) view.findViewById(R.id.SelectionaddComment);
        SelectiongetShapeRange = (Button) view.findViewById(R.id.SelectiongetShapeRange);
        SelectiongetInformation = (Button) view.findViewById(R.id.SelectiongetInformation);
        SelectiongetRightOnScreen = (Button) view.findViewById(R.id.SelectiongetRightOnScreen);
        SelectiongetBottomOnScreen = (Button) view.findViewById(R.id.SelectiongetBottomOnScreen);
        InlineShapesgetWidth = (Button) view.findViewById(R.id.InlineShapesgetWidth);
        InlineShapesgetHeight = (Button) view.findViewById(R.id.InlineShapesgetHeight);
        InlineShapessetScaleWidth = (Button) view.findViewById(R.id.InlineShapessetScaleWidth);
        InlineShapessetScaleHeight = (Button) view.findViewById(R.id.InlineShapessetScaleHeight);
        InlineShapessetHeight = (Button) view.findViewById(R.id.InlineShapessetHeight);
        InlineShapessetWidth = (Button) view.findViewById(R.id.InlineShapessetWidth);
        InlineShapesgetScaleWidth = (Button) view.findViewById(R.id.InlineShapesgetScaleWidth);
        InlineShapesgetScaleHeight = (Button) view.findViewById(R.id.InlineShapesgetScaleHeight);
        InlineShapesgetOLE = (Button) view.findViewById(R.id.InlineShapesgetOLE);
        InlineShapesgetCount = (Button) view.findViewById(R.id.InlineShapesgetCount);
        InlineShapessetAlternativeText = (Button) view.findViewById(R.id.InlineShapessetAlternativeText);
        InlineShapesgetAlternativeText = (Button) view.findViewById(R.id.InlineShapesgetAlternativeText);
        InlineShapesaddOLEControl = (Button) view.findViewById(R.id.InlineShapesaddOLEControl);

//		document
        document_getName = (Button) view.findViewById(R.id.document_getName);
        document_getPage = (Button) view.findViewById(R.id.document_getPage);
        document_getPageCount = (Button) view.findViewById(R.id.document_getPageCount);
        document_getSelection = (Button) view.findViewById(R.id.document_getSelection);
        document_print = (Button) view.findViewById(R.id.document_print);
        document_save = (Button) view.findViewById(R.id.document_save);
        document_acceptAllRevisions = (Button) view.findViewById(R.id.document_acceptAllRevisions);
        document_acceptAllRevise = (Button) view.findViewById(R.id.document_acceptAllRevise);
        document_pageUp = (Button) view.findViewById(R.id.document_pageUp);
        document_pageDown = (Button) view.findViewById(R.id.document_pageDown);
        document_getCommentNumber = (Button) view.findViewById(R.id.document_getCommentNumber);
        document_isInRevisionMode = (Button) view.findViewById(R.id.document_isInRevisionMode);
        document_getUser = (Button) view.findViewById(R.id.document_getUser);
        document_isInHandWriterMode = (Button) view.findViewById(R.id.document_isInHandWriterMode);
        document_getBuiltInDocumentProperties = (Button) view.findViewById(R.id.document_getBuiltInDocumentProperties);
        document_saveToImage = (Button) view.findViewById(R.id.document_saveToImage);
        document_changeRevisionState = (Button) view.findViewById(R.id.document_changeRevisionState);
        document_close2 = (Button) view.findViewById(R.id.document_close2);
        document_delAllComments = (Button) view.findViewById(R.id.document_delAllComments);
        document_denyAllRevision = (Button) view.findViewById(R.id.document_denyAllRevision);
        document_getProtectionType2 = (Button) view.findViewById(R.id.document_getProtectionType2);
        document_setSaved = (Button) view.findViewById(R.id.document_setSaved);
        document_getViewProgress = (Button) view.findViewById(R.id.document_getViewProgress);
        document_getViewScale = (Button) view.findViewById(R.id.document_getViewScale);
        document_getViewScrollX = (Button) view.findViewById(R.id.document_getViewScrollX);
        document_getViewScrollY = (Button) view.findViewById(R.id.document_getViewScrollY);
        document_isLoadOK = (Button) view.findViewById(R.id.document_isLoadOK);
        document_protect2 = (Button) view.findViewById(R.id.document_protect2);
        document_range = (Button) view.findViewById(R.id.document_range);
        document_redo2 = (Button) view.findViewById(R.id.document_redo2);
        document_rejectAllRevisions = (Button) view.findViewById(R.id.document_rejectAllRevisions);
        document2_save = (Button) view.findViewById(R.id.document2_save);
        document_setUser = (Button) view.findViewById(R.id.document_setUser);
        document_undo2 = (Button) view.findViewById(R.id.document_undo2);
        document_undoClear = (Button) view.findViewById(R.id.document_undoClear);
        document_unProtectDocument = (Button) view.findViewById(R.id.document_unProtectDocument);
        document_getContent = (Button) view.findViewById(R.id.document_getContent);

//		shapes
        Shapes_addPicture = (Button) view.findViewById(R.id.Shapes_addPicture);
        Shapes_addPicture2 = (Button) view.findViewById(R.id.Shapes_addPicture2);
        Shapes_addTextbox2 = (Button) view.findViewById(R.id.Shapes_addTextbox2);
        Shapes_addOLEControl = (Button) view.findViewById(R.id.Shapes_addOLEControl);
        Shapes_addShape = (Button) view.findViewById(R.id.Shapes_addShape);

//		shape
        shape_getIPictureFormat = (Button) view.findViewById(R.id.shape_getIPictureFormat);
        shape_getOLE = (Button) view.findViewById(R.id.shape_getOLE);
        shape_select = (Button) view.findViewById(R.id.shape_select);
        shape_getWrapFormat = (Button) view.findViewById(R.id.shape_getWrapFormat);
        shape_hasInk = (Button) view.findViewById(R.id.shape_hasInk);
        shape_incrementTop = (Button) view.findViewById(R.id.shape_incrementTop);
        shape_incrementLeft = (Button) view.findViewById(R.id.shape_incrementLeft);
        shape_getWidth = (Button) view.findViewById(R.id.shape_getWidth);
        shape_setVisible = (Button) view.findViewById(R.id.shape_setVisible);
        shape_getVisible = (Button) view.findViewById(R.id.shape_getVisible);
        shape_getType = (Button) view.findViewById(R.id.shape_getType);
        shape_getTop = (Button) view.findViewById(R.id.shape_getTop);
        shape_getTextFrame = (Button) view.findViewById(R.id.shape_getTextFrame);
        shape_getLine = (Button) view.findViewById(R.id.shape_getLine);
        shape_getLeft = (Button) view.findViewById(R.id.shape_getLeft);
        shape_getHeight = (Button) view.findViewById(R.id.shape_getHeight);
        shape_getGroupItems = (Button) view.findViewById(R.id.shape_getGroupItems);
        shape_getFill = (Button) view.findViewById(R.id.shape_getFill);
        shape_getAlternativeText = (Button) view.findViewById(R.id.shape_getAlternativeText);
        shape_setAlternativeText = (Button) view.findViewById(R.id.shape_setAlternativeText);

//		View
        View_putRevisionsView = (Button) view.findViewById(R.id.View_putRevisionsView);
        View_putShowRevisionsAndComments = (Button) view.findViewById(R.id.View_putShowRevisionsAndComments);
        View_getShowRevisionsAndComments = (Button) view.findViewById(R.id.View_getShowRevisionsAndComments);
        View_getRevisionsView = (Button) view.findViewById(R.id.View_getRevisionsView);

//		Other
        setVisibleFill = (Button) view.findViewById(R.id.setVisibleFill);
        setVisibleLine = (Button) view.findViewById(R.id.setVisibleLine);
        getGroupItemsItem = (Button) view.findViewById(R.id.getGroupItemsItem);
        getGroup = (Button) view.findViewById(R.id.getGroup);
        getTextRange = (Button) view.findViewById(R.id.getTextRange);
        groupCount = (Button) view.findViewById(R.id.groupCount);
        getOpenDocument = (Button) view.findViewById(R.id.getOpenDocument);
        getRevision = (Button) view.findViewById(R.id.getRevision);
        btnReadMode = (Button) view.findViewById(R.id.btnReadMode);
        btnWorkbookgetName = (Button) view.findViewById(R.id.btnWorkbookgetName);
//		截止以上houjing补充
        btnfairCopy = (Button) view.findViewById(R.id.btnfairCopy);

        //增值
        view.findViewById(R.id.pic2doc).setOnClickListener(this);
        view.findViewById(R.id.pic2ET).setOnClickListener(this);
        view.findViewById(R.id.pic2PPT).setOnClickListener(this);
        view.findViewById(R.id.pic2PDF).setOnClickListener(this);
        view.findViewById(R.id.openCameraOcr).setOnClickListener(this);
        view.findViewById(R.id.pdfkit2doc).setOnClickListener(this);
        view.findViewById(R.id.pdfkitOcr2Text).setOnClickListener(this);
        view.findViewById(R.id.pdfkitSign).setOnClickListener(this);
        view.findViewById(R.id.pdfkitFileSizeReduce).setOnClickListener(this);
        view.findViewById(R.id.pdfkitMerge).setOnClickListener(this);
        view.findViewById(R.id.pdfkitExtract).setOnClickListener(this);
        view.findViewById(R.id.extractFile).setOnClickListener(this);
        view.findViewById(R.id.mergeFile).setOnClickListener(this);
        view.findViewById(R.id.pptPlayRecord).setOnClickListener(this);
        view.findViewById(R.id.documentBatchSlim).setOnClickListener(this);
        view.findViewById(R.id.openCameraDoc).setOnClickListener(this);
        view.findViewById(R.id.openCameraPPT).setOnClickListener(this);
        view.findViewById(R.id.pdfkitAnotation).setOnClickListener(this);
        view.findViewById(R.id.pdfkitDocument2pdf).setOnClickListener(this);
        view.findViewById(R.id.goShareplay).setOnClickListener(this);
    }

    private void createView() {
        File file = new File(docPath);
        txt_fileName.setText(WINDOW_NOTE + "\n" + file.getName());

        btnHideAll.setOnClickListener(this);
        btnCloseWindow.setOnClickListener(this);
        btnNewDocument.setOnClickListener(this);
        btnOpenFile.setOnClickListener(this);
        btnCloseFile.setOnClickListener(this);
        btnGetPath.setOnClickListener(this);
        btnGetPageCount.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSaveAs.setOnClickListener(this);
        btnHiddenToolBar.setOnClickListener(this);
        btnHiddenMenuBar.setOnClickListener(this);
        btnIsModified.setOnClickListener(this);
        btnHandWriteComment.setOnClickListener(this);
        btnChangeReviseMode.setOnClickListener(this);
        btnChangeMarkupMode.setOnClickListener(this);
        btnAcceptAllRevision.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnRedo.setOnClickListener(this);
        btnClearAllComments.setOnClickListener(this);
        btnGetCurrentPageNum.setOnClickListener(this);
        btnSaveCurrentPageToImage.setOnClickListener(this);
        btnSaveAllPageToImage.setOnClickListener(this);
        btnGetLength.setOnClickListener(this);
        btnGetScrollY.setOnClickListener(this);
        btnGetScrollX.setOnClickListener(this);
        btnAddDocumentVariable.setOnClickListener(this);
        btnGetDocumentVariable.setOnClickListener(this);
        btnReadOnlyProtect.setOnClickListener(this);
        btnCommentProtect.setOnClickListener(this);
        btnTrackedProtect.setOnClickListener(this);
        btnUnProtect.setOnClickListener(this);
        btnIsProtectOn.setOnClickListener(this);
        btnGetProtectionType.setOnClickListener(this);
        btnToggleInkFinger.setOnClickListener(this);
        btnToggleInk.setOnClickListener(this);
        btnToggleToPen.setOnClickListener(this);
        btnToggleToHighLightPen.setOnClickListener(this);
        btnToggleToEraser.setOnClickListener(this);
        btnSetInkColor.setOnClickListener(this);
        btnGetInkColor.setOnClickListener(this);
        btnGetInkPenThick.setOnClickListener(this);
        btnGetInkHighLightThick.setOnClickListener(this);
        btnSetInkThick.setOnClickListener(this);
        btnGetShowRevisions.setOnClickListener(this);
        btnDeleteShape.setOnClickListener(this);
        btnGetPage.setOnClickListener(this);
        btnSetPageOrientation.setOnClickListener(this);
        btnSetPageWidth.setOnClickListener(this);
        btnSetPageHeight.setOnClickListener(this);
        btnSetPageLeft.setOnClickListener(this);
        btnSetPageRight.setOnClickListener(this);
        btnSetPageTop.setOnClickListener(this);
        btnSetPageBottom.setOnClickListener(this);

        //下面是选区操作
        btnCopy.setOnClickListener(this);
        btnCut.setOnClickListener(this);
        btnPaste.setOnClickListener(this);
        btnTypeText.setOnClickListener(this);
        btnGetText.setOnClickListener(this);
        btnInsertParagraph.setOnClickListener(this);
        btnGetLeft.setOnClickListener(this);
        btnGetTop.setOnClickListener(this);
        btnGetStart.setOnClickListener(this);
        btnGetEnd.setOnClickListener(this);
        btnSetSelection.setOnClickListener(this);
        btngetFont.setOnClickListener(this);

        //InlineShape
        btnAddInlinePicture.setOnClickListener(this);
        //Shapes
        btnAddPicture.setOnClickListener(this);
        btnAddTextBox.setOnClickListener(this);
        btnAddNoneTextBox.setOnClickListener(this);

        //font
        btnRemoveStrikeThrough.setOnClickListener(this);
        btnSetStrikeThrough.setOnClickListener(this);

        btnPrePage.setOnClickListener(this);
        btnNextPage.setOnClickListener(this);

        btnGetShapeLeft.setOnClickListener(this);
        btnGetShapeTop.setOnClickListener(this);
        btnGetShapeHeight.setOnClickListener(this);
        btnGetShapeWidth.setOnClickListener(this);
        btnGetTextBoxLineColor.setOnClickListener(this);
        btnGetTextBoxFLine.setOnClickListener(this);
        btnGetDocumentProp.setOnClickListener(this);
        btnSavePDF.setOnClickListener(this);
        btnDeleteComment.setOnClickListener(this);
        btnAddComment.setOnClickListener(this);
        btnAddBookmark.setOnClickListener(this);
        btnDeleteBookmark.setOnClickListener(this);
//		houjing补充
        btnGetForbittenInk.setOnClickListener(this);
        btngetBookmarkText.setOnClickListener(this);
        btnsetBookmarkText.setOnClickListener(this);
        btngetPageOrientation.setOnClickListener(this);
        btngetPageWidth.setOnClickListener(this);
        btngetPageHeight.setOnClickListener(this);
        btngetLeftMargin.setOnClickListener(this);
        btngetRightMargin.setOnClickListener(this);
        btngetTopMargin.setOnClickListener(this);
        btngetBottomMargin.setOnClickListener(this);
        btngetActiveDocument.setOnClickListener(this);
        btngetSelection.setOnClickListener(this);
        btnrangecopy.setOnClickListener(this);
        btnrangepaste.setOnClickListener(this);
        btnrangecollapse.setOnClickListener(this);
        btngetUnderline.setOnClickListener(this);
        btnsetUnderline.setOnClickListener(this);
        btnsetStyle.setOnClickListener(this);
        btngetStyle.setOnClickListener(this);
        btnsetItalic.setOnClickListener(this);
        btnisItalic.setOnClickListener(this);
        btnsetBold.setOnClickListener(this);
        btnisBold.setOnClickListener(this);
        btnrangegetFont.setOnClickListener(this);
        btnrangesetEnd.setOnClickListener(this);
        btnrangegetEnd.setOnClickListener(this);
        btnrangesetStart.setOnClickListener(this);
        btnrangegetStart.setOnClickListener(this);
        btnrangesetText.setOnClickListener(this);
        btnrangegetText.setOnClickListener(this);
        btnrangedelete.setOnClickListener(this);
        btnrangeexpand.setOnClickListener(this);
        font_setBold.setOnClickListener(this);
        font_setItalic.setOnClickListener(this);
        font_setTextColor.setOnClickListener(this);
        font_part_setName.setOnClickListener(this);
        font_setSize.setOnClickListener(this);
        font_setStrikeThrough.setOnClickListener(this);
        font_setDoubleStrikeThrough.setOnClickListener(this);
        font_part_setNoneStrikeThrough.setOnClickListener(this);
        font_getSuperscript.setOnClickListener(this);
        font_setSuperscript.setOnClickListener(this);
        font_getSubscript.setOnClickListener(this);
        font_setSubscript.setOnClickListener(this);
        font_getBoldBi.setOnClickListener(this);
        font_setBoldBi.setOnClickListener(this);
        font_getSmallCaps.setOnClickListener(this);
        font_setSmallCaps.setOnClickListener(this);
        font_getAllCaps.setOnClickListener(this);
        font_setAllCaps.setOnClickListener(this);
        font_setNoneStrikeThrough.setOnClickListener(this);
        font_getDoubleStrikeThrough2.setOnClickListener(this);
        font_setDoubleStrikeThrough2.setOnClickListener(this);
        font_setStrikeThrough2.setOnClickListener(this);
        font_shrink.setOnClickListener(this);
        font_grow.setOnClickListener(this);
        font_getSize.setOnClickListener(this);
        font2_setSize.setOnClickListener(this);
        font_getNameFarEast.setOnClickListener(this);
        font_setNameFarEast.setOnClickListener(this);
        font_getNameAscii.setOnClickListener(this);
        font_setName.setOnClickListener(this);
        font_getTextColor.setOnClickListener(this);
        font2_setTextColor.setOnClickListener(this);
        font_getUnderlineColor.setOnClickListener(this);
        font_setUnderlineColor.setOnClickListener(this);
        font_getUnderline2.setOnClickListener(this);
        font_setUnderline2.setOnClickListener(this);
        font_getItalic2.setOnClickListener(this);
        font_setItalic2.setOnClickListener(this);
        font_setBold2.setOnClickListener(this);
        font_getItalic.setOnClickListener(this);
        SelectionaddComment.setOnClickListener(this);
        SelectiongetShapeRange.setOnClickListener(this);
        SelectiongetInformation.setOnClickListener(this);
        SelectiongetRightOnScreen.setOnClickListener(this);
        SelectiongetBottomOnScreen.setOnClickListener(this);
        InlineShapesgetWidth.setOnClickListener(this);
        InlineShapesgetHeight.setOnClickListener(this);
        InlineShapessetScaleWidth.setOnClickListener(this);
        InlineShapessetScaleHeight.setOnClickListener(this);
        InlineShapessetHeight.setOnClickListener(this);
        InlineShapessetWidth.setOnClickListener(this);
        InlineShapesgetScaleWidth.setOnClickListener(this);
        InlineShapesgetScaleHeight.setOnClickListener(this);
        InlineShapesgetOLE.setOnClickListener(this);
        InlineShapesgetCount.setOnClickListener(this);
        InlineShapessetAlternativeText.setOnClickListener(this);
        InlineShapesgetAlternativeText.setOnClickListener(this);
        InlineShapesaddOLEControl.setOnClickListener(this);
//		document
        document_getName.setOnClickListener(this);
        document_getPage.setOnClickListener(this);
        document_getPageCount.setOnClickListener(this);
        document_getSelection.setOnClickListener(this);
        document_print.setOnClickListener(this);
        document_save.setOnClickListener(this);
        document_acceptAllRevisions.setOnClickListener(this);
        document_acceptAllRevise.setOnClickListener(this);
        document_pageUp.setOnClickListener(this);
        document_pageDown.setOnClickListener(this);
        document_getCommentNumber.setOnClickListener(this);
        document_isInRevisionMode.setOnClickListener(this);
        document_getUser.setOnClickListener(this);
        document_isInHandWriterMode.setOnClickListener(this);
        document_getBuiltInDocumentProperties.setOnClickListener(this);
        document_saveToImage.setOnClickListener(this);
        document_changeRevisionState.setOnClickListener(this);
        document_close2.setOnClickListener(this);
        document_delAllComments.setOnClickListener(this);
        document_denyAllRevision.setOnClickListener(this);
        document_getProtectionType2.setOnClickListener(this);
        document_setSaved.setOnClickListener(this);
        document_getViewProgress.setOnClickListener(this);
        document_getViewScale.setOnClickListener(this);
        document_getViewScrollX.setOnClickListener(this);
        document_getViewScrollY.setOnClickListener(this);
        document_isLoadOK.setOnClickListener(this);
        document_protect2.setOnClickListener(this);
        document_range.setOnClickListener(this);
        document_redo2.setOnClickListener(this);
        document_rejectAllRevisions.setOnClickListener(this);
        document2_save.setOnClickListener(this);
        document_setUser.setOnClickListener(this);
        document_undo2.setOnClickListener(this);
        document_undoClear.setOnClickListener(this);
        document_unProtectDocument.setOnClickListener(this);
        document_getContent.setOnClickListener(this);

//		Shapes
        Shapes_addPicture.setOnClickListener(this);
        Shapes_addPicture2.setOnClickListener(this);
        Shapes_addTextbox2.setOnClickListener(this);
        Shapes_addOLEControl.setOnClickListener(this);
        Shapes_addShape.setOnClickListener(this);

//		shape
        shape_getIPictureFormat.setOnClickListener(this);
        shape_getOLE.setOnClickListener(this);
        shape_select.setOnClickListener(this);
        shape_getWrapFormat.setOnClickListener(this);
        shape_hasInk.setOnClickListener(this);
        shape_incrementTop.setOnClickListener(this);
        shape_incrementLeft.setOnClickListener(this);
        shape_getWidth.setOnClickListener(this);
        shape_setVisible.setOnClickListener(this);
        shape_getVisible.setOnClickListener(this);
        shape_getType.setOnClickListener(this);
        shape_getTop.setOnClickListener(this);
        shape_getTextFrame.setOnClickListener(this);
        shape_getLine.setOnClickListener(this);
        shape_getLeft.setOnClickListener(this);
        shape_getHeight.setOnClickListener(this);
        shape_getGroupItems.setOnClickListener(this);
        shape_getFill.setOnClickListener(this);
        shape_getAlternativeText.setOnClickListener(this);
        shape_setAlternativeText.setOnClickListener(this);

//		View
        View_putRevisionsView.setOnClickListener(this);
        View_putShowRevisionsAndComments.setOnClickListener(this);
        View_getShowRevisionsAndComments.setOnClickListener(this);
        View_getRevisionsView.setOnClickListener(this);

//		Other
        setVisibleFill.setOnClickListener(this);
        setVisibleLine.setOnClickListener(this);
        getGroupItemsItem.setOnClickListener(this);
        getGroup.setOnClickListener(this);
        getTextRange.setOnClickListener(this);
        groupCount.setOnClickListener(this);
        getOpenDocument.setOnClickListener(this);
        getRevision.setOnClickListener(this);
        btnReadMode.setOnClickListener(this);
        btnWorkbookgetName.setOnClickListener(this);
//		houjing补充以上
        btnfairCopy.setOnClickListener(this);

        txt_fileName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                FloatingFunc.onTouchEvent(arg1, txt_fileName);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
//		try {
//			mDoc.getApplication().getActiveDocument();
//		} catch (RemoteException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();
//		}
        int id = v.getId();
        if (id == R.id.btnHideAll) {
            hideAll();
        } else if (id == R.id.btnCloseWindow) {
            this.stopSelf();
        } else if (id == R.id.btnNewDocument) {
            newDocment();
        } else if (id == R.id.btnOpenFile) {
            if (Util.isPDFFile(docPath)) {
                // openPDFDoc();
                openDocument();
            } else if (Util.isPptFile(docPath)) {
                openPresentation();
            } else if (Util.isExcelFile(docPath)) {
                openWorkBook();
            } else {
                openDocument();
            }
        } else if (id == R.id.btnCloseFile) {
            if (Util.isPDFFile(docPath)) {
                closePDFDoc();
            } else if (Util.isPptFile(docPath)) {
                closePresentation();
            } else if (Util.isExcelFile(docPath)) {
                closeWorkBook();
            } else {
                closeFile();
            }
        } else if (id == R.id.btnGetPath) {
            getPath();
        } else if (id == R.id.btnGetPageCount) {
            getPageCount();
        } else if (id == R.id.btnSave) {
            saveDocument();
        } else if (id == R.id.btnSaveAs) {
            saveAsDocument();
        } else if (id == R.id.btnHiddenToolBar) {
            hiddenToolBar();
        } else if (id == R.id.btnHiddenMenuBar) {
            hiddenMenuBar();
        } else if (id == R.id.btnIsModified) {
            isModified();
        } else if (id == R.id.btnHandWriteComment) {
            handWriteComment();
        } else if (id == R.id.btnChangeReviseMode) {
            changeRevisionMode();
        } else if (id == R.id.btnChangeMarkupMode) {
            changeMarkupMode();
        } else if (id == R.id.btnAcceptAllRevision) {
            acceptAllRevision();
        } else if (id == R.id.btnUndo) {
            undo();
        } else if (id == R.id.btnRedo) {
            redo();
        } else if (id == R.id.btnClearAllComments) {
            clearAllComments();
        } else if (id == R.id.btnGetCurrentPageNum) {
            getCurrentPageNum();
        } else if (id == R.id.btnSaveCurrentPageToImage) {
            saveCurrentPageToImage();
        } else if (id == R.id.btnSaveAllPageToImage) {
            saveAllPageToImage();
        } else if (id == R.id.btnGetLength) {
            getLength();
        } else if (id == R.id.btnGetScrollY) {
            getScrollY();
        } else if (id == R.id.btnGetScrollX) {
            getScrollX();

            //以下是选区相关操作
        } else if (id == R.id.btnCopy) {
            copy();
        } else if (id == R.id.btnCut) {
            cut();
        } else if (id == R.id.btnPaste) {
            paste();
        } else if (id == R.id.btnTypeText) {
            typeText();
        } else if (id == R.id.btnGetText) {
            getText();
        } else if (id == R.id.btnInsertParagraph) {
            insertParagraph();
        } else if (id == R.id.btnGetLeft) {
            getLeft();
        } else if (id == R.id.btnGetTop) {
            getTop();
        } else if (id == R.id.btnGetStart) {
            getStart();
        } else if (id == R.id.btnGetEnd) {
            getEnd();
        } else if (id == R.id.btnSetSelection) {
            setSelection();
        } else if (id == R.id.btngetFont) {
            getFont();
            //InlineShape
        } else if (id == R.id.btnAddInlinePicture) {
            addInlinePicture();
            //Shapes
        } else if (id == R.id.btnAddPicture) {
            addPicture();
        } else if (id == R.id.btnAddTextBox) {
            addTextBox();
        } else if (id == R.id.btnAddNoneTextBox) {
            addNoneTextBox();
        } else if (id == R.id.btnAddDocumentVariable) {
            addDocumentVariable();
        } else if (id == R.id.btnGetDocumentVariable) {
            getDocumentVariable();
        } else if (id == R.id.btnReadOnlyProtect) {
            protectDocument(READONLY);
        } else if (id == R.id.btnCommentProtect) {
            protectDocument(COMMENTS);
        } else if (id == R.id.btnTrackedProtect) {
            protectDocument(TRACKEDCHANGES);
        } else if (id == R.id.btnUnProtect) {
            unProtectDocument(PROTECT_PASSWORD);
        } else if (id == R.id.btnIsProtectOn) {
            isProtectOn();
        } else if (id == R.id.btnGetProtectionType) {
            getProtectionType();
        } else if (id == R.id.btnRemoveStrikeThrough) {
            removeStrikeThrough();
        } else if (id == R.id.btnSetStrikeThrough) {
            setStrikeThrough();
        } else if (id == R.id.btnPrePage) {
            turnPrePage();
        } else if (id == R.id.btnNextPage) {
            turnNextPage();
        } else if (id == R.id.btnGetShapeLeft) {
            getShapeLeft();
        } else if (id == R.id.btnGetShapeTop) {
            getShapeTop();
        } else if (id == R.id.btnGetShapeHeight) {
            getShapeHeight();
        } else if (id == R.id.btnGetShapeWidth) {
            getShapeWidth();
        } else if (id == R.id.btnGetTextBoxLineColor) {
            getTextBoxLineColor();
        } else if (id == R.id.btnGetTextBoxFLine) {
            getTextBoxFLine();
        } else if (id == R.id.btnToggleInkFinger) {
            toggleInkFinger();
        } else if (id == R.id.btnToggleInk) {
            toggleInk();
        } else if (id == R.id.btnToggleToPen) {
            toggleToPen();
        } else if (id == R.id.btnToggleToHighLightPen) {
            toggleToHighLightPen();
        } else if (id == R.id.btnToggleToEraser) {
            toggleToEraser();
        } else if (id == R.id.btnSetInkColor) {
            setInkColor();
        } else if (id == R.id.btnGetInkColor) {
            getInkColor();
        } else if (id == R.id.btnGetInkPenThick) {
            getInkPenThick();
        } else if (id == R.id.btnGetInkHighLightThick) {
            getInkHighLightThick();
        } else if (id == R.id.btnSetInkThick) {
            setInkThick();
        } else if (id == R.id.btnGetForbittenInk) {
            getForbittenInk();
        } else if (id == R.id.btnGetShowRevisions) {
            getShowRevisions();
        } else if (id == R.id.btnDeletShape) {
            deleteShape();
        } else if (id == R.id.btnGetDocProp) {
            getDocProp();
        } else if (id == R.id.btnGetPage) {
            getPage();
        } else if (id == R.id.btnSetPageOrientation) {
            setPageOrientation();
        } else if (id == R.id.btnSetPageWidth) {
            setPageWidth();
        } else if (id == R.id.btnSetPageHeight) {
            setPageHeigth();
        } else if (id == R.id.btnSetPageLeft) {
            setPageLeft();
        } else if (id == R.id.btnSetPageRight) {
            setPageRight();
        } else if (id == R.id.btnSetPageTop) {
            setPageTop();
        } else if (id == R.id.btnSetPageBottom) {
            setPageBottom();
        } else if (id == R.id.btnSavePDF) {
            saveAsPDF();
        } else if (id == R.id.btnAddComment) {
            addComment();
        } else if (id == R.id.btnDeleteComment) {
            deleteComment();
        } else if (id == R.id.btnAddBookmark) {
            addBookmark();
        } else if (id == R.id.btnDeleteBookmark) {
            deleteBookmark();
            //		houjing补充
        } else if (id == R.id.btngetBookmarkText) {
            getBookmarkText();
        } else if (id == R.id.btnsetBookmarkText) {
            setBookmarkText();
        } else if (id == R.id.btngetPageOrientation) {
            getPageOrientation();
        } else if (id == R.id.btngetPageWidth) {
            getPageWidth();
        } else if (id == R.id.btngetPageHeight) {
            getPageHeight();
        } else if (id == R.id.btngetLeftMargin) {
            getLeftMargin();
        } else if (id == R.id.btngetRightMargin) {
            getRightMargin();
        } else if (id == R.id.btngetTopMargin) {
            getTopMargin();
        } else if (id == R.id.btngetBottomMargin) {
            getBottomMargin();
        } else if (id == R.id.btngetActiveDocument) {
            getActiveDocument();
        } else if (id == R.id.btngetSelection) {
            getSelection();
        } else if (id == R.id.btnrangecopy) {
            rangecopy();
        } else if (id == R.id.btnrangepaste) {
            rangepaste();
        } else if (id == R.id.btnrangecollapse) {
            rangecollapse();
        } else if (id == R.id.btngetUnderline) {
            getUnderline();
        } else if (id == R.id.btnsetUnderline) {
            setUnderline();
        } else if (id == R.id.btnsetStyle) {
            setStyle();
        } else if (id == R.id.btngetStyle) {
            getStyle();
        } else if (id == R.id.btnsetItalic) {
            setItalic();
        } else if (id == R.id.btnisItalic) {
            rangeIsItalic();
        } else if (id == R.id.btnsetBold) {
            setBold();
        } else if (id == R.id.btnisBold) {
            isBold();
        } else if (id == R.id.btnrangegetFont) {
            rangegetFont();
        } else if (id == R.id.btnrangesetEnd) {
            rangesetEnd();
        } else if (id == R.id.btnrangegetEnd) {
            rangegetEnd();
        } else if (id == R.id.btnrangesetStart) {
            rangesetStart();
        } else if (id == R.id.btnrangegetStart) {
            rangegetStart();
        } else if (id == R.id.btnrangesetText) {
            rangesetText();
        } else if (id == R.id.btnrangegetText) {
            rangegetText();
        } else if (id == R.id.btnrangedelete) {
            rangedelete();
        } else if (id == R.id.btnrangeexpand) {
            rangeexpand();
        } else if (id == R.id.font_setBold) {
            font_setBold();
        } else if (id == R.id.font_setItalic) {
            font_setItalic();
        } else if (id == R.id.font_setTextColor) {
            font_setTextColor();
        } else if (id == R.id.font_part_setName) {
            font_part_setName();
        } else if (id == R.id.font_setStrikeThrough) {
            font_setStrikeThrough();
        } else if (id == R.id.font_setDoubleStrikeThrough) {
            font_setDoubleStrikeThrough();
            //		case R.id.font_part_setNoneStrikeThrough:
//			font_setNoneStrikeThrough();
//			break;
        } else if (id == R.id.font_getSuperscript) {
            font_getSuperscript();
        } else if (id == R.id.font_setSuperscript) {
            font_setSuperscript();
        } else if (id == R.id.font_getSubscript) {
            font_getSubscript();
        } else if (id == R.id.font_setSubscript) {
            font_setSubscript();
        } else if (id == R.id.font_getBoldBi) {
            font_getBoldBi();
        } else if (id == R.id.font_setBoldBi) {
            font_setBoldBi();
        } else if (id == R.id.font_getSmallCaps) {
            font_getSmallCaps();
        } else if (id == R.id.font_setSmallCaps) {
            font_setSmallCaps();
        } else if (id == R.id.font_getAllCaps) {
            font_getAllCaps();
        } else if (id == R.id.font_setAllCaps) {
            font_setAllCaps();
            //		case R.id.font_setNoneStrikeThrough:
//			font_setNoneStrikeThrough();
//			break;
        } else if (id == R.id.font_getDoubleStrikeThrough2) {
            font_getDoubleStrikeThrough2();
        } else if (id == R.id.font_setDoubleStrikeThrough2) {
            font_setDoubleStrikeThrough2();
        } else if (id == R.id.font_setStrikeThrough2) {
            font_setStrikeThrough2();
        } else if (id == R.id.font_shrink) {
            font_shrink();
        } else if (id == R.id.font_grow) {
            font_grow();
        } else if (id == R.id.font_getSize) {
            font_getSize();
        } else if (id == R.id.font2_setSize) {
            font_setSize();
        } else if (id == R.id.font_getNameFarEast) {
            font_getNameFarEast();
        } else if (id == R.id.font_setNameFarEast) {
            font_setNameFarEast();
        } else if (id == R.id.font_getNameAscii) {
            font_getNameAscii();
        } else if (id == R.id.font_setName) {
            font_setName();
        } else if (id == R.id.font_getTextColor) {
            font_getTextColor();
            //		case R.id.font2_setTextColor:
//			font2_setTextColor();
//			break;
        } else if (id == R.id.font_getUnderlineColor) {
            font_getUnderlineColor();
        } else if (id == R.id.font_setUnderlineColor) {
            font_setUnderlineColor();
        } else if (id == R.id.font_getUnderline2) {
            font_getUnderline2();
        } else if (id == R.id.font_setUnderline2) {
            font_setUnderline2();
        } else if (id == R.id.font_getItalic2) {
            font_getItalic2();
        } else if (id == R.id.font_setItalic2) {
            font_setItalic2();
        } else if (id == R.id.font_setBold2) {
            font_setBold2();
        } else if (id == R.id.font_getItalic) {
            font_getItalic();
        } else if (id == R.id.SelectionaddComment) {
            SelectionaddComment();
        } else if (id == R.id.SelectiongetShapeRange) {
            SelectiongetShapeRange();
        } else if (id == R.id.SelectiongetInformation) {
            SelectiongetInformation();
        } else if (id == R.id.SelectiongetRightOnScreen) {
            SelectiongetRightOnScreen();
        } else if (id == R.id.SelectiongetBottomOnScreen) {
            SelectiongetBottomOnScreen();
        } else if (id == R.id.InlineShapesgetWidth) {
            InlineShapesgetWidth();
        } else if (id == R.id.InlineShapesgetHeight) {
            InlineShapesgetHeight();
        } else if (id == R.id.InlineShapessetScaleWidth) {
            InlineShapessetScaleWidth();
        } else if (id == R.id.InlineShapessetScaleHeight) {
            InlineShapessetScaleHeight();
        } else if (id == R.id.InlineShapessetHeight) {
            InlineShapessetHeight();
        } else if (id == R.id.InlineShapessetWidth) {
            InlineShapessetWidth();
        } else if (id == R.id.InlineShapesgetScaleWidth) {
            InlineShapesgetScaleWidth();
        } else if (id == R.id.InlineShapesgetScaleHeight) {
            InlineShapesgetScaleHeight();
        } else if (id == R.id.InlineShapesgetOLE) {
            InlineShapesgetOLE();
        } else if (id == R.id.InlineShapesgetCount) {
            InlineShapesgetCount();
            //		case R.id.InlineShapessetAlternativeText:
//			InlineShapessetAlternativeText();
//			break;
        } else if (id == R.id.InlineShapesgetAlternativeText) {
            InlineShapesgetAlternativeText();
        } else if (id == R.id.InlineShapesaddOLEControl) {
            InlineShapesaddOLEControl();
            //document
        } else if (id == R.id.document_getName) {
            document_getName();
        } else if (id == R.id.document_getPage) {
            document_getPage();
        } else if (id == R.id.document_getPageCount) {
            document_getPageCount();
        } else if (id == R.id.document_getSelection) {
            document_getSelection();
        } else if (id == R.id.document_print) {
            document_print();
        } else if (id == R.id.document_save) {
            document_save();
        } else if (id == R.id.document_acceptAllRevisions) {
            document_acceptAllRevisions();
        } else if (id == R.id.document_acceptAllRevise) {
            document_acceptAllRevise();
        } else if (id == R.id.document_pageUp) {
            document_pageUp();
        } else if (id == R.id.document_pageDown) {
            document_pageDown();
        } else if (id == R.id.document_getCommentNumber) {
            document_getCommentNumber();
        } else if (id == R.id.document_isInRevisionMode) {
            document_isInRevisionMode();
        } else if (id == R.id.document_getUser) {
            document_getUser();
        } else if (id == R.id.document_isInHandWriterMode) {
            document_isInHandWriterMode();
        } else if (id == R.id.document_getBuiltInDocumentProperties) {
            document_getBuiltInDocumentProperties();
        } else if (id == R.id.document_saveToImage) {
            document_saveToImage();
        } else if (id == R.id.document_changeRevisionState) {
            document_changeRevisionState();
        } else if (id == R.id.document_close2) {
            document_close2();
        } else if (id == R.id.document_delAllComments) {
            document_delAllComments();
        } else if (id == R.id.document_denyAllRevision) {
            document_denyAllRevision();
        } else if (id == R.id.document_getProtectionType2) {
            document_getProtectionType2();
        } else if (id == R.id.document_setSaved) {
            document_setSaved();
        } else if (id == R.id.document_getViewProgress) {
            document_getViewProgress();
        } else if (id == R.id.document_getViewScale) {
            document_getViewScale();
        } else if (id == R.id.document_getViewScrollX) {
            document_getViewScrollX();
        } else if (id == R.id.document_getViewScrollY) {
            document_getViewScrollY();
        } else if (id == R.id.document_isLoadOK) {
            document_isLoadOK();
        } else if (id == R.id.document_protect2) {
            document_protect2();
        } else if (id == R.id.document_range) {
            document_range();
        } else if (id == R.id.document_redo2) {
            document_redo2();
        } else if (id == R.id.document_rejectAllRevisions) {
            document_rejectAllRevisions();
        } else if (id == R.id.document2_save) {
            save();
        } else if (id == R.id.document_setUser) {
            document_setUser();
        } else if (id == R.id.document_undo2) {
            document_undo2();
        } else if (id == R.id.document_undoClear) {
            document_undoClear();
        } else if (id == R.id.document_unProtectDocument) {
            document_unProtectDocument();
        } else if (id == R.id.document_getContent) {
            document_getContent();
            //Shapes
        } else if (id == R.id.Shapes_addPicture) {
            Shapes_addPicture();
        } else if (id == R.id.Shapes_addPicture2) {
            Shapes_addPicture2();
        } else if (id == R.id.Shapes_addTextbox2) {
            Shapes_addTextbox2();
        } else if (id == R.id.Shapes_addOLEControl) {
            Shapes_addOLEControl();
        } else if (id == R.id.Shapes_addShape) {
            Shapes_addShape();

//shape
        } else if (id == R.id.shape_getIPictureFormat) {
            shape_getIPictureFormat();
        } else if (id == R.id.shape_getOLE) {
            shape_getOLE();
        } else if (id == R.id.shape_select) {
            shape_select();
        } else if (id == R.id.shape_getWrapFormat) {
            shape_getWrapFormat();
        } else if (id == R.id.shape_hasInk) {
            shape_hasInk();
        } else if (id == R.id.shape_incrementTop) {
            shape_incrementTop();
        } else if (id == R.id.shape_incrementLeft) {
            shape_incrementLeft();
        } else if (id == R.id.shape_getWidth) {
            shape_getWidth();
        } else if (id == R.id.shape_setVisible) {
            shape_setVisible();
        } else if (id == R.id.shape_getVisible) {
            shape_getVisible();
        } else if (id == R.id.shape_getType) {
            shape_getType();
        } else if (id == R.id.shape_getTop) {
            shape_getTop();
        } else if (id == R.id.shape_getTextFrame) {
            shape_getTextFrame();
        } else if (id == R.id.shape_getLine) {
            shape_getLine();
        } else if (id == R.id.shape_getLeft) {
            shape_getLeft();
        } else if (id == R.id.shape_getHeight) {
            shape_getHeight();
        } else if (id == R.id.shape_getGroupItems) {
            shape_getGroupItems();
        } else if (id == R.id.shape_getFill) {
            shape_getFill();
        } else if (id == R.id.shape_getAlternativeText) {
            shape_getAlternativeText();
        } else if (id == R.id.shape_setAlternativeText) {
            shape_setAlternativeText();

//View
        } else if (id == R.id.View_putRevisionsView) {
            View_putRevisionsView();
        } else if (id == R.id.View_putShowRevisionsAndComments) {
            View_putShowRevisionsAndComments();
        } else if (id == R.id.View_getShowRevisionsAndComments) {
            View_getShowRevisionsAndComments();
        } else if (id == R.id.View_getRevisionsView) {
            View_getRevisionsView();

//						Other
        } else if (id == R.id.setVisibleFill) {
            setVisibleFill();
        } else if (id == R.id.setVisibleLine) {
            setVisibleLine();
        } else if (id == R.id.getGroupItemsItem) {
            getGroupItemsItem();
        } else if (id == R.id.getGroup) {
            getGroup();
        } else if (id == R.id.getTextRange) {
            getTextRange();
        } else if (id == R.id.groupCount) {
            groupCount();
        } else if (id == R.id.getOpenDocument) {
            getOpenDocument();
        } else if (id == R.id.getRevision) {
            getRevision();
        } else if (id == R.id.btnReadMode) {
            changeReadMode();
        } else if (id == R.id.btnWorkbookgetName) {
            WorkbookgetName();
        } else if (id == R.id.btnfairCopy) {
            docFairCopy();
        } else if (id == R.id.pic2doc || id == R.id.pic2ET || id == R.id.pic2PPT || id == R.id.pic2PDF || id == R.id.openCameraOcr || id == R.id.pdfkit2doc || id == R.id.pdfkitOcr2Text || id == R.id.pdfkitSign || id == R.id.pdfkitFileSizeReduce || id == R.id.pdfkitMerge || id == R.id.pdfkitExtract || id == R.id.extractFile || id == R.id.mergeFile || id == R.id.pptPlayRecord || id == R.id.documentBatchSlim || id == R.id.openCameraDoc || id == R.id.openCameraPPT || id == R.id.pdfkitAnotation || id == R.id.pdfkitDocument2pdf || id == R.id.goShareplay) {
            PremiumUtils.doPremium(v.getId(), mContext, mService, docPath);
            //		以上houjing补充
        }
    }

    private void getOpenDocument() {
        if (mService == null)
            return;

        try {
            int count = mService.getDocuments().getCount();
            for (int i = 0; i < count; i++) {
                mService.getDocuments().getDocument(i + 1).getName();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    boolean isOn = true;

    private void changeReadMode() {
        if (mService == null)
            return;
        try {
            mDoc.switchReadMode(isOn);
            isOn = !isOn;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void getRevision() {
        if (mService == null)
            return;

        try {
            ArrayList<String> revisionList = new ArrayList<String>();
            for (int i = 0; i < mDoc.getRevisions().getCount(); i++) {
                StringBuffer strBuff = new StringBuffer();
                Revision revision = mDoc.getRevisions().item(i + 1);
                String Author = revision.getAuthor();
                String Date = revision.getDate();
                strBuff.append(Date);
                strBuff.append(Author);
                strBuff.append("Revision: type == ");
                strBuff.append(revision.getType());
                strBuff.append("Range[");
                strBuff.append(revision.getRange().getStart());
                strBuff.append(",");
                strBuff.append(revision.getRange().getEnd());
                strBuff.append("]");
                strBuff.append(revision.getRange().getText());
                revisionList.add(strBuff.toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // 新建文档
    private void newDocment() {
        if (mService == null)
            return;

        if (null != mDoc) {
            Util.showToast(mContext, "文档已经打开，请先关闭文档再新建！");
            return;
        }

        LoadDocThread mythreadNew = new LoadDocThread(NEW_DOCUMENT_PATH, NEW_DOCUMENT);
        mythreadNew.start();
    }

    // 打开文档
    private void openDocument() {
        if (mService == null)
            return;

        LoadDocThread mythread = new LoadDocThread(docPath, OPEN_DOCUMENT);
        mythread.start();
    }

    //关闭文档
    private void closeFile() {
        if (!isDocumentOpened() && !isPDFDocOpened() &&
                !isWorkBookOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (Util.isPDFFile(docPath)) {
                if (!(mPdfReader == null)) {
                    Util.showToast(mContext, "mPdfReader获取不为空");
                    mPdfReader.close();
                }
            } else if (Util.isExcelFile(docPath)) {
                mWorkBook.close();
            } else if (Util.isPptFile(docPath)) {
                mPresentation.close();
            } else {
                mDoc.close();
                mDoc = null;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //文档路径
    private void getPath() {
        if (!isDocumentOpened() && !isPDFDocOpened() &&
                !isWorkBookOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String path = null;
            if (Util.isPDFFile(docPath)) {
                path = mPdfReader.getPath();
            } else if (Util.isExcelFile(docPath)) {
                path = mWorkBook.getPath();
            } else if (Util.isPptFile(docPath)) {
                path = mPresentation.getPath();
            } else {
                path = mDoc.getPath();
            }
            Util.showToast(mContext, path);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //文档页数
    private void getPageCount() {
        if (!isDocumentOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int pageCount = mDoc.getPageCount();
            String message = "文档页数为 ： " + pageCount;
            Util.showToast(mContext, message);
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            int pageCount = mPresentation.getPageCount();
            String message = "文档页数为 ： " + pageCount;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            int pageCount = 0;
            for (int i = 0, size = mWorkBook.getSheetCount(); i < size; ++i) {
                cn.wps.moffice.service.spreadsheet.Worksheet workSheet = mWorkBook.getWorksheet(i);
                pageCount += workSheet.getPageCount();
            }
            String message = "文档页数为 ： " + pageCount;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }
    }

    //保存文档
    private void saveDocument() {
        if (!isDocumentOpened() && !isWorkBookOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (Util.isExcelFile(docPath)) {
                mWorkBook.save();
            } else if (Util.isPptFile(docPath)) {
                mPresentation.save();
            } else {
                mDoc.save(true);
            }

            Util.showToast(mContext, "保存成功");
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //另存文档
    private void saveAsDocument() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.saveAs(SAVE_AS_PATH, SaveFormat.DOC, "", "");

            Util.showToast(mContext, "另存成功，另存路径为 ： " + SAVE_AS_PATH);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void saveAsPDF() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String SAVE_PDF_PATH = "/sdcard/专业版另存文档.pdf";

            mDoc.saveAs(SAVE_PDF_PATH, SaveFormat.PDF, "", "");

            Util.showToast(mContext, "另存成功，另存路径为 ： " + SAVE_AS_PATH);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //隐藏工具栏
    private void hiddenToolBar() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.hiddenToolBar();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //隐藏菜单栏
    private void hiddenMenuBar() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.hiddenMenuBar();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //是否修改
    private void isModified() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            boolean isModified = mDoc.isModified();
            String message = "是否修改  ： " + isModified;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //操作手绘
    private void handWriteComment() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mShowHandWriteComment) {
                mDoc.closeHandWriteComment();
                mShowHandWriteComment = false;
                btnHandWriteComment.setText("打开手绘");
            } else {
                mDoc.showHandWriteComment();
                mShowHandWriteComment = true;
                btnHandWriteComment.setText("关闭手绘");
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //进入或退出修订模式
    private void changeRevisionMode() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mEnterRevisionMode) {
                mDoc.exitReviseMode();
                mEnterRevisionMode = false;
                btnChangeReviseMode.setText("进入修订模式");
            } else {
                mDoc.enterReviseMode();
                mEnterRevisionMode = true;
                btnChangeReviseMode.setText("退出修订模式");
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private Boolean mChangeMarkupMode = true;

    private void changeMarkupMode() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mChangeMarkupMode) {
                mDoc.getActiveWindow().getView().setMarkupMode(WdRevisionsMode.wdInLineRevisions);
                mChangeMarkupMode = false;
                btnChangeMarkupMode.setText("在批注框中显示修订");
            } else {
                mDoc.getActiveWindow().getView().setMarkupMode(WdRevisionsMode.wdBalloonRevisions);
                mChangeMarkupMode = true;
                btnChangeMarkupMode.setText("显示嵌入式修订");
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //接受所有修订
    private void clearAllComments() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.clearAllComments();
            Util.showToast(mContext, "删除所有批注成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //撤销
    private void undo() {
        if (!isDocumentOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.undo();

            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
//		if (isPresentationOpened()) {
//			try {
////				mPresentation.undo();
//
//			} catch (RemoteException e) {
//				Util.showToast(mContext, FAIL_NOTE);
//				e.printStackTrace();
//			}
//		}
        if (isWorkBookOpened()) {
            try {
                mWorkBook.undo();

            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    //还原
    private void redo() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.redo();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //接受所有修订
    private void acceptAllRevision() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.acceptAllRevision();
            Util.showToast(mContext, "接受所有修订成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //当前页页码
    private void getCurrentPageNum() {
        if (!isDocumentOpened() && !isPDFDocOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int number = Util.isPDFFile(docPath) ? mPdfReader.getCurrentPageNum() : mDoc.getCurrentPageNum(0);   //参数0没有什么意义，为后期功能兼容保留的
            String message = "当前页页码为  ： " + number;
            Util.showToast(mContext, message);
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            int number = mPresentation.getCurrentPageIndex();   //参数0没有什么意义，为后期功能兼容保留的
            String message = "当前页页码为  ： " + number;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            Worksheet workSheet = mWorkBook.getActiveSheet();
            int number = workSheet.getCurrentPageIndex();
            String message = "当前页页码为  ： " + number;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }
    }

    //截图当前页
    private void saveCurrentPageToImage() {
        if (!isDocumentOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.saveCurrentPageToImage(SAVE_CURRUNT_PAGE, PictureFormat.PNG, 0, 0,
                    PrintOutItem.wdPrintContent, Color.WHITE, 1190, 1682);
            Util.showToast(mContext, "截图成功，图片已经保存到 ： " + SAVE_CURRUNT_PAGE);
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            String result = mPresentation.exportCurPageToImage(SAVE_CURRUNT_PAGE, PictureFormat.PNG);
            Util.showToast(mContext, "截图成功，图片已经保存到 ： " + result);
            return;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Throwable e) {

        }

        try {
            Worksheet workSheet = mWorkBook.getActiveSheet();
//			boolean result = workSheet.saveCurrentPage(SAVE_CURRUNT_PAGE, PictureFormat.JPEG);
//			Util.showToast(mContext, "截图成功，图片已经保存 ： " + result);
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        } catch (Throwable e) {

        }
    }

    private void saveAllPageToImage() {
        if (!isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            List<String> paths = mPresentation.exportImage(SAVE_IMAGE_PATH,
                    PictureFormat.PNG, new PrintProgress() {

                        @Override
                        public IBinder asBinder() {
                            // TODO Auto-generated method stub
                            return null;
                        }

                        @Override
                        public boolean isCanceled() throws RemoteException {
                            // TODO Auto-generated method stub
                            return false;
                        }

                        @Override
                        public void exportProgress(int progress)
                                throws RemoteException {
                            // TODO Auto-generated method stub

                        }
                    });
            Util.showToast(mContext, paths.toString());
            return;
        } catch (RemoteException e) {

        } catch (Exception e) {

        }

        try {
            for (int i = 0, size = mWorkBook.getSheetCount(); i < size; ++i) {
                Worksheet workSheet = mWorkBook.getWorksheet(i);
                workSheet.saveToImage(SAVE_IMAGE_PATH, PictureFormat.PNG, 100, 1);
            }
            //可用新加的一种方法
//			mWorkBook.saveToImage(SAVE_IMAGE_PATH, PictureFormat.PNG, 100, 1);
            Util.showToast(mContext, SAVE_IMAGE_PATH);
            return;
        } catch (RemoteException e) {

        } catch (Exception e) {

        }
    }

    //文档长度
    private void getLength() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int length = mDoc.getLength();
            String message = "文档长度为  ： " + length;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //当前文档已经划过的Y长度
    private void getScrollY() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float y = mDoc.getScrollY();
            String message = "已经划过的y长度  ： " + y;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //当前文档已经划过的Y长度
    private void getScrollX() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float x = mDoc.getScrollX();
            String message = "已经划过的x长度  ： " + x;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //添加文档属性
    private void addDocumentVariable() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        addDocumentVariableDialog();
        Util.showToast(mContext, "插入成功");
    }

    //获得文档属性
    private void getDocumentVariable() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        getDocumentVariableDialog();
    }

    //文档保护
    private void protectDocument(int protectionType) {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.isProtectOn()) {
                Util.showToast(mContext, "文档已经被保护！");
                return;
            }

            mDoc.protect(PROTECT_PASSWORD, protectionType, true);
            Util.showToast(mContext, "设置文档保护成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //取消文档保护
    private void unProtectDocument(String password) {
        try {
            if (!mDoc.isProtectOn()) {
                Util.showToast(mContext, "文档未加保护！");
                return;
            }
            password = "123";
            mDoc.unprotect(password);
            Util.showToast(mContext, "文档解保护成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //文档是否保护
    private void isProtectOn() {
        try {
            boolean isProtect = mDoc.isProtectOn();
            String message = "文档是否保护 ： " + isProtect;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //文档保护类型
    private void getProtectionType() {
        try {
            int protectionType = mDoc.getProtectionType();
            String message;

            switch (protectionType) {
                case ProtectionType.NONE:
                    message = "文档保护模式 ： 无保护";
                    break;
                case ProtectionType.COMMENTS:
                    message = "文档保护模式 ： 批注保护";
                    break;
                case ProtectionType.TRACKEDCHANGES:
                    message = "文档保护模式 ： 修订保护";
                    break;
                case ProtectionType.READONLY:
                    message = "文档保护模式 ： 制度保护";
                    break;
                default:
                    message = "文档保护模式 ： 无保护";
                    break;
            }

            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //复制 选区
    private void copy() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().copy();
            Util.showToast(mContext, "选区内容已经复制到剪切板");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //剪切 选区
    private void cut() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().cut();
            Util.showToast(mContext, "选区内容已经剪切到剪切板");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //粘贴 选区
    private void paste() {
        if (!isDocumentOpened() && isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().paste();
            Util.showToast(mContext, "粘贴成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //插入文字到 选区
    private void typeText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().typeText(INSERT_STR);
            Util.showToast(mContext, "插入文字成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //获得 选区文字
    private void getText() {
        if (!isDocumentOpened() && !isWorkBookOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String content = "";
            if (Util.isPDFFile(docPath)) {
                content = "";
            } else if (Util.isExcelFile(docPath)) {
                Range range = mWorkBook.range(0, 100);
                content = range.getText();
            } else if (Util.isPptFile(docPath)) {
                cn.wps.moffice.service.presentation.Range range = mPresentation.range(0, 100);
                content = range.getText();
            } else {
                content = mDoc.getSelection().getText();
            }

            String message = "选区内容为  ： " + content;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //插入一个段
    private void insertParagraph() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().insertParagraph();
            Util.showToast(mContext, "插入段成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //选区左边界坐标
    private void getLeft() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float left = mDoc.getSelection().getLeft();
            String message = "选区左边界坐标 ： " + left;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //选区上边界坐标
    private void getTop() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float top = mDoc.getSelection().getTop();
            String message = "选区上边界坐标 ： " + top;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //选区开始 cp位置
    private void getStart() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int start = mDoc.getSelection().getStart();
            String message = "选区开始cp ： " + start;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //选区结束 cp位置
    private void getEnd() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int end = mDoc.getSelection().getEnd();
            String message = "选区结束cp ： " + end;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //设置选区
    private void setSelection() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            setSelectionDialog(mDoc.getLength());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //选区字体信息
    private void getFont() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String name = mDoc.getSelection().getFont().getName();
            float size = mDoc.getSelection().getFont().getSize();
            int color = mDoc.getSelection().getFont().getTextColor();
            boolean bold = mDoc.getSelection().getFont().getBold();
            boolean italic = mDoc.getSelection().getFont().getItalic();

            String message = "字体名称 ： " + name + "字体大小 ： " + size + "字体颜色 ： " + color + "是否粗体 ： " + bold + "是否斜体： " + italic;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //添加嵌入式的图片到选区
    private void addInlinePicture() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        if (!new File(INLINE_PIC_PATH).exists()) {
            Util.showToast(mContext, "图片不存在！图片路径应为:/sdcard/DCIM/ico.png");
            return;
        }

        try {
            mDoc.getSelection().getInlineShapes().addPicture(INLINE_PIC_PATH);
            Util.showToast(mContext, "插入嵌入图成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //添加浮动图
    private void addPicture() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        addPictureDialog();
    }

    //添加文本框
    private void addTextBox() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int width = (int) mDoc.getSelection().getLeft() - mDoc.getScrollX();
            int height = (int) mDoc.getSelection().getTop() - mDoc.getScrollY();
            mDoc.getShapes().addTextBox(width, height, 80, 40, 0x00ff0000, false, 0x0000ff00, false, 0x0000ff, (float) 20.5, "宋体", "通过啦！！！");
            Util.showToast(mContext, "插入文本框成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //添加空文本框
    private void addNoneTextBox() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            int width = (int) mDoc.getSelection().getLeft() - mDoc.getScrollX();
            int height = (int) mDoc.getSelection().getTop() - mDoc.getScrollY();
            mDoc.getShapes().addTextBox(width, height, 80, 40, 0x00ff0000, false, 0x0000ff00, false, 0x0000ff, (float) 20.5, "宋体", "");
            Util.showToast(mContext, "插入空文本框成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void removeStrikeThrough() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().getFont().setNoneStrikeThrough();
            Util.showToast(mContext, "去掉删除线成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void setStrikeThrough() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getSelection().getFont().setStrikeThrough();
            Util.showToast(mContext, "添加删除线成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void toggleInkFinger() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.toggleInkFinger();
                Util.showToast(mContext, "使用手指成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        } else if (isPresentationOpened()) {
            try {
                mPresentation.toggleInkFinger();
                Util.showToast(mContext, "使用手指成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }

        } else if (isWorkBookOpened()) {
            try {
                mWorkBook.toggleInkFinger();
                Util.showToast(mContext, "使用手指成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }

        }
    }

    private boolean isEnableInk = true;

    private void toggleInk() {
        if (!isDocumentOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (Util.isExcelFile(docPath)) {
                isEnableInk = !isEnableInk;
                mWorkBook.toggleForbiddenInk(isEnableInk);
                if (isEnableInk)
                    Util.showToast(mContext, "启用墨迹成功！");
                else
                    Util.showToast(mContext, "停用墨迹成功！");
            } else {
                isEnableInk = !isEnableInk;
                mDoc.toggleForbiddenInk(isEnableInk);
                if (isEnableInk)
                    Util.showToast(mContext, "启用墨迹成功！");
                else
                    Util.showToast(mContext, "停用墨迹成功！");
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }

//		try
//		{
//			isEnableInk = !isEnableInk;
//			mWorkBook.toggleForbiddenInk(isEnableInk);
//			if (isEnableInk)
//				Util.showToast(mContext, "启用墨迹成功！");
//			else
//				Util.showToast(mContext, "停用墨迹成功！");
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    private void toggleToPen() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.toggleToPen();
                Util.showToast(mContext, "切换笔成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        } else if (isPresentationOpened()) {
            try {
                mPresentation.toggleToPen();
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void toggleToHighLightPen() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.toggleToHighLightPen();
                Util.showToast(mContext, "切换荧光笔成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        } else if (isPresentationOpened()) {
            try {
                mPresentation.toggleToHighLightPen();
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void toggleToEraser() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.toggleToEraser();
                Util.showToast(mContext, "切换橡皮擦成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
        if (isPresentationOpened()) {
            try {
                mPresentation.toggleToEraser(true);        //true是清除,false是橡皮
                Util.showToast(mContext, "切换橡皮擦成功！");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void setInkColor() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.setInkColor(0xff0000);
                Util.showToast(mContext, "设置墨迹颜色成功！已设置为红色");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
        if (isPresentationOpened()) {
            try {
                mPresentation.setInkColor(0x0000ff);
                Util.showToast(mContext, "设置墨迹颜色成功！已设置为蓝色");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void getInkColor() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                int color = mDoc.getInkColor();
                Util.showToast(mContext, "墨迹颜色为：" + color);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
        if (isPresentationOpened()) {
            try {
                int color = mPresentation.getInkColor();
                Util.showToast(mContext, "墨迹颜色为：" + color);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void getInkPenThick() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                float inkPenThick = mDoc.getInkPenThick();
                Util.showToast(mContext, "笔的粗细为 ： " + inkPenThick);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
        if (isPresentationOpened()) {
            try {
                float inkPenThick = mPresentation.getInkPenThick();
                Util.showToast(mContext, "笔的粗细为 ： " + inkPenThick);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void getInkHighLightThick() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                float highLightThick = mDoc.getInkHighLightThick();
                Util.showToast(mContext, "荧光笔的粗细为 ： " + highLightThick);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
        if (isPresentationOpened()) {
            try {
                float highLightThick = mPresentation.getInkHighLightThick();
                Util.showToast(mContext, "荧光笔的粗细为 ： " + highLightThick);
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private void setInkThick() {
        if (!isDocumentOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        if (isDocumentOpened()) {
            try {
                mDoc.setInkThick((float) 5.0);
                Util.showToast(mContext, "设置笔的粗细成功，笔的粗细为 5.0磅");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        } else if (isPresentationOpened()) {
            try {
                mPresentation.setInkThick((float) 10.0);
                Util.showToast(mContext, "设置笔的粗细成功，笔的粗细为 5.0磅");
            } catch (RemoteException e) {
                Util.showToast(mContext, FAIL_NOTE);
                e.printStackTrace();
            }
        }
    }

    private boolean isDocumentOpened() {
        return null != mDoc;
    }

    //隐藏所有按钮
    private void hideAll() {
        if (mIsHindeAll) {
            mIsHindeAll = false;
            btnHideAll.setText("隐藏所有");
            allBtnGroup.setVisibility(View.VISIBLE);
        } else {
            mIsHindeAll = true;
            btnHideAll.setText("展开所有");
            allBtnGroup.setVisibility(View.GONE);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("FloatingService", "onStart");
        mDoc = null;
        //setForeground(true);
        view = LayoutInflater.from(this).inflate(R.layout.float_test_view, null);
        findViewByID();
        createView();
        FloatingFunc.show(this.getApplicationContext(), view);

        File file = new File(docPath);
        txt_fileName.setText(WINDOW_NOTE + "\n" + file.getName());
        super.onStart(intent, startId);
    }

    /**
     * 停止服务调用
     */
    public static void stopService() {
        Log.i("FloatingService", "btnCloseWindow");
        if (mContext != null){
            FloatingFunc.close(mContext);
        }
        isBound = false;
        mDoc = null;
        mPdfReader = null;
        mPresentation = null;
        //关闭自身service
        if (myContext != null){
            ((Service) myContext).stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        Log.d("FloatingService", "onDestroy");

        FloatingFunc.close(this.getApplicationContext());
        if (mService != null)
            unbindService(connection);

        mDoc = null;
        mPdfReader = null;
        mPresentation = null;
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
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = OfficeService.Stub.asInterface(service);
            isBound = true;
            try {
                mService.getApps().openApp();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBound = false;
        }
    };

	public final String packageName = Define.PACKAGENAME_KING_PRO;
//	public final String packageName = Define.PACKAGENAME_ENG;

    // bind service
    private synchronized boolean bindOfficeService() {
        // 关闭自启动后，先启动moffice进程
        final Intent startAppIntent = new Intent();
        startAppIntent.setClassName(packageName, "cn.wps.moffice.second_dev.StartAppActivity");
        startAppIntent.setAction("cn.wps.moffice.second_dev.StartApp");
        startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startAppIntent);

        // 先唤醒moffice进程，再绑定service
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Define.OFFICE_READY_ACTION.equals(intent.getAction())) {
                    synchronized (this) {
                        this.notifyAll();
                    }
                }
            }
        };
        registerReceiver(receiver, new IntentFilter(Define.OFFICE_READY_ACTION));
        final Intent wakeAppIntent = new Intent();
        wakeAppIntent.setClassName(packageName, Define.OFFICE_ACTIVITY_NAME);
        wakeAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(wakeAppIntent);
        synchronized (this) {
            try {
                this.wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(receiver);
        isChangedFlag = false;
        final Intent intent = new Intent(Define.OFFICE_SERVICE_ACTION);
        intent.setPackage(packageName);
        intent.putExtra("DisplayView", true);
        startService(intent);
        boolean bindOk = false;
        for (int i = 0; i < 10; ++i) {
            bindOk = bindService(intent, connection, Service.BIND_AUTO_CREATE);
            if (bindOk)
                break;
            else {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }

        if (!bindOk) {
            // bind failed, maybe wps office is not installd yet.
            Util.showToast(mContext, "服务绑定失败！");
            unbindService(connection);
            return false;
        }

        return true;
    }

    // 设置文档路径
    public static void setDocPath(String path) {
        docPath = path;
    }

    public static Document getDocument() {
        return mDoc;
    }

    public static void setAllowCallBack(AllowChangeCallBack allow) {
        mAllow = allow;
    }

    public void turnPrePage() {
        if (!isDocumentOpened() && !isPDFDocOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mPdfReader != null) {
                mPdfReader.transitionPre();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            if (mPresentation != null)
                mPresentation.transitionPre();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void turnNextPage() {
        if (!isDocumentOpened() && !isPDFDocOpened() && !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mPdfReader != null) {
                mPdfReader.transitionNext();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        try {
            if (mPresentation != null)
                mPresentation.transitionNext();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void getShapeLeft() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

//		try
//		{
//			int left = (int)mDoc.getShapes().getLeft();
//			Util.showToast(mContext, "对象左边缘坐标为： " + left);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    public void getShapeTop() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

//		try
//		{
//			int top = (int)mDoc.getShapes().getPicTop();
//			Util.showToast(mContext, "对象上边缘坐标为： " + top);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    public void getShapeHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            int height = (int) mDoc.getShapes().item(1).getHeight();
            Util.showToast(mContext, "对象高度 为： " + height);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    public void getShapeWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            int width = (int) mDoc.getShapes().item(1).getWidth();
            Util.showToast(mContext, "对象宽度为： " + width);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    public void getTextBoxLineColor() {
//		if (!isDocumentOpened())
//		{
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//		try
//		{
//			int color = (int)mDoc.getShapes().getTextBoxLineColor();
//			Util.showToast(mContext, "文本框边框颜色为： " + color);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    public void getTextBoxFLine() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

//		try
//		{
//			boolean fLine = mDoc.getShapes().getTextBoxFLine();
//			Util.showToast(mContext, "文本框边框是否显示： " + fLine);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    class LoadDocThread extends Thread// 内部类
    {
        String path;
        int flag;
        boolean isShow = true;

        public LoadDocThread(String path, int openFlag) {
            this.path = path;
            this.flag = openFlag;
        }

        public LoadDocThread(String path, int openFlag, boolean isShow) {
            this.path = path;
            this.flag = openFlag;
            this.isShow = isShow;
        }

        public void run() {
            // 打开文档
            if (mService == null && !bindOfficeService()) {
                Util.showToast(mContext, "操作失败，service可能未正常连接");
                return;
            }

            try {
                Intent intent = Util.getOpenIntent(mContext, new WpsOpenBean(this.path, true,Define.EDIT_MODE));
//				mService.setFileId("sss");
                mService.setSecurityKey("123465".getBytes());
                if (OPEN_DOCUMENT == flag) {
                    Intent i = mService.getDocuments().getDocumentIntent(path, "", intent);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    mDoc = mService.getDocuments().waitForDocument(path);
                    for (int k = 0; k < 10; ++k) {
                        if (!mDoc.isLoadOK()) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } else {
                            Util.showToast(mContext, "文档已就绪，可以开始进行AIDL调用了");
//							如需在文档打开后，立即自动完成文档某些操作，可以在这里添加相关代码，如下面这句，开启手写
//							mDoc.toggleInkFinger();
                            break;
                        }

                    }
                } else if (NEW_DOCUMENT == flag)
                    mDoc = mService.newDocument(path, intent);
//				Util.showToast(mContext, "文档已打开 !");
                if (mDoc.getApplication() != null)
                    Util.showToast(mContext, "Window.get_Version : " + mDoc.getApplication().getVersion());
            } catch (RemoteException e) {
                mDoc = null;
                e.printStackTrace();
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
            if (mService == null && !bindOfficeService()) {
                Util.showToast(mContext, "操作失败，service可能为正常连接");
                return;
            }

            try {
                mService.setSecurityKey("123456".getBytes());
                Intent intent = Util.getPDFOpenIntent(mContext, this.path, true);
                Intent i = mService.getPDFReaders().getPDFReaderIntent(path, "", intent);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                mPdfReader = mService.getPDFReaders().waitForPDFReader(path);
                if (mPdfReader == null) {
                    Util.showToast(mContext, "PDF对象获取失败");
                }
            } catch (RemoteException e) {
                mPdfReader = null;
                e.printStackTrace();
            }
        }
    }

    class LoadPresentationThread extends Thread {
        String mFilePath;

        public LoadPresentationThread(String filePath) {
            mFilePath = filePath;
        }

        public void run() {
            // 打开文档
            if (mService == null && !bindOfficeService()) {
                Util.showToast(mContext, "操作失败，service可能未正常连接");
                return;
            }

            try {
                Intent intent = Util.getOpenIntent(mContext, new WpsOpenBean(mFilePath, true,Define.EDIT_MODE));
//				mService.setFileId("asd");
//                String password = "abc123";
                mPresentation = mService.openPresentation(mFilePath, "123456", intent);
//				Intent i = mService.getPresentations().getPresentationIntent(mFilePath, "abc123", intent);
//				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(i);
//				mPresentation = mService.getPresentations().waitForPresentation(mFilePath);
            } catch (RemoteException e) {
                mPresentation = null;
                e.printStackTrace();
            }
        }
    }

    class LoadWorkBookThread extends Thread {
        String mFilePath;

        public LoadWorkBookThread(String filePath) {
            mFilePath = filePath;
        }

        public void run() {
            // 打开文档
            if (mService == null && !bindOfficeService()) {
                Util.showToast(mContext, "操作失败，service可能为正常连接");
                return;
            }

            try {
                Intent intent = Util.getOpenIntent(mContext, new WpsOpenBean(mFilePath, true,Define.EDIT_MODE));
//				mService.setFileId("asdf");
                Intent i = mService.getWorkbooks().getBookExIntent(mFilePath, "", intent);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                mWorkBook = mService.getWorkbooks().waitForWorkbook(mFilePath);
//				Worksheet worksheet = mWorkBook.getActiveSheet();
//				String name = mWorkBook.getName();
//				DocumentProperties properties = mWorkBook.getDocumentProperties();
//				String s1 = mWorkBook.range(0, 100).getText();
//				File sdcard = Environment.getExternalStorageDirectory();
//				File newFile = new File(sdcard, "pictest");
//				worksheet.saveToImage(newFile.getAbsolutePath(), PictureFormat.PNG, 100, 1);
////				worksheet.saveCurrentPage(newFile.getAbsolutePath(), PictureFormat.PNG, 100);
//				int s2 = worksheet.getCurrentPageIndex();
//				int s3 = worksheet.getPageCount();
//				String s4 = worksheet.getName();
//				System.out.println(name);
            } catch (RemoteException e) {
                mWorkBook = null;
                e.printStackTrace();
            }
        }
    }

    // 打开文档
    private void openPDFDoc() {
        if (mService == null)
            return;

        LoadPDFDocThread mythread = new LoadPDFDocThread(docPath);
        mythread.start();
    }

    private void openPresentation() {
        LoadPresentationThread loadThread = new LoadPresentationThread(docPath);
        loadThread.start();
    }

    private void openWorkBook() {
        if (mService == null)
            return;
        LoadWorkBookThread loadThread = new LoadWorkBookThread(docPath);
        loadThread.start();
    }

    private void closePDFDoc() {
        if (!isPDFDocOpened()) {
            Util.showToast(mContext, "操作失败，isPDFDocOpened取空");
            return;
        }

        try {
            mPdfReader.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closeWorkBook() {
        if (!isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mWorkBook.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void closePresentation() {
        if (!isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mPresentation.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private boolean isPDFDocOpened() {
        return mPdfReader != null;
    }

    private boolean isPresentationOpened() {
        return mPresentation != null;
    }

    private boolean isWorkBookOpened() {
        return mWorkBook != null;
    }

    //设置selection的位置 对话框
    private void setSelectionDialog(int length) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View ParamDialogView = inflater.inflate(R.layout.param_text_dialog, null);

        AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
                .setTitle("请输入光标的位置(不大于" + (length - 1) + ")")
                .setView(ParamDialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editAddText = (EditText) ParamDialogView.findViewById(R.id.editAddText);
                        int pos = -1;
                        String str = editAddText.getText().toString();
                        if ("".equals(str) || str.length() == 0) {
                            Toast.makeText(mContext, "请输入数值", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            try {
                                pos = Integer.valueOf(str);
                            } catch (NumberFormatException e) {
                                Toast.makeText(mContext, "输入不是数字，请重新操作", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                return;
                            }
                        }

                        try {
                            mDoc.getSelection().setSelection(pos, pos, true);
                        } catch (RemoteException e) {
                            e.printStackTrace();
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

    //插入浮动图片对话框, shape
    private void addPictureDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View ParamDialogView = inflater.inflate(R.layout.shapes_dialog, null);
        EditText editFilePath = (EditText) ParamDialogView
                .findViewById(R.id.filePath);
        editFilePath.setText(INLINE_PIC_PATH);

        EditText editLeft = (EditText) ParamDialogView
                .findViewById(R.id.left);
        editLeft.setText(10 + "");

        EditText editTop = (EditText) ParamDialogView
                .findViewById(R.id.top);
        editTop.setText(10 + "");

        Bitmap bitmap = BitmapFactory.decodeFile(INLINE_PIC_PATH);

        EditText editWidth = (EditText) ParamDialogView
                .findViewById(R.id.width);
        editWidth.setText(bitmap.getWidth() + "");

        EditText editHeight = (EditText) ParamDialogView
                .findViewById(R.id.height);
        editHeight.setText(bitmap.getHeight() + "");

        EditText editCp = (EditText) ParamDialogView
                .findViewById(R.id.cp);
        editCp.setText(0 + "");


        AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
                .setTitle("设置图片属性")
                .setView(ParamDialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editFilePath = (EditText) ParamDialogView
                                .findViewById(R.id.filePath);
                        EditText editLeft = (EditText) ParamDialogView
                                .findViewById(R.id.left);
                        EditText editTop = (EditText) ParamDialogView
                                .findViewById(R.id.top);
                        EditText editWidth = (EditText) ParamDialogView
                                .findViewById(R.id.width);
                        EditText editHeight = (EditText) ParamDialogView
                                .findViewById(R.id.height);
                        EditText editCp = (EditText) ParamDialogView
                                .findViewById(R.id.cp);

                        RadioGroup raInkToFileGroup = (RadioGroup) ParamDialogView.findViewById(R.id.raInkToFileGroup);
                        RadioButton raInkToFile = (RadioButton) ParamDialogView.findViewById(raInkToFileGroup.getCheckedRadioButtonId());

                        RadioGroup raSaveWithDocumentGroup = (RadioGroup) ParamDialogView.findViewById(R.id.raSaveWithDocumentGroup);
                        RadioButton raSaveWithDocument = (RadioButton) ParamDialogView.findViewById(raSaveWithDocumentGroup.getCheckedRadioButtonId());

                        RadioGroup raWrapTypeGroup = (RadioGroup) ParamDialogView.findViewById(R.id.raWrapTypeGroup);
                        RadioButton raWrapType = (RadioButton) ParamDialogView.findViewById(raWrapTypeGroup.getCheckedRadioButtonId());

                        String filePath = editFilePath.getText().toString();
                        String leftString = editLeft.getText().toString();
                        String topString = editTop.getText().toString();
                        String widthsString = editWidth.getText().toString();
                        String heightsString = editHeight.getText().toString();
                        String cpString = editCp.getText().toString();

                        if (!new File(filePath).exists()) {
                            Util.showToast(mContext, "图片不存在!!");
                            return;
                        }
                        if ("".equals(leftString) || "".equals(topString) || "".equals(widthsString) ||
                                "".equals(heightsString) || "".equals(cpString)) {
                            Util.showToast(mContext, "请填写正确的参数!!");
                            return;
                        }

                        float left = Float.valueOf(leftString);
                        float top = Float.valueOf(topString);
                        float width = Float.valueOf(widthsString);
                        float height = Float.valueOf(heightsString);
                        int cp = Integer.valueOf(cpString);

                        boolean inkToFile = Boolean.valueOf(raInkToFile.getText().toString());
                        boolean saveWithDocument = Boolean.valueOf(raSaveWithDocument.getText().toString());
                        String wrapType = raWrapType.getText().toString();

                        try {
                            float mLeaf = mDoc.getSelection().getLeft();
                            float mTop = mDoc.getSelection().getTop();
                            int mCp = mDoc.getSelection().getStart(); //获取选区的开始位置，始终在选区的开始插入图片

//							mDoc.getShapes().addPicture(filePath, inkToFile, saveWithDocument, mLeaf, mTop, width, height, mCp, WrapType.valueOf(wrapType));

                        } catch (RemoteException e) {
                            Toast.makeText(mContext, "插入失败!!", Toast.LENGTH_SHORT)
                                    .show();
                            e.printStackTrace();
                        }
                        Util.showToast(mContext, "插入图片成功!!");
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

    // 插入自定义属性的key value值
    private void addDocumentVariableDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View ParamDialogView = inflater.inflate(R.layout.param_variable_dialog, null);

        AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
                .setTitle("插入文本内容")
                .setView(ParamDialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editAddText1 = (EditText) ParamDialogView
                                .findViewById(R.id.editAddText1);

                        EditText editAddText2 = (EditText) ParamDialogView
                                .findViewById(R.id.editAddText2);

                        if ("".equals(editAddText1.getText().toString())
                                || editAddText1.getText().toString().length() == 0) {
                            Util.showToast(mContext, "请输入文本");
                            return;
                        }

                        String key = editAddText1.getText().toString();
                        String value = editAddText2.getText().toString();
                        try {
                            mDoc.addDocumentVariable(key, value);
                            Util.showToast(mContext, "插入自定义属性 ：" + key + " : " + value + " 成功！");
                        } catch (RemoteException e) {
                            Util.showToast(mContext, FAIL_NOTE);
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            Util.showToast(mContext, VARIABLE_EXIST);
                            e.printStackTrace();
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

    //弹出key值对应的文档属性
    private void getDocumentVariableDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View ParamDialogView = inflater.inflate(R.layout.param_text_dialog, null);

        AlertDialog paramAlertDialog = new AlertDialog.Builder(mContext)
                .setTitle("插入文本内容")
                .setView(ParamDialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editAddText = (EditText) ParamDialogView
                                .findViewById(R.id.editAddText);
                        if ("".equals(editAddText.getText().toString()) || editAddText.getText().toString().length() == 0) {
                            Util.showToast(mContext, "请输入文本");
                            return;
                        }

                        String key = editAddText.getText().toString();
                        String value = "";
                        try {
                            value = mDoc.getDocumentVariable(key);
                            Util.showToast(mContext, key + " 对应的value为: " + value);
                        } catch (RemoteException e) {
                            Util.showToast(mContext, FAIL_NOTE);
                            e.printStackTrace();
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

    private void getForbittenInk() {
        if (!isDocumentOpened() & !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
//		if (Util.isExcelFile(docPath))
//		{
//			isEnableInk = !isEnableInk;
//			mWorkBook.toggleForbiddenInk(isEnableInk);
//			if (isEnableInk)
//				Util.showToast(mContext, "启用墨迹成功！");
//			else
//				Util.showToast(mContext, "停用墨迹成功！");
//		}
//		else
//		{
//			isEnableInk = !isEnableInk;
//			mDoc.toggleForbiddenInk(isEnableInk);
//			if (isEnableInk)
//				Util.showToast(mContext, "启用墨迹成功！");
//			else
//				Util.showToast(mContext, "停用墨迹成功！");
        try {
            if (Util.isExcelFile(docPath)) {
                boolean isShow = (boolean) mWorkBook.getForbiddenInk();
                String str = isShow ? "墨迹禁用" : "墨迹启用 ";
                Util.showToast(mContext, str);
            } else {
                boolean isShow = (boolean) mDoc.isForbiddenInk();
                String str = isShow ? "墨迹禁用" : "墨迹启用 ";
                Util.showToast(mContext, str);
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }

        if (!isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            boolean isShow = (boolean) mWorkBook.getForbiddenInk();
            String str = isShow ? "墨迹禁用" : "墨迹启用 ";
            Util.showToast(mContext, str);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void getShowRevisions() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            boolean isShow = (boolean) mDoc.isShowReviewingPane();
            String str = isShow ? "修订已显示" : "修订未显示 ";
            Util.showToast(mContext, str);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void deleteShape() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
//			ArrayList<Long> shapes = new ArrayList<Long>();
//			for (int i = 1; i <= mDoc.getShapes().getCount(); i++)
//			{
//				if (mDoc.getShapes().item(i).hasInk() == MsoTriState.msoTrue)
//				{
//					shapes.add(mDoc.getShapes().item(i).getID());
//				}
//			}
//
//			for (int i = 0; i < shapes.size(); i++)
//			{
//				Long id = shapes.get(i);
//				for (int j = 1; j <= mDoc.getShapes().getCount(); j++)
//				{
//					if (mDoc.getShapes().item(j).getID() == id)
//					{
//						mDoc.getShapes().item(j).delete();
//					}
//				}
//			}
            for (int j = 1; j <= mDoc.getShapes().getCount(); j++) {
                if (mDoc.getShapes().item(j) != null)
                    mDoc.getShapes().item(j).delete();
            }
//			if (mDoc.getShapes().item(1).hasInk() == MsoTriState.msoTrue)
//			{
//				mDoc.getShapes().item(1).delete();
//				Util.showToast(mContext, "对象已成功删除！");
//			}
//			else
//			{
//				Util.showToast(mContext, "非墨迹对象！");
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void getDocProp() {
        if (mService == null)
            return;

        try {
            if (Util.isExcelFile(docPath)) {
                DocumentProperties prop = mWorkBook.getDocumentProperties();
                if (null != prop)
                    Util.showToast(mContext, "文档作者： " + prop.get("Author"));
                else
                    Util.showToast(mContext, "获取文档属性异常" + prop);
                return;
            } else if (Util.isPptFile(docPath)) {
                DocumentProperties prop = mPresentation.getDocumentProperties();
                if (null != prop)
                    Util.showToast(mContext, "文档作者： " + prop.get("Author"));
                else
                    Util.showToast(mContext, "获取文档属性异常" + prop);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null != mDoc) {
            try {
                mDoc.close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Util.showToast(mContext, "已关闭打开文档！");
        }

        Util.showToast(mContext, "文档正在后台打开...");

        LoadDocThread mythread = new LoadDocThread(docPath, OPEN_DOCUMENT, false);
        mythread.start();

        while (null == mDoc) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        Util.showToast(mContext, "文档已在后台打开！");
        try {
            DocumentProperties prop = mDoc.getBuiltInDocumentProperties();
            if (null != prop)
                Util.showToast(mContext, "文档作者： " + prop.get("author"));
            else
                Util.showToast(mContext, "获取文档属性异常" + prop);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }

    }

    private void getPage() {
        if (mService == null)
            return;

        if (null != mDoc) {
            try {
                mDoc.close();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Util.showToast(mContext, "已关闭打开文档！");
        }

        Util.showToast(mContext, "文档正在后台打开...");

        LoadDocThread mythread = new LoadDocThread(docPath, OPEN_DOCUMENT, false);
        mythread.start();

        while (null == mDoc) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        Util.showToast(mContext, "文档已在后台打开！");
        try {
            Page page = mDoc.getPage(0);
            String str = "获取截图失败！";
            if (null != page) {
                if (page.saveToImage(SAVE_IMAGE_PATH + "\\test.jpg", PictureFormat.JPEG, 100, page.getWidth(), page.getHeight(), 64, PrintOutItem.wdPrintContent))
                    str = "获取截图成功！";
            }

            Util.showToast(mContext, str);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private boolean page = false;

    private void setPageOrientation() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String text = "设置页面横向";
            WdOrientation orientation = WdOrientation.wdOrientLandscape;
            if (page) {
                orientation = WdOrientation.wdOrientPortrait;
                text = "设置页面竖向";
            }

            page = !page;

            mDoc.getPageSetup().setOrientation(orientation);
            btnSetPageOrientation.setText(text);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int pageWidthIndex = 2;

    private void setPageWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setPageWidth(++pageWidthIndex * 50);
            Util.showToast(mContext, "页面宽度设置为" + pageWidthIndex * 50);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int pageHeigthIndex = 1;

    private void setPageHeigth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setPageHeight(++pageHeigthIndex * 50);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int pageLeftIndex = 1;

    private void setPageLeft() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setLeftMargin(pageLeftIndex++ * 20);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int pageRightIndex = 1;

    private void setPageRight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setRightMargin(pageRightIndex++ * 20);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }


    private int pageTopIndex = 1;

    private void setPageTop() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setTopMargin(pageTopIndex++ * 20);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int pageBottomIndex = 1;

    private void setPageBottom() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getPageSetup().setBottomMargin(pageBottomIndex++ * 20);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void deleteComment() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.getComments().count() == 0) {
                Util.showToast(mContext, "文档没有批注");
                return;
            } else {
                Util.showToast(mContext, "文档批注数目" + mDoc.getComments().count());
            }
            mDoc.getComments().item(1).Delete();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private int index = 0;

    private void addComment() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getComments().add(mDoc.getSelection().getRange(), "xiaoliTest" + index++);

            Util.showToast(mContext, "批注数量为：" + mDoc.getComments().count()
                    + "第一个批注的内容为" + mDoc.getComments().item(1));
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void addBookmark() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String name = "xiaoliTest" + index++;
            mDoc.getBookmarks().add(name, mDoc.getSelection().getRange());
            Util.showToast(mContext, "已添加书签：" + name);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void deleteBookmark() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.getBookmarks().count() == 0) {
                Util.showToast(mContext, "文档没有书签");
                return;
            } else {
                Util.showToast(mContext, "文档书签数目" + mDoc.getBookmarks().count());
            }
            mDoc.getBookmarks().item(1).delete();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	houjing添加：表格Workbook对象接口方法测试实现

    //	getName();//获取工作簿的名称
    private void WorksheetgetName() {
        if (!isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String WorkbookName;
//			Worksheet workSheet = mWorkBook.getActiveSheet();
            WorkbookName = mWorkBook.getName();
            String message = "当前工作薄名称为  ： " + WorkbookName;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	getName();//获取工作表名称
    private void WorkbookgetName() {
        if (!isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            String WorkSheetName;
            Worksheet workSheet = mWorkBook.getActiveSheet();
            WorkSheetName = workSheet.getName();
            String message = "当前工作薄名称为  ： " + WorkSheetName;
            Util.showToast(mContext, message);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取Excel文档基本属性信息
    private void WorkbookProperties() {
        if (!isWorkBookOpened() || !isPresentationOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            DocumentProperties WorkbookProperties;
//			Worksheet workSheet = mWorkBook.getActiveSheet();
            WorkbookProperties = mWorkBook.getDocumentProperties();
            if (null != WorkbookProperties)
                Util.showToast(mContext, "文档作者： " + WorkbookProperties.get("author"));
            else
                Util.showToast(mContext, "获取文档属性异常" + WorkbookProperties);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	表格获取文本信息
//	private void WorkBookgetText()
//	{
//		if (!isWorkBookOpened())
//		{
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//
//		try
//		{
//			Worksheet workSheet = mWorkBook.getActiveSheet();
//			String content = workSheet.getText()
//			String message = "选区内容为  ： " + content;
//			Util.showToast(mContext, message);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
//	}
//	获取书签值
    private void getBookmarkText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.getBookmarks().count() == 0) {
                Util.showToast(mContext, "文档没有书签");
                return;
            } else {
                int count = mDoc.getBookmarks().count();
                String message = "书签总数为：" + count + "第一个书签值为： " + mDoc.getBookmarks().item(1).range();
                Util.showToast(mContext, message);
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	给第一个书签赋值
    private void setBookmarkText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.getBookmarks().count() == 0) {
                Util.showToast(mContext, "文档没有书签");
                return;
            } else {
                String arg0 = "test";
                mDoc.getBookmarks().item(1).range().setText(arg0);
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取页面方向
    private void getPageOrientation() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面方向： " + mDoc.getPageSetup().getOrientation());
            return;
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取页面宽度
    private void getPageWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面宽度： " + mDoc.getPageSetup().getPageWidth());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取页面高度
    private void getPageHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "页面高度： " + mDoc.getPageSetup().getPageHeight());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取左边距
    private void getLeftMargin() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面左边距： " + mDoc.getPageSetup().getLeftMargin());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取右边距
    private void getRightMargin() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面右边距： " + mDoc.getPageSetup().getRightMargin());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取上边距
    private void getTopMargin() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面上边距： " + mDoc.getPageSetup().getTopMargin());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取下边距
    private void getBottomMargin() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "页面下边距： " + mDoc.getPageSetup().getBottomMargin());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	获取活动的文档对象名称Application接口getActiveDocument
    private void getActiveDocument() {
        if (!isDocumentOpened() && !isPresentationOpened() && !isWorkBookOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (isDocumentOpened()) {
                Util.showToast(mContext, "getActiveDocument： " + mDoc.getApplication().getActiveDocument().getName());
            }
//			else if (isWorkBookOpened())
//			{
//				mWorkBook = mService.getActiveWorkbook();
//				Util.showToast(mContext, "getActiveWorkbook： " + mWorkBook.getName());
//			}
//			else if (isPresentationOpened())
//			{
//				mPresentation = mService.getActivePresentation();
//				Util.showToast(mContext, "getActivePresentation:  "+mPresentation.getName());
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }


    //	获取选区对象Application接口getSelection()剪切
    private void getSelection() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getApplication().getSelection().cut();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： copy
    private void rangecopy() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.range(0, 10).copy();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： paste
    private void rangepaste() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.range(0, 10).paste();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： collapse
    private void rangecollapse() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            cn.wps.moffice.service.doc.Range range = mDoc.range(0, 10);
            WdCollapseDirection collapse = WdCollapseDirection.wdCollapseEnd;
            range.collapse(collapse);
            Util.showToast(mContext, "设置range的开始CP值为:" + range.getStart());
            Util.showToast(mContext, "设置range的结束CP值为:" + range.getEnd());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： setUnderline
    private void setUnderline() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdUnderline test = WdUnderline.wdUnderlineDash;
            mDoc.range(0, 10).setUnderline(test);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： getUnderline
    private void getUnderline() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区下划线类型： " + mDoc.range(0, 10).getUnderline());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法： setStyle--待修改案例1215
    private void setStyle() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {

            Variant setstyle = new Variant(1);
            mDoc.range(0, 10).setStyle(setstyle);
            Util.showToast(mContext, "设置range(0, 10)样式为标题样式1");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法：  getStyle
    private void getStyle() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区主题样式：" + mDoc.range(0, 10).getStyle());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法：  isItalic
    private void rangeIsItalic() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区倾斜：" + mDoc.range(0, 10).isItalic());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法：  setItalic
    private void setItalic() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

//		try {
//			if(mDoc.range(0, 10).isItalic())
//			try
//			{
//				mDoc.range(0, 10).setItalic(false);
//			}
//			catch (RemoteException e)
//			{
//				Util.showToast(mContext, FAIL_NOTE);
//				e.printStackTrace();
//			}
//			if(!mDoc.range(0, 10).isItalic())
        try {
            mDoc.range(0, 10).setItalic(false);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }

//		} catch (RemoteException e) {
//			// TODO 自动生成的 catch 块
//			e.printStackTrace();

    }

    //	Range对象方法：  isBold
    private void isBold() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区加粗：" + mDoc.range(0, 10).isBold());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Range对象方法：  setBold
    private void setBold() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.range(0, 10).setBold(true);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类getFont接口
    private void rangegetFont() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "获取Range范围字体：" + mDoc.range(0, 10).getFont());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类setEnd接口---待确认具体设置选区后确认是否OK
    private void rangesetEnd() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            cn.wps.moffice.service.doc.Range range = mDoc.range(0, 10);
            range.setEnd(5);
            Util.showToast(mContext, "设置range的结束CP值为5:" + range.getEnd());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类getEnd接口
    private void rangegetEnd() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "获取结束Cp位置：" + mDoc.range(0, 10).getEnd());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类getStart接口
    private void rangegetStart() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "获取起始Cp位置：" + mDoc.range(0, 10).getStart());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类setStart接口---待确认具体设置选区后确认是否OK
    private void rangesetStart() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            cn.wps.moffice.service.doc.Range range = mDoc.range(0, 10);
            range.setStart(3);
            Util.showToast(mContext, "设置range的开始CP值为3:" + range.getStart());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类getText接口
    private void rangegetText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "获取某个区域段的文本内容：" + mDoc.range(0, 10).getText());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类setText接口
    private void rangesetText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            String test = "123456";
            mDoc.range(0, 5).setText(test);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类delete接口
    private void rangedelete() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdUnits Units = WdUnits.wdCharacter;
            mDoc.range(0, 5).delete(Units, 3);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	range类expand接口
    private void rangeexpand() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdUnits Units = WdUnits.wdParagraph;
            mDoc.range(0, 5).expand(Units);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 Selection类addComment方法,选区添加批注内容test123
    private void SelectionaddComment() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdUnits Units = WdUnits.wdParagraph;
            mDoc.getSelection().addComment("test123");
            Util.showToast(mContext, "添加批注内容test123");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 Selection类getShapeRange方法
    private void SelectiongetShapeRange() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdUnits Units = WdUnits.wdParagraph;
            Util.showToast(mContext, "shapeRange对象获取" + mDoc.getSelection().getShapeRange());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 Selection类getInformation方法
    private void SelectiongetInformation() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdUnits Units = WdUnits.wdParagraph;
            WdInformation info1 = WdInformation.wdCapsLock;
            Util.showToast(mContext, "getInformation: " + mDoc.getSelection().getInformation(info1));
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 Selection类getRightOnScreen方法
    private void SelectiongetRightOnScreen() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "光标右位置: " + mDoc.getSelection().getRightOnScreen());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 Selection类getRightOnScreen方法
    private void SelectiongetBottomOnScreen() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "光标上位置: " + mDoc.getSelection().getBottomOnScreen());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getWidth方法
    private void InlineShapesgetWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "第一个嵌入型对象宽度: " + mDoc.getSelection().getInlineShapes().item(1).getWidth());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getHeight方法
    private void InlineShapesgetHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            int a = mDoc.getSelection().getInlineShapes().item(1).getHeight();
            Util.showToast(mContext, "第一个嵌入型对象宽度: " + a);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setScaleWidth方法
    private void InlineShapessetScaleWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getInlineShapes().item(1).setScaleWidth(3);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setScaleHeight方法
    private void InlineShapessetScaleHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getInlineShapes().item(1).setScaleHeight(3);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setHeight方法
    private void InlineShapessetHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getInlineShapes().item(1).setHeight(300);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setHeight方法
    private void InlineShapessetWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getInlineShapes().item(1).setWidth(300);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getScaleWidth方法
    private void InlineShapesgetScaleWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "第一个嵌入型对象宽度比例 : " + mDoc.getSelection().getInlineShapes().item(1).getScaleWidth());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getScaleWidth方法
    private void InlineShapesgetScaleHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "第一个嵌入型对象高度比例 : " + mDoc.getSelection().getInlineShapes().item(1).getScaleHeight());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getOLE方法
    private void InlineShapesgetOLE() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "获取嵌入型OLE对象 : " + mDoc.getSelection().getInlineShapes().item(1).getOLE().toString());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类getCount方法
    private void InlineShapesgetCount() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "嵌入型对象数量 : " + mDoc.getSelection().getInlineShapes().getCount());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setAlternativeText\getAlternativeText方法
    private void InlineShapesgetAlternativeText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getInlineShapes().item(1).setAlternativeText("testget");
            Util.showToast(mContext, "嵌入型对象描述设置获取显示 : " + mDoc.getSelection().getInlineShapes().item(1).getAlternativeText());

        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	 InlineShapes类setAlternativeText\getAlternativeText方法
    private void InlineShapesaddOLEControl() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            cn.wps.moffice.service.doc.Range Range = mDoc.range(0, 10);
            mDoc.getSelection().getInlineShapes().addOLEControl("tset", Range);

        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Font类方法
    private void font_setBold() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            if (mDoc.getSelection().getFont().getBold()) {
                mDoc.getSelection().getFont().setBold(false);
            } else {
                mDoc.getSelection().getFont().setBold(true);
            }

        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	Font类方法
    private void font_setItalic() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            if (mDoc.getSelection().getFont().getItalic()) {
                mDoc.getSelection().getFont().setItalic(false);
            } else {
                mDoc.getSelection().getFont().setItalic(true);
            }

        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setTextColor() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setTextColor(0xff0000);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_part_setName() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setName("宋体");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setStrikeThrough() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            if (!mDoc.getSelection().getFont().getStrikeThrough()) {
                mDoc.getSelection().getFont().setStrikeThrough();
            } else {
                mDoc.getSelection().getFont().setNoneStrikeThrough();
            }

        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setDoubleStrikeThrough() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setDoubleStrikeThrough();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getSuperscript() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "当前文本是上标：" + mDoc.getSelection().getFont().getSuperscript());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setSuperscript() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdBool test = WdBool.True;
            mDoc.getSelection().getFont().setSuperscript(test);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getBoldBi() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "当前文本设置加粗：" + mDoc.getSelection().getFont().getBoldBi());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setBoldBi() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdBool test = mDoc.getSelection().getFont().getBoldBi();
            if (test != null) {
                mDoc.getSelection().getFont().setBoldBi(null);
            } else {
                WdBool a = WdBool.True;
                mDoc.getSelection().getFont().setBoldBi(a);
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getSmallCaps() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "当前文本全部小写：" + mDoc.getSelection().getFont().getSmallCaps());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setSmallCaps() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdBool test = mDoc.getSelection().getFont().getSmallCaps();
//			if(test!=null)
//			{
//				mDoc.getSelection().getFont().setSmallCaps(null);
//			}
//			else
//			{
            WdBool a = WdBool.True;
            mDoc.getSelection().getFont().setSmallCaps(a);
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getAllCaps() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "当前文本全部大写：" + mDoc.getSelection().getFont().getAllCaps());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setAllCaps() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdBool test = WdBool.True;
//			if(test!=null)
//			{
            mDoc.getSelection().getFont().setAllCaps(test);
//			}
//			else
//			{
//				WdBool a = WdBool.True;
//				mDoc.getSelection().getFont().setAllCaps(a);
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getDoubleStrikeThrough2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "当前文本双删除线：" + mDoc.getSelection().getFont().getDoubleStrikeThrough2());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setDoubleStrikeThrough2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdBool test = mDoc.getSelection().getFont().getDoubleStrikeThrough2();
//			if(test!=null)
//			{
            WdBool arg0 = WdBool.True;
            mDoc.getSelection().getFont().setDoubleStrikeThrough2(arg0);
//			}
//			else
//			{
//				WdBool a = WdBool.True;
//				mDoc.getSelection().getFont().setDoubleStrikeThrough2(a);
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setStrikeThrough2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
////			WdBool test = mDoc.getSelection().getFont().getDoubleStrikeThrough2();
//			if(mDoc.getSelection().getFont().getDoubleStrikeThrough2() != null)
//			{
            WdBool arg0 = WdBool.True;
            mDoc.getSelection().getFont().setStrikeThrough2(arg0);
//			}
//			else
//			{
//				WdBool a = WdBool.True;
//				mDoc.getSelection().getFont().setStrikeThrough2(a);
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_shrink() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().shrink();
            Util.showToast(mContext, "缩小字体成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_grow() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().grow();
            Util.showToast(mContext, "放大字体成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getSize() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区字体大小" + mDoc.getSelection().getFont().getSize());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setSize() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setSize(16);
            Util.showToast(mContext, "设置字体大小为：16磅");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getNameFarEast() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区字体名称为：" + mDoc.getSelection().getFont().getNameFarEast());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setNameFarEast() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setNameFarEast("仿宋");
            Util.showToast(mContext, "设置字体名称为：仿宋");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getNameAscii() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "西文字体名称：" + mDoc.getSelection().getFont().getNameAscii());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setName() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setName("微软雅黑");
            Util.showToast(mContext, "设置字体名称：微软雅黑");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getTextColor() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区文本颜色" + mDoc.getSelection().getFont().getTextColor());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getUnderlineColor() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "选区文本下划线颜色" + mDoc.getSelection().getFont().getUnderlineColor());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setUnderlineColor() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.getSelection().getFont().setUnderlineColor(0xff0000);
            Util.showToast(mContext, "设置文本下划线颜色0xff0000");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getUnderline2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			mDoc.getSelection().getFont().getUnderline2()
            Util.showToast(mContext, "获取下划线类型" + mDoc.getSelection().getFont().getUnderline2());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setUnderline2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdUnderline underline = WdUnderline.wdUnderlineDashHeavy;
            mDoc.getSelection().getFont().setUnderline2(underline);
            Util.showToast(mContext, "设置下划线类型wdUnderlineDashHeavy");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getItalic2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdUnderline underline = WdUnderline.wdUnderlineDashHeavy;
//			mDoc.getSelection().getFont().getItalic2();
            Util.showToast(mContext, "选区倾斜：" + mDoc.getSelection().getFont().getItalic2());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setItalic2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdBool test = WdBool.True;
            mDoc.getSelection().getFont().setItalic2(test);
            Util.showToast(mContext, "设置倾斜成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setBold2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            WdBool test = WdBool.True;
            mDoc.getSelection().getFont().setBold2(test);
            Util.showToast(mContext, "设置加粗成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getSubscript() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdBool test = WdBool.True;
//			mDoc.getSelection().getFont().getSubscript()
            Util.showToast(mContext, "选区是下标：" + mDoc.getSelection().getFont().getSubscript());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_setSubscript() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdBool test = mDoc.getSelection().getFont().getSubscript();
//			WdBool test = WdBool.True;
//			mDoc.getSelection().getFont().getSubscript()
//			if(test == null)
//			{
            WdBool a = WdBool.True;
            mDoc.getSelection().getFont().setSubscript(a);
//			}
//			else
//			{
//				mDoc.getSelection().getFont().setSubscript(null);
//			}
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void font_getItalic() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
//			WdBool test = WdBool.True;
//			mDoc.getSelection().getFont().getSubscript()
            Util.showToast(mContext, "选区倾斜：" + mDoc.getSelection().getFont().getItalic());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //待实现方法
    private void document_getName() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "文档的标题：" + mDoc.getName());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	重复
    private void document_getPage() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "文档第1页高度：" + mDoc.getPage(0));
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //	重复
    private void document_getPageCount() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "文档第2页高度：" + mDoc.getPageCount());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //废弃
    private void document_getSelection() {
//		mDoc.getSelection().copy();
    }

    private void document_print() {
//		mDoc.printOut(1);
    }

    private void document_save() {
        try {
            mDoc.save(true);
        } catch (RemoteException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    private void document_acceptAllRevisions() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.acceptAllRevisions();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_acceptAllRevise() {

    }

    private void document_pageUp() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.pageUp();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_pageDown() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.pageDown();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getCommentNumber() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "文档批注个数：" + mDoc.getCommentNumber());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_isInRevisionMode() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "修订模式：" + mDoc.isInRevisionMode());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getUser() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "作者名称：" + mDoc.getUser());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_isInHandWriterMode() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            Util.showToast(mContext, "作者名称：" + mDoc.isInHandWriterMode());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getBuiltInDocumentProperties() {

    }

    private void document_saveToImage() {

    }

    private void document_changeRevisionState() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.changeRevisionState(50, true);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_close2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            WdSaveOptions a = WdSaveOptions.wdDoNotSaveChanges;
            WdOriginalFormat b = WdOriginalFormat.wdOriginalDocumentFormat;
            boolean c = true;
            mDoc.close2(a, b, c);
//			mDoc.saveAs(SAVE_AS_PATH, SaveFormat.DOC, "", "");
//
//			Util.showToast(mContext, "另存成功，另存路径为 ： " + SAVE_AS_PATH);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    //重复接口
    private void document_delAllComments() {
//		if (!isDocumentOpened())
//		{
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//
//		try
//		{
//			mDoc.denyAllRevision();
////			mDoc.saveAs(SAVE_AS_PATH, SaveFormat.DOC, "", "");
////
////			Util.showToast(mContext, "另存成功，另存路径为 ： " + SAVE_AS_PATH);
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    private void document_denyAllRevision() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.denyAllRevision();
            Util.showToast(mContext, "拒绝所有修订成功！");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getProtectionType2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "文档保护类型：" + mDoc.getProtectionType2());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_setSaved() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.setSaved(true);
            Util.showToast(mContext, "文档直接关闭成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getViewProgress() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float a = mDoc.getViewProgress();
            Util.showToast(mContext, "文档打开进度：" + a);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getViewScale() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float a = mDoc.getViewScale();
            Util.showToast(mContext, "文档缩放比：" + a);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getViewScrollX() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float a = mDoc.getViewScrollX();
            Util.showToast(mContext, "获取划过X坐标：" + a);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getViewScrollY() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            float a = mDoc.getViewScrollY();
            Util.showToast(mContext, "获取划过Y坐标：" + a);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_isLoadOK() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
//			float a = mDoc.isLoadOK()mDoc.isLoadOK();
            Util.showToast(mContext, "文档加载完成：" + mDoc.isLoadOK());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_protect2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            WdProtectionType arg0 = WdProtectionType.wdAllowOnlyComments;
            mDoc.protect2(arg0, false, "", false, false);
            Util.showToast(mContext, "文档设置为批注保护模式。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_range() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.range(0, 5).cut();
            Util.showToast(mContext, "指定range范围剪切成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_redo2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.redo2(3);
            Util.showToast(mContext, "恢复三步操作成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_rejectAllRevisions() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.rejectAllRevisions();
            Util.showToast(mContext, "rejectAllRevisions方法拒绝所有修订成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void save() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.save(false);
            Util.showToast(mContext, "自动保存所有文档成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_setUser() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.setUser("houjingtest");
            Util.showToast(mContext, "设置修订批注的作者名为:" + mDoc.getUser());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_undo2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.undo2(3);
            Util.showToast(mContext, "撤销三步操作成功");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_undoClear() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.undoClear();
            Util.showToast(mContext, "清除撤销操作列表成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_unProtectDocument() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.unprotect("");
            Util.showToast(mContext, "解锁文档保护成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void document_getContent() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "getContent接口操作：" + mDoc.getContent().getStoryLength());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void Shapes_addPicture() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
//			String arg0;
//			boolean arg1;
//			boolean arg2;
            float arg3 = 10;
            float arg4 = 10;
            float arg5 = 100;
            float arg6 = 100;
            int arg7 = 7;
            WrapType arg8 = WrapType.BottomOfText;
            mDoc.getShapes().addPicture(INLINE_PIC_PATH, false, false, arg3, arg4, arg5, arg6, arg7, arg8);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void Shapes_addPicture2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
//			String arg0;
//			boolean arg1;
//			boolean arg2;
            int arg3 = 10;
            int arg4 = 10;
            int arg5 = 100;
            int arg6 = 100;
            cn.wps.moffice.service.doc.Range anchor = mDoc.getSelection().getRange();
//			int arg7 = 7;
//			WrapType arg8 = WrapType.BottomOfText;
            mDoc.getShapes().addPicture2(INLINE_PIC_PATH, false, false, arg3, arg4, arg5, arg6, anchor);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void Shapes_addTextbox2() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            cn.wps.moffice.service.doc.Range anchor = mDoc.getSelection().getRange();
            int arg4 = 100;
            int arg3 = 100;
            int arg2 = 10;
            int arg1 = 10;
            MsoTextOrientation arg0 = MsoTextOrientation.msoTextOrientationHorizontal;
            //			int arg7 = 7;
//			WrapType arg8 = WrapType.BottomOfText;
            mDoc.getShapes().addTextbox2(arg0, arg1, arg2, arg3, arg4, anchor);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void Shapes_addOLEControl() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {

            cn.wps.moffice.service.doc.Range anchor = mDoc.getSelection().getRange();
            int arg4 = 100;
            int arg3 = 100;
            int arg2 = 10;
            int arg1 = 10;
            String arg0 = "DSEAL.DsealCtrl.1";
            //			int arg7 = 7;
//			WrapType arg8 = WrapType.BottomOfText;
            mDoc.getShapes().addOLEControl(arg0, arg1, arg2, arg3, arg4, anchor);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void Shapes_addShape() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            MsoAutoShapeType type = MsoAutoShapeType.msoShape32pointStar;
            cn.wps.moffice.service.doc.Range anchor = mDoc.getSelection().getRange();
            int arg4 = 100;
            int arg3 = 100;
            int arg2 = 10;
            int arg1 = 10;
            mDoc.getShapes().addShape(type, arg1, arg2, arg3, arg4, anchor);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getIPictureFormat() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "第一个图片格式：" + mDoc.getShapes().item(1).getIPictureFormat());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getOLE() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获得OLE对象：" + mDoc.getShapes().item(1).getOLE());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_select() {
//		接口未实现
//		if (!isDocumentOpened())
//		{
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//
//		try
//		{
//			Variant arg0 = null;
//			Util.showToast(mContext, "获得OLE对象：" + mDoc.getShapes().item(1).select(arg0));
//		}
//		catch (RemoteException e)
//		{
//			Util.showToast(mContext, FAIL_NOTE);
//			e.printStackTrace();
//		}
    }

    private void shape_getWrapFormat() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象绕排方式 ：" + mDoc.getShapes().item(1).getWrapFormat());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_hasInk() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "墨迹对象 ：" + mDoc.getShapes().item(1).hasInk());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_incrementTop() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getShapes().item(1).incrementTop(10);
            Util.showToast(mContext, "垂直方向移动对象,参数10");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_incrementLeft() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getShapes().item(1).incrementLeft(10);
            Util.showToast(mContext, "水平方向移动对象,参数10");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getWidth() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象宽度：" + mDoc.getShapes().item(1).getWidth());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_setVisible() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            MsoTriState arg0 = MsoTriState.msoFalse;
            mDoc.getShapes().item(1).setVisible(arg0);
            Util.showToast(mContext, "设置第一个对象不可见。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getVisible() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "第一个对象可见状态：" + mDoc.getShapes().item(1).getVisible());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getType() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "第一个对象类型：" + mDoc.getShapes().item(1).getType());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getTop() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象顶部位置：" + mDoc.getShapes().item(1).getTop());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getTextFrame() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获取文本框对象：" + mDoc.getShapes().item(1).getTextFrame());
            mDoc.getShapes().item(1).getTextFrame().deleteText();
            Util.showToast(mContext, "删除文本框对象成功。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getLine() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获取线性对象：" + mDoc.getShapes().item(1).getLine());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getLeft() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象水平位置 ：" + mDoc.getShapes().item(1).getLeft());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getHeight() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象高度：" + mDoc.getShapes().item(1).getHeight());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getGroupItems() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获取GroupShapes组合对象：" + mDoc.getShapes().item(1).getGroupItems());
            mDoc.getShapes().item(1).getGroupItems().item(1).delete();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getFill() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象填充格式 ：" + mDoc.getShapes().item(1).getFill());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_getAlternativeText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "对象描述：" + mDoc.getShapes().item(1).getAlternativeText());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void shape_setAlternativeText() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getShapes().item(1).setAlternativeText("设置对象描述成功！");
            Util.showToast(mContext, "对象描述：" + mDoc.getShapes().item(1).getAlternativeText());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void View_putRevisionsView() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            WdRevisionsView arg0 = WdRevisionsView.wdRevisionsViewFinal;
            mDoc.getActiveWindow().getView().putRevisionsView(arg0);
            Util.showToast(mContext, "显示修订原始状态。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void View_putShowRevisionsAndComments() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            mDoc.getActiveWindow().getView().putShowRevisionsAndComments(false);
            Util.showToast(mContext, "设置显示修订和批注界面。");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void View_getShowRevisionsAndComments() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "是否显示修订和批注界面：" + mDoc.getActiveWindow().getView().getShowRevisionsAndComments());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void View_getRevisionsView() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "修订视图显示状态：" + mDoc.getActiveWindow().getView().getRevisionsView());
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void setVisibleFill() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            MsoTriState arg0 = MsoTriState.msoFalse;
            mDoc.getShapes().item(1).getFill().setVisible(arg0);
            Util.showToast(mContext, "设置成功，第一个对象填充颜色空白");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void setVisibleLine() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {

            MsoTriState arg0 = MsoTriState.msoFalse;
            mDoc.getShapes().item(1).getLine().setVisible(arg0);
            Util.showToast(mContext, "设置成功，第一个对象设置为无边框");
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void getGroupItemsItem() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            if (mDoc.getSelection().getShapeRange().getGroupItems().item(1) == null) {
                Util.showToast(mContext, "获取对象失败");
            } else {
                Util.showToast(mContext, "组合对象获取成功。");
            }
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void getGroup() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获取组合对象后水平移动：" + mDoc.getSelection().getShapeRange().group());
            mDoc.getSelection().getShapeRange().group().incrementLeft(10);
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void getTextRange() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
            Util.showToast(mContext, "获取文本框中的range对象后剪切：" + mDoc.getSelection().getShapeRange().getTextFrame().getTextRange());
            mDoc.getSelection().getShapeRange().getTextFrame().getTextRange().cut();
        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void groupCount() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }

        try {
//			if(!(null == mDoc.getSelection().getShapeRange()))
//			{
//				Util.showToast(mContext, "获取getShapeRange成功");
//			}
            if (!(mDoc.getSelection().getShapeRange().getGroupItems() == null)) {
                Util.showToast(mContext, "获取组合对象成功");
            } else {
                Util.showToast(mContext, "获取文本框中的range对象后剪切：" + mDoc.getSelection().getShapeRange().getGroupItems().getCount());
            }


        } catch (RemoteException e) {
            Util.showToast(mContext, FAIL_NOTE);
            e.printStackTrace();
        }
    }

    private void docFairCopy() {
        if (!isDocumentOpened()) {
            Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
            return;
        }
        try {
            mDoc.fairCopy(SAVE_AS_PATH, "");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

//	private void showThumbView() {
//		if (!isPresentationOpened()) {
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//		try {
//			mPresentation.showThumbView(true);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//	private void closeThumbView() {
//		if (!isPresentationOpened()) {
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//		try {
//			mPresentation.showThumbView(false);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void PlayState() {
//		if (!isPresentationOpened()) {
//			Util.showToast(mContext, "操作失败，文档未打开，请先打开或者新建文档");
//			return;
//		}
//		try {
//			Util.showToast(mContext,""+mPresentation.isPlayState());
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}


}
