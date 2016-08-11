package com.example.admin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.myapplication.utils.BitmapUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class SmallVideoActivity extends AppCompatActivity implements View.OnClickListener,SurfaceHolder.Callback {

    private static final String TAG = SmallVideoActivity.class.getSimpleName();

    private SurfaceView surfaceView;
    private Button btnReset,btnSend,btnPlay;
    private ProgressBar  progressBar;
    private SimpleDraweeView draweeView;

    private Camera camera;
    private Vibrator vibrator;
    private MediaRecorder recorder;

    private boolean isStartRecord;
    private Timer timer;
    private int count=0;
    private boolean isRecordEnded;
    private String previewPath;
    private String videoPath;
    private boolean isTypeSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_video);

        initView();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause()====================>");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()====================>");
        if (camera != null){
            camera.release();
            camera = null;
        }
    }

    private void initView() {
        btnPlay = (Button) findViewById(R.id.smallvideo_btn_play);
        btnSend = (Button) findViewById(R.id.smallvideo_btn_send);
        btnReset = (Button) findViewById(R.id.smallvideo_btn_reset);

        progressBar = (ProgressBar) findViewById(R.id.smallvideo_pbar_time);
        surfaceView = (SurfaceView) findViewById(R.id.smallvideo_sfview_camera);
        draweeView = (SimpleDraweeView) findViewById(R.id.smallvideo_sd_prewdrawable);

        final SurfaceHolder surfaceHolder  = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        findViewById(R.id.smallvideo_txt_end).setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        btnSend.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.i(TAG,"onLongClick 开始录制--->");
                if (!isRecordEnded) {
                    vibrator.vibrate(100);
                    initMediaRecord();
                    isStartRecord = true;
                    isTypeSend = false;
                    handler.sendEmptyMessage(10);
                }
                return true;
            }
        });
        btnSend.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
//                        isRecordEnded = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.removeMessages(10);
                        if (isTypeSend){
                           sendVideo();
                        }else {
                            if (!isRecordEnded){
                                if (count <= 20){
                                    if (recorder != null){
                                        try {
                                            recorder.stop();
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Toast.makeText(getApplicationContext(),"录制时间过短,请重新录制!",Toast.LENGTH_SHORT).show();
                                    count = 0;
                                    progressBar.setProgress(0);
                                }else { //录制成功
                                    stopRecord();
                                    recordSuccess();
                                }
                                stopTimer();
                            }
                            isRecordEnded = true;//标志录制完成
                        }
                        break;
                }
                return false;
            }
        });
        progressBar.setMax(300);
    }


    private void stopRecord() {
        if (recorder != null){
            try {
                recorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        if (camera != null){
            camera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Camera.Parameters ps = camera.getParameters();
                    if(ps.getPictureFormat() == PixelFormat.JPEG) {
                        //拍照后会自动停止预览,

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        options.inSampleSize = BitmapUtils.calculateInSampleSize(options, 320, 480);
                        options.inJustDecodeBounds = false;
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        Matrix m = new Matrix();
                        m.setRotate(90,(float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                        Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                        String path = Environment.getExternalStorageDirectory()+File.separator+"smallvideo_"+System.currentTimeMillis()+".jpg";
                        try {
                            File file = new File(path);
                            if (!file.exists()) file.createNewFile();
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            bm.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
                            fileOutputStream.close();
                            previewPath = path;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        draweeView.setBackgroundDrawable(new BitmapDrawable(bm));
                        draweeView.setVisibility(View.VISIBLE);
                        surfaceView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void recordSuccess() {
        vibrator.vibrate(100);
        btnPlay.setVisibility(View.VISIBLE);
        btnReset.setVisibility(View.VISIBLE);
        btnSend.setBackgroundResource(R.mipmap.video_send);
        isTypeSend = true;
    }


    private void stopTimer(){
        if (timer !=null){
            timer.cancel();
            timer = null;
        }
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                count +=2;
                progressBar.setProgress(count);
                if (count <= 300) {
                    sendEmptyMessageDelayed(10, 100); //每0.1s发送一次
                }else {
                    count = 0;
                    recordSuccess();
                    stopRecord();
                    isRecordEnded = true;
                    stopTimer();
                }
            }
        }
    };




    public void initMediaRecord(){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }

        recorder = new MediaRecorder();
        recorder.reset();

        camera = Camera.open();
        // 设置摄像头预览顺时针旋转90度，才能使预览图像显示为正确的，而不是逆时针旋转90度的。
        camera.setDisplayOrientation(90);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Log.d(TAG,"autoFocus start===>");
                                if (success && !isRecordEnded) {
                                    Log.d(TAG,"autoFocus success===>");
                                    camera.setOneShotPreviewCallback(null);
                                }
                                Log.d(TAG,"autoFocus end===>");
                            }
                        });
                    }
                });
            }
        },0,3000);
        camera.unlock();

        recorder.setCamera(camera);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
        recorder.setOrientationHint(90 );
        recorder.setVideoSize(640, 480);// 设置分辨率：
        recorder.setVideoEncodingBitRate(5*1024*1024);// 设置帧频率，然后就清晰了
        videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"smallvideo_"+System.currentTimeMillis()+ ".mp4";
        recorder.setOutputFile(/*Environment.getExternalStorageDirectory().getAbsolutePath()+"/xxx2.mp4"*/videoPath);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            recorder.release();
            recorder = null;
            if (camera !=null) {
                camera.release();
                camera = null;
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.smallvideo_btn_play:
                Intent intent = new Intent(this,VideoPlayActivity.class);
                intent.putExtra("videopath",videoPath);
                startActivity(intent);
                break;
            case R.id.smallvideo_btn_reset:
                btnReset.setVisibility(View.GONE);
                btnPlay.setVisibility(View.GONE);
                btnSend.setBackgroundResource(R.mipmap.video_record);
                progressBar.setProgress(0);
                count = 0;
                surfaceView.setVisibility(View.VISIBLE);
                draweeView.setVisibility(View.GONE);
                isStartRecord = false;
                if (camera != null){
                    camera.startPreview();
                }
                break;
            case R.id.smallvideo_txt_end:
                finish();
                break;
        }
    }

    private void sendVideo() {
        if (TextUtils.isEmpty(previewPath)) return;
        Intent intent = new Intent();
        intent.putExtra("imgpath",previewPath);
        intent.putExtra("videopath",videoPath);
        setResult(0x22,intent);
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG,"surfaceCreated()====================>");
        try {
            if (camera == null)
                camera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
       if (camera != null){
          try {
              camera.setPreviewDisplay(holder);
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG,"surfaceChanged()====================>");
        camera.setDisplayOrientation(90);
        camera.startPreview();
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG,"surfaceDestroyed()====================>");
        if (camera != null){
            try {
                camera.setPreviewDisplay(null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.release();
            camera = null;
        }
    }


}
