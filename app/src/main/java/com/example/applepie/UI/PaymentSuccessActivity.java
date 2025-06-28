package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.MainActivity;
import com.example.applepie.R;

public class PaymentSuccessActivity extends AppCompatActivity {

    private TextView txtTitle;
    private Button btnTrackOrder;
    private TextView btnContinueShopping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);

        // Ánh xạ view
        txtTitle = findViewById(R.id.txtTitle);
        btnTrackOrder = findViewById(R.id.btnTrackOrder);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);



        // Theo dõi đơn hàng → mở MyOrdersActivity
        btnTrackOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyOrdersActivity.class);
            startActivity(intent);
            finish(); // kết thúc màn thành công để tránh quay lại
        });

        // Tiếp tục mua sắm → về trang chính
        btnContinueShopping.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
