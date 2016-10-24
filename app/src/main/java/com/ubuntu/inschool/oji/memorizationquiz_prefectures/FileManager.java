package com.ubuntu.inschool.oji.memorizationquiz_prefectures;

import android.util.Log;

import com.ubuntu.inschool.oji.memorizationquiz_prefectures.layout.MainActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by oji on 16/10/11.
 * File読み書き用クラス
 */
public class FileManager {
    //変数宣言
    private File            filePath        = null;
    private StringBuilder   fileContents    = new StringBuilder();
    private String          readLine        = "";

    private FileReader      reader          = null;
    private BufferedReader  bufferedReader  = null;

    private FileOutputStream    outputStream = null;
    private BufferedWriter      writer       = null;

    public FileManager(File filePath) {
        this.filePath = filePath;
    }

    //File作成
    public boolean createFile() {
        try {
            return filePath.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //File削除
    public boolean deleteFile() {
        if (filePath.exists() && filePath.isFile()) {
            return filePath.delete();
        }
        return false;
    }

    //File読み込み
    public String read() {

        if (filePath.isFile() && filePath.canRead()) {

            try {
                reader          = new FileReader(filePath);
                bufferedReader  = new BufferedReader(reader);
                fileContents    = new StringBuilder();

                readLine = bufferedReader.readLine();
                while (readLine != null) {
                    fileContents.append(readLine).append(MainActivity.BR);
                    readLine = bufferedReader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileContents.toString();
    }

    //File書き出し
    public void write() {

        if (filePath.exists()) {
            try {
                outputStream = new FileOutputStream(filePath);
                writer       = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                writer.write(fileContents.toString());
                writer.flush();
                writer.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d("[Write_Error]", "Couldn't find such a file");
        }
    }

    public void setFileContents(String contents) {
        this.fileContents = new StringBuilder(contents);
    }
}
