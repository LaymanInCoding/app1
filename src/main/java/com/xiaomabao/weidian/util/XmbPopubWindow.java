package com.xiaomabao.weidian.util;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.xiaomabao.weidian.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaomabao.weidian.AppContext;
import com.xiaomabao.weidian.adapters.ShopChooseAdapter;
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.services.ShopService;
import com.xiaomabao.weidian.services.UpdateService;
import com.xiaomabao.weidian.defines.Const;
import com.xiaomabao.weidian.models.UpdateInfo;
import com.xiaomabao.weidian.views.ShopSettingActivity;
import com.yxp.permission.util.lib.PermissionUtil;
import com.yxp.permission.util.lib.callback.PermissionResultCallBack;

import org.hybridsquad.android.library.CropHelper;
import org.hybridsquad.android.library.CropParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.blurry.Blurry;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
//import static u.aly.au.R;

public class XmbPopubWindow {
    public static PopupWindow popupWindow;
    public static PopupWindow alertPopupWindow;

    public static void showTranparentLoading(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.loading_anim_transparent, null);
        if (XmbPopubWindow.popupWindow != null && XmbPopubWindow.popupWindow.isShowing()) {
            XmbPopubWindow.popupWindow.dismiss();
            XmbPopubWindow.popupWindow = null;
        }
        XmbPopubWindow.popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbPopubWindow.popupWindow.setTouchable(true);
        XmbPopubWindow.popupWindow.showAsDropDown(contentView);
    }

    public static void hideLoading() {
        if (XmbPopubWindow.popupWindow != null && XmbPopubWindow.popupWindow.isShowing()) {
            XmbPopubWindow.popupWindow.dismiss();
            XmbPopubWindow.popupWindow = null;
        }
    }

    public static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static void showShare(Activity activity, String share_img_path, String share_path, HashMap<String, String> hashMap) {
        Logger.e(hashMap.toString());
        View contentView = LayoutInflater.from(activity).inflate(R.layout.popwindow_share, null);
        PopupWindow shareWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        shareWindow.setTouchable(true);
        shareWindow.showAsDropDown(contentView);
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Const.WEIXIN_APP_ID);
        api.registerApp(Const.WEIXIN_APP_ID);
        contentView.findViewById(R.id.share_weixin_friend).setOnClickListener((view) -> {
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = share_path;
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            msg.title = hashMap.get("title");
            msg.description = hashMap.get("desc");
            Bitmap thumb;
            if (hashMap.containsKey("logo") && !hashMap.get("logo").trim().equals("")) {
                thumb = BitmapUtils.urlToBitmap(hashMap.get("logo"));
            } else {
                thumb = BitmapUtils.matrixImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_logo));
            }
            msg.thumbData = BitmapUtils.Bitmap2Bytes(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = XmbPopubWindow.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        });
        contentView.findViewById(R.id.share_weixin_circle).setOnClickListener((view) -> {
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = share_path;
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            msg.title = hashMap.get("desc");
            msg.description = hashMap.get("desc");
            Bitmap thumb;
            if (hashMap.containsKey("logo") && !hashMap.get("logo").trim().equals("")) {
                thumb = BitmapUtils.urlToBitmap(hashMap.get("logo"));
            } else {
                thumb = BitmapUtils.matrixImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_logo));
            }
            msg.thumbData = BitmapUtils.Bitmap2Bytes(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = XmbPopubWindow.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
        });

        contentView.findViewById(R.id.share_close).setOnClickListener((view) ->
                shareWindow.dismiss()
        );
        contentView.findViewById(R.id.share_copy_link).setOnClickListener((view) -> {
            ClipboardManager myClipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            myClipboard.setText(share_path);
            XmbPopubWindow.showAlert(activity, "复制成功~");
        });
        contentView.findViewById(R.id.share_qr_code).setOnClickListener((view) -> {
            Blurry.with(activity).radius(25).sampling(3).onto((ViewGroup) ((ViewGroup) activity
                    .findViewById(android.R.id.content)).getChildAt(0));
            shareWindow.dismiss();
            XmbPopubWindow.showQrcode(activity, share_img_path);
        });
    }

    public static void showShopChooseWindow(Activity activity, HashMap<String, String> hashMap, String ShareType, String type) {
        Logger.e(hashMap.toString());
        View contentView = LayoutInflater.from(activity).inflate(R.layout.popwindow_shop_choose, null);
        PopupWindow shopChooseWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        RecyclerView shopList = (RecyclerView) contentView.findViewById(R.id.shop_list_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ShopChooseAdapter adapter = new ShopChooseAdapter(AppContext.instance().getShopShareInfoArrayList(), activity);
        adapter.setOnItemChooseListener(new ShopChooseAdapter.OnItemChooseListener() {
            @Override
            public void onItemChoose(int position) {
                ShopBase.ShopBaseInfo.ShopShareInfo shopShareInfo = AppContext.instance().getShopShareInfoArrayList().get(position);
                String share_id = shopShareInfo.id;
                hashMap.put("logo", shopShareInfo.shop_avatar);
                shopChooseWindow.dismiss();
                if (ShareType.equals("Show_Share")) {
                    if (type.equals("1")) {
                        hashMap.put("title", shopShareInfo.shop_name);
                        hashMap.put("desc", shopShareInfo.shop_description);
                        XmbPopubWindow.showShare(activity, ShopService.SHARE_QRCODE_URL + "/" + AppContext.getToken(activity) + "/" + share_id + "/" + type, AppContext.getShopShareVipUrl(activity) + "?c=" + share_id, hashMap);
                    } else if (type.equals("0")) {
                        hashMap.put("title", shopShareInfo.shop_name);
                        hashMap.put("desc", shopShareInfo.shop_description);
                        XmbPopubWindow.showShare(activity, ShopService.SHARE_QRCODE_URL + "/" + AppContext.getToken(activity) + "/" + share_id + "/" + type, AppContext.getShopShareUrl(activity) + "?c=" + share_id, hashMap);
                    } else if (type.equals("2")) {
                        hashMap.put("title", shopShareInfo.shop_name + "给您送大礼了~");
                        XmbPopubWindow.showShare(activity, ShopService.SHARE_QRCODE_URL + "/" + AppContext.getToken(activity) + "/" + share_id + "/" + type, AppContext.getShopInviteUrl(activity), hashMap);
                    } else if (type.equals("3")) {
                        hashMap.put("title", shopShareInfo.shop_name);
                        hashMap.put("desc", shopShareInfo.shop_description);
                        XmbPopubWindow.showShare(activity, ShopService.SHARE_QRCODE_URL + "/" + AppContext.getToken(activity) + "/" + share_id + "/" + type, AppContext.getShopShareNormalUrl(activity) + "?c=" + share_id, hashMap);
                    }
                } else if (ShareType.equals("Show_Goods_Share")) {
                    XmbPopubWindow.showGoodsShare(activity, hashMap, share_id, type);
                }
            }
        });
        shopList.setLayoutManager(manager);
        shopList.setHasFixedSize(true);
        shopList.setAdapter(adapter);
        shopChooseWindow.setTouchable(true);
        shopChooseWindow.showAtLocation(contentView, Gravity.START, 0, 0);
        contentView.findViewById(R.id.choose_exit).setOnClickListener(v -> shopChooseWindow.dismiss());
    }


    public static void showUpdateWindow(Activity activity, UpdateInfo updateInfo) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_update, null);
        PopupWindow updateWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        updateWindow.setTouchable(true);
        updateWindow.showAsDropDown(contentView);
        ((TextView) contentView.findViewById(R.id.latest_version)).setText(updateInfo.latest_version);
        ((TextView) contentView.findViewById(R.id.new_version_size)).setText(updateInfo.size);
        ((TextView) contentView.findViewById(R.id.version_description)).setText(updateInfo.version_description);
