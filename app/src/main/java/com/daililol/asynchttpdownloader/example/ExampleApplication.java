package com.daililol.asynchttpdownloader.example;

import android.app.Application;

import com.daililol.asynchttpdownloader.library.AsyncHttpDownloader;

/**
 * Created by DennyShum on 9/4/15.
 * I write to make sure everyone can read. For this reason I will never write a code comment.
 */
public class ExampleApplication extends Application{

    @Override
    public void onCreate(){

        // If you want to set default storage to save the destination files, you should do it so
        // once the application is started.
        // Or you can do it when you first time implement the downloader.
        AsyncHttpDownloader.getInstance().setDefaultDirectoryUnderSdCard("ExampleAsyncDownload/downloads");
    }
}
