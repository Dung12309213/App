package com.example.applepie.UI;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import  com.example.applepie.R;

public class PaymentMethodsActivity extends AppCompatActivity {

    private ImageButton backButton;
    private EditText edtPaymentInput;
    private ImageView cardIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paymentmethods); // Đảm bảo tên file đúng

        // === ÁNH XẠ VIEW ===
        backButton = findViewById(R.id.imageButton2);
        edtPaymentInput = findViewById(R.id.edt_paymentInput2);
        cardIcon = findViewById(R.id.paymentIcon2);

        // === XỬ LÝ SỰ KIỆN NÚT BACK ===
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed(); // Trở về màn trước đó
            }
        });

        // === TÙY CHỌN: TEST NHẬP THẺ MỚI ===
        edtPaymentInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String cardNumber = edtPaymentInput.getText().toString().trim();
                if (!cardNumber.isEmpty()) {
                    Toast.makeText(PaymentMethodsActivity.this, "Thẻ đã nhập: " + cardNumber, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
