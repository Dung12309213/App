package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // Import Log
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
import com.example.applepie.MainActivity;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.UserSessionManager;

// THÊM IMPORTS FIREBASE AUTH
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteOtpActivity extends BaseActivity {

    EditText Dotp1, Dotp2, Dotp3, Dotp4;
    Button btnVerify;
    ImageButton btnBack;
    TextView txtResend;
    FirebaseFirestore db;
    private UserSessionManager sessionManager;

    // THÊM BIẾN FIREBASE AUTH
    private FirebaseAuth mAuth;

    private String userIdToDelete;
    private String otpCodeExpected;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new UserSessionManager(this);
        mAuth = FirebaseAuth.getInstance(); // KHỞI TẠO FIREBASE AUTH
        db = FirebaseFirestore.getInstance(); // KHỞI TẠO FIRESTORE

        // Lấy dữ liệu từ Intent
        userIdToDelete = getIntent().getStringExtra("userId");
        otpCodeExpected = getIntent().getStringExtra("otpCode");
        userEmail = getIntent().getStringExtra("email");

        addViews();
        addEvents();
        setupOtpInput();
    }

    private void addEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi lại mã
        txtResend.setOnClickListener(v -> {
            if (userEmail != null && !userEmail.isEmpty()) {
                // Tạo lại OTP mới (random 4 số)
                String newOtp = String.valueOf((int)(Math.random() * 9000) + 1000);
                EmailSender.sendDeleteOTP(userEmail, newOtp);
                otpCodeExpected = newOtp; // Cập nhật mã OTP kỳ vọng
                Toast.makeText(this, getString(R.string.msg_resend_sent), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không tìm thấy email để gửi OTP.", Toast.LENGTH_SHORT).show();
            }
        });

        // Xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            String enteredCode = Dotp1.getText().toString()
                    + Dotp2.getText().toString()
                    + Dotp3.getText().toString()
                    + Dotp4.getText().toString();

            if (enteredCode.equals(otpCodeExpected)) {
                // Mã OTP đúng, tiến hành xoá tài khoản Firebase Auth và Firestore
                deleteUserAccount();
            } else {
                Toast.makeText(DeleteOtpActivity.this, getString(R.string.OTP_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        // Kiểm tra xem người dùng hiện tại có phải là người đang muốn xóa không
        if (user != null && user.getUid().equals(userIdToDelete)) {
            // Bước 1: Xóa tài khoản Firebase Authentication
            user.delete()
                    .addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            Log.d("DeleteAccount", "User account deleted from Firebase Auth.");

                            // Bước 2: Xóa tài liệu người dùng trong Firestore
                            db.collection("User").document(userIdToDelete).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("DeleteAccount", "User profile deleted from Firestore.");
                                        Toast.makeText(DeleteOtpActivity.this, getString(R.string.User_Delete_Successfull), Toast.LENGTH_SHORT).show();

                                        // Bước 3: Xóa session người dùng trong ứng dụng
                                        sessionManager.logout();

                                        // Bước 4: Chuyển hướng về màn hình chính hoặc màn hình đăng nhập
                                        Intent intent = new Intent(DeleteOtpActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa hết các activity cũ
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("DeleteAccount", "Error deleting user profile from Firestore.", e);
                                        Toast.makeText(DeleteOtpActivity.this, "Lỗi khi xóa hồ sơ người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        // Nếu xóa Firestore profile thất bại, tài khoản Auth vẫn bị xóa.
                                        // Cần một cơ chế xử lý lỗi phức tạp hơn cho ứng dụng thực tế.
                                    });
                        } else {
                            Log.e("DeleteAccount", "Error deleting user account from Firebase Auth: " + authTask.getException().getMessage());
                            Toast.makeText(DeleteOtpActivity.this, "Lỗi khi xóa tài khoản: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // FirebaseAuth có thể yêu cầu re-authenticate nếu phiên quá cũ cho thao tác nhạy cảm này.
                            // Nếu authTask.getException() là FirebaseAuthRecentLoginRequiredException, bạn cần yêu cầu người dùng đăng nhập lại.
                        }
                    });
        } else {
            // Lỗi: Người dùng hiện tại không khớp hoặc không đăng nhập.
            Toast.makeText(DeleteOtpActivity.this, "Lỗi phiên: Vui lòng đăng nhập lại và thử xoá.", Toast.LENGTH_LONG).show();
            sessionManager.logout(); // Đảm bảo session được xóa
            Intent intent = new Intent(DeleteOtpActivity.this, LoginScreen1.class); // Về màn hình đăng nhập
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }


    private void addViews() {
        Dotp1 = findViewById(R.id.Dotp1);
        Dotp2 = findViewById(R.id.Dotp2);
        Dotp3 = findViewById(R.id.Dotp3);
        Dotp4 = findViewById(R.id.Dotp4);
        btnVerify = findViewById(R.id.btnDeleteVerify);
        btnBack = findViewById(R.id.btnDeleteBack);
        txtResend = findViewById(R.id.txtDeleteResend);
    }
    private void setupOtpInput() {
        Dotp1.addTextChangedListener(new GenericTextWatcher(Dotp1, Dotp2));
        Dotp2.addTextChangedListener(new GenericTextWatcher(Dotp2, Dotp3));
        Dotp3.addTextChangedListener(new GenericTextWatcher(Dotp3, Dotp4));
        Dotp4.addTextChangedListener(new GenericTextWatcher(Dotp4, null));
    }

    private static class GenericTextWatcher implements TextWatcher {
        private final EditText currentView;
        private final EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}