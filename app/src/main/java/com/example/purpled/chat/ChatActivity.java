package com.example.purpled.chat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.LocalStorage;
import com.example.purpled.LoginActivity;
import com.example.purpled.Messages;
import com.example.purpled.R;
import com.example.purpled.RegisterActivity;
import com.example.purpled.data.MemoryData;
import com.example.purpled.model.MessagesList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    ImageView backBtn, sendBtn;
    CircleImageView userDP;
    EditText messageInput;
    TextView userName, onlineId;
    String username, dp;
    String chatKey;
    private int generatedChatKey;
    String getUID, getDate, getTime;
    LocalStorage localStorage;
    private RecyclerView chattingView;
    private List<ChatList> chatLists;
    private ChatAdapter chatAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private boolean loadingfirstTime = true;
    private boolean online = false;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    private boolean isOnline(boolean b) {
        try {
            ConnectivityManager cm = (ConnectivityManager) ChatActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onResume() {
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);

                isOnline(true);
                Date date = new Date();

                final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
                final SimpleDateFormat sdf2 = new SimpleDateFormat("HH.mm");

                if (getTime.equals(sdf2.format(date))) {
                    if (getDate.equals(sdf1.format(date))) {

                        onlineId.setVisibility(View.VISIBLE);
                        onlineId.setText("Online22");
                    }
                } else {
                    onlineId.setVisibility(View.GONE);
                }

                if (isOnline(true)) {
                    online = isOnline(true);
                }
                updateOnline();

            }
        }, delay);
        super.onResume();

    }

    private void updateOnline() {
        Date date = new Date();
//        Timestamp timestamp2 = new Timestamp(date.getTime());
        final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy.MM.dd");
        final SimpleDateFormat sdf2 = new SimpleDateFormat("HH.mm");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("online", online);
        userMap.put("date", sdf1.format(date));
        userMap.put("time", sdf2.format(date));

        databaseReference.child("users").child(localStorage.getUid()).child("userState").updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error writing document", e);
            }
        });
    }


    @Override
    public void onPause() {
        super.onPause();
        isOnline(false);
        if (isOnline(false)) {
            handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acivity);

        localStorage = new LocalStorage(this);
        chatLists = new ArrayList<>();
        getTime = getIntent().getStringExtra("time");
        getDate = getIntent().getStringExtra("date");
        backBtn = findViewById(R.id.back_btn);
        sendBtn = findViewById(R.id.sendBtn);
        userDP = findViewById(R.id.profilePic);
        messageInput = findViewById(R.id.input_msg);
        userName = findViewById(R.id.userName);
        onlineId = findViewById(R.id.online);
        username = getIntent().getStringExtra("name");
        dp = getIntent().getStringExtra("profile_pic");
        chatKey = getIntent().getStringExtra("chat_key");
        getUID = getIntent().getStringExtra("uid");


        chattingView = findViewById(R.id.chattingView);

        chatAdapter = new ChatAdapter(this, chatLists);
        chattingView.setHasFixedSize(true);
        chattingView.setLayoutManager(new LinearLayoutManager(this));
        chattingView.setAdapter(chatAdapter);

        userName.setText(username);
        onlineId.setText("Online22");

//        Picasso.get().load(dp).into(userDP);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (chatKey.isEmpty()) {
                    chatKey = "1";
                    if (snapshot.hasChild("chat")) {
                        chatKey = String.valueOf(snapshot.child("chat").getChildrenCount() + 1);
                    }
                }

                if (snapshot.hasChild("chat")) {

                    if (snapshot.child("chat").child(chatKey).hasChild("messages")) {

                        chatLists.clear();


                        for (DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()) {

                            if (messageSnapshot.hasChild("msg") && messageSnapshot.hasChild("uid")) {
                                final String messageTimeStamp = messageSnapshot.getKey();
                                final String getUid = messageSnapshot.child("uid").getValue(String.class);
                                final String getMsg = messageSnapshot.child("msg").getValue(String.class);
                                final String getTime = messageSnapshot.child("time").getValue(String.class);
                                final String getDate = messageSnapshot.child("date").getValue(String.class);


                                ChatList chatList = new ChatList(getUid, username, getMsg, getDate, getTime);
                                chatLists.add(chatList);
                                chatAdapter.updateChatList(chatLists);

                                if (loadingfirstTime || Long.parseLong(messageTimeStamp) > Long.parseLong(MemoryData.getLastMsgTs(ChatActivity.this, chatKey))) {
                                    loadingfirstTime = false;


                                    MemoryData.saveLastMsg(messageTimeStamp, chatKey, ChatActivity.this);
                                    chatAdapter.updateChatList(chatLists);

                                    chattingView.scrollToPosition(chatLists.size() - 1);

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

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(messageInput.getText())) {
                    Toast.makeText(ChatActivity.this, "Your text box is empty", Toast.LENGTH_SHORT).show();
                } else {
                    final String getTxtMsg = messageInput.getText().toString();
                    final String currentTimeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

                    MediaPlayer music = MediaPlayer.create(ChatActivity.this, R.raw.message_sent);
                    music.start();

                    Date date = new Date();

                    final SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyy");
                    final SimpleDateFormat sdf2 = new SimpleDateFormat("HH.mm aa");

                    databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUID);
                    databaseReference.child("chat").child(chatKey).child("user_2").setValue(localStorage.getUid());
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("msg").setValue(getTxtMsg);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("uid").setValue(getUID);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("time").setValue(sdf2.format(date));
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("date").setValue(sdf1.format(date));

                    messageInput.setText("");
                    chattingView.scrollToPosition(chatLists.size()-1);
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}