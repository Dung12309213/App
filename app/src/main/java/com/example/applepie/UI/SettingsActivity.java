package com.example.applepie.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity {


    private UserSessionManager sessionManager;

    private ImageButton btnBack;
    private ConstraintLayout itemSettingNotification, itemSettingPassword, itemSettingDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sessionManager = new UserSessionManager(this);

        addViews();
        addEvents();

        checkLoggedIn();
    }

    private void addViews() {
        btnBack = findViewById(R.id.imageButton2);
        itemSettingNotification = findViewById(R.id.itemSettingNotification);
        itemSettingPassword = findViewById(R.id.itemSettingPassword);
        itemSettingDeleteAccount = findViewById(R.id.itemSettingDeleteAccount);
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

                        String userId = sessionManager.getUserId();
                        if (userId != null && !userId.isEmpty()) {
                            Log.d("SettingsActivity", "UserId: " + userId);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("User").document(userId).get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            String email = document.getString("email");

                                            Log.d("SettingsActivity", "UserId: " + userId);

                                            // Kiểm tra nếu email là null hoặc rỗng
                                            if (email == null || email.isEmpty()) {
                                                Log.e("SettingsActivity", "Email is null or empty");
                                                // Có thể xử lý thêm ở đây, ví dụ hiển thị thông báo lỗi
                                                return;  // Nếu email không hợp lệ, dừng lại
                                            }

                                            // Tạo mã OTP
                                            String otpCode = generateOtp();

                                            // Gửi email với mã OTP
                                            EmailSender.sendDeleteOTP(email, otpCode);

                                            // Tạo Intent để chuyển đến DeleteOtpActivity và truyền userId và otpCode
                                            Intent intent = new Intent(this, DeleteOtpActivity.class);
                                            intent.putExtra("userId", userId);
                                            intent.putExtra("otpCode", otpCode);
                                            intent.putExtra("email", email);  // Truyền email từ mảng

                                            startActivity(intent);

                                        } else {
                                            Log.d("Firestore", "Get failed with ", task.getException());
                                        }
                                    });
                        } else {
                            Log.e("SettingsActivity", "UserId is null or empty");
                        }

                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }


    @SuppressLint("Range")
    private void checkLoggedIn() {
        // Lấy dữ liệu người dùng từ UserSessionManager
        String userName = sessionManager.getUserName();

        // Kiểm tra nếu người dùng đã đăng nhập
        if (!userName.equals("Guest")) {
            // Nếu đã đăng nhập
            itemSettingPassword.setVisibility(View.VISIBLE);
            itemSettingDeleteAccount.setVisibility(View.VISIBLE);
        } else {
            // Nếu chưa đăng nhập
            itemSettingPassword.setVisibility(View.GONE);
            itemSettingDeleteAccount.setVisibility(View.GONE);
        }
    }

    private String generateOtp() {
        // Tạo mã OTP ngẫu nhiên 4 chữ số
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}
