package com.daililol.asynchttpdownloader.library;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by DennyShum on 9/3/15.
 */
public class StorageManager {

    private static StorageManager instance;
    private String storagePath = "/temp/";

    synchronized public static StorageManager getInstance(){
        if (instance == null) instance = new StorageManager();
        return instance;
    }

    synchronized public void setDefaultDirectoryUnderSdCard(String pathUnderSdcard){
        storagePath = "/" + pathUnderSdcard + "/";
    }

    public String getDefaultStorageDirectory(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            String sdCardStorage = Environment.getExternalStorageDirectory().getPath() + storagePath;
            File file = new File(sdCardStorage);
            if (!file.exists()) file.mkdirs();
            if (file.exists() && file.isDirectory() && file.canWrite()){
                return sdCardStorage;
            }else{
                Log.i("StorageManager", String.format("The destination directory %s may not be writable.", sdCardStorage));
                return null;
            }
        }

        Log.i("StorageManager", "SD card not available, nowhere to save the downloaded files.");
        return null;
    }

    public String getDefaultFilePath(String downloadUrl){
        if (getDefaultStorageDirectory() == null) return null;
        return getDefaultStorageDirectory() + getMd5String(downloadUrl);
    }


    private String getMd5String(String val) {

        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();
            return getString(m);
        } catch (NoSuchAlgorithmException ne) {
            return "";
        }

    }

    private String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
