package com.example.purpled.myUploads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.R;
import com.example.purpled.chat.ChatActivity;
import com.example.purpled.model.Users;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyUploadsAdapter extends RecyclerView.Adapter<MyUploadsAdapter.MyViewHolder> {

    private List<MyUploadsList> uploadsList;
    private final Context context;

    public MyUploadsAdapter(Context ctx){

        this.context = ctx;
        uploadsList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uploads_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MyUploadsList uploadsmodel = uploadsList.get(position);
        holder.trackname.setText(uploadsmodel.getTrackname());
        holder.genre.setText(uploadsmodel.getGenre());
        holder.description.setText(uploadsmodel.getDescription());

        Picasso.get().load(uploadsmodel.getImage()).into(holder.trackImg);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
            }
        });

    }

    public void add(MyUploadsList myUploadsList){
        uploadsList.add(myUploadsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return uploadsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, trackname, genre, description;
        private ImageView trackImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            trackname = (TextView) itemView.findViewById(R.id.track_title);
            genre = (TextView) itemView.findViewById(R.id.genre);
            description = (TextView) itemView.findViewById(R.id.track_desc);
            trackImg = (ImageView) itemView.findViewById(R.id.track_pic);
        }

    }
}