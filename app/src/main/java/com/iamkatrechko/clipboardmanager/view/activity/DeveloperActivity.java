package com.iamkatrechko.clipboardmanager.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.iamkatrechko.clipboardmanager.R;
import com.iamkatrechko.clipboardmanager.domain.util.PrefsManager;
import com.iamkatrechko.clipboardmanager.domain.util.UtilPreferences;
import com.iamkatrechko.clipboardmanager.domain.service.ClipboardService;
import com.iamkatrechko.clipboardmanager.domain.service.CancelViewService;
import com.iamkatrechko.clipboardmanager.domain.service.FloatingViewService;
import com.iamkatrechko.clipboardmanager.domain.service.HideNotificationService;

/**
 * Активность меню разработчика
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
public class DeveloperActivity extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClipboardService.class);
                startService(intent);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ClipboardService.class);
                stopService(intent);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent hideIntent = new Intent(getApplicationContext(), HideNotificationService.class);
                startService(hideIntent);
            }
        });
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", "фыпфыпфыпып");
                clipboard.setPrimaryClip(clip);
            }
        });

        findViewById(R.id.button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
                        startService(intent);
                    }
                }else {
                    Intent intent = new Intent(getApplicationContext(), FloatingViewService.class);
                    startService(intent);
                }
            }
        });

        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                    }else{
                        Intent intent = new Intent(getApplicationContext(), CancelViewService.class);
                        startService(intent);
                    }
                }else {
                    Intent intent = new Intent(getApplicationContext(), CancelViewService.class);
                    startService(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
