package com.example.purpled;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class LocalMusic extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar mToolbar;
    ListView listView;
    String[] items;
    LocalStorage localStorage;
    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_music);

        Paper.init(this);

        localStorage = new LocalStorage(this);

        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.release();
        }

        mToolbar = findViewById(R.id.nav_action);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        listView = findViewById(R.id.listView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(LocalMusic.this, mDrawerLayout, R.string.nav_open, R.string.nav_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displaySong();
    }

    public ArrayList<File> findSongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files) {
            if (
                    singleFile.isDirectory() &&
                            !singleFile.isHidden()
            ) {
                arrayList.addAll(findSongs(singleFile));
            } else {
                if (singleFile.getName().endsWith(".mp3")) {
                    arrayList.add(singleFile);
                }


            }

        }
        return arrayList;
    }

    public void displaySong() {
        final ArrayList<File> mysongs = findSongs(Environment.getExternalStorageDirectory());
        items = new String[mysongs.size()];
        for (int i = 0; i < mysongs.size(); i++) {
            items[i] = mysongs.get(i).getName().toString().replace(".mp3", "")
                    .replace(".wav", "");
        }
        customAdapter customAdapter = new customAdapter();
        listView.setAdapter(customAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String songName = (String) listView.getItemAtPosition(i);

                Intent playeractivity = new Intent(LocalMusic.this, LocalMusicPlayer.class);
                startActivity(playeractivity.putExtra("songs", mysongs)
                        .putExtra("songTitle", songName)
                        .putExtra("pos", i));

                String songname = mysongs.get(i).getName().toString().replace(".mp3", "")
                        .replace(".wav", "");


            }
        });
    }

    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View listView = getLayoutInflater().inflate(R.layout.song_list_viewholder, null);
            TextView songTitle = listView.findViewById(R.id.list_title);
            songTitle.setSelected(true);
            songTitle.setText(items[i]);
            return listView;
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
                Intent intent = new Intent(LocalMusic.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.logout:
                Paper.book().destroy();
                Intent logout = new Intent(LocalMusic.this, LoginActivity.class);
                logout.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logout);
                finish();
                break;

        }
        return false;
    }
}