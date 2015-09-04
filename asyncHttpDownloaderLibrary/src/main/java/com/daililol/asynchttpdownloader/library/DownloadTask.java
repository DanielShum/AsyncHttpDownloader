package com.daililol.asynchttpdownloader.library;

import android.os.AsyncTask;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by DennyShum on 9/2/15.
 */
public class DownloadTask extends AsyncTask<String, Integer, String>{

    private static enum ResultType{
        UPDATE_PROGRESS,
        FAILED,
        SUCCESS
    }

    private String downloadUrl;
    private String destinationFileUrl;

    private long timeTaskCreated = System.currentTimeMillis();
    private long timeDownloadStarted = 0;
    private DownloadTaskCallback callback;
    private boolean isCanceled = false;
    private int totalByte = 0;
    private int byteDownloaded = 0;

    //http connection
    private URL httpUrl;
    private URLConnection urlConnection;
    private InputStream inputStream;
    private FileOutputStream outputStream;

    public static interface DownloadTaskCallback{
        public void onProgressUpdate(String downloadUrl, int totalSize, int completed);
        public void onFailed(String downloadUrl, String errorMessage);
        public void onSuccess(String downloadUrl, File file);
        public void onCancelTask(String downloadUrl);
    }

    public long getTimeTaskCreated(){
        return timeTaskCreated;
    }

    public long getTimeDownloadStarted(){
        return timeDownloadStarted;
    }

    public int getTotalByte(){
        return totalByte;
    }

    public int getCompleted(){
        return byteDownloaded;
    }

    public DownloadTask(String downloadUrl, String destinationFile){
        this.downloadUrl = downloadUrl;
        this.destinationFileUrl = destinationFile;
    }

    public void setCallback(DownloadTaskCallback callback){
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            httpUrl = new URL(downloadUrl);
            urlConnection = httpUrl.openConnection();
            urlConnection.setReadTimeout(35000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();

            //ERROR 1
            if (inputStream == null) {
                return "Can not open connection.";
            }

            totalByte = urlConnection.getContentLength();
            outputStream = new FileOutputStream(new File(destinationFileUrl));
            timeDownloadStarted = System.currentTimeMillis();

            byte[] buffer = new byte[1024];
            int byteRead = 0;

            while ((byteRead = inputStream.read(buffer)) != -1){
                if (isCanceled || Thread.currentThread().isInterrupted()){
                    break;
                }
                outputStream.write(buffer, 0, byteRead);
                byteDownloaded += byteRead;
                publishProgress(totalByte, byteDownloaded);
            }

            outputStream.close();
            inputStream.close();
            return (totalByte == byteDownloaded) ? destinationFileUrl : "Download may be interrupted";
        } catch (Exception e) {
            return "I see an exception";
        }


    }

    @Override
    public void onPostExecute(String string){
        if (string.equals(destinationFileUrl)){
            if (callback != null)
                callback.onSuccess(downloadUrl, new File(destinationFileUrl));
        }else{
            if (callback != null)
                callback.onFailed(downloadUrl, string);
        }

    }

    @Override
    public void onProgressUpdate(Integer... progress){
        if (callback != null)
            callback.onProgressUpdate(downloadUrl, totalByte, byteDownloaded);
    }

    public void start(){
        if (Build.VERSION.SDK_INT >= 11){
            executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            execute();
        }

    }

    public void cancelTask(){
        isCanceled = true;
        try{
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            super.cancel(true);
        }catch (Exception e){}

        if (callback != null) callback.onCancelTask(downloadUrl);
        new File(destinationFileUrl).delete();
    }
}
























