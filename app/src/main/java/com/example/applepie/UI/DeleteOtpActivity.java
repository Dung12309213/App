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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.MainActivity;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteOtpActivity extends AppCompatActivity {

    EditText Dotp1, Dotp2, Dotp3, Dotp4;
    Button btnVerify;
    ImageButton btnBack;
    TextView txtResend;
    FirebaseFirestore db;
    private UserSessionManager sessionManager;

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
        addViews();
        addEvents();
        setupOtpInput();
    }

    private void addEvents() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi lại mã
        txtResend.setOnClickListener(v -> {
            String email = getIntent().getStringExtra("email");

            // Tạo lại OTP mới (random 4 số)
            String newOtp = String.valueOf((int)(Math.random() * 9000) + 1000);

            // Gửi email
            EmailSender.sendDeleteOTP(email, newOtp);

            // Gửi lại mã sang activity này nếu bạn muốn cập nhật để so sánh
            getIntent().putExtra("otpCode", newOtp);  // Cập nhật mã mới vào intent

            Toast.makeText(this, getString(R.string.msg_resend_sent), Toast.LENGTH_SHORT).show();
        });
        // Xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            String userId = getIntent().getStringExtra("userId");
            String otpCode = getIntent().getStringExtra("otpCode");
            String code = Dotp1.getText().toString()
                    + Dotp2.getText().toString()
                    + Dotp3.getText().toString()
                    + Dotp4.getText().toString();

            if (code.equals(otpCode)) {
                // Mã OTP đúng
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("User")
                        .document(userId)
                        .delete();
                Toast.makeText(DeleteOtpActivity.this, getString(R.string.User_Delete_Successfull), Toast.LENGTH_SHORT).show();
                sessionManager.logout();
                Intent intent = new Intent(DeleteOtpActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(DeleteOtpActivity.this, getString(R.string.OTP_wrong), Toast.LENGTH_SHORT).show();
            }
        });

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
        Dotp1.addTextChangedListener(new LoginScreenOTP.GenericTextWatcher(Dotp1, Dotp2));
        Dotp2.addTextChangedListener(new LoginScreenOTP.GenericTextWatcher(Dotp2, Dotp3));
        Dotp3.addTextChangedListener(new LoginScreenOTP.GenericTextWatcher(Dotp3, Dotp4));
        Dotp4.addTextChangedListener(new LoginScreenOTP.GenericTextWatcher(Dotp4, null));
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