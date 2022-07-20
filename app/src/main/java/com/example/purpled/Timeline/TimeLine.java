package com.example.purpled.Timeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import timber.log.Timber;

public class TimeLine extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView backBtn;
    LocalStorage localStorage;
    private List<TimelineList> timelineLists;
    private TimelineAdapter timelineAdapter;
    private CollectionReference collectionReference, userRef;
    private String username = "", dp = "";
    private int likes = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        localStorage = new LocalStorage(this);
        recyclerView = findViewById(R.id.timelinelist);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        timelineAdapter = new TimelineAdapter(this);
        recyclerView.setAdapter(timelineAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        collectionReference = FirebaseFirestore.getInstance().collection("tracks");
        userRef = FirebaseFirestore.getInstance().collection("Users");

        loaduploads();
    }

    private void loaduploads() {

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String key = document.getId();
                        String id = (String) document.get("uid");
                        String privacy = (String) document.get("privacy");
                        Log.d("uids", id);

                        if (privacy.equals("public")){
                            String desc = (String) document.get("description");
                            String genre = (String) document.get("genre");
                            String track_image = (String) document.get("track_image");
                            String track_url = (String) document.get("track_url");
                            String trackname = (String) document.get("track_name");
                            String time = (String) document.get("time");
                            String date = (String) document.get("date");

                            if (document.contains("likes")){
                                likes = Integer.parseInt(document.get("likes").toString());
                            }

                            String timeStamp = time + "   " + date ;

                            userRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                                            String uid = documentSnapshot.getId();

                                            if (uid.equals(id)){
                                                username = "@" + documentSnapshot.get("username");
                                                Log.d("username: ", username);

                                                if (documentSnapshot.contains("userProfile")){
                                                    dp = (String) documentSnapshot.get("userProfile");
                                                    Log.d("image : ", dp);

                                                }
                                            }
                                        }

                                    }
                                    TimelineList timelineList = new TimelineList(trackname, desc, genre, track_image, track_url,
                                            username, likes, timeStamp, dp, key);
                                    timelineAdapter.add(timelineList);
                                }
                            });


                        }

                        }


                } else {
                    Toast.makeText(TimeLine.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}