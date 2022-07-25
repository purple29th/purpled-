package com.example.purpled.messages;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.chat.ChatAdapter;
import com.example.purpled.data.MemoryData;
import com.example.purpled.databinding.ActivityMessagesBinding;
import com.example.purpled.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class Messages extends AppCompatActivity {

    LocalStorage localStorage;
    RecyclerView msglistView;
    private CircleImageView profildePic;
    private CollectionReference userCollection;
    private CollectionReference chatCollectionFDB;
    private DatabaseReference chatCollection;
    private int unseenMessages = 0;
    private String lastMessage = "";
    private String chatKey = "";
    private List<MessagesList> messagesList;
    private MessagesAdapter messagesAdapter;
    private boolean dataSet = false, userStateOnline = false;
    private String getUsername;
    private String getUserProfile, userStateDate, userStateTime, getUserOne, getUserTwo;
    ActivityMessagesBinding binding;
    DatabaseReference databaseReference;
    private String UserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        localStorage = new LocalStorage(this);

        messagesAdapter = new MessagesAdapter(this);
        binding.messages.setAdapter(messagesAdapter);
        binding.messages.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        userInfoDisplay(binding.profilePic);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uid = dataSnapshot.getKey();

//                    Log.e(TAG, uid);

                    if (!uid.equals(localStorage.getUid())) {
                        UserId = uid;
                        String username = dataSnapshot.child("username").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        String profilePic = "";
                        final String[] lastMsg = {""};

                        if (dataSnapshot.hasChild("userProfile")) {
                            profilePic = dataSnapshot.child("userProfile").getValue(String.class);
                        }
//                       Users users =  new Users("","","","","",username,UserId);
//                       messagesAdapter.add(users);

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("chats");

                        String finalProfilePic = profilePic;
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {

                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;

                                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                            final String getKey2 = dataSnapshot2.getKey();
                                            Log.d("snapshot2", getKey2);


                                            if (dataSnapshot2.hasChild("senderId") && dataSnapshot2.hasChild("receiverId")
                                                    && dataSnapshot2.hasChild("message")) {

                                                    getUserOne = dataSnapshot2.child("senderId").getValue(String.class);
                                                    getUserTwo = dataSnapshot2.child("receiverId").getValue(String.class);


                                                    Log.d("tag uids", getUserOne+" and "+getUserTwo);
                                                    if (getUserOne.equals(localStorage.getUid()) && getUserTwo.equals(UserId)
                                                            || getUserOne.equals(UserId) && getUserTwo.equals(localStorage.getUid())) {

//                                                        chatDataSnapshot.getRef().orderByKey().limitToLast(1);

//                                                        final long getMsgKey = Long.parseLong(chatDataSnapshot.getKey());
//                                                        final long getLastSeenMsg = Long.parseLong(MemoryData.getLastMsgTs(Messages.this, getKey));

                                                        lastMessage = dataSnapshot2.child("message").getValue(String.class);
                                                        Log.d("lastmsg", lastMessage);

//                                                    Toast.makeText(Messages.this, lastMessage, Toast.LENGTH_SHORT).show();

//                                                    if (getMsgKey > getLastSeenMsg) {
//                                                            unseenMessages++;
//                                                        }


                                                    }



                                            }
                                        }


                                    }

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Users users = new Users("", "", "", "", finalProfilePic, username, UserId, email);
                        messagesAdapter.add(users);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void userInfoDisplay(CircleImageView profildePic) {
        DocumentReference UserRef;
        UserRef = FirebaseFirestore.getInstance().collection("Users").document(localStorage.getUid());

        UserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {

                        UserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String username = documentSnapshot.getString("username");
                                String userImg = documentSnapshot.getString("userProfile");

                                if (userImg == null) {
                                    Log.w(TAG, "No Profile Image");
                                } else {
                                    Picasso.get().load(userImg).into(profildePic);
                                }


                            }

                        });


                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}