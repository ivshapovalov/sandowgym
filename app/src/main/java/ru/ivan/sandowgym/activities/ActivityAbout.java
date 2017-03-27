package ru.ivan.sandowgym.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import ru.ivan.sandowgym.R;

import static ru.ivan.sandowgym.common.Common.setTitleOfActivity;

public class ActivityAbout extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitleOfActivity(this);

        int tvMessageId = getResources().getIdentifier("tvMessage", "id", getPackageName());
        TextView tvMessage= (TextView) findViewById(tvMessageId);
        if (tvMessage != null) {
            PackageInfo pInfo = null;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                String version = pInfo.versionName;
//                int versionCode = BuildConfig.VERSION_CODE;
//                String versionName = BuildConfig.VERSION_NAME;
                StringBuilder message=new StringBuilder("This program will help you to train Sandows gymnastics");
                message.append("\n").append("Version ").append(version).append("\n")
                        .append("2016.12.14");
                tvMessage.setText(message.toString());
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
