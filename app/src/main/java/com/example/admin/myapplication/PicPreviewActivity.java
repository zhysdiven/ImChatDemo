package com.example.admin.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.admin.myapplication.utils.BitmapUtils;

public class PicPreviewActivity extends AppCompatActivity {

    private static final String TAG = PicPreviewActivity.class.getSimpleName();
    private ImageView ivPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_preview);

        initview();

        initdata();
    }

    private void initdata() {
        String type = getIntent().getStringExtra("type");
        if (!TextUtils.isEmpty(type) && "type_look".equals(type)){
            findViewById(R.id.btn_send).setVisibility(View.GONE);
        }
        Uri uri = getIntent().getParcelableExtra("img_uri");
        Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromResource(uri.getPath(), 720, 1080);
        if (bitmap == null) bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_default_pic);
        ivPic.setImageBitmap(bitmap);
    }

    private void initview() {
        ivPic = (ImageView) findViewById(R.id.iv_picpreview);
        //noinspection ConstantConditions
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(0x130,intent);
                finish();
            }
        });
        //noinspection ConstantConditions
        findViewById(R.id.root_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: clear bitmap");
        ivPic.setImageBitmap(null);
        ivPic = null;
    }
}
