package com.openplatform.adas.datamodel;

import java.io.Serializable;

/**
 * Author : ZhongJiHao
 * Organization : Shenzhen AiDriving CO.,LTD
 * Date :  2019/4/30 10:21
 * Description :
 */
public class UpdateItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String apkType;
    private long fileSize;
    private String fileMd5;
    private String downloadUrl;
    private long progress;
    private long version;
    private DownloadStatus status = DownloadStatus.NONE;

    public enum DownloadStatus{
        NONE,WAITE,PAUSE,FAILED,CHECK,INSTALL,COMPLETE
    }

    public String getApkType() {
        return apkType;
    }

    public void setApkType(String apkType) {
        this.apkType = apkType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String md5) {
        this.fileMd5 = md5;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public DownloadStatus getStatus() {
        return status;
    }

    public void setStatus(DownloadStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UpdateItem ["
                +"apkType=" +apkType
                +", fileSize=" + fileSize + ", fileMd5=" + fileMd5
                + ", downloadUrl=" + downloadUrl
                + ", progress=" + progress
                + ", version=" + version
                + ", status=" + status
                + "]";
    }
}
