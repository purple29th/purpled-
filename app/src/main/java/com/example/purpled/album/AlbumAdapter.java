package com.example.purpled.album;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.ApiPlayer;
import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.model.SongListClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private final LayoutInflater inflater;
    private final List<AlbumListClass> albumListClasses;
    LocalStorage localStorage;

    public AlbumAdapter(Context ctx, List<AlbumListClass> albumListClasses){

        inflater = LayoutInflater.from(ctx);
        this.albumListClasses = albumListClasses;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.album_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.trackTitle.setText(albumListClasses.get(position).getTrackAlbumName());
        holder.trackArtist.setText(albumListClasses.get(position).getTrackArtist()+" . "+
                albumListClasses.get(position).getTrackUrl() + " . "+
                albumListClasses.get(position).getTractAmount()+" Songs");

        Picasso.get().load(albumListClasses.get(position).getTrackImage()).into(holder.trackImage);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                localStorage = new LocalStorage(view.getContext());
//
//                Intent intent = new Intent(view.getContext(), ApiPlayer.class);
//                view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        .putExtra("trackArtist", albumListClasses.get(position).getTrackArtist())
//                        .putExtra("trackTitle", albumListClasses.get(position).getTrackAlbumName())
//                        .putExtra("trackDuration", albumListClasses.get(position).getTrackDuration())
//                        .putExtra("trackImage", albumListClasses.get(position).getTrackImage())
//                        .putExtra("trackUrl", albumListClasses.get(position).getTrackUrl()));
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return albumListClasses.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView trackArtist, trackDuration, trackTitle;
        ImageView trackImage;
        RelativeLayout viewHolder;

        public MyViewHolder(View itemView) {
            super(itemView);

            trackArtist = (TextView) itemView.findViewById(R.id.list_artist);
            trackTitle = (TextView) itemView.findViewById(R.id.list_title);
            trackImage = (ImageView) itemView.findViewById(R.id.list_img);
            viewHolder = itemView.findViewById(R.id.view_holder_layout);

            trackTitle.setSelected(true);

        }

    }
}