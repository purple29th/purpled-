package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private ImageView backBtn;
    private CircleImageView profilePic;
    private EditText username, phoneNumber;
    private Button updateBtn;
    private LocalStorage localStorage;
    private ProgressDialog progressDialog;
    private static int ImagePick = 1;
    private boolean isImagePicked = false;
    private Uri ImageUri;
    private String usernameee, dp, phone;
    private StorageReference TrackRefImage;
    String downloadImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        localStorage = new LocalStorage(this);
        progressDialog = new ProgressDialog( this );

        backBtn = findViewById(R.id.back_btn);
        profilePic = findViewById(R.id.user_profile);
        username = findViewById(R.id.user_name);
        phoneNumber = findViewById(R.id.phone_number);
        updateBtn = findViewById(R.id.updatebtn);

        TrackRefImage = FirebaseStorage.getInstance().getReference().child("ProfilePic");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAccount();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDp();
            }
        });

        getUserInfo();

    }

    private void getUserInfo() {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("Users").document(localStorage.getUid());

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @SuppressLint("ResourceAsColor")
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String usernamee = documentSnapshot.getString("username");
                                String userImg = documentSnapshot.getString("userProfile");
                                String userphone = documentSnapshot.getString("phoneNumber");

                                if (username == null) {
                                    Log.w(TAG, "No UserName");
                                } else {
                                    username.setHint("@"+usernamee);
                                    usernameee = usernamee;
                                }

                                if (userphone == null) {
                                    Log.w(TAG, "No phone number");
                                } else {
                                    phoneNumber.setHint(userphone);
                                    phone = userphone;
                                }

                                if (userImg == null) {
                                    Log.w(TAG, "No Profile Image");
                                } else {
                                    Picasso.get().load(userImg).into(profilePic);
                                    ImageUri = Uri.parse(userImg);
                                    dp = userImg;
                                }


                            }

                        });


                        Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());
                    }
                }
            }
        });
    }

    private void chooseDp() {
        Intent imageintent = new Intent();
        imageintent.setAction(Intent.ACTION_GET_CONTENT);
        imageintent.setType("image/*");
        startActivityForResult(imageintent, ImagePick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ImagePick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            profilePic.setImageURI(ImageUri);

            isImagePicked = true;

        }

    }

    private void updateAccount() {
        String usernamee = username.getText().toString();
        String phonee = phoneNumber.getText().toString();

        if (TextUtils.isEmpty(usernamee)){
            usernamee = usernameee;
        }
        if (TextUtils.isEmpty(phonee)){
            phonee = phone;
        }
        progressDialog.setTitle( "Updating your account" );
        progressDialog.setMessage( "Please wait...." );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.show();

        update(usernamee, phonee, profilePic);
    }

    private void update(String usernamee, String phonee, CircleImageView profilePic) {

        String uid = localStorage.getUid();

        if (isImagePicked){
            final StorageReference filepath = TrackRefImage.child(ImageUri.getLastPathSegment() + uid + ".jpg");

            final UploadTask uploadTask = filepath.putFile(ImageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(UserProfile.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Toast.makeText(UserProfile.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                throw task.getException();

                            }

                            downloadImageUrl = filepath.getDownloadUrl().toString();
                            return filepath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl = task.getResult().toString();

//                            Toast.makeText( UploadActivity.this, "Got the reack Url Successfully...", Toast.LENGTH_SHORT ).show();

                                SaveToDatabase( usernamee, phonee);
                            }
                        }
                    });
                }
            });
        }else{
            SaveToDatabaseWithoutImg(usernamee, phonee);
        }
    }

    private void SaveToDatabaseWithoutImg(String usernamee, String phonee) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", usernamee);
        userMap.put("phoneNumber", phonee);

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Users");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    collectionReference.document(localStorage.getUid()).update(userMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    db.child("users").child(localStorage.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            finish();
                                            Toast.makeText(UserProfile.this, "Update was successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                                   }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void SaveToDatabase(String usernamee, String phonee) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("username", usernamee);
        userMap.put("phoneNumber", phonee);
        userMap.put("userProfile", downloadImageUrl);

        CollectionReference collectionReference = FirebaseFirestore.getInstance()
                .collection("Users");
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    collectionReference.document(localStorage.getUid()).update(userMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    db.child("users").child(localStorage.getUid()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            finish();
                                            Toast.makeText(UserProfile.this, "Update was successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                                    }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else{
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}

