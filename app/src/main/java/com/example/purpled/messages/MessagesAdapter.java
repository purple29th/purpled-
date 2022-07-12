package com.example.purpled.messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.chat.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private List<MessagesList> messagesLists;
    LocalStorage localStorage;
    private final Context context;

    public MessagesAdapter(Context ctx, List<MessagesList> messagesLists){

        inflater = LayoutInflater.from(ctx);
        this.messagesLists = messagesLists;
        this.context = ctx;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.messages_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.userName.setText(messagesLists.get(position).getUsername());
        holder.lastMessage.setText(messagesLists.get(position).getLastMessage());

        if (messagesLists.get(position).getUnseenMessages() == 0){
            holder.unseenMessage.setVisibility(View.GONE);
            holder.lastMessage.setTextColor(Color.parseColor("#959595"));
        }else{
            holder.unseenMessage.setVisibility(View.VISIBLE);
            holder.unseenMessage.setText(messagesLists.get(position).getUnseenMessages()+"");
            holder.lastMessage.setTextColor(context.getResources().getColor(R.color.purple_700));
        }

        if (!(messagesLists.get(position).getUserProfile()).isEmpty()){
            Picasso.get().load(messagesLists.get(position).getUserProfile()).into(holder.userImage);
        }

        holder.eachMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localStorage = new LocalStorage(view.getContext());

                Intent intent = new Intent(view.getContext(), ChatActivity.class);
                intent.putExtra("name", messagesLists.get(position).getUsername());
                intent.putExtra("profile_pic", messagesLists.get(position).getUserProfile());
                intent.putExtra("chat_key", messagesLists.get(position).getChatKey());
                intent.putExtra("uid", messagesLists.get(position).getUID());

                intent.putExtra("time", messagesLists.get(position).getTime());
                intent.putExtra("date", messagesLists.get(position).getDate());
                intent.putExtra("is_online", messagesLists.get(position).isOnline());

                context.startActivity(intent);

            }
        });

    }

    public void updateData(List<MessagesList> messagesLists){
        this.messagesLists = messagesLists;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, lastMessage, unseenMessage;
        private ImageView userImage;
        private LinearLayout eachMsg;

        public MyViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessages);
            unseenMessage = (TextView) itemView.findViewById(R.id.unseenmessages);
            userImage = (ImageView) itemView.findViewById(R.id.profilePic);
            eachMsg = (LinearLayout) itemView.findViewById(R.id.rootlayout);
        }

    }
}