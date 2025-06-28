package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// THÊM IMPORTS CẦN THIẾT
import com.example.applepie.MainActivity;
import com.example.applepie.Model.User; // Để tạo đối tượng User cho Firestore
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender; // Vẫn dùng cho OTP của bạn
import com.example.applepie.Util.UserSessionManager; // Để lưu session sau khi xác minh thành công
import com.google.firebase.auth.FirebaseAuth; // Để có thể get current user
import com.google.firebase.auth.FirebaseUser; // Để thao tác với FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap; // Vẫn có thể cần nếu bạn tạo Map thủ công
import java.util.Map; // Vẫn có thể cần

public class LoginScreenOTP extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button btnVerify;
    ImageButton btnBack;
    TextView txtResend;
    private boolean fromCheckout = false;

    // THÔNG TIN NGƯỜI DÙNG TỪ LOGINSCREEN2 (đã bỏ mật khẩu)
    private String receivedOtpCode;
    private String userEmailForOTP;
    private String userIdFromAuth; // UID từ Firebase Auth
    private String userName;
    private String userPhone;

    // Firebase instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private UserSessionManager sessionManager; // Để quản lý session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen_otp);

        // Lấy dữ liệu được truyền từ LoginScreen2
        if (getIntent() != null) {
            fromCheckout = getIntent().getBooleanExtra("from_checkout", false);
            receivedOtpCode = getIntent().getStringExtra("otpCode");
            userEmailForOTP = getIntent().getStringExtra("userEmailForOTP"); // <-- Lấy email từ LoginScreen2
            userIdFromAuth = getIntent().getStringExtra("userId"); // <-- Lấy UID từ LoginScreen2 (nếu bạn có truyền)
            userName = getIntent().getStringExtra("userName"); // <-- Lấy tên từ LoginScreen2
            userPhone = getIntent().getStringExtra("userPhone"); // <-- Lấy sđt từ LoginScreen2

            // Log để kiểm tra dữ liệu nhận được
            Log.d("LoginScreenOTP", "OTP: " + receivedOtpCode + ", Email: " + userEmailForOTP + ", UID: " + userIdFromAuth);
        }

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseConnector.getInstance();
        sessionManager = new UserSessionManager(this);

        addViews();
        addEvents();
        setupOtpInput();
    }

    private void addEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> {
            // Nếu người dùng hủy ở bước OTP, có thể bạn muốn xóa tài khoản Firebase Auth vừa tạo.
            // Tuy nhiên, việc này phức tạp và có thể không cần thiết cho đồ án.
            // Để đơn giản, chỉ cần hủy và để tài khoản đó ở trạng thái "chưa xác minh email"
            // (nếu bạn dùng Firebase Email Verification) hoặc đơn giản là tồn tại.
            setResult(RESULT_CANCELED);
            finish();
        });

        // Gửi lại mã
        txtResend.setOnClickListener(v -> {
            if (userEmailForOTP != null && !userEmailForOTP.isEmpty()) {
                // Tạo lại OTP mới (random 4 số)
                String newOtp = String.valueOf((int)(Math.random() * 9000) + 1000);
                EmailSender.sendRegisterOTP(userEmailForOTP, newOtp); // Gửi email
                receivedOtpCode = newOtp; // Cập nhật OTP mới
                Toast.makeText(this, getString(R.string.msg_resend_sent), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không có email để gửi lại OTP.", Toast.LENGTH_SHORT).show();
            }
        });

        // Xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            String enteredCode = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();

            if (enteredCode.equals(receivedOtpCode)) {
                // Mã OTP đúng
                // BƯỚC QUAN TRỌNG: LƯU THÔNG TIN PROFILE VÀ ĐĂNG NHẬP SESSION
                finalizeRegistration();
            } else {
                Toast.makeText(LoginScreenOTP.this, getString(R.string.OTP_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // PHƯƠNG THỨC MỚI ĐỂ HOÀN TẤT ĐĂNG KÝ SAU KHI OTP ĐÚNG
    private void finalizeRegistration() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        // Đảm bảo có user đang đăng nhập từ Firebase Auth và có UID/Email
        if (firebaseUser != null && firebaseUser.getUid().equals(userIdFromAuth) && firebaseUser.getEmail().equals(userEmailForOTP)) {
            // Bước 1: Lưu thông tin hồ sơ bổ sung vào Firestore
            User newUserProfile = new User();
            newUserProfile.setName(userName);
            newUserProfile.setPhone(userPhone);
            newUserProfile.setEmail(userEmailForOTP); // Email từ Firebase Auth/LoginScreen2

            // Lưu vào Firestore với Document ID là Firebase Auth UID
            db.collection("User").document(userIdFromAuth)
                    .set(newUserProfile) // Dùng set() để đặt document ID là UID
                    .addOnSuccessListener(aVoid -> {
                        // Bước 2: Lưu thông tin vào UserSessionManager
                        sessionManager.saveUser(userIdFromAuth, userName, userEmailForOTP); // Cập nhật để lưu email

                        Toast.makeText(LoginScreenOTP.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                        Intent intent;
                        if (fromCheckout) {
                            // Nếu đến từ màn hình thanh toán, có thể muốn quay lại với trạng thái đã đăng nhập
                            // Hoặc chuyển thẳng đến MainActivity, tùy vào luồng UX mong muốn.
                            // Ví dụ: Về MainActivity luôn, hoặc về màn hình Checkout (nếu có logic tự động tải lại giỏ hàng)
                            intent = new Intent(LoginScreenOTP.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa hết các activity trước đó
                            startActivity(intent);
                            finish();
                        } else {
                            // Trường hợp đăng ký thông thường
                            intent = new Intent(LoginScreenOTP.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa hết các activity trước đó
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginScreenOTP.this, "Lỗi khi lưu thông tin hồ sơ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        // Tùy chọn: Xóa người dùng Firebase Auth nếu không thể lưu profile
                        // firebaseUser.delete();
                        setResult(RESULT_CANCELED);
                        finish();
                    });
        } else {
            // Lỗi không tìm thấy người dùng Firebase Auth hoặc thông tin không khớp
            Toast.makeText(LoginScreenOTP.this, "Lỗi: Phiên đăng ký không hợp lệ. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            // Có thể đưa người dùng về màn hình đăng ký ban đầu
            setResult(RESULT_CANCELED);
            finish();
        }
    }


    private void addViews() {
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
        txtResend = findViewById(R.id.txtResend);
    }

    private void setupOtpInput() {
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));
    }

    static class GenericTextWatcher implements TextWatcher {
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