package com.xiaomabao.weidian.services;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.xiaomabao.weidian.R;
import com.xiaomabao.weidian.util.CapturePhotoUtils;
import com.xiaomabao.weidian.util.XmbPopubWindow;
import com.xiaomabao.weidian.views.ShopMenuActivity;
import com.yxp.permission.util.lib.PermissionUtil;
import com.yxp.permission.util.lib.callback.PermissionResultCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.blurry.Blurry;

/**
 * Created by de on 2016/9/9.
 */
public class UpdateService extends Service {

    private String app_name = null ;
    // 安装包下载地址
    private String url = null;
    // 新的安装包本地存储路径
    private String filePath = null;
    // 通知管理器
    private NotificationManager updateNotificationManager = null;
    // 通知
    private Notification updateNotification = null;
    private Notification.Builder builder = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        builder = new Notification.Builder(getApplicationContext());
        app_name = getApplication().getResources().getText(R.string.app_name).toString();
        // 获取传值
        url = intent.getStringExtra("download_url");
        if (url != null) {
            String fileName = url.substring(url.lastIndexOf("/"));
            File uDir = new File(Environment.getExternalStorageDirectory() + "/test/download/");
            if (!uDir.exists() || !uDir.isDirectory()) {
                uDir.mkdirs();
            }
            // 本地目录存储路径
            filePath = uDir + fileName;
            // 使用AsyncTask执行下载请求
            new DownloadAsyncTask().execute(url);
        }
        return super.onStartCommand(intent, 0, 0);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DownloadAsyncTask extends AsyncTask<String, Integer, Void> {

        //构造Notification
        @Override
        protected void onPreExecute() {
            updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            updateNotification = builder
                    .setTicker("正在更新" + app_name)
                    .setContentTitle(app_name)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            updateNotification.tickerText ="正在更新"+app_name;
            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.view_notification_download);
            updateNotification.contentView =contentView;
            contentView.setProgressBar(R.id.progress, 100, 0, false);
            contentView.setTextViewText(R.id.text_title, "已下载:0%");
//            contentView.setOnClickPendingIntent(R.id.update_notification_window,);
//            updateNotification.contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, pendingIntent, 0);
            updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
            updateNotificationManager.notify(101, updateNotification);
        }


        @Override
        protected Void doInBackground(String... params) {
            // 下载百分比
            int downPercentage = 0;
            // 上次缓存文件大小
            int cachedSize = 0;
            // 临时文件大小
            long tmpTotalSize = 0;
            // 待下载文件总大小
            int totalSize = 0;

            HttpURLConnection httpUrlConn = null;
            InputStream httpInputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                URL url = new URL(params[0]);
                httpUrlConn = (HttpURLConnection) url.openConnection();
                if (cachedSize > 0) {
                    // 方便以后实现断点续传
                    httpUrlConn.setRequestProperty("RANGE", "bytes=" + cachedSize + "-");
                }
//                httpUrlConn.setConnectTimeout(.CONNECT_TIMEOUT);
//                httpUrlConn.setReadTimeout(NetworkConfig.CONNECT_TIMEOUT);
                // 获取文件总大小
                totalSize = httpUrlConn.getContentLength();
                if (httpUrlConn.getResponseCode() == 200) {
                    httpInputStream = httpUrlConn.getInputStream();
                    fileOutputStream = new FileOutputStream(new File(filePath));
                    byte buffer[] = new byte[4096];
                    int bufferSize = 0;
                    while ((bufferSize = httpInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, bufferSize);
                        tmpTotalSize += bufferSize;
                        int tmpDownPercentage = (int) (tmpTotalSize * 100 / totalSize);
                        if (tmpDownPercentage - downPercentage > 5) {
                            downPercentage += 5;
                            publishProgress(tmpDownPercentage);
                        }
                    }
                    // 下载结束
                    publishProgress(100);
                }
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(-1);
            } finally {
                try {
                    if (httpUrlConn != null)
                        httpUrlConn.disconnect();
                    if (httpInputStream != null)
                        httpInputStream.close();
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress(-1);
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (values[0] != -1) {
                updateNotification.contentView.setTextViewText(R.id.text_title, values[0] >= 100 ? "已完成下载" : "已下载:" + values[0] + "%");
                updateNotification.contentView.setProgressBar(R.id.progress, 100, values[0] >= 100 ? 100 : values[0], false);
            } else {
                updateNotification.contentView.setTextViewText(R.id.text_title, "下载失败!");
            }
            updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
            updateNotificationManager.notify(101, updateNotification);
        }

        @Override
        protected void onPostExecute(Void result) {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);
            installIntent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            startActivity(installIntent);
            updateNotificationManager.cancelAll();
            stopSelf();
        }
    }
}

