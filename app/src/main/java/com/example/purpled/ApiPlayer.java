package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiPlayer extends AppCompatActivity {

    private Button playBtn, nextBtn, prevBtn;
    private TextView songName, songTimeStart, songTimeEnd, songArtist;
    private SeekBar seekMusicBar;
    private Toolbar mToolbar;
    public static MediaPlayer mediaPlayer;
    LocalStorage localStorage;
    private Handler handler = new Handler();
    private String trackUrl, trackName, trackArtist, itemsArray;
    ImageView imageView;
    BarVisualizer barVisualizer;
    Thread updateSeekBar;

    @Override
    protected void onDestroy() {
        if (barVisualizer != null) {
            barVisualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_player);

        Intent intent = getIntent();
        localStorage = new LocalStorage(this);

        trackUrl = intent.getStringExtra("trackUrl");
        trackName = intent.getStringExtra("trackTitle");
        trackArtist = intent.getStringExtra("trackArtist");
        itemsArray = intent.getStringExtra("itemsArray");

        localStorage.setTrackArtist(intent.getStringExtra("trackArtist"));
        localStorage.setTrackImage( intent.getStringExtra("trackImage"));
        localStorage.setTrackTitle(intent.getStringExtra("trackTitle"));
        localStorage.setTrackUrl(intent.getStringExtra("trackUrl"));


        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextbtn);
        prevBtn = findViewById(R.id.prevbtn);
        imageView = findViewById(R.id.play_image);
        barVisualizer = findViewById(R.id.wave);
        songName = findViewById(R.id.play_song_title);
        songArtist = findViewById(R.id.play_song_artist);
        songTimeStart = findViewById(R.id.song_start);
        songTimeEnd = findViewById(R.id.song_end);
        Picasso.get().load(intent.getStringExtra("trackImage")).into(imageView);




        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }

        mediaPlayer = new MediaPlayer();

        songName.setText(trackName);
        songArtist.setText(trackArtist);

        seekMusicBar = findViewById(R.id.seekbar);

        prepareMediaPlayer();


        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playBtn.setBackgroundResource(R.drawable.ic_play_white);
                } else {
                    mediaPlayer.start();
                    playBtn.setBackgroundResource(R.drawable.ic_pause);
                    updateSeekbar();
                }
            }
        });

        seekMusicBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int playerPos = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playerPos);
                songTimeStart.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                seekMusicBar.setSecondaryProgress(i);
            }
        });
    }

    private void prepareMediaPlayer() {
        try {
            mediaPlayer.setDataSource(trackUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekbar();
            songTimeEnd.setText(milliSecondsToTimer(mediaPlayer.getDuration()));

            int audiosessionId = mediaPlayer.getAudioSessionId();
            if (audiosessionId != -1) {
                barVisualizer.setAudioSessionId(audiosessionId);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            songTimeStart.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekbar() {
        if (mediaPlayer.isPlaying()) {
            seekMusicBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    public void startAnimation(View view, Float degree) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation",
                0f, degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    private String milliSecondsToTimer(long milliSeconds) {
        String timerString = "";
        String secondsString;

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);


        if (hours > 0) {
            timerString = hours + ":";
        }

        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }
}