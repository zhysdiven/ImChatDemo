package com.example.admin.myapplication.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created by Admin on 2016/8/8.
 */
public class BitmapUtils {


    public static Bitmap getRoundCornerImage(Bitmap bitmap_bg, Bitmap bitmap_in)
    {
        Bitmap roundConcerImage = Bitmap.createBitmap(190,260, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,190,260);
        Rect rectF = new Rect(0, 0, bitmap_in.getWidth(), bitmap_in.getHeight());
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap_in, rectF, rect, paint);

        bitmap_bg.recycle();bitmap_bg =null;
        bitmap_in.recycle();bitmap_in =null;
        return roundConcerImage;
    }
    public static Bitmap getShardImage(Bitmap bitmap_bg,Bitmap bitmap_in)
    {
        Bitmap roundConcerImage = Bitmap.createBitmap(200,230, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0,0,200,230);
        paint.setAntiAlias(true);
        NinePatch patch = new NinePatch(bitmap_bg, bitmap_bg.getNinePatchChunk(), null);
        patch.draw(canvas, rect);
//        Rect rect2 = new Rect(2,2,498,498);
        Rect rect2 =  new Rect(0,0,200,230);
        canvas.drawBitmap(bitmap_in, rect, rect2, paint);

        bitmap_bg = null;
        bitmap_in = null;

        return roundConcerImage;
    }


    public static Bitmap decodeSampledBitmapFromResource(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(  BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }
        return inSampleSize;
    }

}
