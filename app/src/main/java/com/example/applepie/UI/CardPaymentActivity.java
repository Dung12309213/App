package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class CardPaymentActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtTitle;
    private EditText edtCardHolder, edtCardNumber, edtExpiry, edtCVV;
    private CheckBox checkSaveCard;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        // Ánh xạ view
        btnBack = findViewById(R.id.btnBack);
        txtTitle = findViewById(R.id.txtTitle);
        edtCardHolder = findViewById(R.id.edtCardHolder);
        edtCardNumber = findViewById(R.id.edtCardNumber);
        edtExpiry = findViewById(R.id.edtExpiry);
        edtCVV = findViewById(R.id.edtCVV);
        checkSaveCard = findViewById(R.id.checkSaveCard);
        btnConfirm = findViewById(R.id.btnConfirm);

        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Nút xác nhận
        btnConfirm.setOnClickListener(v -> handleConfirm());
    }

    private void handleConfirm() {
        String cardHolder = edtCardHolder.getText().toString().trim();
        String cardNumber = edtCardNumber.getText().toString().trim();
        String expiry = edtExpiry.getText().toString().trim();
        String cvv = edtCVV.getText().toString().trim();
        boolean saveCard = checkSaveCard.isChecked();

        if (TextUtils.isEmpty(cardHolder) || TextUtils.isEmpty(cardNumber)
                || TextUtils.isEmpty(expiry) || TextUtils.isEmpty(cvv)) {
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

        // Trả dữ liệu về CheckoutActivity
        Intent result = new Intent();
        result.putExtra("cardHolder", cardHolder);
        result.putExtra("cardNumber", cardNumber);
        result.putExtra("expiry", expiry);
        result.putExtra("cvv", cvv);
        result.putExtra("saveCard", saveCard);
        setResult(RESULT_OK, result);
        finish(); // Chỉ kết thúc, KHÔNG mở thêm gì tại đây
    }
}
