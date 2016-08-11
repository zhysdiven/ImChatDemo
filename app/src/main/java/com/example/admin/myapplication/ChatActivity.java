package com.example.admin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.adapter.ChatMoreAdapter;
import com.example.admin.myapplication.adapter.ChatMsgAdapter;
import com.example.admin.myapplication.adapter.FaceAdapter;
import com.example.admin.myapplication.bean.ChatItemBean;
import com.example.admin.myapplication.bean.ChatMsgBean;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0x110;
    private static final int SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE = 0x11;
    private static final int SMALLVIDEO_ACTIVITY_REQUEST_CODE = 0x21;

    private ImageView btnBack, btnSetting, btnMore, btnImg, btnChanges;
    private Button /*btnMore,*/btnVoice/*,btnImg*/;
    private TextView txtTitle;
    private RecyclerView recyChatMsg, recyInputMore, recyInputFace;
    private EditText edtInput;
    private boolean isTypeWord, isMoreTool, isShowFace;
    private List<ChatMsgBean> datas;
    private ChatMsgAdapter msgAdapter;
    private ChatMoreAdapter chatMoreAdapter;
    private InputMethodManager im;
    private FaceAdapter faceAdapter;
    private TextView txtSend;
    private String imgPath;
    private Timer audioTimer;
    private AlertDialog audioDialog;
    private MediaRecorder mediaRecorder;
    private String audioPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);

        initview();
        initListener();
        initData();
    }

    private void initData() {
        datas.addAll(loadData());
        msgAdapter.notifyDataSetChanged();
    }

    public List<ChatMsgBean> loadData() {
        List<ChatMsgBean> lists = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            ChatMsgBean bean = new ChatMsgBean();
            bean.setId("100000" + i);

            Random random = new Random();
            bean.setType(random.nextInt(2));
            bean.setSend(random.nextBoolean() ? "15818694245" : "10000000000000");
            if (bean.getType() == 2) {
                bean.setMsg("");
            } else {
                if (bean.getType() == 3) bean.setType(0);
                bean.setMsg("消息：xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" + random.nextInt(1000000));
            }
            bean.setTime(random.nextBoolean() ? "14:" + random.nextInt(60) : "");
            if (bean.getSend().equals("15818694245")) {
                bean.setState(random.nextInt(3) == 1 ? "error" : "success");
            }
            lists.add(bean);
        }
        return lists;
    }

    private void initListener() {
        btnBack.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnChanges.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnVoice.setOnClickListener(this);
        btnImg.setOnClickListener(this);
        txtSend.setOnClickListener(this);
        edtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(edtInput.getText().toString())) {
                    sendWordMsg();
                }
                return true;
            }
        });
        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isMoreTool) closeMore();
                    if (isShowFace) closeFace();
                }
            }
        });


        initVoice();
    }

    private boolean isStartAudioRd;

    private void initVoice() {
        btnVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "开始录音---------------->");
                btnVoice.setText("松开发送");
                isStartAudioRd = true;
                showAudioDialog();
                return false;
            }
        });
        btnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "取消录音---------------->");
                        audioRecordEndEvent();
                        break;
                }
                return false;
            }
        });
    }

    private void audioRecordEndEvent() {
        stopRecord();
        isStartAudioRd = false;
        btnVoice.setText("按下录音");
        closeAudioDialog();
        if (audioCount <= 1) {
            Toast.makeText(ChatActivity.this, "请再多说一点吧", Toast.LENGTH_SHORT).show();
        } else {
            sendAudioMsg();
            audioPath = "";
        }
    }


    private int count;

    private void initview() {
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSetting = (ImageView) findViewById(R.id.btn_setting);
        btnChanges = (ImageView) findViewById(R.id.chat_btn_changes);
        btnMore = (ImageView) findViewById(R.id.chat_btn_more);
        btnVoice = (Button) findViewById(R.id.chat_btn_voice);
        btnImg = (ImageView) findViewById(R.id.chat_btn_image);
        edtInput = (EditText) findViewById(R.id.chat_edt_input);
        txtSend = (TextView) findViewById(R.id.chat_txt_send);

        edtInput.setFocusable(true);
        edtInput.requestFocus();

        initRecyMsg();

        initChatMore();

        iniRecyFace();

        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(edtInput.getText().toString())) {
                    txtSend.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.GONE);
                } else {
                    txtSend.setVisibility(View.GONE);
                    btnMore.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void initChatMore() {
        recyInputMore = (RecyclerView) findViewById(R.id.chat_recyclerview__more);
        recyInputMore.setLayoutManager(new GridLayoutManager(this, 4));
        chatMoreAdapter = new ChatMoreAdapter(getChatTools());
        chatMoreAdapter.setOnItemClickListener(new ChatMoreAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "图片", Toast.LENGTH_SHORT).show();
                    Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(picture, SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (position == 1) {
                    Log.d(TAG, "Take Picture Button Click");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imgPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "img_" + System.currentTimeMillis() + ".jpg";
                    File file = new File(imgPath);
                    if (!file.exists()) try {    //file必须存在，否则Result中无法接收data数据
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (position == 2) {
                    startActivityForResult(new Intent(ChatActivity.this, SmallVideoActivity.class), SMALLVIDEO_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        recyInputMore.setAdapter(chatMoreAdapter);
    }

    private void iniRecyFace() {
        recyInputFace = (RecyclerView) findViewById(R.id.chat_recyclerview__face);
        recyInputFace.setLayoutManager(new GridLayoutManager(this, 7));
        faceAdapter = new FaceAdapter(Config.initFace());
        recyInputFace.setAdapter(faceAdapter);
        faceAdapter.setOnItemClickListener(new FaceAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == faceAdapter.getItemCount() - 1) {
                    int selectionEnd = edtInput.getSelectionEnd();
                    int selectionStart = edtInput.getSelectionStart();
                    if (!TextUtils.isEmpty(edtInput.getText().toString())) {  //f_static_004f_static_001
                        String s = edtInput.getText().toString();
                        int index = s.lastIndexOf("f_static_0");
                        if (index != -1) {
                            if (index + 12 == selectionStart) {
                                edtInput.getText().delete(index, selectionEnd);
                            } else {
                                edtInput.getText().delete(selectionEnd - 1, selectionEnd);
                            }
                        } else {
                            edtInput.getText().delete(selectionEnd - 1, selectionEnd);
                        }
                    }
                } else {
                    int icon = faceAdapter.getItem(position).getIcon();
                    String content = faceAdapter.getItem(position).getContent();
                    String s = edtInput.getText().toString() + content;

                    SpannableString spannableString = new SpannableString(content);
                    spannableString.setSpan(new ImageSpan(ChatActivity.this, icon), 0, content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    edtInput.append(spannableString);
                    edtInput.setSelection(s.length());
                }
            }
        });
    }

    private void initRecyMsg() {
        datas = new ArrayList<>();
        recyChatMsg = (RecyclerView) findViewById(R.id.chat_recyclerview_msg);
        msgAdapter = new ChatMsgAdapter(this, datas, "15818694245");
        recyChatMsg.setLayoutManager(new LinearLayoutManager(this));
        recyChatMsg.setItemAnimator(new DefaultItemAnimator());
        recyChatMsg.setAdapter(msgAdapter);
//        recyChatMsg.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int y,y1;
//                switch (event.getAction()){
//                    case MotionEvent.ACTION_DOWN:
//                        y = (int) v.getY();
//                        Log.d(TAG,"down y:"+y);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        y1 = (int) v.getY();
//                        Log.d(TAG,"move y:"+y1);
//                        break;
//                }
//                return false;
//            }
//        });
        recyChatMsg.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isScrolled = Math.abs(dy) > 5;
                if (isScrolled) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (dy > 0) {
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItemPosition <= lastpositonIdle) return;

//                        if(lastpositons != lastVisibleItemPosition) {
                        ChatMsgBean item = msgAdapter.getItem(lastVisibleItemPosition + 1);
                        if (item == null) return;
                        if (item.getType() == 3) {
//                                View childAt = recyclerView.getChildAt(lastVisibleItemPosition+1);
//                                if (childAt != null) {
//                                    VideoView videoView = (VideoView) childAt.findViewById(R.id.video_play);
//                                    if (!videoView.isPlaying()) {
//                                        Log.d(TAG, "videoView start");
//                                        videoView.start();
////                                        firstpostions = firstVisibleItemPosition;
//                                        firstpositonIdle = lastVisibleItemPosition+1;
//                                    }
//                                }

//                                lastpositons = lastVisibleItemPosition;
//                                msgAdapter.notifyItemChanged(lastVisibleItemPosition);
//                                lastpositonIdle = lastVisibleItemPosition+1;
                            Log.d(TAG, "notifyItemChanged down position:" + lastVisibleItemPosition + ",com position:" + lastCompletelyVisibleItemPosition + "," + item.getTime());
                        }
//                        }
                    } else {
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        if (firstVisibleItemPosition >= firstpositonIdle) return;
//                        if (firstpostions != firstVisibleItemPosition) {
                        ChatMsgBean item = msgAdapter.getItem(firstVisibleItemPosition - 1);
                        if (item == null) return;
                        if (item.getType() == 3) {
//                                View childAt = recyclerView.getChildAt(firstVisibleItemPosition-1);
//                                if (childAt != null) {
//                                    VideoView videoView = (VideoView) childAt.findViewById(R.id.video_play);
//                                    if (!videoView.isPlaying()) {
//                                        Log.d(TAG, "videoView start");
//                                        videoView.start();
////                                        firstpostions = firstVisibleItemPosition;
//                                        firstpositonIdle = firstVisibleItemPosition-1;
//                                    }
//                                }
//                                firstpostions = firstVisibleItemPosition;
//                                msgAdapter.notifyItemChanged(firstVisibleItemPosition);
                            Log.d(TAG, "notifyItemChanged up position:" + firstVisibleItemPosition + ", com position:" + firstCompletelyVisibleItemPosition + "," + item.getTime());
                        }
//                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i("aaa", "11111111111111111111111");
                    im.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (isMoreTool) {
                        closeMore();
                    }
                    if (isShowFace) closeFace();
                } else {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    firstpositonIdle = layoutManager.findFirstVisibleItemPosition();
                    lastpositonIdle = layoutManager.findLastVisibleItemPosition();
//                    TODO
//                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                    Log.i(TAG,"first:"+firstVisibleItemPosition+",last:"+lastVisibleItemPosition);

//                    msgAdapter.notifyDataSetChanged();
//                    msgAdapter.notifyItemChanged(firstVisibleItemPosition);
//                    msgAdapter.notifyItemChanged(lastVisibleItemPosition);

//                    if (layoutManager.findFirstVisibleItemPosition() == 0){
//                        if (count >=1){
//                            List<ChatMsgBean> chatMsgBeen = loadData();
//                            int size = msgAdapter.getItemCount();
//                            Log.d("size","size:"+size);
//                            msgAdapter.addDatas(0,chatMsgBeen);
//                            recyclerView.smoothScrollToPosition(chatMsgBeen.size());
//                            count=0;
//                        }else {
//                            count++;
//                        }
//                    }
                }
            }
        });
    }

    private int firstpositonIdle, lastpositonIdle; //静止时的第一可见项
    private int firstpostions, lastpositons;
    private boolean isFastScrolling;

    public List<ChatItemBean> getChatTools() {
        List<ChatItemBean> list = new ArrayList<>();
        list.add(new ChatItemBean(R.mipmap.im_pic_up, "图片视频", "pic_video"));
        list.add(new ChatItemBean(R.mipmap.im_camera_up, "拍摄", "camera"));
        list.add(new ChatItemBean(R.mipmap.im_video_up, "小视屏", "small_video"));
        return list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_setting:
                break;
            case R.id.chat_btn_changes:
                if (isTypeWord) { //文字发送
                    setTypeWord();
                } else { //语音发送
                    setTypeVoice();
                    if (isShowFace) closeFace();
                }
                if (isMoreTool) {
                    closeMore();
                }
                break;
            case R.id.chat_btn_more:
                if (isMoreTool) {
                    closeMore();
                } else {
                    recyInputMore.setVisibility(View.VISIBLE);
                    isMoreTool = true;
                }
                if (isShowFace) closeFace();
                break;
            case R.id.chat_btn_image:
                if (isTypeWord) {
                    setTypeWord();
                }
                if (isShowFace) {
                    closeFace();
                } else {
                    recyInputFace.setVisibility(View.VISIBLE);
                    isShowFace = true;
                }
                im.hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (isMoreTool) closeMore();
                break;
            case R.id.chat_txt_send:
                sendWordMsg();
                break;
        }
    }

    private void setTypeWord() {
        edtInput.setVisibility(View.VISIBLE);
        edtInput.setFocusable(true);
        edtInput.requestFocus();
        btnVoice.setVisibility(View.GONE);
        btnChanges.setImageResource(R.mipmap.im_keyboard_up);
        isTypeWord = false;
    }

    private void setTypeVoice() {
        edtInput.setVisibility(View.GONE);
        btnVoice.setVisibility(View.VISIBLE);
        btnVoice.setText("按下录音");
        btnChanges.setImageResource(R.mipmap.im_audio_up);
        isTypeWord = true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {        //&& resultCode == RESULT_OK
//            Intent intent = new Intent("com.android.camera.action.CROP");
//            intent.setDataAndType(Uri.fromFile(new File(imgPath)), "image/*");
//            // 是否可裁剪
//            intent.putExtra("crop", "true");
//
//            // aspectX aspectY 是宽高的比例
//            intent.putExtra("aspectX", 1);
//            intent.putExtra("aspectY", 1);
//
//            // outputX,outputY 是剪裁图片的宽高
//            intent.putExtra("outputX", 300);
//            intent.putExtra("outputY", 300);
//            intent.putExtra("return-data", true);
//            intent.putExtra("noFaceDetection", true);
//            System.out.println("22================");
//            startActivityForResult(intent, 1111);
            try {
                Intent intent = new Intent(this, PicPreviewActivity.class);
                intent.putExtra("img_uri", Uri.fromFile(new File(imgPath)));
                startActivityForResult(intent, 0x12);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {  //系统拍照
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            sendPicMsg(picturePath);
        } else if (requestCode == 0x12 && resultCode == 0x130) {
            if (data != null) {
                sendPicMsg(Uri.fromFile(new File(imgPath)).getPath());
            }
        } else if (requestCode == SMALLVIDEO_ACTIVITY_REQUEST_CODE && resultCode == 0x22) {
            if (data != null) {
                String imgpath = data.getStringExtra("imgpath");
                String videopath = data.getStringExtra("videopath");
                sendVideoMsg(imgpath, videopath);
            }
        }
    }


    private void sendVideoMsg(String imgpath, String videopath) {
        Log.i("input", "视频发送--------->");
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        chatMsgBean.setType(3);
        chatMsgBean.setVideoPath(videopath);
        chatMsgBean.setMsg(imgpath);
        Log.i("input", "视频发送--------->video:" + videopath + ",picture:" + imgpath);
        sendMsg(chatMsgBean);
    }

    private void sendPicMsg(String picPath) {
        Log.i("input", "图片发送--------->");
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        chatMsgBean.setType(2);
        chatMsgBean.setMsg(picPath);
        sendMsg(chatMsgBean);
    }

    private void sendAudioMsg() {
        Log.i("input", "语音发送--------->");
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        chatMsgBean.setType(1);
        chatMsgBean.setMsg(audioPath);
        chatMsgBean.setAudioLength(audioCount);
        Log.d(TAG, "sendAudioMsg ==>" + chatMsgBean.toString());
        sendMsg(chatMsgBean);
        audioCount = 0;
    }

    private void sendWordMsg() {
        Log.i("input", "文本发送--------->" + edtInput.getText().toString());
        ChatMsgBean chatMsgBean = new ChatMsgBean();
        chatMsgBean.setType(0);
        chatMsgBean.setMsg(edtInput.getText().toString());
        sendMsg(chatMsgBean);
    }

    private void sendMsg(ChatMsgBean chatMsgBean) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        chatMsgBean.setTime(time);
        chatMsgBean.setSend("15818694245");
        chatMsgBean.setState("success");
        msgAdapter.addData(chatMsgBean);
        recyChatMsg.smoothScrollToPosition(msgAdapter.getItemCount());
        edtInput.setText("");
        Log.d(TAG, "sendMsg ==>" + chatMsgBean.toString());
    }

    private void closeMore() {
        recyInputMore.setVisibility(View.GONE);
        isMoreTool = false;
    }

    private void closeFace() {
        isShowFace = false;
        recyInputFace.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TODO 查看图片后返回不能滚动,
//        recyChatMsg.smoothScrollToPosition(msgAdapter.getItemCount());
//        msgAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecord();
        if (msgAdapter != null) {
            msgAdapter.destory();
            msgAdapter = null;
        }
        faceAdapter = null;
        chatMoreAdapter = null;
        if (datas != null) {
            datas.clear();
            datas = null;
        }
        System.gc();  //5.0上需要结合使用
        System.runFinalization();
    }

    private int audioCount = 0;

    private void showAudioDialog() {
        audioDialog = new AlertDialog.Builder(this).create();
        View view = View.inflate(this, R.layout.dialog_audio_record, null);
        final TextView txtTimeCount = (TextView) view.findViewById(R.id.txt_timecount);
        audioDialog.setView(view, 0, 0, 0, 0);
        audioDialog.show();

        initRecord();

        audioCount = 0;
        audioTimer = new Timer();
        audioTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (audioCount < 30) {
                            txtTimeCount.setText("[" + audioCount + "]");
                        } else {
                            audioRecordEndEvent();
                        }
                        audioCount++;
                    }
                });
            }
        }, 0, 1000);
        mediaRecorder.start();
    }

    private void initRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if (TextUtils.isEmpty(audioPath))
            audioPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "audio_" + System.currentTimeMillis() + ".3gp";
        mediaRecorder.setOutputFile(audioPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    private void closeAudioDialog() {
        if (audioDialog != null) {
            audioDialog.cancel();
            audioDialog = null;
        }
        if (audioTimer != null) {
            audioTimer.cancel();
            audioTimer = null;
        }
    }


}
