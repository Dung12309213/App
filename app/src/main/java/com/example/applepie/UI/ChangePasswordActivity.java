package com.example.applepie.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import  com.example.applepie.R;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextInputEditText edtCurrentPassword, edtNewPassword, edtConfirmPassword;
    private Button btnChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Ánh xạ các thành phần trong layout
        btnBack = findViewById(R.id.imageButton2);
        edtCurrentPassword = findViewById(R.id.editPassword2);
        edtNewPassword = findViewById(R.id.editPassword1);
        edtConfirmPassword = findViewById(R.id.editPassword3);
        btnChangePassword = findViewById(R.id.btnLogin2);

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

            // TODO: Thực hiện gọi API hoặc logic đổi mật khẩu
            Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Quay lại sau khi đổi thành công
        });
    }
}
