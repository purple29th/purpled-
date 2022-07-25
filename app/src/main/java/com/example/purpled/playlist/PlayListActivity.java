package com.example.purpled.playlist;

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
import com.example.purpled.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class PlayListActivity extends AppCompatActivity {
    private RecyclerView playlist;
    private List<playlistList> playlistLists;
    private playlistAdapter playlistAdapter;
    private CollectionReference collectionReference;
    LocalStorage localStorage;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);

        localStorage = new LocalStorage(this);

        playlist = findViewById(R.id.myuploadlist);
        backBtn = findViewById(R.id.back_btn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        playlistAdapter = new playlistAdapter(this);
        playlist.setAdapter(playlistAdapter);
        playlist.setLayoutManager(new LinearLayoutManager(this));

        collectionReference = FirebaseFirestore.getInstance().collection("Users");


        loaduploads();
    }

    private void loaduploads() {

        collectionReference.document(localStorage.getUid()).collection("myplaylist").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String track_image = (String) document.get("trackImage");
                        String track_url = (String) document.get("trackUrl");
                        String trackname = (String) document.get("trackTitle");
                        String trackartist = (String) document.get("trackArtist");

                        playlistList playlistList = new playlistList(trackname, trackartist, track_image, track_url);
                        playlistAdapter.add(playlistList);
                    }
                } else {
                    Toast.makeText(PlayListActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}