package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
                finish();
            }
        } ).start();
    }



    private void doWork() {
        for (int progress = 0; progress < 60; progress += 20) {
            try {
                Thread.sleep( 1000 );
                progressBar.setProgress( progress );
            } catch (Exception e) {
                e.printStackTrace();
                Timber.e( e.getMessage() );
            }
        }
    }

    private void startApp(){
        Intent homeintent = new Intent( SplashActivity.this, SplashAnimation.class );
        startActivity( homeintent );

    }
}