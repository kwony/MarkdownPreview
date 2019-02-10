package com.kwony.mdpreview.Utilities;

import android.content.res.Resources;
import android.os.Environment;

import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileManager {

    public static final boolean createFolder(String dst, String folderName) {
        boolean folderCreated = true;
        File folder = new File(dst + File.separator + folderName);

        if (!folder.exists()) {
            folderCreated = folder.mkdir();
        }

        return folderCreated;
    }

    public static final void createFile(String dst, String fileName, String initialValue) {
        File file = new File(dst + File.separator + fileName);

        if (file.exists())
            file.delete();

        try {
            file.createNewFile();

            final String welcomeString = new String(initialValue);
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            osw.write(welcomeString);
            osw.flush();
            osw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final boolean deleteFile(FileInfo fileInfo) {
        File file = new File(fileInfo.getFilePath()
                + File.separator + fileInfo.getFileName());

        return file.delete();
    }

    public static final boolean checkFileExist(FileInfo fileInfo) {
        File file = new File(fileInfo.getFilePath()
                + File.separator + fileInfo.getFileName());

        return file.exists();
    }

    public static final boolean compareFileContent(FileInfo firstFileInfo, FileInfo secondFileInfo) {
        File firstFile = new File(firstFileInfo.getFilePath()
                + File.separator + firstFileInfo.getFileName());

        File secondFile = new File(secondFileInfo.getFilePath()
                + File.separator + secondFileInfo.getFileName());

        try {
            FileInputStream f1 = new FileInputStream(firstFile);
            FileInputStream f2 = new FileInputStream(secondFile);

            int data;

            while ((data = f1.read()) != -1) {
                if (data != f2.read())
                    return false;
            }

        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static final StringBuffer readFileValue(String filePath, String fileName) {
        StringBuffer outStringBuf = new StringBuffer();
        String inputLine = null;

        File file = new File(filePath + File.separator + fileName);

        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fIn = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);

            while (true) {
                try {
                    inputLine = inBuff.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }

                if (inputLine == null)
                    break;

                outStringBuf.append(inputLine);
                outStringBuf.append("\n");
            }
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        return outStringBuf;
    }

    public static final void writeFileValue(String filePath, String fileName, String value) {
        File file = new File(filePath + File.separator + fileName);

        if (file.exists()) {
            try {
                final String welcomeString = new String(value);
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                try {
                    osw.write(welcomeString);
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyFile(FileInfo srcInfo, FileInfo dstInfo) throws IOException {
        File srcFile = new File(srcInfo.getFilePath() + File.separator + srcInfo.getFileName());
        File dstFile = new File(dstInfo.getFilePath() + File.separator + dstInfo.getFileName());

        if (!srcFile.exists())
            return false;

        if (!dstFile.exists())
            dstFile.createNewFile();

        try (InputStream in = new FileInputStream(srcFile)) {
            try (OutputStream out = new FileOutputStream(dstFile)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }

        return true;
    }
}
