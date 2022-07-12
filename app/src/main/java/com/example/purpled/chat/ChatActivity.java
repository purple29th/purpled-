package com.example.purpled.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.data.MemoryData;
import com.example.purpled.model.MessagesList;
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
    String getUID;
    LocalStorage localStorage;
    private RecyclerView chattingView;
    private List<ChatList> chatLists;
    private ChatAdapter chatAdapter;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private boolean loadingfirstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_acivity);

        localStorage = new LocalStorage(this);
        chatLists = new ArrayList<>();

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

                if (snapshot.hasChild("chat")){

                    if (snapshot.child("chat").child(chatKey).hasChild("messages")){

                        chatLists.clear();


                        for (DataSnapshot messageSnapshot : snapshot.child("chat").child(chatKey).child("messages").getChildren()){

                            if (messageSnapshot.hasChild("msg") && messageSnapshot.hasChild("uid")){
                                final String messageTimeStamp = messageSnapshot.getKey();
                                final  String getUid = messageSnapshot.child("uid").getValue(String.class);
                                final String getMsg = messageSnapshot.child("msg").getValue(String.class);

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                SimpleDateFormat stf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());

                                Timestamp timestamp = new Timestamp(Long.parseLong(messageTimeStamp));
                                Date date = new Date(timestamp.getTime());


                                ChatList chatList= new ChatList(getUid, username,getMsg,sdf.format(date),stf.format(date));
                                chatLists.add(chatList);
                                chatAdapter.updateChatList(chatLists);

                                if(loadingfirstTime || Long.parseLong(messageTimeStamp) > Long.parseLong(MemoryData.getLastMsgTs(ChatActivity.this, chatKey))){
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
                if (TextUtils.isEmpty(messageInput.getText())){
                    Toast.makeText(ChatActivity.this, "Your text box is empty", Toast.LENGTH_SHORT).show();
                }else{
                    final String getTxtMsg = messageInput.getText().toString();
                    final String currentTimeStamp = String.valueOf(System.currentTimeMillis()).substring(0, 10);

                    databaseReference.child("chat").child(chatKey).child("user_1").setValue(getUID);
                    databaseReference.child("chat").child(chatKey).child("user_2").setValue(localStorage.getUid());
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("msg").setValue(getTxtMsg);
                    databaseReference.child("chat").child(chatKey).child("messages").child(currentTimeStamp).child("uid").setValue(getUID);

                    messageInput.setText("");
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