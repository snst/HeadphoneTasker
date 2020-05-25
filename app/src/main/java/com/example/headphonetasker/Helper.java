package com.example.headphonetasker;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

public class Helper {
    public static int default_screen_off_millis = 120000;
    public static int default_brightness = 100;
    public static int default_brightness_mode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;


    public static void set_screen_brightness(Context c, int val) {
        Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, val);
    }

    public static int get_screen_brightness(Context c)
    {
        return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 100);
    }

    public static void set_screen_brightness_mode(Context c, int val) {
        Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, val);
    }

    public static int get_screen_brightness_mode(Context c)
    {
        return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    public static void save_default_values_from_system(Context c)
    {
        default_screen_off_millis = get_screen_off_timeout(c);
        default_brightness = get_screen_brightness(c);
        default_brightness_mode = get_screen_brightness_mode(c);
    }

    public static void restore_default_values(Context c)
    {
        set_screen_brightness(c, default_brightness);
        set_screen_brightness_mode(c, default_brightness_mode);
        set_screen_off_timeout(c, default_screen_off_millis);
    }


    public static void set_screen_off_timeout(Context c, int val)
    {
        android.provider.Settings.System.putInt(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, val);
        Log.i("KRH", "set_screen_off_timeout: " + val);
    }

    public static int get_screen_off_timeout(Context c)
    {
        return android.provider.Settings.System.getInt(c.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 120000);
    }

    public static void turn_display_off(Context c) {
        set_screen_off_timeout(c, 50);
    }

    public static void set_default_screen_off_timeout(Context c) {
        set_screen_off_timeout(c, default_screen_off_millis);
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
        Helper.set_default_screen_off_timeout(context);
        PowerManager powerManager = (PowerManager) context.getSystemService(context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "appname::WakeLock");

        wakeLock.acquire();
        wakeLock.release();
    }
}
