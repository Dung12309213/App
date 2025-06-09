package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvNotificationSettings, tvPasswordManager, tvDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting); // Đặt đúng tên file layout bạn dùng

        // === ÁNH XẠ VIEW ===
        btnBack = findViewById(R.id.imageButton2);
        tvNotificationSettings = findViewById(R.id.title);    // title = notification settings
        tvPasswordManager = findViewById(R.id.title1);        // title1 = password manager
        tvDeleteAccount = findViewById(R.id.title2);          // title2 = delete account

        // === XỬ LÝ NÚT QUAY LẠI ===
        btnBack.setOnClickListener(v -> finish());

        // === XỬ LÝ CÁC MỤC MENU ===

        // Notification Settings
        //tvNotificationSettings.setOnClickListener(v -> {
        //    Intent intent = new Intent(SettingsActivity.this, NotificationSettingsActivity.class);
        //   startActivity(intent);
        //});

        // Password Manager
        // Password Manager
        tvPasswordManager.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Delete Account
        //tvDeleteAccount.setOnClickListener(v -> {
        //   Intent intent = new Intent(SettingsActivity.this, DeleteAccountActivity.class);
        //   startActivity(intent);
        //});
    }
}
