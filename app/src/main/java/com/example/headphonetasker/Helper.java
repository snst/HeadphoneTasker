package com.example.headphonetasker;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import java.util.Timer;
import java.util.TimerTask;

public class Helper {
    static int default_display_time = 120000;
    static int map_display_time = 15000;

    public static void setScreenOffTimeout(ContentResolver cr, int val)
    {
        android.provider.Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, val);
        Log.i("KRH", "Set SCREEN_OFF_TIMEOUT: " + val);
    }

    public static int getScreenOffTimeout(ContentResolver cr)
    {
        return android.provider.Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, 120000);
    }

    public static void setScreenOffTimeoutToShowMap(ContentResolver cr) {
        setScreenOffTimeout(cr, map_display_time);
    }

    public static void setScreenOffTimeoutToTurnOffDisplay(ContentResolver cr) {
        setScreenOffTimeout(cr, 100);
    }

    public static void setScreenOffTimeoutDefault(ContentResolver cr) {
        setScreenOffTimeout(cr, default_display_time);
    }


    public static boolean isDisplayOn(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

    public static void turnDisplayOn(Context context) {
        Log.i("KR", "turn_on_screen()");
        Helper.setScreenOffTimeoutToShowMap(context.getContentResolver());
        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

        wakeLock.acquire();
        wakeLock.release();
    }

    public static void turnDisplayOff(Context context) {
        Log.i("KR", "turn_off_screen()");
        Helper.setScreenOffTimeoutToTurnOffDisplay(context.getContentResolver());
        final Context c = context;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isDisplayOn(c))
                {
                    Helper.setScreenOffTimeoutDefault(c.getContentResolver());
                }
            }
        }, 5000);
    }

}
