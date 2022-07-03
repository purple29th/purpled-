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

import com.example.purpled.R;
import com.example.purpled.model.SongListClass;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final List<SongListClass> songListArrayList;

    public SongListAdapter(Context ctx, List<SongListClass> songListArrayList){

        inflater = LayoutInflater.from(ctx);
        this.songListArrayList = songListArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.song_list_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.trackArtist.setText(songListArrayList.get(position).getTrackArtist());
        holder.trackTitle.setText(songListArrayList.get(position).getTrackTitle());
        holder.trackDuration.setText(songListArrayList.get(position).getTrackDuration());
        Picasso.get().load(songListArrayList.get(position).getTrackImage()).into(holder.trackImage);

//        holder.year.setText(rogerModelArrayList.get(position).getYear());


    }

    @Override
    public int getItemCount() {
        return songListArrayList.size();
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