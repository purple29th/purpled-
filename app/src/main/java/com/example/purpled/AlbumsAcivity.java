package com.example.purpled;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.purpled.album.AlbumAdapter;
import com.example.purpled.album.AlbumListClass;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.paperdb.Paper;
import timber.log.Timber;

public class AlbumsAcivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    List<AlbumListClass> albumListClasses;
    private AlbumAdapter albumAdapter;
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
    private Button profileBtn;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onResume() {
        super.onResume();


        userInfoDisplay(profileImageView, userName);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums_acivity);

        Paper.init(this);
        localStorage = new LocalStorage(this);
        mediaPlayer = new MediaPlayer();

        songTitle = findViewById(R.id.track_title);
        songAuthor = findViewById(R.id.track_artist);
        songImg = findViewById(R.id.track_img);
        loading = findViewById(R.id.loading_container);


        albumListClasses = new ArrayList<>();

        mToolbar = findViewById(R.id.nav_action);
        setSupportActionBar(mToolbar);
        listView = findViewById(R.id.song_list_recyclerView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(AlbumsAcivity.this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

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

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlbumsAcivity.this, UserProfile.class);
                startActivity(intent);
            }
        });

        checkSpotify();

    }

    private void checkSpotify() {
        String url = "https://spotify23.p.rapidapi.com/albums/?ids=20r762YmB5HeofjMCiPMLv%2C1VAc77UvK5wj8ZSWCo3V2b%2C5sY6UIQ32GqwMLAfSNEaXb%2C64nbgEEIcY4g1ElVLONJ0w%2C7iOAJaGBmk67o337zaqt0R%2C7GoZNNb7Yl74fpk8Z6I2cv%2C2T8UlI17u5hwTqu6zkpkW7%2C1ATL5GLyefJaxhQzSPVrLX%2C6kgDkAupBVRSqbJPUaTJwQ%2C79ONNoS4M9tfIA1mYLBYVX%2C3IBcauSj5M2A6lTeffJzdv";

        new Thread(new Runnable() {
            @Override
            public void run() {
                Http http = new Http(AlbumsAcivity.this, url);
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
                                JSONArray getItems = response.getJSONArray("albums");


                                //iterating through the array list
                                for (int i = 0; i < getItems.length(); i++) {
                                    JSONObject objectArray = getItems.getJSONObject(i);

//                                    JSONObject track = objectArray.getJSONObject("tracks");
                                    String trackamnt = String.valueOf(objectArray.getInt("total_tracks"));
                                    String albumname = objectArray.getString("name");
                                    String year = objectArray.getString("release_date").substring(0, 4);
                                    JSONArray artist = objectArray.getJSONArray("artists");
                                    JSONArray imagearray = objectArray.getJSONArray("images");


                                    //setting track details to SongList class
                                    AlbumListClass albumListClass = new AlbumListClass();
                                    albumListClass.setTrackAlbumName(albumname);
                                    albumListClass.setTractAmount(trackamnt);
                                    albumListClass.setTrackUrl(year);

//                                    if (i == 0) {
//
////                                        localStorage.setTrackTitle(track.getString("name"));
//                                        tracktitle = track.getString("name");
//                                        trackurl = track.getString("preview_url");
////                                        localStorage.setTrackUrl(track.getString("preview_url"));
//
//
//                                        for (int u = 0; u < artist.length(); u++) {
//                                            if (u == 0) {
//                                                JSONObject aristsname = artist.getJSONObject(u);
////                                                localStorage.setTrackArtist(aristsname.getString("name"));
//
//                                                trackartist = aristsname.getString("name");
//                                            }
//
//                                        }
//
//                                        for (int a = 0; a < imagearray.length(); a++) {
//                                            if (a == 0) {
//                                                JSONObject url = imagearray.getJSONObject(a);
////                                                localStorage.setTrackImage(url.getString("url"));
//                                                trackimg = url.getString("url");
//                                            }
//
//                                        }
//                                    }

                                    for (int u = 0; u < artist.length(); u++) {
                                        if (u == 0) {
                                            JSONObject aristsname = artist.getJSONObject(u);
                                            albumListClass.setTrackArtist(aristsname.getString("name"));
                                        }

                                    }

                                    for (int a = 0; a < imagearray.length(); a++) {
                                        if (a == 0) {
                                            JSONObject url = imagearray.getJSONObject(a);
                                            albumListClass.setTrackImage(url.getString("url"));

                                        }

                                    }

                                    albumListClasses.add(albumListClass);
                                }
                                listView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                                albumAdapter = new AlbumAdapter(getApplicationContext(), albumListClasses);
                                listView.setAdapter(albumAdapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(AlbumsAcivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else if (code == 422) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(AlbumsAcivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == 401) {
                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(AlbumsAcivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (code == 403) {

                            try {
                                JSONObject response = new JSONObject(http.getResponse());
                                String msg = response.getString("message");
                                Toast.makeText(AlbumsAcivity.this, msg, Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(AlbumsAcivity.this, "Error " + code, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
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
                Intent intent = new Intent(AlbumsAcivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.timeline:
                Intent timeline = new Intent(AlbumsAcivity.this, TimeLine.class);
                startActivity(timeline);
                break;

            case R.id.albums:
                Intent album = new Intent(AlbumsAcivity.this, AlbumsAcivity.class);
                startActivity(album);
                finish();
                break;

            case R.id.local_music:
                Intent localmusic = new Intent(AlbumsAcivity.this, LocalMusic.class);
                startActivity(localmusic);
                finish();
                break;

            case R.id.upload:
                Intent upload = new Intent(AlbumsAcivity.this, UploadActivity.class);
                startActivity(upload);
                break;

            case R.id.genre:
                Intent recommendations = new Intent(AlbumsAcivity.this, RecommendationsNav.class);
                startActivity(recommendations);
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                googleSignInClient.signOut();

                Paper.book().destroy();
                Intent logout = new Intent(AlbumsAcivity.this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                finish();
                break;



        }
        return false;
    }
}