package com.example.next.videoview;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class Player extends AppCompatActivity {
    private SurfaceView surfaceView;
    private LibVLC libVLC;
    private String mFilepath ;
    private MediaPlayer mediaPlayer = null;
    private SurfaceHolder holder;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mFilepath = "rtsp://192.168.35.144:8554/test.sdp";
        surfaceView = findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();
        //createPlayer(mFilepath);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        createPlayer(mFilepath);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        releasePlayer();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        releasePlayer();
    }

    private void createPlayer(String media)
    {
        ArrayList<String> options = new ArrayList<String>();
        options.add("--aout=opensles");
        options.add("--audio-time-stretch"); // time stretching
        options.add("-vvv"); // verbosity
        libVLC = new LibVLC(this, options);
        holder.setKeepScreenOn(true);
        mediaPlayer = new MediaPlayer(libVLC);
        mediaPlayer.setEventListener(mPlayerListener);
        final IVLCVout vout = mediaPlayer.getVLCVout();
        vout.setVideoView(surfaceView);
        vout.attachViews();
        Media m = new Media(libVLC, Uri.parse(media));
        mediaPlayer.setMedia(m);
        mediaPlayer.play();
    }


    private MediaPlayer.EventListener mPlayerListener = new MyPlayerListener(this);
    private static class MyPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<Player> mOwner;

        public MyPlayerListener(Player owner) {
            mOwner = new WeakReference<Player>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            Player player = mOwner.get();
            switch (event.type) {
                case MediaPlayer.Event.EndReached:
                    Log.d("Player", "MediaPlayerEndReached");
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                default:
                    break;
            }
        }
    }

    private void releasePlayer()
    {
        mediaPlayer.stop();
    }
}
