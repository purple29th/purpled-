package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class RecommendationsNav extends AppCompatActivity {

    Button hipHop, rnb, reggae, soul, country, afro;
    String recommendationUpdate;
    LocalStorage localStorage;
    ProgressDialog progressDialog;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_nav);
        localStorage =  new LocalStorage(this);
        progressDialog = new ProgressDialog(this);

        hipHop = findViewById(R.id.hiphop);
        soul = findViewById(R.id.soul);
        afro = findViewById(R.id.afro);
        country = findViewById(R.id.country);
        rnb = findViewById(R.id.rNb);
        reggae = findViewById(R.id.reggae);
        backBtn = findViewById(R.id.back_btn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        reggae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=7J09i0Z2lhcl4yKgnDVWvp&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rnb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=35lZKb4s9WJYHZErKDqrtN&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=6xQh0a4iRnjLJjUfnYc1Im&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        afro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=6AlvY4xsfwl8ZDFwbBhIYL&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        soul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=3JjkeMIa3kE2sx3JwHf0hD&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        hipHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recommendationUpdate =  "https://spotify23.p.rapidapi.com/playlist_tracks/?id=1Hz7FY5h1wbKgSm2ISJkFS&offset=0&limit=100";
                localStorage.setMyGenre(recommendationUpdate);
                Intent intent = new Intent(RecommendationsNav.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}