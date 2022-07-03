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

        trackUrl = intent.getStringExtra("trackUrl");
        trackName = intent.getStringExtra("trackTitle");
        trackArtist = intent.getStringExtra("trackArtist");


        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextbtn);
        prevBtn = findViewById(R.id.prevbtn);
        imageView = findViewById(R.id.play_image);
        barVisualizer = findViewById(R.id.wave);
        songName = findViewById(R.id.play_song_title);
        songTimeStart = findViewById(R.id.song_start);
        songTimeEnd = findViewById(R.id.song_end);
        Picasso.get().load(intent.getStringExtra("trackImage")).into(imageView);

        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }


        mediaPlayer = new MediaPlayer();
        mediaPlayer.start();


        songName.setText(trackName + " by " + trackArtist);

        seekMusicBar = findViewById(R.id.seekbar);

//        if (mediaPlayer != null){
//            Toast.makeText(this, "not null", Toast.LENGTH_SHORT).show();
//        }
//
//        if (mediaPlayer.isPlaying()) {
//            Toast.makeText(this, "is playing", Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this, "not playing", Toast.LENGTH_SHORT).show();
//        }

        stopAudio();

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

//        nextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                position = ((position+1)%mySongs.size());
//                Uri uri1 = Uri.parse(mySongs.get(position).toString());
//                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri1);
//                songTitle = mySongs.get(position).getName().toString().replace(".mp3", "")
//                        .replace(".wav", "");
//                songName.setText(songTitle);
//                mediaPlayer.start();
//
//                String endTime = createTime(mediaPlayer.getDuration());
//                songTimeEnd.setText(endTime);
//
////                localStorage.setPos(String.valueOf(position));
////                localStorage.setSongtitle(songTitle);
//
//                int audiosessionId = mediaPlayer.getAudioSessionId();
//                if (audiosessionId != -1){
//                    barVisualizer.setAudioSessionId(audiosessionId);
//                }
//
//                startAnimation(imageView, 360f);
//            }
//        });






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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                seekMusicBar.setProgress(0);
//               playBtn.setBackgroundResource(R.drawable.ic_play_white);
               songTimeStart.setText("0");
               songTimeEnd.setText("0");
               mediaPlayer.reset();
               prepareMediaPlayer();
            }
        });
    }

    private void stopAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void prepareMediaPlayer() {
                try {
                    mediaPlayer.setDataSource(trackUrl);
                    mediaPlayer = mediaPlayer;
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