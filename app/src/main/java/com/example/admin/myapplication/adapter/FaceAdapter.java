package com.example.admin.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.bean.FaceBean;

import java.util.List;


/**
 * Created by Admin on 2016/8/4.
 */
public class FaceAdapter extends RecyclerView.Adapter<FaceAdapter.ViewHolder>{

    private List<FaceBean> datas;
    private onItemClickListener listener;

    public FaceAdapter(List<FaceBean> datas){
        this.datas = datas;
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }

    public FaceBean getItem(int position){
        if (position <= datas.size() && position>=0){
            return datas.get(position);
        }
        return datas.get(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.layout_chat_face,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FaceBean faceBean = datas.get(position);
        holder.icon.setImageResource(faceBean.getIcon());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.ic_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(getLayoutPosition());
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

}
