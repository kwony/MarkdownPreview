package com.kwony.mdpreview.Utilities;

import android.content.res.Resources;
import android.os.Environment;

import com.kwony.mdpreview.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

        if (!file.exists()) {
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
}
