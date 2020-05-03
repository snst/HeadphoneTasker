package com.example.headphonetasker;

import android.app.PendingIntent;
import android.content.ComponentName;
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
import java.util.List;


public class BackgroundAudioService extends MediaBrowserServiceCompat {

    private MediaSessionCompat mMediaSessionCompat;
    private VolumeProviderCompat myVolumeProvider = null;

    Context context = this;

    private MediaSessionCompat.Callback mMediaSessionCallback = new MediaSessionCompat.Callback() {

        @Override
        public boolean onMediaButtonEvent(Intent mediaButtonEvent) {
            KeyEvent event = (KeyEvent) mediaButtonEvent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.i("KR", "onMediaButtonEvent: " + event.getKeyCode() + ", action:" + event.getAction() + ", flags: " + event.getFlags());
            if (event.getKeyCode() == 79) {

                if (event.getAction() == 0) {

                    if (Helper.isDisplayOn(context))
                        Helper.turnDisplayOff(context);
                    else
                        Helper.turnDisplayOn(context);
                }
            }
            return true;
        }
    };


    public void triggerZoom(boolean zoom_in) {
        String str = zoom_in ? "KRZ_IN" : "KRZ_OUT";
        Log.i("KR", "triggerZoom: " + str);
        Intent i = new Intent(str);
        sendBroadcast(i);
    }

    public void initVol() {
        myVolumeProvider = new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 10, 5) {
            @Override
            public void onAdjustVolume(int direction) {
                // <0 volume down
                // >0 volume up
                Log.i("KR", "onAdjustVolume: " + direction);

                if (Helper.isDisplayOn(context)) {
                    if (direction == 1) {
                        triggerZoom(true);
                    } else if (direction == -1) {
                        triggerZoom(false);
                    }
                } else {
                    Helper.turnDisplayOn(context);
                }
            }
        };
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSession();

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

    private void initMediaSession() {
        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), "Tag", mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);
        initVol();
        mMediaSessionCompat.setPlaybackToRemote(myVolumeProvider);

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