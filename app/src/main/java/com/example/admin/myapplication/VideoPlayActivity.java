package com.example.admin.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {

    private static final String TAG = VideoPlayActivity.class.getSimpleName();

    private VideoView videoplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoplay = (VideoView) findViewById(R.id.video_play);
        final String videopath = getIntent().getStringExtra("videopath");
        if (TextUtils.isEmpty(videopath)){
            videoplay.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath()+"/xxx2.mp4");
        }else {
            videoplay.setVideoPath(videopath);
        }
        MediaController mediaco = new MediaController(this);
        videoplay.setMediaController(mediaco);
        mediaco.setMediaPlayer(videoplay);
        mediaco.requestFocus();

        videoplay.start();

        //noinspection ConstantConditions
        findViewById(R.id.txt_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoplay.stopPlayback();
                finish();
            }
        });


        videoplay.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG,"setOnCompletionListener onCompletion() ===>");
                videoplay.start();
            }
        });

        videoplay.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG,"setOnPreparedListener onPrepared() ========>");
                mp.start();
                mp.setLooping(true);
            }
        });
    }

}
