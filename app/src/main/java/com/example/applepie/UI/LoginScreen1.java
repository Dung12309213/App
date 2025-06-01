package com.example.applepie.UI;

import com.example.applepie.MainActivity;
import com.example.applepie.Model.UserList;
import com.example.applepie.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginScreen1 extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;
    TextView txtRegister;

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

        // Login button logic
        btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_fill_info), Toast.LENGTH_SHORT).show();
                return;
            }

            if (userList.isValidUser(username, password)) {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        });

        // Register redirect
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, LoginScreen2.class);
            startActivity(intent);
        });
    }
}
