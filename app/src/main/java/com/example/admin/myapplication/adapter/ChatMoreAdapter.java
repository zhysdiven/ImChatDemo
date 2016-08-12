package com.example.admin.myapplication.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.bean.ChatItemBean;

import java.util.List;

/**
 * Created by Admin on 2016/8/4.
 */
public class ChatMoreAdapter extends RecyclerView.Adapter<ChatMoreAdapter.ViewHolder> {

    private final List<ChatItemBean> datas;
    private onItemClickListener listener;

    public ChatMoreAdapter(List<ChatItemBean> datas){
        this.datas = datas;
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(parent.getContext(), R.layout.layout_chat_tool,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatItemBean bean = datas.get(position);
        holder.txtName.setText(bean.getName());
        holder.icon.setImageResource(bean.getIcon());
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        private final TextView txtName;
        private final ImageView icon;
        public ViewHolder(View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
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
