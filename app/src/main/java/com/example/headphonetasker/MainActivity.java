package com.example.headphonetasker;

import android.content.ComponentName;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Helper.setScreenOffTimeoutDefault(getContentResolver());
                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().stop();
                //stopService(service);
                finishAffinity();
                System.exit(0);
            }
        });

        btn = (Button) findViewById(R.id.button_set_turn_off_time);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText edit = (EditText) findViewById(R.id.editText_turn_off_time);
                Helper.map_display_time = Integer.parseInt(edit.getText().toString())*1000;
            }
        });

        btn = (Button) findViewById(R.id.button_play);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().play();
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

