package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns; // Import để xác thực email
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

// THÊM IMPORTS FIREBASE AUTH
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore; // Chỉ cần nếu bạn cần tra cứu email từ SĐT

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editEmailOrPhone; // Đổi tên thành editEmail nếu bạn chỉ hỗ trợ email
    private Button btnSendOtp;
    private ImageButton btnBack;

    // KHAI BÁO FIREBASE AUTH
    private FirebaseAuth mAuth;
    // Có thể cần Firestore nếu bạn muốn cho phép nhập SĐT và tìm email từ SĐT
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Khởi tạo Firebase Auth và Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore

        // Ánh xạ view
        editEmailOrPhone = findViewById(R.id.editEmailOrPhone);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnBack = findViewById(R.id.btnBack);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi mã OTP (thực ra là gửi email đặt lại mật khẩu)
        btnSendOtp.setOnClickListener(v -> {
            String input = editEmailOrPhone.getText().toString().trim();

            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra xem input có phải là email hợp lệ không
            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                // Nếu là email, gửi email đặt lại mật khẩu trực tiếp qua Firebase Auth
                sendPasswordResetEmail(input);
            } else {

                findEmailByPhoneNumberAndSendReset(input);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, "Email đặt lại mật khẩu đã được gửi đến " + email + ". Vui lòng kiểm tra email của bạn.", Toast.LENGTH_LONG).show();
                        finish(); // Kết thúc activity sau khi gửi email
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Lỗi không xác định.";
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi khi gửi email đặt lại mật khẩu: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void findEmailByPhoneNumberAndSendReset(String phoneNumber) {
        // Tìm kiếm email của người dùng dựa trên số điện thoại trong Firestore
        db.collection("User")
                .whereEqualTo("phone", phoneNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Tìm thấy email
                        String email = task.getResult().getDocuments().get(0).getString("email");
                        if (email != null && !email.isEmpty()) {
                            sendPasswordResetEmail(email);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, "Không tìm thấy email liên kết với số điện thoại này.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, "Không tìm thấy tài khoản với số điện thoại này.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}