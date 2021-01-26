# libs_develop_wps
Android 调用WPS提供的AIDL或第三方方式打开，进行查看或编辑文档功能

一、初始化（在需要调用WPS功能的页面进行，WpsPlugin创建时会打开）
// 初始化WPS插件
  wpsPlugin = new WpsPlugin(getActivity(), new WpsPlugin.IWpsPluginInterface() {
      @Override
      public void onSaveFile(String filePath) {
          // 这里进行了保存操作，直接进行上传，确保用户的每次保存都上传到服务器
      }

      @Override
      public void onClosedWindow(String filePath) {
          // 这里是WPS页面退出的监听事件
      }

      @Override
      public void onOpenFile(String filePath) {
          // 打开文件成功
      }

      @Override
      public void onOpenFailed(String filePath, String msg) {
          // 打开失败
          
      }

      @Override
      public void unbind(String filePath) {
          // 服务解除绑定 用户关闭WPS窗口，可以作为PDF文件关闭监听
          Logger.e("unbind", filePath);
      }
  });
  
  二、调用WPS打开文档
  // 默认为只读模式打开
  String openMode = JsonUtil.getString2Json(object, WpsPlugin.OPEN_MODE, Define.READ_ONLY);
  String serialNum = JsonUtil.getString2Json(object, WpsPlugin.SERIAL_NUMBER_OTHER, "");
  // 默认修订
  // 有书签不打开修订，没有默认打开修订（默认打开修订，书签的概念比较模糊，全部交给H5页面传入的参数为准）
  boolean enterReviseMode = JsonUtil.getBoolean2Json(object, WpsPlugin.ENTER_REVISE_MODE, true);
  JSONArray bookMarks = JsonUtil.getJsonArray2Json(object, WpsPlugin.BOOK_MARKS);
  WpsOpenBean wpsOpenBean = new WpsOpenBean();
  wpsOpenBean.setFilePath(absolutePath);
  wpsOpenBean.setAIDL(true);
  wpsOpenBean.setOpenMode(openMode);
  wpsOpenBean.setBookMarks(bookMarks.toString());
  wpsOpenBean.setReviseMode(enterReviseMode);
  wpsOpenBean.setUserName(userName);
  // 安卓激活：外部传入序列号
  wpsOpenBean.setSerialNumberOther(serialNum);
  // 没有书签时按照第三方模式打开(AIDL方式太复杂，感觉不太稳定)
  if (Util.isPDFFile(absolutePath)){
      wpsOpenBean.setOpenType(WpsPlugin.OPEN_TYPE_AIDL);
  }else{
      wpsOpenBean.setOpenType(bookMarks != null && bookMarks.length() == 0 ? WpsPlugin.OPEN_TYPE_THIRD_PARTY : WpsPlugin.OPEN_TYPE_AIDL);
  }
  wpsPlugin.openFile(wpsOpenBean);
