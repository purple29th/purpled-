package com.example.purpled.chat;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.LocalStorage;
import com.example.purpled.R;
import com.example.purpled.data.MemoryData;
import com.example.purpled.databinding.ActivityChatAcivityBinding;
import com.example.purpled.databinding.ActivityMessagesBinding;
import com.example.purpled.messages.MessagesAdapter;
import com.example.purpled.model.MessageModel;
import com.example.purpled.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    ImageView backBtn, sendBtn;
    CircleImageView userDP;
    EditText messageInput;
    TextView userName, onlineId;
    String username, dp;
    String chatKey;
    private int generatedChatKey;
    String getUID, getDate, getTime, userone, usertwo;
    LocalStorage localStorage;
    private RecyclerView chattingView;
    private List<ChatList> chatLists;
    private ChatAdapter chatAdapter;
    DatabaseReference databaseReferenceReceiver, databaseReferenceSender;

    private boolean loadingfirstTime = true;
    private boolean online = false;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    String receiverId;
    String senderRoom, receiverRoom;
    ActivityChatAcivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatAcivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        localStorage = new LocalStorage(this);
        chatAdapter = new ChatAdapter(this);
        binding.chattingView.setAdapter(chatAdapter);
        binding.chattingView.setLayoutManager(new LinearLayoutManager(this));

        receiverId = getIntent().getStringExtra("id");
        binding.userName.setText(getIntent().getStringExtra("username"));

        senderRoom = localStorage.getUid() + receiverId;
        receiverRoom = receiverId + localStorage.getUid();

        binding.online.setText("Online");
        databaseReferenceSender = FirebaseDatabase.getInstance().getReference().child("chats").child(senderRoom);
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference().child("chats").child(receiverRoom);


        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatAdapter.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    chatAdapter.add(messageModel);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //chat send button
        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.inputMsg.getText().toString();
                if (message.trim().length() > 0) {
                    sendMsg(message);
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendMsg(String message) {
//        String messageId = UUID.randomUUID().toString();
        Date date = new Date();
        final SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyy");
        final SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");

        final SimpleDateFormat sdf3 = new SimpleDateFormat("dd-MM-yyy");
        final SimpleDateFormat sdf4 = new SimpleDateFormat("HH:mm aa");


        final String messageId = sdf1.format(date)+sdf2.format(date);
        MessageModel messageModel = new MessageModel(messageId, localStorage.getUid(), message, receiverId, sdf3.format(date), sdf4.format(date));

        chatAdapter.add(messageModel);
        databaseReferenceSender.child(messageId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                binding.inputMsg.setText("");
                databaseReferenceReceiver.child(messageId).setValue(messageModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        MediaPlayer music = MediaPlayer.create(ChatActivity.this, R.raw.message_sent);
                        music.start();
                    }
                });
            }
        });

    }
}