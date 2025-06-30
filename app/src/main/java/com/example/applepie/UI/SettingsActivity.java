package com.example.applepie.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast; // Thêm import Toast

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth; // THÊM IMPORT NÀY
import com.google.firebase.auth.FirebaseUser; // THÊM IMPORT NÀY
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class SettingsActivity extends BaseActivity {

    private UserSessionManager sessionManager;
    private FirebaseAuth mAuth; // KHAI BÁO FIREBASE AUTH

    private ImageButton btnBack;
    private ConstraintLayout itemSettingNotification, itemSettingPassword, itemSettingDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sessionManager = UserSessionManager.getInstance(this);
        mAuth = FirebaseAuth.getInstance(); // KHỞI TẠO FIREBASE AUTH

        addViews();
        addEvents();

        checkLoggedIn();
    }

    private void addViews() {
        btnBack = findViewById(R.id.imageButton2);
        itemSettingPassword = findViewById(R.id.itemSettingPassword);
        itemSettingDeleteAccount = findViewById(R.id.itemSettingDeleteAccount);
        // itemSettingNotification = findViewById(R.id.itemSettingNotification); // Nếu bạn có item này
    }

    private void addEvents() {
        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Password Manager
        itemSettingPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // Delete Account (hiện dialog xác nhận)
        itemSettingDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa tài khoản")
                    .setMessage("Bạn có chắc muốn xóa tài khoản này không? Thao tác này sẽ yêu cầu bạn xác nhận lại mật khẩu của mình.")
                    .setPositiveButton("Có", (dialog, which) -> {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        if (firebaseUser != null) {
                            // Chuyển sang màn hình xác thực lại
                            // Tạo một Intent để chuyển sang ReauthenticateActivity
                            Intent intent = new Intent(SettingsActivity.this, DeleteAccountActivity.class);
                            // Bạn có thể không cần truyền UID hay Email vì ReauthenticateActivity sẽ tự lấy FirebaseUser hiện tại
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Bạn chưa đăng nhập. Vui lòng đăng nhập để xóa tài khoản.", Toast.LENGTH_SHORT).show();
                            Log.e("SettingsActivity", "No Firebase User is currently logged in.");
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @SuppressLint("Range")
    private void checkLoggedIn() {
        String userName = sessionManager.getUserName();

        if (!userName.equals("Guest")) {
            itemSettingPassword.setVisibility(View.VISIBLE);
            itemSettingDeleteAccount.setVisibility(View.VISIBLE);
        } else {
            itemSettingPassword.setVisibility(View.GONE);
            itemSettingDeleteAccount.setVisibility(View.GONE);
        }
    }
}