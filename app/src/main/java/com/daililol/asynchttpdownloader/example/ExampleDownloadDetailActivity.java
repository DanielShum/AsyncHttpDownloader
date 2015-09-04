package com.daililol.asynchttpdownloader.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.daililol.asynchttpdownloader.library.AsyncHttpDownloader;
import com.daililol.asynchttpdownloader.library.AsyncHttpDownloaderListener;
import com.daililol.asynchttpdownloader.library.DownloadTask;

import java.io.File;

/**
 * Created by DennyShum on 9/4/15.
 */
public class ExampleDownloadDetailActivity extends Activity implements AsyncHttpDownloaderListener.Callback{


    public static String KEY_INPUT_DOWNLOAD_URL = "KEY_INPUT_DOWNLOAD_URL";
    public static void launch(Context context, String downloadUrl){
        Intent intent = new Intent(context, ExampleDownloadDetailActivity.class);
        intent.putExtra(KEY_INPUT_DOWNLOAD_URL, downloadUrl);
        context.startActivity(intent);
    }

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;

    private String downloadUrl;
    private AsyncHttpDownloaderListener downloaderListener;
    private DownloadTask downloadTask;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if (!getIntent().getExtras().containsKey(KEY_INPUT_DOWNLOAD_URL)){
            throw new IllegalArgumentException("No download url provided. Please use launch() to start this activity.");
        }

        downloadUrl = getIntent().getExtras().getString(KEY_INPUT_DOWNLOAD_URL);
        setContentView(R.layout.example_download_detail_activity);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);

        textView1.setText("Download Url: \n" +  downloadUrl);
        downloadTask = AsyncHttpDownloader.getInstance().getDownloadTask(downloadUrl);

        if (downloadTask == null){
            // If a download task can not be found, reasons may be:
            // Finished - once a task is finished, it will remove from the mission pool immediately.
            // Failed - once a task is failed, it will also eliminated.
            // Canceled - Canceled by user.
            // Never exists.
            textView2.setText("Download task not found. It may be finished, or may be failed.");
            return;
        }

        if (downloadTask.getTimeDownloadStarted() == 0){
            textView2.setText("Time download started: \nwaiting.");
        }else{
            textView2.setText("Time download started: \n" + downloadTask.getTimeDownloadStarted());
        }

        textView3.setText("Total bytes: \n" + downloadTask.getTotalByte());
        textView4.setText("Completed bytes: \n" + downloadTask.getCompleted());
        if (downloadTask.getTotalByte() > 0 && downloadTask.getCompleted() > 0){
            int percentage = (int)((float)downloadTask.getCompleted() / (float)downloadTask.getTotalByte() * 100.0f);
            textView5.setText("Progress: \n" + percentage + "%");
        }else{
            textView5.setText("Progress: \n0%");
        }


        downloaderListener = new AsyncHttpDownloaderListener(this);
        downloaderListener.register(this);


    }


    @Override
    public void onProgressUpdate(String downloadUrl, int total, int completed) {
        if (!this.downloadUrl.equals(downloadUrl)) return;

        textView2.setText("Time download started: \n" + downloadTask.getTimeDownloadStarted());
        textView4.setText("Completed bytes: \n" + completed);

        if (total > 0 && completed > 0){
            int percentage = (int)((float)completed / (float)total * 100.0f);
            textView5.setText("Progress: \n" + percentage + "%");
        }else{
            textView5.setText("Progress: \n0%");
        }
    }

    @Override
    public void onFailed(String downloadUrl, String errorMessage) {
        if (!this.downloadUrl.equals(downloadUrl)) return;
        textView5.setText("Progress: \nfailed");
    }

    @Override
    public void onSuccess(String downloadUrl, File file) {
        if (!this.downloadUrl.equals(downloadUrl)) return;
        textView5.setText("Progress: \ndone");
    }

    @Override
    public void onDestroy(){
        if (downloaderListener != null) downloaderListener.unregister(this);
        super.onDestroy();
    }


}














