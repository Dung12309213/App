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


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Model.User;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.NetworkUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class LoginScreen2 extends AppCompatActivity {
    EditText edtRegisterName, edtRegisterPhone, edtRegisterEmail, edtRegisterPassword;
    Button btnCompleteRegister;
    TextView tvToLogin;
    private boolean fromCheckout = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


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

        if (getIntent() != null && getIntent().hasExtra("from_checkout")) {
            fromCheckout = getIntent().getBooleanExtra("from_checkout", false);
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseConnector.getInstance();

        addViews();
        addEvents();

    }
    private static final int REQUEST_CODE_OTP = 103;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_OTP) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Xác nhận OTP bị hủy hoặc không thành công.", Toast.LENGTH_SHORT).show();
            }
        }
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

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();
                                String userEmail = firebaseUser.getEmail();

                                User newUserProfile = new User();
                                newUserProfile.setName(name);
                                String normalizedPhone = phone;
                                if (phone.startsWith("0")) {
                                    normalizedPhone = "+84" + phone.substring(1);
                                }

                                final String finalNormalizedPhone = normalizedPhone;
                                newUserProfile.setPhone(normalizedPhone);
                                newUserProfile.setEmail(userEmail);

                                db.collection("User").document(userId)
                                        .set(newUserProfile) // Dùng set() để đặt document ID là UID
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("Register", "User profile added to Firestore with ID: " + userId);

                                            // Tạo mã OTP ngẫu nhiên
                                            String otpCode = generateOtp();
                                            EmailSender.sendRegisterOTP(userEmail, otpCode); // Gửi OTP đến email đã đăng ký

                                            Intent intent = new Intent(LoginScreen2.this, LoginScreenOTP.class);
                                            intent.putExtra("otpCode", otpCode);  // Truyền mã OTP

                                            intent.putExtra("userEmailForOTP", userEmail);
                                            intent.putExtra("userId", userId);
                                            intent.putExtra("userName", name);
                                            intent.putExtra("userPhone", finalNormalizedPhone);


                                            startActivityForResult(intent, REQUEST_CODE_OTP);

                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Register", "Error adding user profile to Firestore", e);
                                            Toast.makeText(LoginScreen2.this, "Lỗi khi lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });

                            } else {
                                Toast.makeText(LoginScreen2.this, "Đăng ký thành công nhưng không lấy được người dùng.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Đăng ký thất bại trong Firebase Authentication
                            Log.w("Register", "createUserWithEmail:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginScreen2.this, "Email này đã được đăng ký. Vui lòng sử dụng email khác hoặc đăng nhập.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginScreen2.this, "Đăng ký thất bại: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });

        tvToLogin.setOnClickListener(v -> {
            if (fromCheckout) {
                setResult(RESULT_CANCELED);
            }
            finish();
        });
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean isValidPhone(String phone) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^(\\+?84|0)\\d{9,10}$");
        java.util.regex.Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    private String generateOtp() {
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}