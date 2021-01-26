package cn.wps.moffice.demo.bean;

import java.io.Serializable;

import cn.wps.moffice.demo.plugin.WpsPlugin;
import cn.wps.moffice.demo.util.Define;

/**
 * WPS打开时需要传入的参数集合
 */
public class WpsOpenBean implements Serializable {
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 是否AIDL打开
     */
    private boolean isAIDL = true;
    /**
     * 打开方式
     */
    private String openMode = Define.READ_ONLY;
    /**
     * 是否打开修订
     */
    private boolean reviseMode = true;
    /**
     * 用户名称
     */
    private String userName;
    /**
     * android 激活外部传入序列号
     */
    private String serialNumberOther = "";
    /**
     * 书签
     */
    private String bookMarks = "";
    /**
     * 打开方式
     */
    private String openType = WpsPlugin.OPEN_TYPE_THIRD_PARTY;


    public WpsOpenBean() {
    }

    public WpsOpenBean(String filePath, boolean isAIDL, String openMode) {
        this(filePath,isAIDL,openMode,true,"userName","","");
    }

    public WpsOpenBean(String filePath, boolean isAIDL, String openMode, boolean reviseMode, String userName, String bookMarks, String serialNumberOther) {
        this.filePath = filePath;
        this.isAIDL = isAIDL;
        this.openMode = openMode;
        this.reviseMode = reviseMode;
        this.userName = userName;
        this.bookMarks = bookMarks;
        this.serialNumberOther = serialNumberOther;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public boolean isAIDL() {
        return isAIDL;
    }

    public void setAIDL(boolean AIDL) {
        isAIDL = AIDL;
    }

    public String getOpenMode() {
        return openMode;
    }

    public void setOpenMode(String openMode) {
        this.openMode = openMode;
    }

    public boolean isReviseMode() {
        return reviseMode;
    }

    public void setReviseMode(boolean reviseMode) {
        this.reviseMode = reviseMode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSerialNumberOther() {
        return serialNumberOther;
    }

    public String getBookMarks() {
        return bookMarks;
    }

    public void setBookMarks(String bookMarks) {
        this.bookMarks = bookMarks;
    }

    public void setSerialNumberOther(String serialNumberOther) {
        this.serialNumberOther = serialNumberOther;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    @Override
    public String toString() {
        return "WpsOpenBean{" +
                "filePath='" + filePath + '\'' +
                ", isAIDL=" + isAIDL +
                ", openMode='" + openMode + '\'' +
                ", reviseMode=" + reviseMode +
                ", userName='" + userName + '\'' +
                ", bookMarks='" + bookMarks + '\'' +
                ", serialNumberOther='" + serialNumberOther + '\'' +
                '}';
    }
}
