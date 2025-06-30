package com.example.applepie.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Base.BaseActivity;
import  com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends BaseActivity {

    private ImageButton btnBack;
    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;
    private UserSessionManager userSessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        userSessionManager = UserSessionManager.getInstance(this);
        mAuth = FirebaseAuth.getInstance();

        addViews();
        addEvents();

    }

    private void addEvents() {
        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Sự kiện đổi mật khẩu
        btnChangePassword.setOnClickListener(v -> {
            String currentPass = edtCurrentPassword.getText().toString().trim();
            String newPass = edtNewPassword.getText().toString().trim();
            String confirmPass = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra đầu vào
            if (TextUtils.isEmpty(currentPass) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirmPass)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newPass.length() < 6) { // Kiểm tra độ dài mật khẩu mới
                Toast.makeText(this, "New password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = mAuth.getCurrentUser();

            // Lấy email từ UserSessionManager (đảm bảo UserSessionManager của bạn đã lưu email)
            String userEmailFromSession = userSessionManager.getUserEmail(); // Đảm bảo phương thức này có trong UserSessionManager

            if (user != null && !TextUtils.isEmpty(userEmailFromSession)) {
                // Bước 1: Yêu cầu xác thực lại người dùng bằng mật khẩu hiện tại
                // Tạo AuthCredential từ email và mật khẩu hiện tại
                AuthCredential credential = EmailAuthProvider.getCredential(userEmailFromSession, currentPass);

                user.reauthenticate(credential)
                        .addOnCompleteListener(reauthTask -> {
                            if (reauthTask.isSuccessful()) {
                                // Xác thực lại thành công, tiến hành đổi mật khẩu
                                user.updatePassword(newPass)
                                        .addOnCompleteListener(updatePassTask -> {
                                            if (updatePassTask.isSuccessful()) {
                                                Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                                                finish(); // Quay lại sau khi đổi thành công
                                            } else {
                                                // Xử lý lỗi khi cập nhật mật khẩu (ví dụ: mật khẩu quá yếu)
                                                Toast.makeText(ChangePasswordActivity.this, "Failed to change password: " + updatePassTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            } else {
                                // Xác thực lại thất bại (mật khẩu hiện tại không đúng hoặc lỗi khác)
                                Toast.makeText(ChangePasswordActivity.this, "Authentication failed. Please check your current password.", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                // Người dùng chưa đăng nhập hoặc không có email trong session
                Toast.makeText(ChangePasswordActivity.this, "User not logged in or session expired. Please log in again.", Toast.LENGTH_SHORT).show();
                // Tùy chọn: Xóa session và chuyển hướng về màn hình đăng nhập nếu cần
                userSessionManager.logout();
                // startActivity(new Intent(ChangePasswordActivity.this, LoginActivity.class));
                // finish();
            }
        });
    }

    private void addViews() {
        btnBack = findViewById(R.id.imageButton2);
        edtCurrentPassword = findViewById(R.id.editPassword2);
        edtNewPassword = findViewById(R.id.editPassword1);
        edtConfirmPassword = findViewById(R.id.editPassword3);
        btnChangePassword = findViewById(R.id.btnLogin2);
    }
}
