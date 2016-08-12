package com.example.admin.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.admin.myapplication.Config;
import com.example.admin.myapplication.PicPreviewActivity;
import com.example.admin.myapplication.R;
import com.example.admin.myapplication.VideoPlayActivity;
import com.example.admin.myapplication.bean.ChatMsgBean;
import com.example.admin.myapplication.utils.BitmapUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @Created Zhy
 * @date 2016/8/49:17
 */
public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolders> {

    private static final String TAG = ChatMsgAdapter.class.getName();
    private List<ChatMsgBean> datas;
    private String currentNo;
    private Activity activity;
    private MediaPlayer mediaPlayer;
    private AnimationDrawable animation;
    private ChatMsgAdapter.ViewHolders lastHolders ; //前一个操作的holders
    private PopupWindow popupWindow;

    public ChatMsgAdapter(Activity activity,List<ChatMsgBean> datas,String currentNo){
        this.datas = datas;
        this.currentNo = currentNo;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getSend().equals(currentNo)?0:1;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addData(ChatMsgBean bean){
        if (datas != null){
            datas.add(bean);
            notifyDataSetChanged();
        }
    }

    public void addDatas(int location ,List<ChatMsgBean> data){
        if (datas != null){
            datas.addAll(location,data);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0){
            view = View.inflate(parent.getContext(), R.layout.layout_chatmsg_me2, null);
        }else {
            view = View.inflate(parent.getContext(), R.layout.layout_chatmsg_other, null);
        }
        return new ViewHolders(view,viewType);
    }

    @Override
    public void onBindViewHolder(final ViewHolders holder, final int position) {
        final ChatMsgBean chatMsgBean = datas.get(position);
//        Log.i("Adapter","position:"+position+"," +chatMsgBean.toString());

        if (getItemViewType(position)==0 ){
            if ("error".equals(chatMsgBean.getState())){
                holder.icError.setVisibility(View.VISIBLE);
            }else {
                holder.icError.setVisibility(View.GONE);
            }
            if (chatMsgBean.getType()==2) holder.icError.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(chatMsgBean.getTime()) || getItemViewType(position)==1){
            holder.txtTime.setVisibility(View.GONE);
        }else {
            holder.txtTime.setVisibility(View.VISIBLE);
            holder.txtTime.setText(chatMsgBean.getTime());
        }

        if (chatMsgBean.getType()==0){ //文本
            holder.txtContent.setVisibility(View.VISIBLE);
            holder.icVoice.setVisibility(View.GONE);
            holder.picture.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            changeFace(holder.txtContent,chatMsgBean.getMsg());
            holder.layout_video.setVisibility(View.GONE);     holder.videoView.setVisibility(View.GONE);
        }else if(chatMsgBean.getType()==1){//语音
            holder.icVoice.setVisibility(View.VISIBLE);
            holder.txtContent.setVisibility(View.GONE);
            holder.picture.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            if (getItemViewType(position) == 0){
                holder.txtContent.setVisibility(View.VISIBLE);
                int len = chatMsgBean.getAudioLength();
                String length = getVoiceTxtWidth(len);
                holder.txtContent.setText(len<=0?"":len+length);
            }
            holder.layout_video.setVisibility(View.GONE);   holder.videoView.setVisibility(View.GONE);
        }else if (chatMsgBean.getType()==2){//图片
            holder.picture.setVisibility(View.VISIBLE);
            holder.txtContent.setVisibility(View.GONE);
            holder.icVoice.setVisibility(View.GONE);
            if (getItemViewType(position) == 0){

                holder.picture.setTag(chatMsgBean.getMsg());
                Log.e(TAG, "onBindViewHolder: " + chatMsgBean.getMsg());
                showImage(R.mipmap.im_msg_my, holder.picture, chatMsgBean.getMsg());
            }   else {
                showImage(R.mipmap.im_msg_others,holder.picture, chatMsgBean.getMsg());
            }
            holder.content.setVisibility(View.GONE);
            holder.layout_video.setVisibility(View.VISIBLE);
            holder.videoView.setVisibility(View.GONE);
//            holder.videoTag.setVisibility(View.GONE);   holder.videoView.setVisibility(View.GONE);
        }else if (chatMsgBean.getType()==3){//视频
            holder.picture.setVisibility(View.VISIBLE);
            holder.txtContent.setVisibility(View.GONE);
            holder.icVoice.setVisibility(View.GONE);
            holder.content.setVisibility(View.GONE);

            if (getItemViewType(position) == 0){
                holder.videoView.setVisibility(View.VISIBLE);
                holder.videoView.setVideoPath(chatMsgBean.getVideoPath());

                holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
//                        Log.d(TAG,"setOnCompletionListener onCompletion() ===> date:"+chatMsgBean.getTime());
                        holder.videoView.start();
                    }
                });
                holder.videoView.start();
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.setLooping(true);
                    }
                });
                holder.videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                x = (int) v.getX();
                                y = (int) v.getY();
                                break;
                            case MotionEvent.ACTION_UP:
                                if (Math.abs(x-v.getX())<5 && Math.abs(y-v.getY())<5){
                                    if (!TextUtils.isEmpty(chatMsgBean.getVideoPath())){
                                        Log.d(TAG,"视频item点击=====>"+chatMsgBean.toString());
                                        Intent intent = new Intent(activity, VideoPlayActivity.class);
                                        intent.putExtra("videopath",chatMsgBean.getVideoPath());
                                        activity.startActivity(intent);
                                    }
                                }
                                break;
                        }
                        return true;
                    }
                });
            }
            holder.layout_video.setVisibility(View.GONE);
        }else { //文本
            changeFace(holder.txtContent,chatMsgBean.getMsg());
            holder.txtContent.setVisibility(View.VISIBLE);
            holder.picture.setVisibility(View.GONE);
            holder.icVoice.setVisibility(View.GONE);
            holder.content.setVisibility(View.VISIBLE);
            holder.layout_video.setVisibility(View.GONE); holder.videoView.setVisibility(View.GONE);
        }
        holder.picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatMsgBean.getType() ==2 && !TextUtils.isEmpty(chatMsgBean.getMsg())){
                    Intent intent = new Intent(activity, PicPreviewActivity.class);
                    intent.putExtra("img_uri", Uri.fromFile(new File(chatMsgBean.getMsg())));
                    intent.putExtra("type","type_look");
                    activity.startActivity(intent);
                }else {
                    Toast.makeText(activity,"无法加载图片",Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatMsgBean.getType() == 1){       //语音
                    if (getItemViewType(position) == 0){
                        if (isAudioFinished) {
                            isAudioFinished = false;
                        } else { //先结束前面未播放完的
                            stopAnim();
                            stopMediaplay();
                            lastHolders.icVoice.setBackgroundResource(R.mipmap.im_audio_play_my_v3);
                        }
                        audioPlay(chatMsgBean.getMsg(), holder.icVoice);
                        lastHolders = holder;
                    }else {
                        Toast.makeText(activity,"无法获取录音",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopWindow(v,position);
                return true;
            }
        });

//        Log.i(TAG,"view width :"+holder.itemView.getWidth()+",height:"+holder.itemView.getHeight());
    }
    private void showPopWindow(View v, final int position) {
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_msg_popwindow,null);
        view.findViewById(R.id.msgpop_txt_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(position);
                notifyDataSetChanged();
                popupWindow.dismiss();
            }
        });

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        int height = v.getHeight();Log.i(TAG,"view height :"+height );
        Log.i(TAG,"view width :"+v.getWidth() );
        popupWindow.showAsDropDown(v, 0,-height-120);
    }

    private boolean isAudioFinished = true;
    private int x,y,x1,y1;

    private void startAudioAnim(ImageView imageView){
        Log.i(TAG,"录音动画---startAudioAnim------>");
        imageView.setBackgroundResource(R.drawable.anim_msg_audio_my);
        animation = (AnimationDrawable) imageView.getBackground();
        animation.start();
    }

    private void audioPlay(String path, final ImageView imageView){
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else {
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.i(TAG,"录音播放成功---audioPlay------>");
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {     //播放完成监听
                    isAudioFinished = true;
                    stopAnim();
                    imageView.setBackgroundResource(R.mipmap.im_audio_play_my_v3);
                }
            });
            startAudioAnim(imageView);
        } catch (IOException e) {
            Log.i(TAG,"录音播放失败---audioPlay------>");
            e.printStackTrace();
            Toast.makeText(activity,"无法获取录音",Toast.LENGTH_SHORT).show();
            stopMediaplay();
            stopAnim();
            imageView.setBackgroundResource(R.mipmap.im_audio_play_my_v3);
        }
    }

    private void stopAnim() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    private void stopMediaplay() {
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private String getVoiceTxtWidth(int audioLength) {
        String s =" ";
        for (int i=0;i<audioLength;i++){
              s += " ";
        }
        return s;
    }

    private void showImage(final int bg, final SimpleDraweeView imageView, final String picture){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap_bg = BitmapFactory.decodeResource(activity.getResources(), bg);
                Bitmap bitmap_in;
                if (TextUtils.isEmpty(picture)) {
                    bitmap_in = BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_default_pic);
                } else {
                    bitmap_in = BitmapUtils.decodeSampledBitmapFromResource(picture,190,230);
                }
                final Bitmap bp = BitmapUtils.getRoundCornerImage(bitmap_bg, bitmap_in);
//                final Bitmap bp2 = BitmapUtils.getShardImage(bitmap_bg, bp);
//
                if (bitmap_bg != null)bitmap_bg.recycle();
                bitmap_bg = null;
                if (bitmap_in != null)bitmap_in.recycle();
                bitmap_in = null;
//                bp.recycle(); if (bp!=null)bp = null;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (imageView.getTag().equals(picture)) {
                            Log.d(TAG, "onBindViewHolder: new bitmap to load of holder.picture :");
                            imageView.setImageBitmap(bp);
                        }else {
//                            imageView.setImageBitmap(null);
                            Log.e(TAG, "onBindViewHolder: new bitmap to load of holder.picture :");
                        }
                    }
                });
            }
        }).start();
    }


    private void changeFace(TextView txt,String msg) {
        txt.setText("");
        if (TextUtils.isEmpty(msg)){
            txt.setText(msg);return;
        }
        if (msg.contains("f_static_0")){
            while (msg.contains("f_static_0")){
                int index = msg.indexOf("f_static_0");
                if (index != -1){
                    txt.append(msg.substring(0,index));   //表情前数据
                    String str = msg.substring(index, index + 12);
                    Integer icon = Config.faceMap.get(str);
                    if (icon != null){
                        SpannableString spannableString = new SpannableString(str);
                        spannableString.setSpan(new ImageSpan(activity,icon),0,12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        txt.append(spannableString);   //表情数据
                    }else {
                        txt.append(str);   //普通数据
                    }
                    msg = msg.substring(index+12);
                }
            }
            txt.append(msg);
        }else {
            txt.setText(msg);
        }
    }

    class ViewHolders extends RecyclerView.ViewHolder{
        private TextView txtTime,txtContent;
        private SimpleDraweeView picture,header;
        private ImageView icError,icVoice,videoTag;
        private LinearLayout content;
        private RelativeLayout layout_video;
        private VideoView videoView;

        public ViewHolders(View itemView, int viewType) {
            super(itemView);
            content = (LinearLayout) itemView.findViewById(R.id.chatmsg_content);
            layout_video = (RelativeLayout) itemView.findViewById(R.id.layout_video);

            videoView = (VideoView) itemView.findViewById(R.id.video_play);

            txtTime = (TextView) itemView.findViewById(R.id.chatmsg_txt_time);
            txtContent = (TextView) itemView.findViewById(R.id.content_word);
            icVoice = (ImageView) itemView.findViewById(R.id.content_voice);
//SimpleDraweeView
            header = (SimpleDraweeView) itemView.findViewById(R.id.chatmsg_draww_head);
            picture = (SimpleDraweeView) itemView.findViewById(R.id.content_pic);
            if (viewType == 0){
                icError = (ImageView) itemView.findViewById(R.id.img_error);
            }
        }

    }

    public void destory(){
        if (datas != null) {
            datas.clear();
        }
        if (popupWindow != null){
            popupWindow.dismiss();popupWindow = null;
        }
        activity = null;
        stopAnim();
        stopMediaplay();
    }


    public ChatMsgBean getItem(int position){
         if (datas != null){
             if (position >0 && datas.size()-1 > position){
                  return datas.get(position);
             }
         }
        return null;
    }

}
