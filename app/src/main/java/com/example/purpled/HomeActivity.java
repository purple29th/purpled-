package com.example.purpled;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.model.SongListClass;
import com.example.purpled.viewholder.SongListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    List<SongListClass> songListClasses;
    private SongListAdapter spotifyAdapter;
    TextView songTitle, songAuthor;
    RecyclerView listView;
    String[] items;
    LocalStorage localStorage;
    ImageButton homePlayBtn;
    private MediaPlayer mediaPlayer;
    private ImageView songImg;
    private String songUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);
        songTitle = findViewById(R.id.track_title);
        songAuthor = findViewById(R.id.track_artist);
        songImg = findViewById(R.id.track_img);
        homePlayBtn = findViewById(R.id.play_btn);

        localStorage = new LocalStorage(this);
        mediaPlayer = new MediaPlayer();

        songListClasses = new ArrayList<>();

        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        listView = findViewById(R.id.song_list_recyclerView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(HomeActivity.this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        askuserforpermission();
        homePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    homePlayBtn.setImageResource(R.drawable.ic_play_white);
                }else{
                    mediaPlayer.start();
                    homePlayBtn.setImageResource(R.drawable.ic_pause);
                }
            }
        });
    }

    public void askuserforpermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        checkSpotify();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
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

    private void checkSpotify() {
        String url = "https://spotify23.p.rapidapi.com/playlist_tracks/?id=1Hz7FY5h1wbKgSm2ISJkFS&offset=0&limit=100";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(HomeActivity.this, url);
                http.send();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Integer code = http.getStatusCode();

                        if (code == 200) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                JSONArray getSth = response.getJSONArray("items");

                                for (int i = 0; i < getSth.length(); i++) {
                                    JSONObject objectArray = getSth.getJSONObject(i);

                                    JSONObject track = objectArray.getJSONObject("track");
                                    JSONArray artist = track.getJSONArray("artists");
                                    JSONObject album = track.getJSONObject("album");
                                    JSONArray imagearray = album.getJSONArray("images");

                                    SongListClass spotifytracks = new SongListClass();


                                    spotifytracks.setTrackUrl(track.getString("preview_url"));
                                    spotifytracks.setTrackDuration(milliSecondsToTimer( Integer.parseInt(track.getString("duration_ms"))));
                                    spotifytracks.setTrackTitle(track.getString("name"));

                                    if (i == 0){
                                        songTitle.setText(track.getString("name"));
                                        try {
                                            mediaPlayer.setDataSource(track.getString("preview_url"));
                                            mediaPlayer.prepare();
                                        }catch (Exception e){
                                            Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        for (int u = 0; u < artist.length(); u++) {
                                            if (u == 0){
                                                JSONObject aristsname = artist.getJSONObject(u);
                                                songAuthor.setText(aristsname.getString("name"));
                                            }

                                        }

                                        for (int a = 0; a < imagearray.length(); a++) {
                                            if (a == 0){
                                                JSONObject url = imagearray.getJSONObject(a);
                                                Picasso.get().load(url.getString("url")).into(songImg);
                                            }

                                        }

                                        for (int a = 0; a < imagearray.length(); a++) {
                                            if (a == 0){
                                                JSONObject url = imagearray.getJSONObject(a);
                                                songUrl = url.getString("url");
                                            }

                                        }
                                    }

                                    spotifytracks.setTrackAlbumName(album.getString("name"));

                                    for (int u = 0; u < artist.length(); u++) {
                                        if (u == 0){
                                            JSONObject aristsname = artist.getJSONObject(u);
                                            spotifytracks.setTrackArtist(aristsname.getString("name"));
                                        }

                                    }

                                    for (int a = 0; a < imagearray.length(); a++) {
                                        if (a == 0){
                                            JSONObject url = imagearray.getJSONObject(a);
                                            spotifytracks.setTrackImage(url.getString("url"));

                                        }

                                    }

                                    songListClasses.add(spotifytracks);
                                }
                                listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                spotifyAdapter = new SongListAdapter(getApplicationContext(), songListClasses);
                                listView.setAdapter(spotifyAdapter);
                                listView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(HomeActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == 403) {

                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(HomeActivity.this, "Error " + code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }

    private void prepareMediaPlayer(String songurl){
        try {
            mediaPlayer.setDataSource(songurl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout:
                Paper.book().destroy();
                Intent logout = new Intent(HomeActivity.this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                finish();
                break;

        }
        return false;
    }
}