package com.example.admin.myapplication;

import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @Created Zhy
 * @date 2016/8/11 13:56
 */
public class BaseChatActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BaseChatActivity.class.getSimpleName();
    private static final int SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE = 0x110;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0x120;
    private static final int SMALLVIDEO_ACTIVITY_REQUEST_CODE = 0x130;

    private ImageView btnBack,btnSetting,btnMore,btnImg,btnChanges;
    private Button btnVoice;
    private TextView txtTitle,txtSend;
    private RecyclerView recyChatMsg,recyInputMore,recyInputFace;
    private EditText edtInput;
    private ChatMoreAdapter chatMoreAdapter;
    private FaceAdapter faceAdapter;
    private ChatMsgAdapter msgAdapter;


    private String imgPath;
    private List<ChatMsgBean> dataMsgs;
    private int firstpositonIdle,lastpositonIdle;
    private InputMethodManager im;
    private boolean isMoreTool,isShowFace;
    private String audioPath;
    private int audioCount;
    private AlertDialog audioDialog;
    private MediaRecorder mediaRecorder;
    private Timer audioTimer;
    private boolean isTypeWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_chat_view);

        initView();
        initListener();
    }

    private void initView(){
        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtSend = (TextView) findViewById(R.id.txt_send);
        edtInput = (EditText) findViewById(R.id.edt_input);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnSetting = (ImageView) findViewById(R.id.btn_setting);
        btnMore = (ImageView) findViewById(R.id.btn_more);
        btnImg = (ImageView) findViewById(R.id.btn_image);
        btnChanges = (ImageView) findViewById(R.id.btn_changes);
        btnVoice = (Button) findViewById(R.id.btn_voice);

        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        initRecyMsg();
        initChatMore();
        iniRecyFace();
    }


    private void initRecyMsg() {
        dataMsgs = new ArrayList<>();
        recyChatMsg = (RecyclerView) findViewById(R.id.chat_recyclerview_msg);
        msgAdapter = new ChatMsgAdapter(this,dataMsgs, "15818694245");
        recyChatMsg.setLayoutManager(new LinearLayoutManager(this));
        recyChatMsg.setItemAnimator(new DefaultItemAnimator());
        recyChatMsg.setAdapter(msgAdapter);
        recyChatMsg.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                boolean isScrolled = Math.abs(dy) > 5;
                if (isScrolled){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (dy>0){
                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItemPosition <= lastpositonIdle) return;

                        ChatMsgBean item = msgAdapter.getItem(lastVisibleItemPosition+1);
                        if (item == null) return;
                        Log.d(TAG, "notifyItemChanged down position:" + lastVisibleItemPosition + ",com position:"+lastCompletelyVisibleItemPosition+"," + item.getTime());
                    }else {
                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                        int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                        if (firstVisibleItemPosition >= firstpositonIdle) return;
                        ChatMsgBean item = msgAdapter.getItem(firstVisibleItemPosition-1);
                        if (item == null) return;
                        Log.d(TAG, "notifyItemChanged up position:" + firstVisibleItemPosition + ", com position:"+firstCompletelyVisibleItemPosition+"," + item.getTime());
                    }
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE){
                    Log.i("aaa","11111111111111111111111");
                    im.hideSoftInputFromWindow(BaseChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if (isMoreTool){
                        closeMore();
                    }
                    if (isShowFace) closeFace();
                }else {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    firstpositonIdle = layoutManager.findFirstVisibleItemPosition();
                    lastpositonIdle = layoutManager.findLastVisibleItemPosition();
                }
            }
        });
    }

    private void initChatMore() {
        recyInputMore = (RecyclerView) findViewById(R.id.chat_recyclerview__more);
        recyInputMore.setLayoutManager(new GridLayoutManager(this,4));
        chatMoreAdapter = new ChatMoreAdapter(getChatTools());
        chatMoreAdapter.setOnItemClickListener(new ChatMoreAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position ==0){
                    Log.i(TAG, "onItemClick ==> go to system picture select !");
                    Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(picture, SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE);
                }else if (position == 1){
                    Log.i(TAG, "onItemClick ==> go to system camera !");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    imgPath =  getImagePathOfSave();
                    File file = new File(imgPath);
                    if (!file.exists()) try {    //file必须存在，否则Result中无法接收data数据
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Uri uri = Uri.fromFile(file);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }else if (position == 2){
                    Log.i(TAG, "onItemClick ==> go to small video !");
                    startActivityForResult(new Intent(BaseChatActivity.this,SmallVideoActivity.class),SMALLVIDEO_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        recyInputMore.setAdapter(chatMoreAdapter);
    }

    private void iniRecyFace() {
        recyInputFace = (RecyclerView) findViewById(R.id.chat_recyclerview__face);
        recyInputFace.setLayoutManager(new GridLayoutManager(this,7));
        faceAdapter = new FaceAdapter(Config.initFace());
        recyInputFace.setAdapter(faceAdapter);
        faceAdapter.setOnItemClickListener(new FaceAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == faceAdapter.getItemCount()-1){
                    deleteInputEvent();
                }else{
                    selectFaceEvent(position);
                }
            }
        });
    }

    private void initListener() {
        txtSend.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        btnImg.setOnClickListener(this);
        btnChanges.setOnClickListener(this);
        btnVoice.setOnClickListener(this);


        edtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE && !TextUtils.isEmpty(edtInput.getText().toString())) {
                    sendWordMsg();
                }
                return true;
            }
        });
        edtInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (isMoreTool) closeMore();
                    if (isShowFace) closeFace();
                }
            }
        });

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

        btnVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "开始录音---------------->");
                btnVoice.setText("松开发送");
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
                im.hideSoftInputFromWindow(edtInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                if (isMoreTool) closeMore();
                break;
            case R.id.chat_txt_send:
                sendWordMsg();
                break;
        }
    }

    private void initRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        if (TextUtils.isEmpty(audioPath))
            audioPath = getAudioPathOfSave();
        mediaRecorder.setOutputFile(audioPath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void audioRecordEndEvent() {
        stopRecord();
        btnVoice.setText("按下录音");
        closeAudioDialog();
        if (audioCount <= 1) {
            Toast.makeText(BaseChatActivity.this, "请再多说一点吧", Toast.LENGTH_SHORT).show();
        } else {
            sendAudioMsg();
            audioPath = "";
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

    /**
     *
     * 拍照图片保存路径
     * @return   image path
     * @author zhy
     * @create_time 2016/8/12 14:09
     * @update_time 2016/8/12 14:09
     */
    protected String getImagePathOfSave(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator+"img_" + System.currentTimeMillis() + ".jpg";
    }


    /**
     *
     * 录音文件保存路径
     * @return  audio path
     * @author zhy
     * @create_time 2016/8/12 15:31
     * @update_time 2016/8/12 15:31
     */
    protected String getAudioPathOfSave(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "audio_" + System.currentTimeMillis() + ".3gp";
    }

    /**
     * 表情选择事件
     * @param position face item position
     */
    protected void selectFaceEvent(int position) {
        int icon = faceAdapter.getItem(position).getIcon();
        String content = faceAdapter.getItem(position).getContent();
        String s = edtInput.getText().toString()+ content;

        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ImageSpan(BaseChatActivity.this,icon),0,content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        edtInput.append(spannableString);
        edtInput.setSelection(s.length());
    }

    /**
     * 输入栏删除事件,由表情中的删除键触发
     */
    protected void deleteInputEvent() {
        int selectionEnd = edtInput.getSelectionEnd();
        int selectionStart = edtInput.getSelectionStart();
        if (!TextUtils.isEmpty(edtInput.getText().toString())){  //f_static_004f_static_001
            String s = edtInput.getText().toString();
            int index = s.lastIndexOf("f_static_0");
            if (index != -1){
                if(index + 12 == selectionStart){
                    edtInput.getText().delete(index,selectionEnd);
                }else {
                    edtInput.getText().delete(selectionEnd-1,selectionEnd);
                }
            }else {
                edtInput.getText().delete(selectionEnd-1,selectionEnd);
            }
        }
    }

    protected List<ChatItemBean> getChatTools(){
        List<ChatItemBean> list = new ArrayList<>();
        list.add(new ChatItemBean(R.mipmap.im_pic_up,"图片视频","pic_video"));
        list.add(new ChatItemBean(R.mipmap.im_camera_up,"拍摄","camera"));
        list.add(new ChatItemBean(R.mipmap.im_video_up,"小视屏","small_video"));
        return list;
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
