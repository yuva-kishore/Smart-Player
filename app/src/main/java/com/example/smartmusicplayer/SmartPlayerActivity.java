package com.example.smartmusicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper = "";

    private ImageView playPauseBtn , nextBtn , prevBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;
    private Button voiceEnabledBtn;

    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String msongName;

    private String mode = "ON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_player);
        checkVoiceCommandPermission();


        playPauseBtn = findViewById(R.id.play_pause_btn);
        nextBtn = findViewById(R.id.next_btn);
        prevBtn = findViewById(R.id.prev_btn);
        imageView = findViewById(R.id.logo);
        lowerRelativeLayout= findViewById(R.id.lower);
        voiceEnabledBtn = findViewById(R.id.voice_on_btn);
        songNameTxt  = findViewById(R.id.songName);


        parentRelativeLayout = findViewById(R.id.heymyaan);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());


        validateAndStartPlaying();

        imageView.setBackgroundResource(R.drawable.play_logo);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsd) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle bundle) {

                ArrayList<String> matchesFound = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                if(matchesFound != null)
                {
                    keeper=matchesFound.get(0);
                    if(mode.equals("ON"))
                    {
                        if (keeper.equals("pause"))
                        {
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this," Command : "+ keeper ,Toast.LENGTH_LONG).show();
                        }
                        else
                        if (keeper.equals("play"))
                        {
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this," Command : "+ keeper ,Toast.LENGTH_LONG).show();
                        }
                        else
                        if (keeper.equals("next"))
                        {
                            playNextSong();
                            Toast.makeText(SmartPlayerActivity.this," Command : "+ keeper ,Toast.LENGTH_LONG).show();
                        }
                        else
                        if (keeper.equals("previous"))
                        {
                            playPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this," Command : "+ keeper ,Toast.LENGTH_LONG).show();
                        }
                    }

                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }


        });


        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        mediaPlayer.pause();
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;

                    case MotionEvent.ACTION_UP:
                        mediaPlayer.start();
                        speechRecognizer.stopListening();
                        break;

                }

                return false;
            }
        });

        voiceEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode.equals("ON"))
                {
                    mode="OFF";
                    voiceEnabledBtn.setText("Turn on hands free Mode");
                    lowerRelativeLayout.setVisibility(View.VISIBLE);
                }
                else
                {
                    mode="ON";
                    voiceEnabledBtn.setText("Turn off hands free Mode");
                    lowerRelativeLayout.setVisibility(View.GONE);
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPauseSong();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.getCurrentPosition()>0)
                {
                    playPreviousSong();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.getCurrentPosition()>0)
                {
                    playNextSong();
                }
            }
        });


    }

    private void validateAndStartPlaying()
    {
        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();


        mySongs = new ArrayList<>();
        mySongs = (ArrayList) bundle.getParcelableArrayList("song");

        msongName = mySongs.get(position).getName();
        String songName = intent.getStringExtra("name");

        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);

        position = bundle.getInt("position",0);
        Uri uri = Uri.parse(mySongs.get(position).toString());

        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);
        mediaPlayer.start();
    }

    private void checkVoiceCommandPermission(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS , Uri.parse("package:"+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong()
    {

        if(mediaPlayer.isPlaying())
        {
            imageView.setBackgroundResource(R.drawable.pause_logo);
            mediaPlayer.pause();
        }
        else
        {
            mediaPlayer.start();
            imageView.setBackgroundResource(R.drawable.play_logo);
        }
    }

    private void playNextSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = ((position+1)%mySongs.size());

        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);

        msongName = mySongs.get(position).toString();
        songNameTxt.setText(msongName);
        mediaPlayer.start();

        if(mediaPlayer.isPlaying())
        {
            imageView.setBackgroundResource(R.drawable.play_logo);
        }
        else
        {
            imageView.setBackgroundResource(R.drawable.pause_logo);
        }
    }

    private void playPreviousSong()
    {
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position = (position-1);

        if(position<0)
        {
            position=mySongs.size()-1;
        }
        else
        {
            position=position%mySongs.size();
        }


        Uri uri = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(SmartPlayerActivity.this,uri);

        msongName = mySongs.get(position).toString();
        songNameTxt.setText(msongName);
        mediaPlayer.start();

        if(mediaPlayer.isPlaying())
        {
            imageView.setBackgroundResource(R.drawable.play_logo);
        }
        else
        {
            imageView.setBackgroundResource(R.drawable.pause_logo);
        }
    }
}
