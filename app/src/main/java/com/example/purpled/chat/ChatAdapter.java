package com.example.purpled.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.ApiPlayer;
import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.chat.ChatActivity;
import com.example.purpled.model.MessagesList;
import com.example.purpled.model.SongListClass;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private List<ChatList> chatList;
    LocalStorage localStorage;
    private String UID;
    private final Context context;

    public ChatAdapter(Context ctx, List<ChatList> chatList){

        inflater = LayoutInflater.from(ctx);
        this.chatList = chatList;
        this.context = ctx;
        localStorage = new LocalStorage(context);
        this.UID = localStorage.getUid();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.chat_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ChatList list1 = chatList.get(position);

        if (!list1.getUid().equals(UID)){
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.oppLayout.setVisibility(View.GONE);

            holder.myMsg.setText(list1.getMessage());
            holder.myTimeStamp.setText(list1.getDate()+" "+list1.getTime());
        }else {
            holder.myLayout.setVisibility(View.GONE);
            holder.oppLayout.setVisibility(View.VISIBLE);

            holder.oppMsg.setText(list1.getMessage());
            holder.oppTimeStamp.setText(list1.getDate()+" "+list1.getTime());
        }
    }

    public void updateChatList(List<ChatList> chatList){
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView oppMsg, myMsg, oppTimeStamp, myTimeStamp;
        private LinearLayout oppLayout, myLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            oppMsg = (TextView) itemView.findViewById(R.id.oppMsg);
            myMsg = (TextView) itemView.findViewById(R.id.myMsg);
            oppTimeStamp = (TextView) itemView.findViewById(R.id.oppMsgTimeStamp);
            myTimeStamp = (TextView) itemView.findViewById(R.id.myMsgTimeStamp);
            oppLayout = (LinearLayout) itemView.findViewById(R.id.opp_layout);
            myLayout = (LinearLayout) itemView.findViewById(R.id.my_layout);

        }

    }
}

