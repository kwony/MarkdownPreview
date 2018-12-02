package com.kwony.mdpreview;

public class FileInfo {
    private long mFileId;
    private String mFileName;
    private String mFilePath;
    private String mFileDate;

    public FileInfo(long fileId, String fileName, String filePath, String fileDate) {
        mFileId = fileId;
        mFileName = fileName;
        mFilePath = filePath;
        mFileDate = fileDate;
    }

    public long getFileId() {
        return mFileId;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public String getFileDate() {
        return mFileDate;
    }
}
