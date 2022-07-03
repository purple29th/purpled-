package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.squareup.picasso.Picasso;

public class ApiPlayer extends AppCompatActivity {

    private Button playBtn, nextBtn, prevBtn;
    private TextView songName, songTimeStart, songTimeEnd;
    private SeekBar seekMusicBar;
    private Toolbar mToolbar;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private String trackUrl, trackName, trackArtist;
    ImageView imageView;
    BarVisualizer barVisualizer;

    @Override
    protected void onDestroy() {
        if (barVisualizer != null){
            barVisualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_player);

        Intent intent =  getIntent();
        Bundle bundle = intent.getExtras();

//        mySongs = (ArrayList)bundle.getParcelableArrayList("songs");
        trackUrl = intent.getStringExtra("trackUrl");
        trackName = intent.getStringExtra("trackName");
        trackArtist = intent.getStringExtra("trackArtist");

//        position = bundle.getInt("pos", 0);

        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextbtn);
        prevBtn = findViewById(R.id.prevbtn);
        imageView = findViewById(R.id.play_image);
        barVisualizer = findViewById(R.id.wave);
        songName = findViewById(R.id.play_song_title);
        songTimeStart = findViewById(R.id.song_start);
        songTimeEnd = findViewById(R.id.song_end);
        Picasso.get().load(intent.getStringExtra("trackImage")).into(imageView);


        mediaPlayer = new MediaPlayer();
        songName.setText(trackName +" by " + trackArtist);

        seekMusicBar = findViewById(R.id.seekbar);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    playBtn.setBackgroundResource(R.drawable.ic_play_white);
                }else{
                    mediaPlayer.start();
                    playBtn.setBackgroundResource(R.drawable.ic_pause);
                    updateSeekbar();
                }
            }
        });

        seekMusicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        prepareMediaPlayer();
        int audiosessionId = mediaPlayer.getAudioSessionId();
        if (audiosessionId != -1){
            barVisualizer.setAudioSessionId(audiosessionId);
        }
    }

    private void prepareMediaPlayer(){
        try {
            mediaPlayer.setDataSource(trackUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekbar();
            songTimeEnd.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private  Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            songTimeStart.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekbar(){
        if (mediaPlayer.isPlaying()){
            seekMusicBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition()/ mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    public  void startAnimation (View view, Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation",
                0f, degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    private String milliSecondsToTimer(long milliSeconds){
        String timerString = "";
        String secondsString;

        int hours = (int)(milliSeconds/ (1000*60*60));
        int minutes = (int)(milliSeconds % (1000*60*60)) / (1000*60);
        int seconds = (int)((milliSeconds % (1000*60*60)) %(1000*60)/1000);


        if (hours >0){
            timerString = hours+ ":";
        }

        if (seconds<10){
            secondsString = "0" +seconds;
        }else {
            secondsString =""+seconds;
        }

        timerString = timerString +minutes+ ":"+secondsString;
        return timerString;
    }
}