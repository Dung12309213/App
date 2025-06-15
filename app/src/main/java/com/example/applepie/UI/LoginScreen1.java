package com.example.applepie.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Connector.SQLiteHelper;
import  com.example.applepie.MainActivity;
import com.example.applepie.Model.User;
import  com.example.applepie.R;
import com.example.applepie.Utils.NetworkUtils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginScreen1 extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button btnLogin;
    TextView txtRegister;

    SQLiteHelper dbHelper;

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

        addViews();
        addEvents();

        dbHelper = new SQLiteHelper(this);
    }

    private void addEvents() {
        // Login button logic
        btnLogin.setOnClickListener(v -> {
            String username = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginScreen1.this, getString(R.string.login_fill_info), Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi Firebase để kiểm tra thông tin đăng nhập
            checkLogin(username, password);
        });

        // Register redirect
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, LoginScreen2.class);
            startActivity(intent);
        });
    }

    private void addViews() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
    }

    private void checkLogin(String email, String password) {
        if (!NetworkUtils.isNetworkAvailable(this)) { // 'this' là context của ProfileActivity
            Toast.makeText(LoginScreen1.this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
            return; // Nếu không có kết nối, không tiếp tục thực hiện login
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("User")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        QuerySnapshot result = task1.getResult();
                        if (result != null && !result.isEmpty()) {
                            DocumentSnapshot document = result.getDocuments().get(0);
                            User user = document.toObject(User.class);

                            dbHelper.saveUser(document.getId(), user.getName());

                            Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            db.collection("User")
                                    .whereEqualTo("phone", email)
                                    .whereEqualTo("password", password)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            QuerySnapshot result2 = task2.getResult();
                                            if (result2 != null && !result2.isEmpty()) {
                                                DocumentSnapshot document = result2.getDocuments().get(0);
                                                User user = document.toObject(User.class);

                                                dbHelper.saveUser(document.getId(), user.getName());

                                                Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(LoginScreen1.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    //Kiểm tra kết nối internet
}
