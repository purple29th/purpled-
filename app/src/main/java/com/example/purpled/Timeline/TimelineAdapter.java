package com.example.purpled.Timeline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purpled.ApiPlayer;
import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private List<TimelineList> timelineLists;
    private final Context context;
    LocalStorage localStorage;
    public static MediaPlayer mediaPlayer;

    public TimelineAdapter(Context ctx){

        this.context = ctx;
        timelineLists = new ArrayList<>();

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timeline_viewholder, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        localStorage = new LocalStorage(context);
        mediaPlayer = new MediaPlayer();

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("tracks");

        TimelineList timelineList = timelineLists.get(position);
        holder.trackname.setText(timelineList.getTrackname());
        holder.genre.setText(timelineList.getGenre());
        holder.description.setText(timelineList.getDescription());
        holder.userName.setText(timelineList.getUsername());
        holder.timestamp.setText(timelineList.getTimestamp());

        String key = timelineList.getKey();

        Picasso.get().load(timelineList.getImage()).into(holder.trackImg);

        if (timelineList.getUserImg().isEmpty()) {
            Log.d("Tag", "no img");
        } else{
            Picasso.get().load(timelineList.getUserImg()).into(holder.dp);
        }

        if (timelineList.getLikes() > 0){
            holder.likeamount.setVisibility(View.VISIBLE);
            holder.likeamount.setText(String.valueOf(timelineList.getLikes()));
        }

        collectionReference.document(key).collection("myLikes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int likey = 0;

                for (QueryDocumentSnapshot doc : value) {
                    String ids = doc.getId();
                    if (ids.equals(localStorage.getUid())) {
                        holder.likeBtn.setVisibility(View.GONE);
                        holder.unlikeBtn.setVisibility(View.VISIBLE);

                    }
//                    else{
//                        holder.likeBtn.setVisibility(View.VISIBLE);
//                        holder.unlikeBtn.setVisibility(View.GONE);
//                    }
                }

                likey = value.getDocuments().size();
                if (likey > 0 ){
                    holder.likeamount.setVisibility(View.VISIBLE);
                    holder.likeamount.setText(String.valueOf(likey));
                }

            }
        });




        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                collectionReference.document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                int likes = 0;

                                if (!document.contains("myLikes")){

                                    HashMap<String, Object> likeMap = new HashMap<>();
                                    likeMap.put("likes", likes+1);

                                    collectionReference.document(key).collection("myLikes").document(localStorage.getUid()).set(likeMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Tag", "Okay");
                                            holder.likeBtn.setVisibility(View.GONE);
                                            holder.unlikeBtn.setVisibility(View.VISIBLE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{

                                    int myLikes = Integer.parseInt(document.get("likes").toString());
                                    HashMap<String, Object> likeupdateMap = new HashMap<>();
                                    likeupdateMap.put("likes", myLikes + 1);

                                    collectionReference.document(key).collection("myLikes").document(localStorage.getUid()).update(likeupdateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Tag", "Okay2");
                                            holder.likeBtn.setVisibility(View.GONE);
                                            holder.unlikeBtn.setVisibility(View.VISIBLE);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }

                            }
                        }
                    }
                });
            }
        });

        holder.unlikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionReference.document(key).collection("myLikes").document(localStorage.getUid())
                        .delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Tag", "Okay");
                        holder.likeBtn.setVisibility(View.VISIBLE);
                        holder.unlikeBtn.setVisibility(View.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        if (timelineList.getUrl() != null) {
//            try {
//                mediaPlayer.setDataSource(timelineList.getUrl());
//                mediaPlayer.prepare();
//                Toast.makeText(context, "Loaded", Toast.LENGTH_SHORT).show();
//            } catch (Exception e) {
//                Timber.tag("Try Song Url").e(e.getMessage());
//            }
//        }

        holder.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ApiPlayer.class);
                view.getContext().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("trackArtist", timelineList.getUsername())
                        .putExtra("trackTitle", timelineList.getTrackname())
                        .putExtra("trackImage", timelineList.getImage())
                        .putExtra("trackUrl", timelineList.getUrl()));
            }
        });
    }

    public void add(TimelineList timelineList){
        timelineLists.add(timelineList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return timelineLists.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView userName, trackname, genre, description, timestamp, likeamount;
        private ImageView trackImg, likeBtn, unlikeBtn;
        private CircleImageView dp;
        private ImageButton playBtn;

        public MyViewHolder(View itemView) {
            super(itemView);

            trackname = (TextView) itemView.findViewById(R.id.track_name);
            genre = (TextView) itemView.findViewById(R.id.genre);
            description = (TextView) itemView.findViewById(R.id.dec);
            userName = (TextView) itemView.findViewById(R.id.username);
            trackImg = (ImageView) itemView.findViewById(R.id.track_img);
            dp = (CircleImageView) itemView.findViewById(R.id.profilePic);
            likeamount = itemView.findViewById(R.id.like_amount);
            timestamp = itemView.findViewById(R.id.posttime);
            likeBtn = itemView.findViewById(R.id.like);
            unlikeBtn = itemView.findViewById(R.id.unlike);
            playBtn = itemView.findViewById(R.id.play_btn);
        }

    }
}