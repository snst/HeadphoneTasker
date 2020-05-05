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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

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

        Helper.default_display_time = Helper.getScreenOffTimeout(getContentResolver());
        Log.i("KR", "default SCREEN_OFF_TIMEOUT: " + Helper.default_display_time);

        Spinner sp = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{""+Helper.default_display_time, "5","15","30", "45", "60", "90", "120", "160"};
        ArrayAdapter adapter= new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String str = adapterView.getItemAtPosition(i).toString();
                    int val = Integer.parseInt(str) * 1000;
                    Helper.setScreenOffTimeout(getContentResolver(), val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

