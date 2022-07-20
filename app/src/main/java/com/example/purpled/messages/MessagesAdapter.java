package com.example.purpled.messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
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
import com.example.purpled.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Users> messagesLists;
    private final Context context;

    public MessagesAdapter(Context ctx){

        this.context = ctx;
        messagesLists = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Users usersmodel = messagesLists.get(position);
        holder.userName.setText(usersmodel.getUsername());
        holder.lastMessage.setText(usersmodel.getLastMsg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", usersmodel.getUid());
                intent.putExtra("username", usersmodel.getUsername());

                context.startActivity(intent);
            }
        });

        if (usersmodel.getImage().isEmpty()) {
            Log.d("Tag", "no  img");
//            holder.userImage.setImageResource(R.drawable.placeholder);
        } else{
            Picasso.get().load(usersmodel.getImage()).into(holder.userImage);
        }
//

    }

    public void add(Users userslist){
        messagesLists.add(userslist);
        notifyDataSetChanged();
    }

    public void clear(){
        messagesLists.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, lastMessage, unseenMessage;
        private CircleImageView userImage;
        private LinearLayout eachMsg;

        public MyViewHolder(View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
            lastMessage = (TextView) itemView.findViewById(R.id.lastMessages);
            unseenMessage = (TextView) itemView.findViewById(R.id.unseenmessages);
            userImage = (CircleImageView) itemView.findViewById(R.id.profilePic);
            eachMsg = itemView.findViewById(R.id.rootlayout);
        }

    }
}