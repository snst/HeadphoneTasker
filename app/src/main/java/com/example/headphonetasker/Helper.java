package com.example.headphonetasker;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

public class Helper {
    static int default_screen_off_millis = 120000;

    public static void set_screen_off_timeout(ContentResolver cr, int val)
    {
        android.provider.Settings.System.putInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, val);
        Log.i("KRH", "set_screen_off_timeout: " + val);
    }

    public static int get_screen_off_timeout(ContentResolver cr)
    {
        return android.provider.Settings.System.getInt(cr, Settings.System.SCREEN_OFF_TIMEOUT, 120000);
    }

    public static void turn_display_off(ContentResolver cr) {
        set_screen_off_timeout(cr, 50);
    }

    public static void set_default_screen_off_timeout(ContentResolver cr) {
        set_screen_off_timeout(cr, default_screen_off_millis);
    }

    public static boolean is_screen_on(Context context) {
        DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                return true;
            }
        }
        return false;
    }

    public static void turn_screen_on(Context context) {
        Log.i("KR", "turn_screen_on()");
        Helper.set_default_screen_off_timeout(context.getContentResolver());
        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

        wakeLock.acquire();
        wakeLock.release();
    }
}
