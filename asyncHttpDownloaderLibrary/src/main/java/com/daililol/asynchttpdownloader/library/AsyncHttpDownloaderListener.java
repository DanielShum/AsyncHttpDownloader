package com.daililol.asynchttpdownloader.library;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

/**
 * Created by DennyShum on 9/2/15.
 */
public class AsyncHttpDownloaderListener extends BroadcastReceiver{

    public static String ActionType = "com.daililol.asynchttpdownloader.library.AsyncHttpDownloaderListener";
    private static String KEY_UPDATE_TYPE = "KEY_UPDATE_TYPE";
    private static String KEY_DOWNLOAD_URL = "KEY_DOWNLOAD_URL";
    private static String KEY_SAVED_FILE = "KEY_SAVED_FILE";
    private static String KEY_ERROR_MESSAGE = "KEY_ERROR_MESSAGE";
    private static String KEY_TOTAL_BYTE = "KEY_TOTAL_BYTE";
    private static String KEY_DOWNLOADED_BYTE = "KEY_DOWNLOADED_BYTE";

    private IntentFilter filter = new IntentFilter(ActionType);

    public static enum UpdateType{
        UPDATE_PROGRESS,
        FAILED,
        SUCCESS
    }

    public Callback callback;

    public AsyncHttpDownloaderListener(Callback callback){
        this.callback =  callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals(ActionType)) return;
        if (!intent.getExtras().containsKey(KEY_UPDATE_TYPE)) return;

        UpdateType updateType = UpdateType.valueOf(intent.getExtras().getString(KEY_UPDATE_TYPE));
        String downloadUrl = intent.getExtras().getString(KEY_DOWNLOAD_URL);

        switch (updateType){
            case UPDATE_PROGRESS:
                int totalByte = intent.getExtras().getInt(KEY_TOTAL_BYTE);
                int downloadByte = intent.getExtras().getInt(KEY_DOWNLOADED_BYTE);
                if (callback != null)
                    callback.onProgressUpdate(downloadUrl, totalByte, downloadByte);
                break;
            case FAILED:
                String errorMessage = intent.getExtras().getString(KEY_ERROR_MESSAGE);
                if (callback != null)
                    callback.onFailed(downloadUrl, errorMessage);
                break;
            case SUCCESS:
                String fileUrl = intent.getExtras().getString(KEY_SAVED_FILE);
                if (callback != null)
                    callback.onSuccess(downloadUrl, new File(fileUrl));
                break;
        }



    }


    public void register(Context context){
        LocalBroadcastManager.getInstance(context).registerReceiver(this, filter);
    }

    public void unregister(Context context){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    public static interface Callback{
        public void onProgressUpdate(String downloadUrl, int total, int completed);
        public void onFailed(String downloadUrl, String errorMessage);
        public void onSuccess(String downloadUrl, File file);
    }

    public static void broadcastUpdateProgressMessage(Context context, String downloadUrl, int total, int completed){
        Intent intent = new Intent(ActionType);
        intent.putExtra(KEY_UPDATE_TYPE, UpdateType.UPDATE_PROGRESS.name());
        intent.putExtra(KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(KEY_TOTAL_BYTE, total);
        intent.putExtra(KEY_DOWNLOADED_BYTE, completed);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastSuccessMessage(Context context, String downloadUrl, String savedFile){
        Intent intent = new Intent(ActionType);
        intent.putExtra(KEY_UPDATE_TYPE, UpdateType.SUCCESS.name());
        intent.putExtra(KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(KEY_SAVED_FILE, savedFile);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void broadcastFailureMessage(Context context, String downloadUrl, String errorMessage){
        Intent intent = new Intent(ActionType);
        intent.putExtra(KEY_UPDATE_TYPE, UpdateType.FAILED.name());
        intent.putExtra(KEY_DOWNLOAD_URL, downloadUrl);
        intent.putExtra(KEY_ERROR_MESSAGE, errorMessage);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
