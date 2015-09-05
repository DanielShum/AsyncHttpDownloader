package com.daililol.asynchttpdownloader.library;

import android.content.Context;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by DennyShum on 9/2/15.
 */
public class AsyncHttpDownloader extends Object{

    private int maxTask = 50;
    private static AsyncHttpDownloader instance;
    private Map<String, DownloadTask> downloadPool;

    synchronized public static AsyncHttpDownloader getInstance(){
        if (instance == null) instance = new AsyncHttpDownloader();
        return  instance;
    }

    public void setDefaultDirectoryUnderSdCard(String directory){
        StorageManager.getInstance().setDefaultDirectoryUnderSdCard(directory);
    }

    public Map<String, DownloadTask>  getDownloadPool(){
        return downloadPool;
    }

    public DownloadTask getDownloadTask(String downloadUrl){
        return getDownloadPool().get(downloadUrl);
    }

    public boolean isDownloadTaskExists(String downloadUrl){
        return getDownloadPool().containsKey(downloadUrl);
    }

    public void setMaxTask(int maxTask){
        this.maxTask = maxTask;
    }

    public AsyncHttpDownloader(){
        downloadPool = new HashMap<String, DownloadTask>();
    }

    synchronized public boolean cancelDownloadTask(String downloadUlr){
        if (!getDownloadPool().containsKey(downloadUlr)) return false;
        DownloadTask task = getDownloadPool().get(downloadUlr);
        task.cancelTask();
        getDownloadPool().remove(downloadUlr);
        return true;
    }

    synchronized public void cancelAllRunningTasks(){
        Set<String> keySet = new HashSet<String>();
        keySet.addAll(getDownloadPool().keySet());
        Iterator<String> iterator = keySet.iterator();

        while (iterator.hasNext()){
            cancelDownloadTask(iterator.next());
        }
    }

    synchronized public boolean addDownloadTask(Context context, String downloadUrl){
        return addDownloadTask(context, downloadUrl, null);
    }

    synchronized public boolean addDownloadTask(final Context context, String downloadUrl, String desFileUrl){

        if (isDownloadTaskExists(downloadUrl)) return true;
        if (getDownloadPool().size() >= maxTask) return false;

        if (desFileUrl == null) desFileUrl = StorageManager.getInstance().getDefaultFilePath(downloadUrl);
        if (desFileUrl == null) return false;

        DownloadTask task = new DownloadTask(downloadUrl, desFileUrl);

        task.setCallback(new DownloadTask.DownloadTaskCallback() {
            long lastUpdateTime = System.currentTimeMillis();
            Context appContext = context.getApplicationContext();

            @Override
            public void onProgressUpdate(String downloadUrl, int totalSize, int completed) {
                if ((System.currentTimeMillis() - lastUpdateTime) > 128){
                    AsyncHttpDownloaderListener.broadcastUpdateProgressMessage(appContext, downloadUrl, totalSize, completed);
                    lastUpdateTime = System.currentTimeMillis();
                }
            }

            @Override
            public void onFailed(String downloadUrl, String errorMessage) {
                AsyncHttpDownloaderListener.broadcastFailureMessage(appContext, downloadUrl, errorMessage);
                getDownloadPool().remove(downloadUrl);
            }

            @Override
            public void onSuccess(String downloadUrl, File file) {
                AsyncHttpDownloaderListener.broadcastSuccessMessage(appContext, downloadUrl, file.getAbsolutePath());
                getDownloadPool().remove(downloadUrl);
            }

            @Override
            public void onCancelTask(String downloadUrl) {
                getDownloadPool().remove(downloadUrl);
            }
        });

        getDownloadPool().put(downloadUrl, task);
        task.start();


        return true;
    }


}
