package com.example.carrepair;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carrepair.okhttp.ApiSetting;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApiSetting setting = new ApiSetting(this);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (setting.getBoolean(ApiSetting.LOGIN_STATUS)) {
                            if (setting.getInt(ApiSetting.USER_TYPE_ID) == 1) {
                                startActivity(new Intent(SplashActivity.this, AdminActivity.class));
                            } else {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            }
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        } else {
                            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    }
                }, 1500);
    }
}
