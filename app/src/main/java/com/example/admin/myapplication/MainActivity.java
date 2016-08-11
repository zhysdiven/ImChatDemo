package com.example.admin.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fresco.initialize(getApplicationContext());
        initView();
    }

    @SuppressWarnings("ConstantConditions")
    private void initView() {
        findViewById(R.id.main_btn_chat).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_btn_chat:
                startActivity(new Intent(this,ChatActivity.class));
                break;
        }
    }
}
