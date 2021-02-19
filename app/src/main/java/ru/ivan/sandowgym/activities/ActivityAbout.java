package ru.ivan.sandowgym.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.convertMillisToString;
import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityAbout extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitleOfActivity(this);

        int tvMessageId = getResources().getIdentifier("tvMessage", "id", getPackageName());
        TextView tvMessage = findViewById(tvMessageId);
        if (tvMessage != null) {
            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
                String date = convertMillisToString(pInfo.lastUpdateTime);
                String message = "This program will help you to train Sandows gymnastics" + "\n" + "Version " + version
                        + "\n Date " + date;
                tvMessage.setText(message);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
