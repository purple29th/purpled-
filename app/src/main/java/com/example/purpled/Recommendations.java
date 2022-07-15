package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class Recommendations extends AppCompatActivity {
    TextView helloTxt;
    Button hipHop, rnb, reggae, soul, country, afro, skipBtn;
    LocalStorage localStorage;
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        if (!sharedpreferences.getBoolean(localStorage.getOneTimeState(), false)) {
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
        } else {
            moveToSecondary();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations);

        localStorage = new LocalStorage(this);

        helloTxt = findViewById(R.id.hello_txt);
        hipHop = findViewById(R.id.hiphop);
        soul = findViewById(R.id.soul);
        afro = findViewById(R.id.afro);
        country = findViewById(R.id.country);
        rnb = findViewById(R.id.rNb);
        reggae = findViewById(R.id.reggae);
        skipBtn = findViewById(R.id.skip_btn);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre("https://spotify23.p.rapidapi.com/playlist_tracks/?id=1Hz7FY5h1wbKgSm2ISJkFS&offset=0&limit=100");
                finish();
            }
        });

        reggae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre("https://spotify23.p.rapidapi.com/playlist_tracks/?id=7J09i0Z2lhcl4yKgnDVWvp&offset=0&limit=100");
                finish();
            }
        });

        rnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre( "https://spotify23.p.rapidapi.com/playlist_tracks/?id=35lZKb4s9WJYHZErKDqrtN&offset=0&limit=100");
                finish();
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre("https://spotify23.p.rapidapi.com/playlist_tracks/?id=6xQh0a4iRnjLJjUfnYc1Im&offset=0&limit=100");
                finish();
            }
        });

        afro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre("https://spotify23.p.rapidapi.com/playlist_tracks/?id=6AlvY4xsfwl8ZDFwbBhIYL&offset=0&limit=100");
                finish();
            }
        });

        soul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre("https://spotify23.p.rapidapi.com/playlist_tracks/?id=3JjkeMIa3kE2sx3JwHf0hD&offset=0&limit=100");
                finish();
            }
        });

        hipHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(localStorage.getOneTimeState(), Boolean.TRUE);
                editor.apply();
                Intent intent = new Intent(Recommendations.this, HomeActivity.class);
                startActivity(intent);
                localStorage.setMyGenre( "https://spotify23.p.rapidapi.com/playlist_tracks/?id=1Hz7FY5h1wbKgSm2ISJkFS&offset=0&limit=100");
                finish();
            }
        });



        userInfoDisplay(helloTxt);
    }

    private void userInfoDisplay(TextView helloTxt) {
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
                                String username = (String) documentSnapshot.get("username");

                                if (username == null) {
                                    Log.w(TAG, "No UserName");
                                } else {
                                    helloTxt.setText("Hello " + username + ", " + "choose your favourite genre");
                                    localStorage.setUserName(username);
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

    public void moveToSecondary() {
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
