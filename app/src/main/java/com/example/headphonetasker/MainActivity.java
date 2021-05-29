package com.example.headphonetasker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaBrowserCompat mMediaBrowserCompat;
    private MediaControllerCompat mMediaControllerCompat;
    TextView mtvStatus;
    public static MainActivity instance;
    private int screen_brightness;
    private int screen_brightness_mode;
    public int last_app = 0;


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

    public void komootToFront() {

        PackageManager pm = this.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("de.komoot.android");
        last_app = 0;
        if (intent != null) {
            this.startActivity(intent);
        }
    }

    public void OsmAndToFront() {

        PackageManager pm = this.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage("net.osmand.plus");
        last_app = 1;
        if (intent != null) {
            this.startActivity(intent);
        }
    }

    public int[] get_zoom_in_coord() {
        final int [] komoot={943,1350};
        final int [] osmand={980,1630};
        switch(last_app) {
            case 1:
                return osmand;
            case 0:
            default:
                return komoot;
        }
    }

    public int[] get_zoom_out_coord() {
        final int [] komoot={943,1471};
        final int [] osmand={980,1823};
        switch(last_app) {
            case 1:
                return osmand;
            case 0:
            default:
                return komoot;
        }
    }

    public void lastAppToFront()
    {
        switch(last_app) {
            case 1:
                OsmAndToFront();
                break;
            case 0:
            default:
                komootToFront();
                break;
        }
    }

    public void taskerToFront() {

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i = 0; i < recentTasks.size(); i++) {
            Log.d("Executed app", "Application executed : "
                    + recentTasks.get(i).baseActivity.toShortString()
                    + "\t\t ID: " + recentTasks.get(i).id + "");
            // bring to front
            if (recentTasks.get(i).baseActivity.toShortString().indexOf("{com.example.headphonetasker/com.example.headphonetasker.MainActivity}") > -1) {
                activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);


        instance = this;
        Helper.save_default_values_from_system(this);
        screen_brightness = Helper.default_brightness;
        screen_brightness_mode = Helper.default_brightness_mode;

        mMediaBrowserCompat = new MediaBrowserCompat(this, new ComponentName(this, BackgroundAudioService.class),
                mMediaBrowserCompatConnectionCallback, getIntent().getExtras());

        mMediaBrowserCompat.connect();

        mtvStatus = (TextView) findViewById(R.id.tvStatus);

        Button btn = (Button) findViewById(R.id.btn_exit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().stop();
                finishAndRemoveTask();
            }
        });

        btn = (Button) findViewById(R.id.btn_komoot);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                komootToFront();
                reset_screen_seetings();
            }
        });

        btn = (Button) findViewById(R.id.btn_OsmAnd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                OsmAndToFront();
                reset_screen_seetings();
            }
        });


        SeekBar sb = (SeekBar) findViewById(R.id.sb_brightness);
        sb.setProgress(Helper.default_brightness);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Helper.set_screen_brightness(instance, i);
                screen_brightness = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        CheckBox cb_brightness = (CheckBox) findViewById(R.id.cb_brightness);
        cb_brightness.setChecked(Helper.default_brightness_mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        cb_brightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                screen_brightness_mode = b ? Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC : Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
                Helper.set_screen_brightness_mode(instance, screen_brightness_mode);
            }
        });


        Log.i("main", "default_screen_off_millis: " + Helper.default_screen_off_millis);

        Spinner sp = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{""+(Helper.default_screen_off_millis / 1000), "5","15","30", "45", "60", "90", "120", "160"};
        ArrayAdapter adapter= new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items);
        sp.setAdapter(adapter);

        sp.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String str = adapterView.getItemAtPosition(i).toString();
                Helper.default_screen_off_millis =  Integer.parseInt(str) * 1000;
                Helper.set_default_screen_off_timeout(instance);
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

    int status_cnt = 0;
    public void set_status(String status)
    {
        status_cnt++;
        final String s = "#" + status_cnt + ": " + status;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mtvStatus.setText(s);
            }
        });

    }

    @Override
    protected void onDestroy() {
        Log.i("main", "onDestroy");
        super.onDestroy();
        Helper.restore_default_values(this);
        if (MediaControllerCompat.getMediaController(MainActivity.this).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING) {
            MediaControllerCompat.getMediaController(MainActivity.this).getTransportControls().pause();
        }
        mMediaBrowserCompat.disconnect();
        unregisterReceiver(receiver);
    }

    private void reset_screen_seetings()
    {
        Log.i("main", "reset_screen_seetings");
        Helper.set_default_screen_off_timeout(this);
        Helper.set_screen_brightness_mode(this, screen_brightness_mode);
        Helper.set_screen_brightness(this, screen_brightness);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("main", action);

            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                reset_screen_seetings();
                lastAppToFront();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            }
        }
    };
}

