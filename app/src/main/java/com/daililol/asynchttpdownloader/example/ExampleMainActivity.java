package com.daililol.asynchttpdownloader.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.daililol.asynchttpdownloader.library.AsyncHttpDownloader;
import com.daililol.asynchttpdownloader.library.AsyncHttpDownloaderListener;
import com.daililol.asynchttpdownloader.library.DownloadTask;

import java.io.File;

/**
 * Created by DennyShum on 9/4/15.
 */
public class ExampleMainActivity extends Activity implements
        AsyncHttpDownloaderListener.Callback,
        View.OnClickListener{

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;

    private Button button1;
    private Button button2;
    private Button button3;

    private AsyncHttpDownloaderListener downloaderListener;
    private final String downloadUrl1 = "http://download.virtualbox.org/virtualbox/4.2.32/VirtualBox-4.2.32-101581-OSX.dmg?downloadUrl1";
    private final String downloadUrl2 = "http://download.virtualbox.org/virtualbox/4.2.32/VirtualBox-4.2.32-101581-OSX.dmg?downloadUrl2";
    private final String downloadUrl3 = "http://download.virtualbox.org/virtualbox/4.2.32/VirtualBox-4.2.32-101581-OSX.dmg?downloadUrl3";


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_main_activity);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar3 = (ProgressBar) findViewById(R.id.progressBar3);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        downloaderListener = new AsyncHttpDownloaderListener(this);
        downloaderListener.register(this);

        AsyncHttpDownloader.getInstance().addDownloadTask(this, downloadUrl1);
        AsyncHttpDownloader.getInstance().addDownloadTask(this, downloadUrl2);
        AsyncHttpDownloader.getInstance().addDownloadTask(this, downloadUrl3);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button1:
                ExampleDownloadDetailActivity.launch(this, downloadUrl1);
                break;
            case R.id.button2:
                ExampleDownloadDetailActivity.launch(this, downloadUrl2);
                break;
            case R.id.button3:
                ExampleDownloadDetailActivity.launch(this, downloadUrl3);
                break;
        }
    }


    @Override
    public void onProgressUpdate(String downloadUrl, int total, int completed) {
        if (total == 0 || completed == 0) return;
        float percentage = (float)completed / (float)total;
        int progress = (int)(percentage * 100.0f);

        switch (downloadUrl){
            case downloadUrl1:
                progressBar1.setProgress(progress);
                break;
            case downloadUrl2:
                progressBar2.setProgress(progress);
                break;
            case downloadUrl3:
                progressBar3.setProgress(progress);
                break;
        }
    }

    @Override
    public void onFailed(String downloadUrl, String errorMessage) {

    }

    @Override
    public void onSuccess(String downloadUrl, File file) {
        switch (downloadUrl){
            case downloadUrl1:
                button1.setText("Done");
                break;
            case downloadUrl2:
                button2.setText("Done");
                break;
            case downloadUrl3:
                button3.setText("Done");
                break;
        }
    }

    @Override
    public void onDestroy(){
        downloaderListener.unregister(this);

        // When the activity is destroyed, cancel all running tasks if you want.
        // If you don't cancel, it will run in the background until all tasks are
        // done (or failed due to unstable internet connection), when you open the
        // activity again, you can still see the running task.
        AsyncHttpDownloader.getInstance().cancelAllRunningTasks();

        super.onDestroy();
    }

}
























