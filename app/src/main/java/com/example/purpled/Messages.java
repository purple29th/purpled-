package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

import com.example.purpled.data.MemoryData;
import com.example.purpled.model.MessagesList;
import com.example.purpled.model.Users;
import com.example.purpled.viewholder.MessagesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    private String getUserProfile, userStateDate = "", userStateTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        localStorage = new LocalStorage(this);
        messagesList = new ArrayList<>();

        profildePic = findViewById(R.id.profilePic);
        msglistView = findViewById(R.id.messages);

        userCollection = FirebaseFirestore.getInstance().collection("Users");
        chatCollectionFDB = FirebaseFirestore.getInstance().collection("Chat");
        chatCollection = FirebaseDatabase.getInstance().getReference();

        msglistView.setHasFixedSize(true);
        msglistView.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessagesAdapter(Messages.this, messagesList);
        msglistView.setAdapter(messagesAdapter);

        userInfoDisplay(profildePic);
        getUsers();

    }

    private void getUsers() {
        chatCollection.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesList.clear();
                unseenMessages = 0;
                lastMessage = "";
                chatKey = "";

                for (DataSnapshot dataSnapshot : snapshot.child("users").getChildren()) {

                    final String getUID = dataSnapshot.getKey();

                    dataSet = false;

                    if (!getUID.equals(localStorage.getUid())) {
                        getUsername = dataSnapshot.child("username").getValue(String.class);

                        if (dataSnapshot.hasChild("userState")){
                            userStateDate = dataSnapshot.child("userState").child("date").getValue(String.class);
                            userStateTime = dataSnapshot.child("userState").child("time").getValue(String.class);
                        }
//                        userStateOnline = (boolean) dataSnapshot.child("userState").child("online").getValue();
                        getUserProfile = "";

//                                if (!(documentSnapshot.get("profilePic").toString()).isEmpty()){
//                                    getUserProfile = documentSnapshot.get("profilePic").toString();
//                                }


                        chatCollection.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                int getChatCounts = (int) snapshot.getChildrenCount();

                                if (getChatCounts > 0) {

                                    for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {

                                        final String getKey = dataSnapshot1.getKey();
                                        chatKey = getKey;
                                        final String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
                                        final String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);

                                        if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")) {


                                            if ((getUserOne.equals(getUID)) && getUserTwo.equals(localStorage.getUid()) || (getUserOne.equals(localStorage.getUid()) && getUserTwo.equals(getUID))) {

                                                for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()) {

                                                    final long getMsgKey = Long.parseLong(Objects.requireNonNull(chatDataSnapshot.getKey()));
                                                    final long getLastSeenMsg = Long.parseLong(MemoryData.getLastMsgTs(Messages.this, getKey));

                                                    lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
                                                    if (getMsgKey > getLastSeenMsg) {
                                                        unseenMessages++;
                                                    }

                                                }

                                            }

                                        }

                                    }

                                }
                                if (!dataSet) {
                                    dataSet = true;
                                    MessagesList messagesLists = new MessagesList(getUsername, getUID, lastMessage, unseenMessages, getUserProfile, chatKey, userStateDate, userStateTime, userStateOnline);
                                    messagesList.add(messagesLists);
                                    messagesAdapter.updateData(messagesList);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
//                        msglistView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                        messagesAdapter = new MessagesAdapter(getApplicationContext(), messagesList);
//                        msglistView.upda(messagesAdapter);

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
                                Users users = documentSnapshot.toObject(Users.class);

                                if (users.getImage() == null) {
                                    Log.w(TAG, "No Profile Image");
                                } else {
                                    Picasso.get().load(users.getImage()).into(profildePic);
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

//package com.example.purpled;
//
//        import static android.content.ContentValues.TAG;
//
//        import androidx.annotation.NonNull;
//        import androidx.appcompat.app.AppCompatActivity;
//        import androidx.recyclerview.widget.LinearLayoutManager;
//        import androidx.recyclerview.widget.RecyclerView;
//
//        import android.annotation.SuppressLint;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.widget.Toast;
//
//        import com.example.purpled.data.MemoryData;
//        import com.example.purpled.model.MessagesList;
//        import com.example.purpled.model.Users;
//        import com.example.purpled.viewholder.MessagesAdapter;
//        import com.google.android.gms.tasks.OnCompleteListener;
//        import com.google.android.gms.tasks.OnSuccessListener;
//        import com.google.android.gms.tasks.Task;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//        import com.google.firebase.firestore.CollectionReference;
//        import com.google.firebase.firestore.DocumentReference;
//        import com.google.firebase.firestore.DocumentSnapshot;
//        import com.google.firebase.firestore.FirebaseFirestore;
//        import com.google.firebase.firestore.QuerySnapshot;
//        import com.squareup.picasso.Picasso;
//
//        import java.util.ArrayList;
//        import java.util.List;
//
//        import de.hdodenhof.circleimageview.CircleImageView;
//
//public class Messages extends AppCompatActivity {
//
//    LocalStorage localStorage;
//    RecyclerView msglistView;
//    private CircleImageView profildePic;
//    private CollectionReference userCollection;
//    private CollectionReference chatCollectionFDB;
//    private DatabaseReference chatCollection;
//    private int unseenMessages = 0;
//    private String lastMessage = "";
//    private String chatKey = "";
//    private List<MessagesList> messagesList;
//    private MessagesAdapter messagesAdapter;
//    private boolean dataSet = false;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_messages);
//
//        localStorage = new LocalStorage(this);
//        messagesList = new ArrayList<>();
//
//        profildePic = findViewById(R.id.profilePic);
//        msglistView = findViewById(R.id.messages);
//
//        userCollection = FirebaseFirestore.getInstance().collection("Users");
//        chatCollectionFDB = FirebaseFirestore.getInstance().collection("Chat");
//        chatCollection = FirebaseDatabase.getInstance().getReference();
//
//        msglistView.setHasFixedSize(true);
//        msglistView.setLayoutManager(new LinearLayoutManager(this));
//
//        messagesAdapter = new MessagesAdapter(Messages.this, messagesList);
//        msglistView.setAdapter(messagesAdapter);
//
//        userInfoDisplay(profildePic);
//        getUsers();
//
//    }
//
//    private void getUsers() {
//        userCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    QuerySnapshot querySnapshot = task.getResult();
//
//                    if (querySnapshot != null) {
//                        messagesList.clear();
//                        unseenMessages = 0;
//                        lastMessage = "";
//                        chatKey = "";
//
//                        for (DocumentSnapshot documentSnapshot: querySnapshot.getDocuments()){
//
//                            final String getUID = documentSnapshot.getId();
//
//                            dataSet = false;
//
//                            if (!getUID.equals(localStorage.getUid())){
//                                final String getUsername = documentSnapshot.get("username").toString();
//                                final String getUserProfile = "";
//
////                                if (!(documentSnapshot.get("profilePic").toString()).isEmpty()){
////                                    getUserProfile = documentSnapshot.get("profilePic").toString();
////                                }
//
//
//
//                                chatCollection.child("chat").addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                        int getChatCounts = (int)snapshot.getChildrenCount();
//                                        Toast.makeText(Messages.this, getChatCounts, Toast.LENGTH_SHORT).show();
//                                        if (getChatCounts > 0){
//                                            Toast.makeText(Messages.this, getChatCounts, Toast.LENGTH_SHORT).show();
//                                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
//
//                                                final String getKey = dataSnapshot1.getKey();
//                                                chatKey = getKey;
//                                                final  String getUserOne = dataSnapshot1.child("user_1").getValue(String.class);
//                                                final  String getUserTwo = dataSnapshot1.child("user_2").getValue(String.class);
//
//                                                if (dataSnapshot1.hasChild("user_1") && dataSnapshot1.hasChild("user_2") && dataSnapshot1.hasChild("messages")){
//
//
//                                                    if ((getUserOne.equals(getUID)) && getUserTwo.equals(localStorage.getUid()) || (getUserOne.equals(localStorage.getUid()) && getUserTwo.equals(getUID))){
//
//                                                        for (DataSnapshot chatDataSnapshot : dataSnapshot1.child("messages").getChildren()){
//
//                                                            final long getMsgKey = Long.parseLong(chatDataSnapshot.getKey());
//                                                            final long getLastSeenMsg = Long.parseLong(MemoryData.getLastMsgTs(Messages.this, getKey));
//
//                                                            lastMessage = chatDataSnapshot.child("msg").getValue(String.class);
//                                                            if (getMsgKey > getLastSeenMsg){
//                                                                unseenMessages++;
//                                                            }
//
//                                                        }
//
//                                                    }
//
//                                                }
//
//                                            }
//
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//                                if (!dataSet){
//                                    dataSet = true;
//                                    MessagesList messagesLists = new MessagesList(getUsername, getUID, lastMessage, unseenMessages, getUserProfile, chatKey);
//                                    messagesList.add(messagesLists);
//                                    messagesAdapter.updateData(messagesList);
//                                }
//                            }
//                        }
////                        msglistView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
////                        messagesAdapter = new MessagesAdapter(getApplicationContext(), messagesList);
////                        msglistView.upda(messagesAdapter);
//
//                        Log.d(TAG, "DocumentSnapshot data: " + querySnapshot.getDocuments());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }
//
//    private void userInfoDisplay(CircleImageView profildePic) {
//        DocumentReference UserRef;
//        UserRef = FirebaseFirestore.getInstance().collection("Users").document(localStorage.getUid());
//
//        UserRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    if (document.exists()) {
//
//                        UserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                            @SuppressLint("ResourceAsColor")
//                            @Override
//                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                Users users = documentSnapshot.toObject(Users.class);
//
//                                if (users.getImage() == null) {
//                                    Log.w(TAG, "No Profile Image");
//                                } else {
//                                    Picasso.get().load(users.getImage()).into(profildePic);
//                                }
//
//
//                            }
//
//                        });
//
//
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }
//}