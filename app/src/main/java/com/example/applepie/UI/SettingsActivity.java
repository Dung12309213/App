package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvNotificationSettings, tvPasswordManager, tvDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Ánh xạ view
        btnBack = findViewById(R.id.imageButton2);
        tvNotificationSettings = findViewById(R.id.title);
        tvPasswordManager = findViewById(R.id.title1);
        tvDeleteAccount = findViewById(R.id.title2);

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_Logged_in", false);

        // Password Manager (yêu cầu login)
        tvPasswordManager.setOnClickListener(v -> {
            if (isLoggedIn) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else {
                redirectToLogin();
            }
        });

        // Delete Account (hiện dialog xác nhận)
        tvDeleteAccount.setOnClickListener(v -> {
            if (isLoggedIn) {
                new AlertDialog.Builder(this)
                        .setTitle("Xoá tài khoản")
                        .setMessage("Bạn có chắc muốn xoá tài khoản này không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Xoá trạng thái đăng nhập
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("is_Logged_in", false);
                            editor.apply();

                            Toast.makeText(this, "Tài khoản đã bị xoá", Toast.LENGTH_SHORT).show();

                            // Quay về màn hình đăng nhập, xoá history
                            Intent intent = new Intent(this, LoginScreen1.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                redirectToLogin();
            }
        });
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginScreen1.class));
    }
}
