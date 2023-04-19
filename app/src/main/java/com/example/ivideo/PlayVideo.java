package com.example.ivideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayVideo extends AppCompatActivity {

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    TextView textView;
    ImageView previous, pause, next;
    ArrayList<File> videos;
    String textContent;
    int position;
    SeekBar seekBar;
    MediaPlayer mediaPlayer;
    SurfaceView surfaceView;
    Thread updateSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        textView = findViewById(R.id.textView);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        surfaceView = findViewById(R.id.surfaceView);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        videos = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentVideo").replace(".mp4", "");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
        Uri uri = Uri.parse(videos.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);

        surfaceView.setKeepScreenOn(true);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mediaPlayer.setDisplay(surfaceHolder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        updateSeek = new Thread(){
            @Override
            public void run() {
                super.run();
                int currentposition = 0;
                try {
                    while (currentposition<mediaPlayer.getDuration()){
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        sleep(800);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };

        updateSeek.setPriority(Thread.MAX_PRIORITY);

        updateSeek.start();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                  pause.setImageResource(R.drawable.play);
                  mediaPlayer.pause();
                }
                else{
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=0){
                    position = position - 1;
                }
                else{
                    position = videos.size() - 1;
                }

                Uri uri = Uri.parse(videos.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                mediaPlayer.setDisplay(surfaceHolder);

                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });

                pause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = videos.get(position).getName().replace(".mp4", "");
                textView.setText(textContent);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if(position!=videos.size()-1){
                    position = position + 1;
                }
                else{
                    position = 0;
                }

                Uri uri = Uri.parse(videos.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

                mediaPlayer.setDisplay(surfaceHolder);

                mediaPlayer.start();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.start();
                    }
                });

                pause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = videos.get(position).getName().replace(".mp4", "");
                textView.setText(textContent);
            }
        });
    }
}