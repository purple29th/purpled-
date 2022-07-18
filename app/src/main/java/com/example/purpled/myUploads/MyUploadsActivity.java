package com.example.purpled.myUploads;

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

public class MyUploadsActivity extends AppCompatActivity {
    private RecyclerView uploadsList;
    private List<MyUploadsList> myUploadsLists;
    private MyUploadsAdapter myUploadsAdapter;
    private CollectionReference collectionReference;
    LocalStorage localStorage;
    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_uploads);

        localStorage = new LocalStorage(this);

        uploadsList = findViewById(R.id.myuploadlist);
        backBtn = findViewById(R.id.back_btn);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        myUploadsAdapter = new MyUploadsAdapter(this);
        uploadsList.setAdapter(myUploadsAdapter);
        uploadsList.setLayoutManager(new LinearLayoutManager(this));

        collectionReference = FirebaseFirestore.getInstance().collection("tracks");


        loaduploads();
    }

    private void loaduploads() {

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.contains("uid")){
                            String id = (String) document.get("uid");
                            if (id.equals(localStorage.getUid())){
                                String desc = (String) document.get("description");
                                String genre = (String) document.get("genre");
                                String track_image = (String) document.get("track_image");
                                String track_url = (String) document.get("track_url");
                                String trackname = (String) document.get("track_name");

                                MyUploadsList myUploadsList = new MyUploadsList(trackname, desc, genre, track_image, track_url);
                                myUploadsAdapter.add(myUploadsList);
                            }
                        }
                    }
                }else {
                    Toast.makeText(MyUploadsActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}