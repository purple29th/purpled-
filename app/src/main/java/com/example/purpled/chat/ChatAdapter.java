package com.example.purpled.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.model.MessageModel;
import com.example.purpled.model.Users;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<MessageModel> chatList;
    private final Context context;
    private LocalStorage localStorage;

    public ChatAdapter(Context ctx){

        this.context = ctx;
        chatList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        localStorage = new LocalStorage(context);
        MessageModel list1 = chatList.get(position);


        if (list1.getSenderId().equals(localStorage.getUid())){
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

    public void add(MessageModel messageModel){
        chatList.add(messageModel);
        notifyDataSetChanged();
    }

    public void clear(){
        chatList.clear();
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

