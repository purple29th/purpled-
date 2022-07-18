package com.example.purpled.viewholder;

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


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.itemView.setOnClickListener(null);
                popupMenu(view, position);
                return false;
            }
        });


    }

    private void popupMenu(View v, int position) {
        PopupMenu popupMenu = new PopupMenu(inflater.getContext(), v);
        //Setting the layout of PopupMenu objects
        popupMenu.getMenuInflater().inflate(R.menu.track_menu, popupMenu.getMenu());
        //Set PopupMenu click event
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addToPlaylist(v, position);
                return true;
            }
        });
        //Show menu
        popupMenu.show();
    }

    private void addToPlaylist(View view, int position) {
        localStorage = new LocalStorage(view.getContext());
        DocumentReference documentReference = FirebaseFirestore.getInstance().
                collection("Users").document(localStorage.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()){
                        HashMap<String, Object> playlistMap = new HashMap<>();
                        playlistMap.put("trackArtist", songListClasses.get(position).getTrackArtist());
                        playlistMap.put("trackTitle",  songListClasses.get(position).getTrackTitle());
                        playlistMap.put("trackDuration", songListClasses.get(position).getTrackDuration());
                        playlistMap.put("trackImage", songListClasses.get(position).getTrackImage());
                        playlistMap.put("trackUrl", songListClasses.get(position).getTrackUrl());


                        documentReference.collection("myplaylist").add(playlistMap)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(view.getContext(), "Successfully added to your playlist", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }else{
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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