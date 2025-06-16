package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.MainActivity;
import com.example.applepie.Model.UserList;
import com.example.applepie.R;

public class LoginScreen1 extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;
    TextView txtRegister, txtForgotPassword;

    UserList userList = new UserList(); // Mock data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen1);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        ImageButton btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_fill_info), Toast.LENGTH_SHORT).show();
                return;
            }

            if (userList.isValidUser(username, password)) {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();

                // ✅ Lưu trạng thái đăng nhập
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_Logged_in", true);
                editor.putString("user_name", username); // Lưu tên người dùng nếu cần
                editor.apply();

                // ➤ Chuyển về MainActivity
                Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        // Mở trang đăng ký
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, LoginScreen2.class);
            startActivity(intent);
        });

        // Mở trang quên mật khẩu
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
}
