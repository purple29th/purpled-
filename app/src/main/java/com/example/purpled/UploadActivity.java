package com.example.purpled;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.myUploads.MyUploadsActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UploadActivity extends AppCompatActivity {
    boolean trackPrivacy;
    private ImageView trackImg, uploadBtn, backBtn;
    private EditText trackTitle, trackDesc;
    private static final int AudioPick = 1, ImagePick = 2;
    private TextView selectTrack;
    private Uri AudioUri, ImageUri;
    private String tracktitle, downloadImageUrl, genre, privacy, description, saveCurrentDate, saveCurrentTime, saveCurrentDate2, saveCurrentTime2, TrackRandomKey, downloadTrackUrl;
    private ProgressDialog progressDialog;
    LocalStorage localStorage;
    private StorageReference TrackRefAudio, TrackRefImage;
    private CollectionReference firestoreColl;
    private Spinner genrelist;
    private ArrayAdapter<CharSequence> adapter;
    private Button myUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        progressDialog = new ProgressDialog(this);
        localStorage = new LocalStorage(this);
        TrackRefAudio = FirebaseStorage.getInstance().getReference().child("Audio");
        TrackRefImage = FirebaseStorage.getInstance().getReference().child("Images");

        trackImg = findViewById(R.id.track_cover_img);
        trackTitle = findViewById(R.id.track_title);
        trackDesc = findViewById(R.id.track_desc);
        uploadBtn = findViewById(R.id.upload_track_Btn);
        genrelist = findViewById(R.id.track_genre);
        backBtn = findViewById(R.id.back_btn);
        selectTrack = findViewById(R.id.track_uri);
        myUploads = findViewById(R.id.my_uploads_btn);

        myUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UploadActivity.this, MyUploadsActivity.class);
                startActivity(intent);
            }
        });

        adapter = ArrayAdapter.createFromResource(this, R.array.genres, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

        selectTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectYourTrack();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        trackImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectYourImage();

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateTrack();
            }
        });


        genrelist.setAdapter(adapter);

    }

    private void selectYourImage() {
        Intent imageintent = new Intent();
        imageintent.setAction(Intent.ACTION_GET_CONTENT);
        imageintent.setType("image/*");
        startActivityForResult(imageintent, ImagePick);
    }

    private void selectYourTrack() {
        Intent audiointent = new Intent();
        audiointent.setAction(Intent.ACTION_GET_CONTENT);
        audiointent.setType("audio/*");
        startActivityForResult(audiointent, AudioPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AudioPick && resultCode == RESULT_OK && data != null) {
            AudioUri = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(AudioUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            selectTrack.setText(returnCursor.getString(nameIndex));


        }
        if (requestCode == ImagePick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            trackImg.setImageURI(ImageUri);

        }

    }

    private void validateTrack() {
        tracktitle = trackTitle.getText().toString();
        description = trackDesc.getText().toString();
        genre = genrelist.getSelectedItem().toString();

        if (AudioUri == null) {
            Toast.makeText(this, "You have not selected any audio file", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please write track description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(tracktitle)) {
            Toast.makeText(this, "Please write reack title", Toast.LENGTH_SHORT).show();
        } else if (genrelist == null) {
            Toast.makeText(this, "Please select genre", Toast.LENGTH_SHORT).show();
        } else if (ImageUri == null) {
            Toast.makeText(this, "You have not selected any image file", Toast.LENGTH_SHORT).show();
        } else {
            uploadTrack();

        }
    }

    private void uploadTrack() {
        progressDialog.setTitle("Adding new track");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Please wait while we store your new track");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("ddMMyyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HHmmss");
        saveCurrentTime = currentTime.format(calendar.getTime());

        SimpleDateFormat currentDate2 = new SimpleDateFormat("dd MMM yy");
        saveCurrentDate2 = currentDate2.format(calendar.getTime());

        SimpleDateFormat currentTime2 = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime2 = currentTime2.format(calendar.getTime());

        TrackRandomKey = localStorage.getUid() + saveCurrentDate + saveCurrentTime;

        final StorageReference filepath = TrackRefAudio.child(AudioUri.getLastPathSegment() + TrackRandomKey + ".audio");

        final UploadTask uploadTask = filepath.putFile(AudioUri);

        final StorageReference filepath2 = TrackRefImage.child(ImageUri.getLastPathSegment() + TrackRandomKey + ".jpg");

        final UploadTask uploadTask2 = filepath2.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(UploadActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
                int currentprogress = (int) progress;
                progressDialog.setProgress(currentprogress);
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask2.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String message = e.toString();
                        Toast.makeText(UploadActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(UploadActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    throw task.getException();

                                }

                                downloadImageUrl = filepath2.getDownloadUrl().toString();
                                return filepath2.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    downloadImageUrl = task.getResult().toString();

//                            Toast.makeText( UploadActivity.this, "Got the reack Url Successfully...", Toast.LENGTH_SHORT ).show();

                                    SavedTrackToDatabase();
                                }
                            }
                        });
                    }
                });

//                Toast.makeText( UploadActivity.this, "Product image uploaded successfully", Toast.LENGTH_SHORT ).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            Toast.makeText(UploadActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            throw task.getException();

                        }

                        downloadTrackUrl = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadTrackUrl = task.getResult().toString();

//                            Toast.makeText( UploadActivity.this, "Got the reack Url Successfully...", Toast.LENGTH_SHORT ).show();

                            SavedTrackToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SavedTrackToDatabase() {
        HashMap<String, Object> trackMap = new HashMap<>();
        trackMap.put("track_image", downloadImageUrl);
        trackMap.put("tid", TrackRandomKey);
        trackMap.put("date", saveCurrentDate2);
        trackMap.put("time", saveCurrentTime2);
        trackMap.put("description", description);
        trackMap.put("track_url", downloadTrackUrl);
        trackMap.put("genre", genre);
        trackMap.put("privacy", privacy);
        trackMap.put("uid", localStorage.getUid());
        trackMap.put("track_name", tracktitle);


        firestoreColl = FirebaseFirestore.getInstance().collection("tracks");
        firestoreColl.document(TrackRandomKey).set(trackMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "Track added successfully..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                String message = e.getMessage();
                Toast.makeText(UploadActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.public_btn:
                if (checked)
                    trackPrivacy = false;
                privacy = "public";
                break;
            case R.id.private_btn:
                if (checked)
                    trackPrivacy = true;
                privacy = "private";

                break;
        }
    }
}