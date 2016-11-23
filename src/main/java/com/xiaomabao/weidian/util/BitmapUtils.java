package com.xiaomabao.weidian.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class BitmapUtils {

    public static Bitmap urlToBitmap(String url) {
        URL fileUrl = null;
        Bitmap  resizeBmp = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            Matrix matrix = new Matrix();
            matrix.postScale(100f / bitmap.getWidth(),100f / bitmap.getWidth()); //长和宽放大缩小的比例
            resizeBmp = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizeBmp;

    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            //这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        //把压缩后的数据baos存放到ByteArrayInputStream中
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    public static Bitmap matrixImage(Bitmap image) {
        Matrix matrix = new Matrix();
        matrix.postScale(100f / image.getWidth(),100f / image.getWidth()); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(image,0,0,image.getWidth(),image.getHeight(),matrix,true);
        return  resizeBmp;
    }

    public static Bitmap getCompressedImage(String srcPath,int max) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = max;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap the_values_bitmap(String uil)
    {
        Bitmap bmp = null;
        try {
            URL imgURL = new URL(uil);
            URLConnection conn = imgURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();

            BufferedInputStream bis = new BufferedInputStream(is);

            //下载之
           bmp = BitmapFactory.decodeStream(bis);

            //关闭Stream
            bis.close();

            is.close();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bmp;
    }
    //把bitmap转化为图片并保存
    public static String saveMyBitmap(Bitmap mBitmap, String bitName,Context context) throws IOException {
        File f = new File(context.getExternalCacheDir()+bitName + ".jpg");
        //判断当前文件是否存在-----
        if (f.exists())
        {
            f.delete();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f.getPath()));
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.close();
        return f.getPath();
    }

    public static Bitmap decodeUriAsBitmap(Context context,Uri uri){
        Bitmap bitmap;
        try{
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        }catch (FileNotFoundException e){
            return null;
        }
        return bitmap;
    }

    public static byte[] Bitmap2Bytes(final Bitmap bmp,final boolean needRecycle) {
        int i = 100;
        int j = 100;

        Bitmap localBitmap = Bitmap.createBitmap(i, j, Bitmap.Config.RGB_565);
        Canvas localCanvas = new Canvas(localBitmap);

        while (true) {
            localCanvas.drawBitmap(bmp, new Rect(0, 0, i, j), new Rect(0, 0,i, j), null);
            if (needRecycle)
                bmp.recycle();
            ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
            localBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                    localByteArrayOutputStream);
            localBitmap.recycle();
            byte[] arrayOfByte = localByteArrayOutputStream.toByteArray();
            try {
                localByteArrayOutputStream.close();
                return arrayOfByte;
            } catch (Exception e) {
                //F.out(e);
            }
            i = bmp.getHeight();
            j = bmp.getHeight();
        }
     }

}
