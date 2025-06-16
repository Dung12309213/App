package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class CardPaymentActivity extends AppCompatActivity {

    private EditText edtCardHolder, edtCardNumber, edtExpiry, edtCVV;
    private TextView txtCardHolderMock, txtCardNumberMock, txtExpiryMock;
    private CheckBox checkSaveCard;
    private Button btnConfirm;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        // Ánh xạ view
        edtCardHolder = findViewById(R.id.edtCardHolder);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtExpiry = findViewById(R.id.edtExpiry);
        edtCVV = findViewById(R.id.edtCVV);
        checkSaveCard = findViewById(R.id.checkSaveCard);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnBack = findViewById(R.id.btnBack);

        // Mock TextViews
        txtCardHolderMock = findViewById(R.id.txtCardHolderMock);
        txtCardNumberMock = findViewById(R.id.txtCardNumberMock);
        txtExpiryMock = findViewById(R.id.txtExpiryMock);

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Đồng bộ thẻ ảo khi nhập liệu
        setupLivePreview();

        // Nút xác nhận
        btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    private void setupLivePreview() {
        edtCardHolder.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtCardHolderMock.setText(s.toString());
            }
        });

        edtCardNumber.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Format thẻ dạng 4-4-4-4
                String raw = s.toString().replaceAll("\\s+", "");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < raw.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(raw.charAt(i));
                }
                txtCardNumberMock.setText(formatted.toString());
            }
        });

        edtExpiry.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtExpiryMock.setText(s.toString());
            }
        });
    }

    private void handleConfirm() {
        String cardHolder = edtCardHolder.getText().toString().trim();
        String cardNumber = edtCardNumber.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String cvv = edtCVV.getText().toString().trim();
        boolean saveCard = checkSaveCard.isChecked();

        if (cardHolder.isEmpty() || cardNumber.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin thẻ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNumber.length() < 12 || cardNumber.length() > 19) {
            Toast.makeText(this, "Số thẻ không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() != 3) {
            Toast.makeText(this, "CVV phải gồm 3 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        // Trả dữ liệu về CheckoutActivity nếu cần
        Intent result = new Intent();
        result.putExtra("cardHolder", cardHolder);
        result.putExtra("cardNumber", cardNumber);
        result.putExtra("expiry", expiry);
        result.putExtra("cvv", cvv);
        result.putExtra("saveCard", saveCard);
        setResult(RESULT_OK, result);

        // Chuyển sang màn hình thanh toán thành công
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        startActivity(intent);
        finish();
    }

    // Adapter để rút gọn TextWatcher
    abstract static class TextWatcherAdapter implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }
}
