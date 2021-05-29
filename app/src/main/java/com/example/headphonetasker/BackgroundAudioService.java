package com.example.headphonetasker;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import java.util.List;


public class BackgroundAudioService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mMediaSessionCompat;
    private VolumeProviderCompat mVolumeProvider = null;
    final int BTN_ZOOM = 1;

    Context context = this;

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
        KeyEvent event = (KeyEvent) mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
        Log.i("audio", "onMediaButtonEvent: " + event.getKeyCode() + ", action:" + event.getAction() + ", flags: " + event.getFlags());
        if (event.getKeyCode() == 79) {

            /*if (event.getAction() == 0) {

                if (Helper.is_screen_on(context))
                    Helper.turn_display_off(context.getContentResolver());
                else
                    Helper.turn_screen_on(context);
            }*/
        }
        return true;
        }
    };


    public void trigger_zoom(boolean zoom_in) {
        String str = zoom_in ? "KRZ_IN" : "KRZ_OUT";
        Log.i("audio", "triggerZoom: " + str);
        Intent i = new Intent(str);
        sendBroadcast(i);
    }

    public void trigger_max() {
        String str = "KRZ_MAX";
        Log.i("audio", "triggerZoom: " + str);
        Intent i = new Intent(str);
        sendBroadcast(i);
    }


    private void handle_second(int btn) {
        if (!Helper.is_screen_on(context)) {
            Helper.turn_screen_on(context);
        } else {
            if (btn == BTN_ZOOM)
                trigger_zoom(false);
            else
                trigger_max();
        }
    }

    ButtonState mButtonState = new ButtonState() {

        @Override
        public void short_click(int btn) {
            Log.i("audio", "shortClick: " + btn);
            MainActivity.instance.set_status("shortClick: " + btn);

            if (!Helper.is_screen_on(context)) {
                MainActivity.instance.lastAppToFront();
                Helper.turn_screen_on(context);
                //Helper.set_screen_brightness(context, 255);
            } else {
                if(btn == BTN_ZOOM)
                    trigger_zoom(true);
                else {
                    Helper.turn_display_off(context);
                    MainActivity.instance.taskerToFront();
                    Helper.set_screen_brightness(context, 0);
                }
            }
        }

        @Override
        public void double_click(int btn) {
            Log.i("audio", "doubleClick: " + btn);
            MainActivity.instance.set_status("doubleClick: " + btn);
            handle_second(btn);
        }

        @Override
        public void repeat_click(int btn) {
            Log.i("audio", "repeatClick: " + btn);
            MainActivity.instance.set_status("repeatClick: " + btn);
            handle_second(btn);
        }

        @Override
        public void long_click(int btn) {
            Log.i("audio", "longClick: " + btn);
            MainActivity.instance.set_status("longClick: " + btn);
        //    handleSecond(btn);
        }
    };


    public void init_volume_provider() {
        mVolumeProvider = new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_ABSOLUTE, 100, 50) {

            @Override
            public void onSetVolumeTo(int direction) {
            }
                @Override
            public void onAdjustVolume(int direction) {
                mButtonState.handle(direction);
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init_media_session();

        mMediaSessionCompat.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, 0, 0)
                .build());
        mMediaSessionCompat.setActive(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMediaSessionCompat.release();
        NotificationManagerCompat.from(this).cancel(1);
    }

    private void init_media_session() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);
        init_volume_provider();
        mMediaSessionCompat.setPlaybackToRemote(mVolumeProvider);

        setSessionToken(mMediaSessionCompat.getSessionToken());
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if (TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }
        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
    }
}