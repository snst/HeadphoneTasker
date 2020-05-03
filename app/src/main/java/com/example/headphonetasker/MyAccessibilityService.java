package com.example.headphonetasker;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.GestureDescription;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageButton;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("KRA", action);
        if (action.equals("KRZ_OUT")) {
            zoomOut();
        } else if (action.equals("KRZ_IN")) {
            zoomIn();
        }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("KRZ_OUT");
        filter.addAction("KRZ_IN");
        registerReceiver(receiver, filter);
    }

    private void createClick(float x, float y) {
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

    public void zoomIn() {
        Log.i("KRA", "zoomIn()");
        createClick(946, 1520);
    }

    public void zoomOut() {
        Log.i("KRA", "zoomOut()");
        createClick(946, 1670);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        /*
        AccessibilityNodeInfo nodeInfo = event.getSource();
        Log.i("KRA", event.toString());
            if (event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.i("KRA", "TYPE_WINDOW_STATE_CHANGED: " + event.getPackageName().toString());
            if(event.getPackageName().toString().endsWith("de.komoot.android"))
            {
                int k=0;
                for(int i=0; i<nodeInfo.getChildCount(); i++) {

                    AccessibilityNodeInfo node = nodeInfo.getChild(i);
                    if (null != node) {
                        //Log.i("KRA-item: ", node.getClassName().toString());
                        if(node.getClassName().equals("android.widget.ImageButton"))
                        {
                        }
                    }
                }
            } else {
                nodeInfoZoomIn = nodeInfoZoomOut = null;
            }
        }
        nodeInfo.recycle();
            */
    }

    /*
    @Override
    public void onServiceConnected(){
        Log.e("KRA", " onServiceConnected");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.packageNames = null;
        setServiceInfo(info);
    }*/


    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
