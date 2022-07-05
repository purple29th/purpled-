package com.example.purpled.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.ApiPlayer;
import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.model.SongListClass;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final List<SongListClass> songListClasses;
    LocalStorage localStorage;

    public SongListAdapter(Context ctx, List<SongListClass> songListClasses){

        inflater = LayoutInflater.from(ctx);
        this.songListClasses = songListClasses;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_list_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.trackTitle.setText(songListClasses.get(position).getTrackTitle());
        holder.trackArtist.setText(songListClasses.get(position).getTrackArtist());
        holder.trackDuration.setText(songListClasses.get(position).getTrackDuration());

        Picasso.get().load(songListClasses.get(position).getTrackImage()).into(holder.trackImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localStorage = new LocalStorage(view.getContext());

                Intent intent = new Intent(view.getContext(), ApiPlayer.class);
                view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("trackArtist", songListClasses.get(position).getTrackArtist())
                        .putExtra("trackTitle", songListClasses.get(position).getTrackTitle())
                        .putExtra("trackDuration", songListClasses.get(position).getTrackDuration())
                        .putExtra("trackImage", songListClasses.get(position).getTrackImage())
                        .putExtra("trackUrl", songListClasses.get(position).getTrackUrl()));

                localStorage.setTrackArtist(songListClasses.get(position).getTrackArtist());
                localStorage.setTrackImage( songListClasses.get(position).getTrackImage());
                localStorage.setTrackTitle(songListClasses.get(position).getTrackTitle());
                localStorage.setTrackUrl(songListClasses.get(position).getTrackUrl());
            }
        });


    }

    @Override
    public int getItemCount() {
        return songListClasses.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView trackArtist, trackDuration, trackTitle;
        ImageView trackImage;

        public MyViewHolder(View itemView) {
            super(itemView);

            trackArtist = (TextView) itemView.findViewById(R.id.list_artist);
            trackDuration = (TextView) itemView.findViewById(R.id.list_duration);
            trackTitle = (TextView) itemView.findViewById(R.id.list_title);
            trackImage = (ImageView) itemView.findViewById(R.id.list_img);

        }

    }
}