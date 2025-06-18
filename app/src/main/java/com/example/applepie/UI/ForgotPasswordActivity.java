package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText editEmailOrPhone;
    private Button btnSendOtp;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Ánh xạ view
        editEmailOrPhone = findViewById(R.id.editEmailOrPhone);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnBack = findViewById(R.id.btnBack);

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi mã OTP
        btnSendOtp.setOnClickListener(v -> {
            String input = editEmailOrPhone.getText().toString().trim();

            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }

            // Có thể validate kỹ hơn tại đây nếu cần

            // Chuyển sang màn hình OTP
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginScreenOTP.class);
            intent.putExtra("user_input", input); // Nếu muốn gửi dữ liệu sang màn OTP
            startActivity(intent);
        });
    }
}
