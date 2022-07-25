package com.example.purpled.playlist;

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

public class playlistAdapter extends RecyclerView.Adapter<playlistAdapter.MyViewHolder> {

    private List<playlistList> uploadsList;
    private final Context context;

    public playlistAdapter(Context ctx){

        this.context = ctx;
        uploadsList = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        playlistList uploadsmodel = uploadsList.get(position);
        holder.trackname.setText(uploadsmodel.getTrackname());
        holder.artist.setText(uploadsmodel.getArtist());

        Picasso.get().load(uploadsmodel.getImage()).into(holder.trackImg);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
            }
        });

    }

    public void add(playlistList myUploadsList){
        uploadsList.add(myUploadsList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return uploadsList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView trackname, artist;
        private ImageView trackImg;

        public MyViewHolder(View itemView) {
            super(itemView);

            trackname = (TextView) itemView.findViewById(R.id.track_title);
            artist = (TextView) itemView.findViewById(R.id.artist);
            trackImg = (ImageView) itemView.findViewById(R.id.track_pic);

            trackname.setSelected(true);
        }

    }
}