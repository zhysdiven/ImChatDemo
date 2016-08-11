//package com.example.admin.myapplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Spannable;
//import android.text.SpannableString;
//import android.text.TextUtils;
//import android.text.style.ImageSpan;
//import android.util.Log;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.VideoView;
//
//import com.example.admin.myapplication.adapter.ChatMoreAdapter;
//import com.example.admin.myapplication.adapter.ChatMsgAdapter;
//import com.example.admin.myapplication.adapter.FaceAdapter;
//import com.example.admin.myapplication.bean.ChatItemBean;
//import com.example.admin.myapplication.bean.ChatMsgBean;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Admin on 2016/8/11.
// */
//public class BaseChatActivity extends AppCompatActivity implements View.OnClickListener {
//
//    private static final String TAG = BaseChatActivity.class.getSimpleName();
//    private static final int SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE = 0x110;
//    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0x120;
//    private static final int SMALLVIDEO_ACTIVITY_REQUEST_CODE = 0x130;
//
//    private ImageView btnBack,btnSetting,btnMore,btnImg,btnChanges;
//    private Button btnVoice;
//    private TextView txtTitle,txtSend;
//    private RecyclerView recyChatMsg,recyInputMore,recyInputFace;
//    private EditText edtInput;
//    private ChatMoreAdapter chatMoreAdapter;
//    private FaceAdapter faceAdapter;
//    private ChatMsgAdapter msgAdapter;
//
//
//    private String imgPath;
//    private List<ChatMsgBean> dataMsgs;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_chat_view);
//
//        initView();
//    }
//
//    private void initView(){
//        txtTitle = (TextView) findViewById(R.id.txt_title);
//        txtSend = (TextView) findViewById(R.id.txt_send);
//        edtInput = (EditText) findViewById(R.id.edt_input);
//
//        btnBack = (ImageView) findViewById(R.id.btn_back);
//        btnSetting = (ImageView) findViewById(R.id.btn_setting);
//        btnMore = (ImageView) findViewById(R.id.btn_more);
//        btnImg = (ImageView) findViewById(R.id.btn_image);
//        btnChanges = (ImageView) findViewById(R.id.btn_changes);
//        btnVoice = (Button) findViewById(R.id.btn_voice);
//
//        txtSend.setOnClickListener(this);
//        btnBack.setOnClickListener(this);
//        btnSetting.setOnClickListener(this);
//        btnMore.setOnClickListener(this);
//        btnImg.setOnClickListener(this);
//        btnChanges.setOnClickListener(this);
//        btnVoice.setOnClickListener(this);
//
//        initRecyMsg();
//        initChatMore();
//        iniRecyFace();
//    }
//
//    private void initRecyMsg() {
//        dataMsgs = new ArrayList<>();
//        recyChatMsg = (RecyclerView) findViewById(R.id.chat_recyclerview_msg);
//        msgAdapter = new ChatMsgAdapter(this,dataMsgs, "15818694245");
//        recyChatMsg.setLayoutManager(new LinearLayoutManager(this));
//        recyChatMsg.setItemAnimator(new DefaultItemAnimator());
//        recyChatMsg.setAdapter(msgAdapter);
//        recyChatMsg.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                boolean isScrolled = Math.abs(dy) > 5;
//                if (isScrolled){
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    if (dy>0){
//                        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
//                        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
//                        if (lastVisibleItemPosition <= lastpositonIdle) return;
//
////                        if(lastpositons != lastVisibleItemPosition) {
//                        ChatMsgBean item = msgAdapter.getItem(lastVisibleItemPosition+1);
//                        if (item == null) return;
//                        if (item.getType() == 3  ) {
//                            View childAt = recyclerView.getChildAt(lastVisibleItemPosition+1);
//                            if (childAt != null) {
//                                VideoView videoView = (VideoView) childAt.findViewById(R.id.video_play);
//                                if (!videoView.isPlaying()) {
//                                    Log.d(TAG, "videoView start");
//                                    videoView.start();
////                                        firstpostions = firstVisibleItemPosition;
//                                    firstpositonIdle = lastVisibleItemPosition+1;
//                                }
//                            }
//
////                                lastpositons = lastVisibleItemPosition;
////                                msgAdapter.notifyItemChanged(lastVisibleItemPosition);
//
//                            lastpositonIdle = lastVisibleItemPosition+1;
//                            Log.d(TAG, "notifyItemChanged down position:" + lastVisibleItemPosition + ",com position:"+lastCompletelyVisibleItemPosition+"," + item.getTime());
//                        }
////                        }
//                    }else {
//                        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                        int firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
//                        if (firstVisibleItemPosition >= firstpositonIdle) return;
////                        if (firstpostions != firstVisibleItemPosition) {
//                        ChatMsgBean item = msgAdapter.getItem(firstVisibleItemPosition-1);
//                        if (item == null) return;
//                        if (item.getType() == 3  ) {
//                            View childAt = recyclerView.getChildAt(firstVisibleItemPosition-1);
//                            if (childAt != null) {
//                                VideoView videoView = (VideoView) childAt.findViewById(R.id.video_play);
//                                if (!videoView.isPlaying()) {
//                                    Log.d(TAG, "videoView start");
//                                    videoView.start();
////                                        firstpostions = firstVisibleItemPosition;
//                                    firstpositonIdle = firstVisibleItemPosition-1;
//                                }
//                            }
////                                firstpostions = firstVisibleItemPosition;
////                                msgAdapter.notifyItemChanged(firstVisibleItemPosition);
//                            Log.d(TAG, "notifyItemChanged up position:" + firstVisibleItemPosition + ", com position:"+firstCompletelyVisibleItemPosition+"," + item.getTime());
//                        }
////                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState != RecyclerView.SCROLL_STATE_IDLE){
//                    Log.i("aaa","11111111111111111111111");
//                    im.hideSoftInputFromWindow(BaseChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    if (isMoreTool){
//                        closeMore();
//                    }
//                    if (isShowFace) closeFace();
//                }else {
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    firstpositonIdle = layoutManager.findFirstVisibleItemPosition();
//                    lastpositonIdle = layoutManager.findLastVisibleItemPosition();
////                    TODO
////                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
////                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
////                    Log.i(TAG,"first:"+firstVisibleItemPosition+",last:"+lastVisibleItemPosition);
//
////                    msgAdapter.notifyDataSetChanged();
////                    msgAdapter.notifyItemChanged(firstVisibleItemPosition);
////                    msgAdapter.notifyItemChanged(lastVisibleItemPosition);
//
////                    if (layoutManager.findFirstVisibleItemPosition() == 0){
////                        if (count >=1){
////                            List<ChatMsgBean> chatMsgBeen = loadData();
////                            int size = msgAdapter.getItemCount();
////                            Log.d("size","size:"+size);
////                            msgAdapter.addDatas(0,chatMsgBeen);
////                            recyclerView.smoothScrollToPosition(chatMsgBeen.size());
////                            count=0;
////                        }else {
////                            count++;
////                        }
////                    }
//                }
//            }
//        });
//    }
//
//    private void initChatMore() {
//        recyInputMore = (RecyclerView) findViewById(R.id.chat_recyclerview__more);
//        recyInputMore.setLayoutManager(new GridLayoutManager(this,4));
//        chatMoreAdapter = new ChatMoreAdapter(getChatTools());
//        chatMoreAdapter.setOnItemClickListener(new ChatMoreAdapter.onItemClickListener() {
//            @Override
//            public void onItemClick(int position, View view) {
//                if (position ==0){
//                    Log.i(TAG, "onItemClick ==> go to system picture select !");
//                    Intent picture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(picture, SYSTEM_IMAGE_ACTIVITY_REQUEST_CODE);
//                }else if (position == 1){
//                    Log.i(TAG, "onItemClick ==> go to system camera !");
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    imgPath =  getImagePathOfSave();
//                    File file = new File(imgPath);
//                    if (!file.exists()) try {    //file必须存在，否则Result中无法接收data数据
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    Uri uri = Uri.fromFile(file);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                }else if (position == 2){
//                    Log.i(TAG, "onItemClick ==> go to small video !");
//                    startActivityForResult(new Intent(BaseChatActivity.this,SmallVideoActivity.class),SMALLVIDEO_ACTIVITY_REQUEST_CODE);
//                }
//            }
//        });
//        recyInputMore.setAdapter(chatMoreAdapter);
//    }
//
//    private void iniRecyFace() {
//        recyInputFace = (RecyclerView) findViewById(R.id.chat_recyclerview__face);
//        recyInputFace.setLayoutManager(new GridLayoutManager(this,7));
//        faceAdapter = new FaceAdapter(Config.initFace());
//        recyInputFace.setAdapter(faceAdapter);
//        faceAdapter.setOnItemClickListener(new FaceAdapter.onItemClickListener() {
//            @Override
//            public void onItemClick(int position, View view) {
//                if (position == faceAdapter.getItemCount()-1){
//                    deleteInputEvent();
//                }else{
//                    selectFaceEvent(position);
//                }
//            }
//        });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    protected String getImagePathOfSave(){
//        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator+"img_" + System.currentTimeMillis() + ".jpg";
//    }
//
//    /**
//     * 表情选择事件
//     * @param position face item position
//     */
//    protected void selectFaceEvent(int position) {
//        int icon = faceAdapter.getItem(position).getIcon();
//        String content = faceAdapter.getItem(position).getContent();
//        String s = edtInput.getText().toString()+ content;
//
//        SpannableString spannableString = new SpannableString(content);
//        spannableString.setSpan(new ImageSpan(BaseChatActivity.this,icon),0,content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//
//        edtInput.append(spannableString);
//        edtInput.setSelection(s.length());
//    }
//
//    /**
//     * 输入栏删除事件,由表情中的删除键触发
//     */
//    protected void deleteInputEvent() {
//        int selectionEnd = edtInput.getSelectionEnd();
//        int selectionStart = edtInput.getSelectionStart();
//        if (!TextUtils.isEmpty(edtInput.getText().toString())){  //f_static_004f_static_001
//            String s = edtInput.getText().toString();
//            int index = s.lastIndexOf("f_static_0");
//            if (index != -1){
//                if(index + 12 == selectionStart){
//                    edtInput.getText().delete(index,selectionEnd);return;
//                }else {
//                    edtInput.getText().delete(selectionEnd-1,selectionEnd);
//                }
//            }else {
//                edtInput.getText().delete(selectionEnd-1,selectionEnd);
//            }
//        }
//    }
//
//    protected List<ChatItemBean> getChatTools(){
//        List<ChatItemBean> list = new ArrayList<>();
//        list.add(new ChatItemBean(R.mipmap.im_pic_up,"图片视频","pic_video"));
//        list.add(new ChatItemBean(R.mipmap.im_camera_up,"拍摄","camera"));
//        list.add(new ChatItemBean(R.mipmap.im_video_up,"小视屏","small_video"));
//        return list;
//    }
//
//}
