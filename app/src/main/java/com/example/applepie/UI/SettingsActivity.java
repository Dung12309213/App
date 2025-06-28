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

import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth; // THÊM IMPORT NÀY
import com.google.firebase.auth.FirebaseUser; // THÊM IMPORT NÀY
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity {

    private UserSessionManager sessionManager;
    private FirebaseAuth mAuth; // KHAI BÁO FIREBASE AUTH

    private ImageButton btnBack;
    private ConstraintLayout itemSettingNotification, itemSettingPassword, itemSettingDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sessionManager = new UserSessionManager(this);
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
                    .setTitle("Xoá tài khoản")
                    .setMessage("Bạn có chắc muốn xoá tài khoản này không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser(); // LẤY NGƯỜI DÙNG HIỆN TẠI TỪ FIREBASE AUTH

                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid(); // LẤY UID TỪ FIREBASE AUTH
                            String userEmail = firebaseUser.getEmail(); // LẤY EMAIL TỪ FIREBASE AUTH

                            Log.d("SettingsActivity", "Firebase Auth User ID: " + userId + ", Email: " + userEmail);

                            if (userId != null && !userId.isEmpty() && userEmail != null && !userEmail.isEmpty()) {
                                // Tạo mã OTP
                                String otpCode = generateOtp();

                                // Gửi email với mã OTP
                                EmailSender.sendDeleteOTP(userEmail, otpCode); // Dùng email từ FirebaseUser

                                // Tạo Intent để chuyển đến DeleteOtpActivity
                                Intent intent = new Intent(this, DeleteOtpActivity.class);
                                intent.putExtra("userId", userId); // Truyền UID của Firebase Auth
                                intent.putExtra("otpCode", otpCode);
                                intent.putExtra("email", userEmail); // Truyền email từ FirebaseUser
                                startActivity(intent);

                            } else {
                                Toast.makeText(this, "Không thể lấy thông tin tài khoản. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
                                Log.e("SettingsActivity", "Firebase User UID or Email is null/empty.");
                            }
                        } else {
                            Toast.makeText(this, "Bạn chưa đăng nhập. Vui lòng đăng nhập để xoá tài khoản.", Toast.LENGTH_SHORT).show();
                            Log.e("SettingsActivity", "No Firebase User is currently logged in.");
                        }
                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @SuppressLint("Range")
    private void checkLoggedIn() {
        // Lấy dữ liệu người dùng từ UserSessionManager (đây chỉ là kiểm tra nhanh để ẩn/hiện nút)
        // Việc quan trọng là kiểm tra FirebaseUser trực tiếp khi người dùng nhấn nút.
        String userName = sessionManager.getUserName();

        if (!userName.equals("Guest")) {
            itemSettingPassword.setVisibility(View.VISIBLE);
            itemSettingDeleteAccount.setVisibility(View.VISIBLE);
        } else {
            itemSettingPassword.setVisibility(View.GONE);
            itemSettingDeleteAccount.setVisibility(View.GONE);
        }
    }

    private String generateOtp() {
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}