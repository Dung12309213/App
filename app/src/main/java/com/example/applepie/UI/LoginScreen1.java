package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Connector.FirebaseConnector;
import  com.example.applepie.MainActivity;
import com.example.applepie.Model.User;
import  com.example.applepie.R;
import com.example.applepie.Util.NetworkUtils;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginScreen1 extends BaseActivity {

    EditText editEmail, editPassword;
    Button btnLogin;
    ImageButton btnBack;
    TextView txtRegister, txtForgotPassword;

    private UserSessionManager sessionManager;
    private boolean fromCheckout = false;
    private static final int REQUEST_CODE_REGISTER = 102;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseConnector.getInstance();
        sessionManager = UserSessionManager.getInstance(this);

        if (getIntent() != null && getIntent().hasExtra("from_checkout")) {
            fromCheckout = getIntent().getBooleanExtra("from_checkout", false);
        }

        addViews();
        addEvents();

    }

    private void addViews() {
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        btnBack = findViewById(R.id.btnBack);
    }


    private void addEvents() {
        // Login button logic
        btnLogin.setOnClickListener(v -> {
            String inputIdentifier = editEmail.getText().toString().trim(); // Đổi từ emailOrPhone sang inputIdentifier
            String password = editPassword.getText().toString().trim();

            if (inputIdentifier.isEmpty() || password.isEmpty()) { // SỬA: Dùng inputIdentifier ở đây
                Toast.makeText(LoginScreen1.this, getString(R.string.login_fill_info), Toast.LENGTH_SHORT).show();
                return;
            }

            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(LoginScreen1.this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
                return;
            }

            // --- LOGIC ĐĂNG NHẬP MỚI ---
            if (isValidEmail(inputIdentifier)) {
                // Nếu là email, thử đăng nhập trực tiếp
                signInWithEmailAndPassword(inputIdentifier, password);
            } else if (isValidPhone(inputIdentifier)) {
                // Nếu là số điện thoại, tìm email liên kết rồi đăng nhập
                findEmailByPhoneNumberAndSignIn(inputIdentifier, password);
            } else {
                // Nếu không phải email hợp lệ cũng không phải số điện thoại hợp lệ
                Toast.makeText(LoginScreen1.this, "Vui lòng nhập Email hoặc Số điện thoại hợp lệ.", Toast.LENGTH_SHORT).show();
            }
        });


        // Register redirect
        txtRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, LoginScreen2.class);
            if (fromCheckout) {
                intent.putExtra("from_checkout", true);
            }
            startActivityForResult(intent, REQUEST_CODE_REGISTER);
        });
        // Mở trang quên mật khẩu
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen1.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromCheckout) {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {

        Pattern pattern = Pattern.compile("^(\\+?84|0)\\d{9,10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
    private void signInWithEmailAndPassword(String email, String password) { // Đổi từ signInWithFirebase sang signInWithEmailAndPassword
        if (!NetworkUtils.isNetworkAvailable(this)) { // Giữ nguyên kiểm tra mạng nếu bạn có
            Toast.makeText(LoginScreen1.this, getString(R.string.internet_required), Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            fetchUserProfileFromFirestore(firebaseUser.getUid()); // SỬA: Chỉ truyền UID
                        } else {
                            Toast.makeText(LoginScreen1.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginScreen1.this, getString(R.string.login_failed) + ": " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void findEmailByPhoneNumberAndSignIn(String phoneNumber, String password) {
        // Chuẩn hóa số điện thoại để tìm kiếm trong Firestore (đảm bảo định dạng "+84xxxxxxxxx")
        String normalizedPhoneNumber = phoneNumber;
        if (phoneNumber.startsWith("0")) {
            normalizedPhoneNumber = "+84" + phoneNumber.substring(1); // Ví dụ cho VN
        } else if (!phoneNumber.startsWith("+")) {
            normalizedPhoneNumber = "+84" + phoneNumber;
        }

        String finalNormalizedPhoneNumber = normalizedPhoneNumber; // Cần biến final cho truy vấn lambda
        db.collection("User")
                .whereEqualTo("phone", finalNormalizedPhoneNumber) // Tìm người dùng có số điện thoại này
                .limit(1) // Chỉ cần tìm một người
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            User user = document.toObject(User.class);
                            if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
                                Log.d("Login", "Tìm thấy người dùng bằng số điện thoại: " + finalNormalizedPhoneNumber + ", đang thử đăng nhập bằng email: " + user.getEmail());
                                signInWithEmailAndPassword(user.getEmail(), password);
                            } else {
                                Toast.makeText(LoginScreen1.this, "Không tìm thấy tài khoản liên kết với số điện thoại này hoặc tài khoản không có email.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(LoginScreen1.this, "Không tìm thấy tài khoản liên kết với số điện thoại này.", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(LoginScreen1.this, "Lỗi khi tìm kiếm tài khoản: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    private void fetchUserProfileFromFirestore(String firebaseUserId) { // Sửa tham số thành chỉ firebaseUserId
        db.collection("User")
                .document(firebaseUserId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            User user = document.toObject(User.class);
                            if (user != null) {
                                // Xác định định danh để lưu vào session: email hay số điện thoại
                                String identifierToSave;
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                                        identifierToSave = currentUser.getEmail();
                                    } else if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
                                        identifierToSave = currentUser.getPhoneNumber();
                                    } else {
                                        identifierToSave = "unknown_user"; // Dự phòng
                                    }
                                } else {
                                    identifierToSave = "unknown_user"; // Dự phòng
                                }

                                sessionManager.saveUser(document.getId(), user.getName(), identifierToSave);

                                Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                handleLoginSuccess();
                            } else {
                                Toast.makeText(LoginScreen1.this, "Lỗi dữ liệu người dùng.", Toast.LENGTH_SHORT).show();
                                mAuth.signOut(); // Đăng xuất khỏi Firebase Auth nếu hồ sơ bị lỗi
                            }
                        } else {
                            // Hồ sơ người dùng không tồn tại trong Firestore, tạo mới.
                            Toast.makeText(LoginScreen1.this, "Không tìm thấy hồ sơ người dùng, tạo hồ sơ mới.", Toast.LENGTH_SHORT).show();
                            User newUserProfile = new User();
                            newUserProfile.setId(firebaseUserId); // Đặt UID làm ID

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                                    newUserProfile.setEmail(currentUser.getEmail());
                                    newUserProfile.setName(currentUser.getEmail().split("@")[0]); // Tên mặc định từ email
                                } else if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
                                    newUserProfile.setPhone(currentUser.getPhoneNumber());
                                    newUserProfile.setName("Người dùng " + currentUser.getPhoneNumber()); // Tên mặc định từ số điện thoại
                                } else {
                                    newUserProfile.setName("Người dùng mới"); // Tên dự phòng
                                }
                            } else {
                                newUserProfile.setName("Người dùng mới"); // Tên dự phòng
                            }

                            db.collection("User").document(firebaseUserId).set(newUserProfile)
                                    .addOnSuccessListener(aVoid -> {
                                        String identifierToSave;
                                        if (currentUser != null) {
                                            if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                                                identifierToSave = currentUser.getEmail();
                                            } else if (currentUser.getPhoneNumber() != null && !currentUser.getPhoneNumber().isEmpty()) {
                                                identifierToSave = currentUser.getPhoneNumber();
                                            } else {
                                                identifierToSave = "unknown_user";
                                            }
                                        } else {
                                            identifierToSave = "unknown_user";
                                        }

                                        sessionManager.saveUser(firebaseUserId, newUserProfile.getName(), identifierToSave);
                                        Toast.makeText(LoginScreen1.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                                        handleLoginSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginScreen1.this, "Lỗi tạo hồ sơ người dùng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                    });
                        }
                    } else {
                        Toast.makeText(LoginScreen1.this, "Lỗi khi tải hồ sơ người dùng: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                });
    }
    private void handleLoginSuccess() {
        if (fromCheckout) {
            Intent data = new Intent();
            setResult(RESULT_OK, data);
            finish();
        } else {
            Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REGISTER) {
            if (resultCode == RESULT_OK) {
                // Nếu đăng ký thành công từ LoginScreen2,
                // Firebase Auth sẽ tự động đăng nhập người dùng mới
                // Sau đó có thể chuyển hướng trực tiếp
                if (fromCheckout) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Intent intent = new Intent(LoginScreen1.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Đăng ký bị hủy hoặc không thành công.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
