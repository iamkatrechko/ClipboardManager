package com.iamkatrechko.clipboardmanager.domain.service;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipboardManager;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils;
import com.iamkatrechko.clipboardmanager.domain.util.ServiceUtils;

import static android.view.accessibility.AccessibilityNodeInfo.ACTION_PASTE;

/**
 * Специальная служба для вставки заметок в поля ввода
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class MyAccessibilityService extends AccessibilityService {

    /** Тег для логирования */
    private static final String TAG = "MyAccessibilityService";
    private AccessibilityNodeInfo activeSource;
    private ClipboardManager mClipboardManager;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) {
            return;
        }

        final int eventType = event.getEventType();
        String eventText = null;
        switch (eventType) {
            /*case AccessibilityEvent.TYPE_VIEW_CLICKED:
                eventText = "Clicked: ";
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                eventText = "Focused: ";
                break;*/
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                eventText = "Long Clicked: ";
                if (source.getClassName().equals("android.widget.EditText")) {
                    activeSource = source;
                    startService(new Intent(this, FloatingViewService.class));
                    if (ServiceUtils.INSTANCE.checkSupportActionPaste(source)) {
                        source.performAction(ACTION_PASTE);
                    }
                }
                break;
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                String label = ClipUtils.getClipboardLabel(MyAccessibilityService.this);
                if (label.equals(ClipUtils.CLIP_LABEL_ACCESSIBILITY)) {
                    try {
                        if (ServiceUtils.INSTANCE.checkSupportActionPaste(activeSource)) {
                            activeSource.performAction(ACTION_PASTE);
                        }
                    } catch (Exception ignored) {

                    }
                }
            }
        });
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
