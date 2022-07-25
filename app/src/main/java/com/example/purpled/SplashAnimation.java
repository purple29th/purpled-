package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SplashAnimation extends AppCompatActivity {

    Button  nextBtn;
    LocalStorage localStorage;
    SharedPreferences sharedpreferences;

    @Override
    protected void onResume() {
        super.onResume();
        sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

        if (!sharedpreferences.getBoolean(localStorage.getOneTimeState(), false)) {
            Log.d("wlc", "Welcome");
        } else {
            moveToSecondary();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_animation);
        localStorage = new LocalStorage(this);

        nextBtn = findViewById(R.id.nextbtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putBoolean(localStorage.getOneTimeState2(), Boolean.TRUE);
//                editor.apply();
                Intent intent = new Intent(SplashAnimation.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void moveToSecondary() {
        // use an intent to travel from one activity to another.
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}