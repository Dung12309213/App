package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.applepie.Model.User;
import  com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.NetworkUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class LoginScreen2 extends AppCompatActivity {
    EditText edtRegisterName, edtRegisterPhone, edtRegisterEmail, edtRegisterPassword;
    Button btnCompleteRegister;
    TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();

    }

    private void addViews() {
        edtRegisterName=findViewById(R.id.edtRegisterName);
        edtRegisterPhone=findViewById(R.id.edtRegisterPhone);
        edtRegisterEmail=findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword=findViewById(R.id.edtRegisterPassword);
        btnCompleteRegister = findViewById(R.id.btnCompleteRegister);
        tvToLogin = findViewById(R.id.tvToLogin);
    }

    private void addEvents() {
        btnCompleteRegister.setOnClickListener(v -> {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(LoginScreen2.this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
                return;
            }

            String name = edtRegisterName.getText().toString().trim();
            String phone = edtRegisterPhone.getText().toString().trim();
            String email = edtRegisterEmail.getText().toString().trim();
            String password = edtRegisterPassword.getText().toString().trim();

            if (name.isEmpty()) {
                edtRegisterName.setError("Name is required");
                edtRegisterName.requestFocus();
                return;
            }
            if (phone.isEmpty() || !isValidPhone(phone)) {
                edtRegisterPhone.setError("Valid phone number is required");
                edtRegisterPhone.requestFocus();
                return;
            }
            if (email.isEmpty() || !isValidEmail(email)) {
                edtRegisterEmail.setError("Valid email is required");
                edtRegisterEmail.requestFocus();
                return;
            }
            if (password.isEmpty() || password.length() < 6) {
                edtRegisterPassword.setError("Password must be at least 6 characters");
                edtRegisterPassword.requestFocus();
                return;
            }

            // Kiểm tra email đã tồn tại trong Firestore
            FirebaseFirestore db = FirebaseConnector.getInstance();
            db.collection("User")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            QuerySnapshot result = task1.getResult();
                            if (result != null && !result.isEmpty()) {
                                // Email đã tồn tại, không cho đăng ký
                                Toast.makeText(LoginScreen2.this, "Email đã tồn tại", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Kiểm tra phone tồn tại
                            db.collection("User")
                                    .whereEqualTo("phone", phone)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            QuerySnapshot result2 = task2.getResult();
                                            if (result2 != null && !result2.isEmpty()) {
                                                // Số điện thoại đã tồn tại, không cho đăng ký
                                                Toast.makeText(LoginScreen2.this, "Số điện thoại đã tồn tại", Toast.LENGTH_SHORT).show();
                                                return;
                                            }


                                            // Nếu không có trùng lặp, tiến hành lưu người dùng vào Firestore
                                            User newUser = new User(name, phone, email, password); // Sử dụng constructor để tạo User

                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("name", newUser.getName());
                                            userMap.put("phone", newUser.getPhone());
                                            userMap.put("email", newUser.getEmail());
                                            userMap.put("password", newUser.getPassword());

                                            // Tạo mã OTP ngẫu nhiên
                                            String otpCode = generateOtp();

                                            EmailSender.sendOTP(email, otpCode);

                                            Intent intent = new Intent(LoginScreen2.this, LoginScreenOTP.class);
                                            intent.putExtra("otpCode", otpCode);  // Truyền mã OTP cho màn hình OTP
                                            intent.putExtra("userMap", (Serializable) userMap);
                                            startActivity(intent);

                                            /*// Thêm người dùng vào Firestore
                                            db.collection("User")
                                                    .add(userMap)
                                                    .addOnSuccessListener(documentReference -> {
                                                        Intent intent = new Intent(LoginScreen2.this, LoginScreen1.class);
                                                        startActivity(intent);
                                                    });*/
                                        }
                                    });
                        }
                    });
        });
        tvToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen2.this, LoginScreen1.class);
            startActivity(intent);
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPhone(String phone) {
        return phone.length() == 10 && phone.startsWith("0");
    }
    private String generateOtp() {
        // Tạo mã OTP ngẫu nhiên 4 chữ số
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}
