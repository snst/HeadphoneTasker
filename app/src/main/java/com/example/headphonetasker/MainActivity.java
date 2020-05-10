package com.example.headphonetasker;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

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

                Helper.set_default_screen_off_timeout(getContentResolver());
                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().stop();
                finishAndRemoveTask();
            }
        });

        Helper.default_screen_off_millis = Helper.get_screen_off_timeout(getContentResolver());
        Log.i("main", "default_screen_off_millis: " + Helper.default_screen_off_millis);

        Spinner sp = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{""+(Helper.default_screen_off_millis / 1000), "5","15","30", "45", "60", "90", "120", "160"};
        ArrayAdapter adapter= new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String str = adapterView.getItemAtPosition(i).toString();
                    int val = Integer.parseInt(str) * 1000;
                    Helper.set_screen_off_timeout(getContentResolver(), val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        Log.i("main", "onDestroy");
        super.onDestroy();
        if (MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
        }
        mMediaBrowserCompat.disconnect();
        unregisterReceiver(receiver);
    }

    private void resetScreenTimeout()
    {
        Helper.set_default_screen_off_timeout(getContentResolver());
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("main", action);

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                resetScreenTimeout();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            }
        }
    };
}

