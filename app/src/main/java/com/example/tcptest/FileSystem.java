package com.example.tcptest;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileSystem {

    public static boolean fileExists(Context c){
        File file = c.getFileStreamPath("data");
        return file.exists();
    }

    public static void createDataFile(Context c){
        String fileName= "data";
        String textToWrite = TwitchConnection.auth_tok+"!"+TwitchConnection.client_tok;
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(textToWrite.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readFile(Context c){
        StringBuilder data = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("config/initialconfig")));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                data.append(mLine).append("!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String result = data.toString();
        String[] s = result.split("!");
        TwitchConnection.auth_tok = s[0];
        TwitchConnection.client_tok = s[1];
        System.out.println(TwitchConnection.auth_tok);
        System.out.println(TwitchConnection.client_tok);

    }

    public static void populateFields(Context c){
        String fileName= "data";
        FileInputStream fis = null;
        try {
            fis = c.openFileInput(fileName);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            StringBuilder stringBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // Error occurred when opening raw file for reading.
                e.printStackTrace();
            } finally {
                String contents = stringBuilder.toString();
                String[] s = contents.split("!");
                TwitchConnection.auth_tok = s[0];
                TwitchConnection.client_tok = s[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
