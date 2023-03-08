package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    TextView titleTv,currentTimeTv,totalTimeTv,logout;
    SeekBar seekBar;
    ImageView pausePlay,nextBtn,previousBtn,musicIcon,musiclist, shuffle;
    ArrayList<AudioModel>songsList;
    AudioModel currentSong;
    MediaPlayer mediaPlayer=MyMediaPlayer.getInstance();
    int x=0;
    int cnt=1;
    private boolean shuffleEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        titleTv=findViewById(R.id.song_tittle);
        currentTimeTv=findViewById(R.id.current_time);
        totalTimeTv=findViewById(R.id.total_time);
        seekBar=findViewById(R.id.seek_bar);
        pausePlay=findViewById(R.id.pause_play);
        nextBtn=findViewById(R.id.next);
        previousBtn=findViewById(R.id.previous);
        musicIcon=findViewById(R.id.music_icon_big);
        musiclist=findViewById(R.id.music_list);
        logout=findViewById(R.id.log_out);
        shuffle = findViewById(R.id.shuffle);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MusicPlayerActivity.this,Login.class);
                startActivity(intent);
                mediaPlayer.pause();
            }
        });

        musiclist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MusicPlayerActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shuffleEnabled = !shuffleEnabled;
                if(cnt%2==0)
                {
                    Toast.makeText(MusicPlayerActivity.this,"Shuffled on",Toast.LENGTH_LONG).show();
                    cnt++;
                }
                else
                {
                    Toast.makeText(MusicPlayerActivity.this, "Shuflled off", Toast.LENGTH_LONG).show();
                    cnt++;
                }
            }
        });
        titleTv.setSelected(true);

        songsList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }
                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    void setResourcesWithMusic(){
        currentSong=songsList.get(MyMediaPlayer.currentIndex);
        titleTv.setText(currentSong.getTittle());
        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v->playNextSong(1));
        previousBtn.setOnClickListener(v->playPreviousSong());

        playMusic();

    }

    private void playMusic(){
        mediaPlayer.reset();
        try{
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                if(shuffleEnabled){
                    playNextSong(new Random().nextInt(songsList.size()));

                }
            });

        }catch (IOException e){
            e.printStackTrace();
        }

    }



    private void playNextSong(int i){
        if(MyMediaPlayer.currentIndex==songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex = (MyMediaPlayer.currentIndex+i)%songsList.size();
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex==0)
            return;
        MyMediaPlayer.currentIndex-=1;
        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();

    }

    public static String convertToMMSS(String duration){
        Long millis=Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }
}