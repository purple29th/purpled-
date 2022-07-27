package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.purpled.Timeline.TimeLine;
import com.example.purpled.playlist.PlayListActivity;
import com.example.purpled.messages.Messages;
import com.example.purpled.model.SongListClass;
import com.example.purpled.viewholder.SongListAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import java.util.Objects;

import io.paperdb.Paper;
import timber.log.Timber;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    List<SongListClass> songListClasses;
    private SongListAdapter spotifyAdapter;
    TextView songTitle, songAuthor, userName;
    RecyclerView listView;
    String[] items;
    LocalStorage localStorage;
    ImageButton homePlayBtn;
    private static long back_pressed;
    public static MediaPlayer mediaPlayer;
    private ImageView songImg, profileImageView;
    private RelativeLayout homeplayer, loading;
    private String songUrl;
    private String tracktitle, trackartist, trackurl, trackimg, recommendation = "";
    private FloatingActionButton floatingActionButton;
    private Button profileBtn;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onResume() {
        super.onResume();

        if (!Objects.equals(localStorage.getTrackImage(), "")) {
            songTitle.setText(localStorage.getTrackTitle());
            songAuthor.setText(localStorage.getTrackArtist());
            Picasso.get().load(localStorage.getTrackImage()).into(songImg);
        }

        if (localStorage.getTrackUrl() != null) {
            try {
                mediaPlayer.setDataSource(localStorage.getTrackUrl());
                mediaPlayer.prepare();
            } catch (Exception e) {
                Timber.tag("Try Song Url").e(e.getMessage());
            }
        }


        userInfoDisplay(profileImageView, userName);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Paper.init(this);
        localStorage = new LocalStorage(this);
        mediaPlayer = new MediaPlayer();

        songTitle = findViewById(R.id.track_title);
        songAuthor = findViewById(R.id.track_artist);
        songImg = findViewById(R.id.track_img);
        homePlayBtn = findViewById(R.id.play_btn);
        homeplayer = findViewById(R.id.player);
        loading = findViewById(R.id.loading_container);

        recommendation = localStorage.getMyGenre();
        floatingActionButton = findViewById(R.id.fab);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, Messages.class);
                startActivity(intent);
            }
        });


        homePlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    homePlayBtn.setImageResource(R.drawable.ic_play_white);
                } else {
                    mediaPlayer.start();
                    homePlayBtn.setImageResource(R.drawable.ic_pause);
                }
            }
        });

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
        View hView = navigationView.getHeaderView(0);
        userName = (TextView) hView.findViewById(R.id.username);
        profileImageView = hView.findViewById(R.id.user_profile);
        profileBtn = hView.findViewById(R.id.profile_btn);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UserProfile.class);
                startActivity(intent);
            }
        });


        askuserforpermission();
        checkSpotify();
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }

    private void userInfoDisplay(ImageView profileImageView, TextView userName) {
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
                                String username = documentSnapshot.getString("username");
                                String userImg = documentSnapshot.getString("userProfile");

                                if (username == null) {
                                    Log.w(TAG, "No UserName");
                                } else {
                                    userName.setText("@" + username);
                                }
                                if (userImg == null) {
                                    Log.w(TAG, "No Profile Image");
                                } else {
                                    Picasso.get().load(userImg).into(profileImageView);
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

    private void checkSpotify() {
        String url = recommendation;

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
                            loading.setVisibility(View.GONE);
                            //Getting JSON response
                            try {
                                //Convert http response into jsonObject
                                JSONObject response = new JSONObject(http.getResponse());

                                //Getting Array list called items in the jsonObject(response)
                                JSONArray getItems = response.getJSONArray("items");


                                //iterating through the array list
                                for (int i = 0; i < getItems.length(); i++) {
                                    JSONObject objectArray = getItems.getJSONObject(i);

                                    JSONObject track = objectArray.getJSONObject("track");
                                    JSONArray artist = track.getJSONArray("artists");
                                    JSONObject album = track.getJSONObject("album");
                                    JSONArray imagearray = album.getJSONArray("images");

                                    //setting track details to SongList class
                                    SongListClass spotifytracks = new SongListClass();

                                    spotifytracks.setTrackUrl(track.getString("preview_url"));
                                    spotifytracks.setTrackDuration(milliSecondsToTimer(Integer.parseInt(track.getString("duration_ms"))));
                                    spotifytracks.setTrackTitle(track.getString("name"));

                                    if (i == 0) {

//                                        localStorage.setTrackTitle(track.getString("name"));
                                        tracktitle = track.getString("name");
                                        trackurl = track.getString("preview_url");
//                                        localStorage.setTrackUrl(track.getString("preview_url"));


                                        for (int u = 0; u < artist.length(); u++) {
                                            if (u == 0) {
                                                JSONObject aristsname = artist.getJSONObject(u);
//                                                localStorage.setTrackArtist(aristsname.getString("name"));

                                                trackartist = aristsname.getString("name");
                                            }

                                        }

                                        for (int a = 0; a < imagearray.length(); a++) {
                                            if (a == 0) {
                                                JSONObject url = imagearray.getJSONObject(a);
//                                                localStorage.setTrackImage(url.getString("url"));
                                                trackimg = url.getString("url");
                                            }

                                        }

//                                        for (int a = 0; a < imagearray.length(); a++) {
//                                            if (a == 0){
//                                                JSONObject url = imagearray.getJSONObject(a);
//                                                songUrl = url.getString("url");
//                                                trackurl =  url.getString("url");
//                                            }
//
//                                        }

                                        homeplayer.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(HomeActivity.this, ApiPlayer.class);
                                                startActivity(intent.putExtra("trackTitle", localStorage.getTrackTitle())
                                                        .putExtra("trackArtist", localStorage.getTrackArtist())
                                                        .putExtra("trackUrl", localStorage.getTrackUrl())
                                                        .putExtra("itemsArray", localStorage.getTracks())
                                                        .putExtra("trackImage", localStorage.getTrackImage()));
                                            }
                                        });
                                    }

                                    spotifytracks.setTrackAlbumName(album.getString("name"));

                                    for (int u = 0; u < artist.length(); u++) {
                                        if (u == 0) {
                                            JSONObject aristsname = artist.getJSONObject(u);
                                            spotifytracks.setTrackArtist(aristsname.getString("name"));
                                        }

                                    }

                                    for (int a = 0; a < imagearray.length(); a++) {
                                        if (a == 0) {
                                            JSONObject url = imagearray.getJSONObject(a);
                                            spotifytracks.setTrackImage(url.getString("url"));

                                        }

                                    }

                                    songListClasses.add(spotifytracks);
                                }
                                listView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                spotifyAdapter = new SongListAdapter(getApplicationContext(), songListClasses);
                                listView.setAdapter(spotifyAdapter);


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

            case R.id.timeline:
                Intent timeline = new Intent(HomeActivity.this, TimeLine.class);
                startActivity(timeline);
                break;

            case R.id.playlist:
                Intent playlist = new Intent(HomeActivity.this, PlayListActivity.class);
                startActivity(playlist);
                break;

            case R.id.albums:
                Intent album = new Intent(HomeActivity.this, AlbumsAcivity.class);
                startActivity(album);
                finish();
                break;

            case R.id.local_music:
                Intent localmusic = new Intent(HomeActivity.this, LocalMusic.class);
                startActivity(localmusic);
                finish();
                break;

            case R.id.upload:
                Intent upload = new Intent(HomeActivity.this, UploadActivity.class);
                startActivity(upload);
                break;

            case R.id.genre:
                Intent recommendations = new Intent(HomeActivity.this, RecommendationsNav.class);
                startActivity(recommendations);
                break;

            case R.id.logout:

                googleSignout();
                break;



        }
        return false;
    }

    private void googleSignout() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut();

        Paper.book().destroy();
        Intent logout = new Intent(HomeActivity.this, LoginActivity.class);
        logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(logout);
        finish();
    }
}