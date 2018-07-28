package com.shalinijain.news_gateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Admin on 23-04-2018.
 */

public class NewsService extends Service {

    private static final String TAG = "NewsService";

    private ServiceReceiver serviceReceiver;

    private boolean isRunning = true;
    private final String ACTION_MSG_TO_SVC = "ACTION_MSG_TO_SVC";
    private final String ACTION_NEWS_STORY = "ACTION_NEWS_STORY";
    private ArrayList<DataofArticle> storyList = new ArrayList<>();
    private ArrayList<DataofArticle> article_data_list = new ArrayList<>();
    private NewsArticleDownloader nad;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceReceiver = new ServiceReceiver();

        //Fill the intentfilter with the service
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SVC);

        //Create a service and register it to broadcast
        registerReceiver(serviceReceiver, filter);

        //Create a thread for NewsService
        new Thread(new Runnable() {
            @Override
            public void run() {

                //Here long running tasks are performed like playing music or getting data from internet
                while (isRunning) {

                    //--Perform the internet download task here


                    if (storyList.isEmpty()) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_NEWS_STORY);
                        Log.d(TAG, "run: ");
                        intent.putExtra("storylist", storyList);
                        sendBroadcast(intent);
                        storyList.clear();
                    }

                }
            }

        }).start();

        //START_STICKY: if something wrong then the program needs to start the service again
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: New Service");

        //Unregister the service receiver object and unregister the service
        unregisterReceiver(serviceReceiver);

        isRunning = false;
        super.onDestroy();
    }

    public void setArticles(ArrayList<DataofArticle> articlesData_list) {
        if (articlesData_list == null) {
            article_data_list.clear();
            storyList.addAll(articlesData_list);
            article_data_list.addAll(articlesData_list);
        }
    }


    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

                case ACTION_MSG_TO_SVC:

                    String source_id = "";
                    DataofSource obj;
                    if (intent.hasExtra("DATA_OBJ")) {
                        obj = (DataofSource) intent.getSerializableExtra("DATA_OBJ");
                        source_id = obj.getSource_id();
                        nad = new NewsArticleDownloader(NewsService.this, source_id);
                        nad.execute();
                    } else {
                        Log.d(TAG, "onReceive: ");
                    }
                    break;
            }
        }
    }
}
