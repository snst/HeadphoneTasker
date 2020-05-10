package com.example.headphonetasker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("acc", action);

        if (action.equals("KRZ_OUT")) {
            zoom_out();
        } else if (action.equals("KRZ_IN")) {
            zoom_in();
        } else if (action.equals("KRZ_MAX")) {
            zoom_max();
        }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("KRZ_OUT");
        filter.addAction("KRZ_IN");
        filter.addAction("KRZ_MAX");
        registerReceiver(receiver, filter);
    }

    private void create_click(int x, int y) {
        int DURATION = 5;
        Path clickPath = new Path();
        clickPath.moveTo(x, y);
        GestureDescription.StrokeDescription clickStroke =
                new GestureDescription.StrokeDescription(clickPath, 0, DURATION);
        GestureDescription.Builder clickBuilder = new GestureDescription.Builder();
        clickBuilder.addStroke(clickStroke);
        GestureDescription gesture = clickBuilder.build();
        dispatchGesture(gesture, null, null);
    }


    public void zoom_max() {
        Log.i("acc", "zoom_max()");
        create_click(946, 1370);
    }

    public void zoom_in() {
        Log.i("acc", "zoom_in()");
        create_click(946, 1520);
    }

    public void zoom_out() {
        Log.i("acc", "zoom_out()");
        create_click(946, 1670);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
