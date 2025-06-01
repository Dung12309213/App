package com.example.applepie.UI;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.applepie.R;
import androidx.appcompat.app.AppCompatActivity;



public class LoginScreenOTP extends AppCompatActivity {

    EditText otp1, otp2, otp3, otp4;
    Button btnVerify;
    ImageButton btnBack;
    TextView txtResend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.applepie.R.layout.activity_login_screen_otp);

        // Ánh xạ view
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);
        txtResend = findViewById(R.id.txtResend);

        // Thiết lập tự động focus
        setupOtpInput();

        // Nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Gửi lại mã
        txtResend.setOnClickListener(v ->
                Toast.makeText(this, getString(R.string.msg_resend_sent), Toast.LENGTH_SHORT).show()
        );

        // Xác nhận OTP
        btnVerify.setOnClickListener(v -> {
            String code = otp1.getText().toString()
                    + otp2.getText().toString()
                    + otp3.getText().toString()
                    + otp4.getText().toString();

            if (code.length() == 4) {
                Toast.makeText(this, getString(R.string.msg_otp_result, code), Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(this, NextActivity.class));
            } else {
                Toast.makeText(this, getString(R.string.msg_enter_full_otp), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInput() {
        otp1.addTextChangedListener(new GenericTextWatcher(otp1, otp2));
        otp2.addTextChangedListener(new GenericTextWatcher(otp2, otp3));
        otp3.addTextChangedListener(new GenericTextWatcher(otp3, otp4));
        otp4.addTextChangedListener(new GenericTextWatcher(otp4, null));
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