//        contentView.findViewById(R.id.refuse_update).setOnClickListener(v -> {
//            updateWindow.dismiss();
//        });
        contentView.findViewById(R.id.update_right_now).setOnClickListener(v -> {
            PermissionUtil.getInstance().request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new PermissionResultCallBack() {
                        @Override
                        public void onPermissionGranted() {
                            Intent updateIntent = new Intent(activity, UpdateService.class);
                            updateIntent.putExtra("download_url", updateInfo.download_url);
                            activity.startService(updateIntent);
//                            updateWindow.dismiss();
                            Toast.makeText(activity, "正在下载更新", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionGranted(String... strings) {

                        }

                        @Override
                        public void onPermissionDenied(String... strings) {

                        }

                        @Override
                        public void onRationalShow(String... strings) {

                        }
                    });
        });

    }

    public static void showGoodsShare(Activity activity, HashMap<String, String> hashMap, String shareId, String type) {
        Logger.e(hashMap.toString());
        View contentView = LayoutInflater.from(activity).inflate(R.layout.popwindow_goods_share, null);
        PopupWindow shareWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        shareWindow.setTouchable(true);
        shareWindow.showAsDropDown(contentView);
        IWXAPI api = WXAPIFactory.createWXAPI(activity, Const.WEIXIN_APP_ID);
        api.registerApp(Const.WEIXIN_APP_ID);
        contentView.findViewById(R.id.share_weixin_friend).setOnClickListener((view) -> {
            WXWebpageObject webpageObject = new WXWebpageObject();
            webpageObject.webpageUrl = hashMap.get("url") + "?c=" + shareId;
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            msg.title = hashMap.get("title");
            msg.description = hashMap.get("desc");
            Bitmap thumb;
            if (hashMap.containsKey("logo") && !hashMap.get("logo").trim().equals("")) {
                thumb = BitmapUtils.urlToBitmap(hashMap.get("logo"));
            } else {
                thumb = BitmapUtils.matrixImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_logo));
            }
            msg.thumbData = BitmapUtils.Bitmap2Bytes(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = XmbPopubWindow.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;
            api.sendReq(req);
        });
        contentView.findViewById(R.id.share_weixin_circle).setOnClickListener((view) -> {
            WXWebpageObject webpageObject = new WXWebpageObject();
            if (type.equals("1")) {
                webpageObject.webpageUrl = hashMap.get("url") + "&c=" + shareId;
            } else {
                webpageObject.webpageUrl = hashMap.get("url") + "?&c=" + shareId;
            }
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
                    .build());
            WXMediaMessage msg = new WXMediaMessage(webpageObject);
            msg.title = hashMap.get("desc");
            msg.description = hashMap.get("desc");
            Bitmap thumb;
            if (hashMap.containsKey("logo") && !hashMap.get("logo").trim().equals("")) {
                thumb = BitmapUtils.urlToBitmap(hashMap.get("logo"));
            } else {
                thumb = BitmapUtils.matrixImage(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.share_logo));
            }
            msg.thumbData = BitmapUtils.Bitmap2Bytes(thumb, true);
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = XmbPopubWindow.buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
            api.sendReq(req);
        });
        contentView.findViewById(R.id.share_close).setOnClickListener((view) ->
                shareWindow.dismiss()
        );
        contentView.findViewById(R.id.share_copy_link).setOnClickListener((view) -> {
            ClipboardManager myClipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
            if (type.equals("1")) {
                myClipboard.setText(hashMap.get("url") + "&c=" + shareId);
            } else {
                myClipboard.setText(hashMap.get("url") + "?&c=" + shareId);
            }
            XmbPopubWindow.showAlert(activity, "复制成功~");
        });
    }

    public static Bitmap getHttpBitmap(String url) {
        Bitmap bitmap = null;
        try {
            URL pictureUrl = new URL(url);
            InputStream in = pictureUrl.openStream();
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static void showQrcode(Activity activity, String path) {
        Log.e("QR_URL", path);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.popwindow_qrcode, null);
        PopupWindow qrcodeWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        qrcodeWindow.setTouchable(true);
        qrcodeWindow.showAsDropDown(contentView);
        contentView.findViewById(R.id.close_qr).setOnClickListener((view) -> {
            Blurry.delete((ViewGroup) ((ViewGroup) activity
                    .findViewById(android.R.id.content)).getChildAt(0));
            qrcodeWindow.dismiss();
        });
        ImageView qrcodeImageView = (ImageView) contentView.findViewById(R.id.qrcode_img);
        Glide.with(activity)
                .load(path)
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(qrcodeImageView);
        contentView.findViewById(R.id.save_qr).setOnClickListener((view) -> {
            PermissionUtil.getInstance().request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new PermissionResultCallBack() {
                        @Override
                        public void onPermissionGranted() {
                            try {
                                String uri = CapturePhotoUtils.insertImage(activity.getContentResolver(), ((GlideBitmapDrawable) qrcodeImageView.getDrawable()).getBitmap(), AppContext.getShopName(activity) + "分享二维码", "");
                                String pic_path = getFilePathByContentResolver(activity, Uri.parse(uri));
                                Uri uri1 = Uri.fromFile(new File(pic_path));
                                Log.e("pic_path", uri1.toString());
                                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                intent.setData(uri1);
                                activity.sendBroadcast(intent);
                            } catch (Exception e) {
                                Log.e("Exception", e.getStackTrace().toString());
                                XmbPopubWindow.showAlert(activity, "请等待图片加载完成~");
                                return;
                            }
                            qrcodeWindow.dismiss();
                            Blurry.delete((ViewGroup) ((ViewGroup) activity
                                    .findViewById(android.R.id.content)).getChildAt(0));
                            XmbPopubWindow.showAlert(activity, "保存成功~");
                        }

                        @Override
                        public void onPermissionGranted(String... strings) {

                        }

                        @Override
                        public void onPermissionDenied(String... strings) {

                        }

                        @Override
                        public void onRationalShow(String... strings) {

                        }
                    });
        });
    }

    //根据URI获取图片的绝对路径
    private static String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }


    public static void showAlert(Context context, String msg) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.alert_gray, null);
        if (XmbPopubWindow.alertPopupWindow != null && XmbPopubWindow.alertPopupWindow.isShowing()) {
            XmbPopubWindow.alertPopupWindow.dismiss();
            XmbPopubWindow.alertPopupWindow = null;
        }
        TextView alertMsgTextView = (TextView) contentView.findViewById(R.id.alert_text);
        alertMsgTextView.setText(msg);
        XmbPopubWindow.alertPopupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        XmbPopubWindow.alertPopupWindow.setTouchable(true);
        XmbPopubWindow.alertPopupWindow.showAsDropDown(contentView);
        Observable<Long> observable = Observable.timer(1, TimeUnit.SECONDS, Schedulers.io());
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((Long) -> {
                    XmbPopubWindow.alertPopupWindow.dismiss();
                });
    }

    public static void showAlertDialog(Context context, String msg) {

    }

    public static void showPickPhotoWindow(ShopSettingActivity mView) {
        View contentView = LayoutInflater.from(mView).inflate(R.layout.popwindow_pick_photo, null);
        View pick1View = contentView.findViewById(R.id.pick1_container);
        View pick2View = contentView.findViewById(R.id.pick2_container);
        View cancelView = contentView.findViewById(R.id.cancel_container);
        PopupWindow popupWindow = new PopupWindow(contentView, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(contentView);
        cancelView.setOnClickListener((View view) ->
                popupWindow.dismiss()
        );
        pick1View.setOnClickListener((View view) ->
                PermissionUtil.getInstance().request(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        new PermissionResultCallBack() {
                            @Override
                            public void onPermissionGranted() {
                                popupWindow.dismiss();
                                CropParams mCropParams = new CropParams();
                                Intent intent = CropHelper.buildCropFromGalleryIntent(mCropParams);
                                CropHelper.clearCachedCropFile(mCropParams.uri);
                                mView.startActivityForResult(intent, CropHelper.REQUEST_CROP);
                            }

                            @Override
                            public void onPermissionGranted(String... strings) {

                            }

                            @Override
                            public void onPermissionDenied(String... strings) {
                                XmbPopubWindow.showAlert(mView, "请打开设置,允许相机授权应用权限");
                            }

                            @Override
                            public void onRationalShow(String... strings) {

                            }
                        })

        );

        pick2View.setOnClickListener((View view) ->
                PermissionUtil.getInstance().request(new String[]{Manifest.permission.CAMERA},
                        new PermissionResultCallBack() {
                            @Override
                            public void onPermissionGranted() {
                                popupWindow.dismiss();
                                CropParams mCropParams = new CropParams();
                                CropHelper.clearCachedCropFile(mCropParams.uri);
                                Intent intent = CropHelper.buildCaptureIntent(mCropParams.uri);
                                mView.startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
                            }

                            @Override
                            public void onPermissionGranted(String... strings) {

                            }

                            @Override
                            public void onPermissionDenied(String... strings) {
                                XmbPopubWindow.showAlert(mView, "请打开设置,允许相机授权应用权限");
                            }

                            @Override
                            public void onRationalShow(String... strings) {

                            }
                        })
        );
    }

}
