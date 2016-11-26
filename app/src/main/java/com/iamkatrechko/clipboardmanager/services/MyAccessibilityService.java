package com.iamkatrechko.clipboardmanager.services;


import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.iamkatrechko.clipboardmanager.Util;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_PASTE;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    private AccessibilityNodeInfo activeSource;
    ClipboardManager mClipboardManager;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }



        final int eventType = event.getEventType();
        String eventText = null;
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Clicked: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "Long Clicked: ";
                if (source.getClassName().equals("android.widget.EditText")){
                    activeSource = source;
                    startService(new Intent(this, FloatingViewService.class));
                    if (Util.checkSupportActionPaste(source)){
                        //source.performAction(ACTION_PASTE);
                    }
                }
                break;
        }

        Log.d(TAG, eventText);
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String label = Util.getClipboardLabel(getApplicationContext());
                if (label.equals("126126126")) {
                    try {
                        if (Util.checkSupportActionPaste(activeSource)) {
                            activeSource.performAction(ACTION_PASTE);
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        });
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onDestroy");
    }
}
