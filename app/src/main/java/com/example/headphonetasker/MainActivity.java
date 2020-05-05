package com.example.headphonetasker;

import android.content.ComponentName;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    int screen_off = 15000;
    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;


    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback = new MediaBrowserCompat.ConnectionCallback() {

        @Override
        public void onConnected() {
            super.onConnected();
            try {
                mMediaControllerCompat = new MediaControllerCompat(MainActivity.this, mMediaBrowserCompat.getSessionToken());
                MediaControllerCompat.setMediaController(MainActivity.this, mMediaControllerCompat);
                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
            } catch (RemoteException e) {
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        Button btn = (Button) findViewById(R.id.btn_exit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.setScreenOffTimeoutDefault(getContentResolver());
                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().stop();
                finishAffinity();
                System.exit(0);
            }
        });

        final Button btn_map = (Button) findViewById(R.id.btn_set_display_time_map);
        final Button btn_default = (Button) findViewById(R.id.btn_set_display_time_default);

        final SeekBar sb_map = (SeekBar) findViewById(R.id.sb_display_map);
        final SeekBar sb_default = (SeekBar) findViewById(R.id.sb_display_default);
        sb_map.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                btn_map.setText(""+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });

        sb_default.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                btn_default.setText(""+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });


        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = sb_map.getProgress() * 1000;
                Helper.map_display_time = val;
            }
        });

        btn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int val = sb_default.getProgress() * 1000;
                Helper.setScreenOffTimeout(getContentResolver(), val);
            }
        });


        Helper.default_display_time = Helper.getScreenOffTimeout(getContentResolver());
        Log.i("KR", "default SCREEN_OFF_TIMEOUT: " + Helper.default_display_time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
        }
        mMediaBrowserCompat.disconnect();
        Log.i("KR", "onDestroy");
    }
}

