package com.example.smartmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String[] allItems;
    private ListView mSongsList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSongsList = findViewById(R.id.songsList);

        appExternalStorageStoragePermission();

    }

    public void appExternalStorageStoragePermission()
    {
        Dexter.withActivity(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {

            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                displayAudioSongsName();
            }
            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }
            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }

        }).check();
        }

        public ArrayList<File> readOnlyAudioSongs(File file){

            ArrayList<File> arrayList = new ArrayList<>();
            File[] allFiles = file.listFiles();

            for (File indFile : allFiles)
            {
                if(indFile.isDirectory() && !indFile.isHidden())
                {
                    arrayList.addAll(readOnlyAudioSongs(indFile));
                }
                else if (indFile.getName().endsWith(".mp3")||indFile.getName().endsWith(".aac")||indFile.getName().endsWith(".wav")||indFile.getName().endsWith(".wma"))
                {
                    arrayList.add(indFile);
                }
            }

            return arrayList;
        }

    private void displayAudioSongsName() {

        final ArrayList<File> audioSongs=readOnlyAudioSongs(Environment.getExternalStorageDirectory());
        allItems = new String[audioSongs.size()];
        for(int i=0;i<audioSongs.size();i++)
        {
            allItems[i]=audioSongs.get(i).getName();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,allItems);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String songName = mSongsList.getItemAtPosition(position).toString();
                Intent intent = new Intent(MainActivity.this,SmartPlayerActivity.class);
                intent.putExtra("song",audioSongs);
                intent.putExtra("name",songName);
                intent.putExtra("position",position);

                startActivity(intent);
            }
        });
    }

}