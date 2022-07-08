package com.example.purpled;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

public class LocalMusicPlayer extends AppCompatActivity {
    Button playBtn, nextBtn, prevBtn;
    TextView songName, songTimeStart, songTimeEnd;
    SeekBar seekMusicBar;
    private Toolbar mToolbar;

    BarVisualizer barVisualizer;
    String songTitle;
    ImageView imageView;
    public  static final  String EXTRA_Name = "song_name";
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;

    Thread updateSeekBar;
    LocalStorage localStorage;

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
        setContentView(R.layout.activity_local_music_player);

        localStorage = new LocalStorage(this);

        playBtn = findViewById(R.id.playBtn);
        nextBtn = findViewById(R.id.nextbtn);
        prevBtn = findViewById(R.id.prevbtn);

        songName = findViewById(R.id.play_song_title);
        songTimeStart = findViewById(R.id.song_start);
        songTimeEnd = findViewById(R.id.song_end);

        seekMusicBar = findViewById(R.id.seekbar);
        barVisualizer = findViewById(R.id.wave);

        imageView = findViewById(R.id.play_image);


        if (mediaPlayer != null){
            mediaPlayer.start();
            mediaPlayer.release();
        }

        Intent intent =  getIntent();
        Bundle bundle = intent.getExtras();

        mySongs = (ArrayList)bundle.getParcelableArrayList("songs");
        String sName = intent.getStringExtra("songTitle");

        position = bundle.getInt("pos", 0);

        songName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        songTitle = mySongs.get(position).getName().toString().replace(".mp3", "")
                .replace(".wav", "");
        songName.setText(songTitle);


        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekBar = new Thread(){
            @Override
            public void run() {
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try {

                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekMusicBar.setProgress(currentPosition);

                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        seekMusicBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();
        seekMusicBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.MULTIPLY);
        seekMusicBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.SRC_IN);

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

        String endTime = createTime(mediaPlayer.getDuration());
        songTimeEnd.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentTime = createTime(mediaPlayer.getCurrentPosition());
                songTimeStart.setText(currentTime);
                handler.postDelayed(this, delay);
            }
        }, delay);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    playBtn.setBackgroundResource(R.drawable.ic_play_white);
                    mediaPlayer.pause();
                }else{
                    playBtn.setBackgroundResource(R.drawable.ic_pause);
                    mediaPlayer.start();

                    TranslateAnimation moveAnim = new TranslateAnimation(-0,25, -0,75);
                    moveAnim.setInterpolator(new AccelerateInterpolator());
                    moveAnim.setDuration(600);
                    moveAnim.setFillEnabled(true);
                    moveAnim.setFillAfter(true);
                    moveAnim.setRepeatMode(Animation.REVERSE);
                    moveAnim.setRepeatCount(1);
                    imageView.startAnimation(moveAnim);
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position+1)%mySongs.size());
                Uri uri1 = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri1);
                songTitle = mySongs.get(position).getName().toString().replace(".mp3", "")
                        .replace(".wav", "");
                songName.setText(songTitle);
                mediaPlayer.start();

                String endTime = createTime(mediaPlayer.getDuration());
                songTimeEnd.setText(endTime);

//                localStorage.setPos(String.valueOf(position));
//                localStorage.setSongtitle(songTitle);

                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1){
                    barVisualizer.setAudioSessionId(audiosessionId);
                }

                startAnimation(imageView, 360f);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextBtn.performClick();
            }
        });

        int audiosessionId = mediaPlayer.getAudioSessionId();
        if (audiosessionId != -1){
            barVisualizer.setAudioSessionId(audiosessionId);
        }

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position = ((position-1)<0)?(mySongs.size()-1):position-1;
                Uri uri1 = Uri.parse(mySongs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),uri1);
                songTitle = mySongs.get(position).getName().toString().replace(".mp3", "")
                        .replace(".wav", "");
                songName.setText(songTitle);
//                localStorage.setPos(String.valueOf(position));
//                localStorage.setSongtitle(songTitle);
                mediaPlayer.start();

                String endTime = createTime(mediaPlayer.getDuration());
                songTimeEnd.setText(endTime);



                int audiosessionId = mediaPlayer.getAudioSessionId();
                if (audiosessionId != -1){
                    barVisualizer.setAudioSessionId(audiosessionId);
                }

                startAnimation(imageView, -360f);
            }
        });

    }

    public  void startAnimation (View view, Float degree){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation",
                0f, degree);
        objectAnimator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.start();
    }

    public String createTime(int duration){

        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time = time+min+":";
        if (sec<10){
            time+= "0";
        }
        time+=sec;
        return time;
    }
}