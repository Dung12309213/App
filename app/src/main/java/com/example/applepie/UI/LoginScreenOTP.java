package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Connector.FirebaseConnector;
import  com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class LoginScreenOTP extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button btnVerify;
    ImageButton btnBack;
    TextView txtResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen_otp);

        addViews();
        addEvents();

        // Thiết lập tự động focus
        setupOtpInput();


    }

    private void addEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi lại mã
        txtResend.setOnClickListener(v -> {
            Map<String, Object> userMap = (Map<String, Object>) getIntent().getSerializableExtra("userMap");
            String email = (String) userMap.get("email"); // Lấy email từ userMap

            // Tạo lại OTP mới (random 4 số)
            String newOtp = String.valueOf((int)(Math.random() * 9000) + 1000);

            // Gửi email
            EmailSender.sendRegisterOTP(email, newOtp);

            // Gửi lại mã sang activity này nếu bạn muốn cập nhật để so sánh
            getIntent().putExtra("otpCode", newOtp);  // Cập nhật mã mới vào intent

            Toast.makeText(this, getString(R.string.msg_resend_sent), Toast.LENGTH_SHORT).show();
        });

        // Xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            Map<String, Object> userMap = (Map<String, Object>) getIntent().getSerializableExtra("userMap");
            String otpCode = getIntent().getStringExtra("otpCode");
            String code = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();

            if (code.equals(otpCode)) {
                // Mã OTP đúng, lưu người dùng vào Firestore
                saveUserToFirestore(userMap);  // Lưu thông tin người dùng vào Firestore
                Toast.makeText(LoginScreenOTP.this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginScreenOTP.this, getString(R.string.OTP_wrong), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirestore(Map<String, Object> userMap) {
        FirebaseFirestore db = FirebaseConnector.getInstance();

        db.collection("User")
                .add(userMap)  // Thêm userMap vào Firestore
                .addOnSuccessListener(documentReference -> {
                    // Thành công
                    Intent intent = new Intent(LoginScreenOTP.this, LoginScreen1.class);
                    startActivity(intent);
                });
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
