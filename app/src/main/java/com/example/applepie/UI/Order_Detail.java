//
package com.example.applepie.UI;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.applepie.R;

public class Order_Detail extends AppCompatActivity {

    // Header and main info
    private ImageView backButton;
    private TextView orderStatusTextView;
    private TextView paymentMethodTextView;
    private TextView deliveryNameTextView;
    private TextView deliveryAddressDetailTextView;
    private TextView deliveryPhoneTextView;
    private TextView orderCodeTextView;
    private TextView copyCodeButton;
    private TextView deliveryTimeTextView;
    private TextView totalAmountTextView;
    private Button cancelOrderButton;

    // Hardcoded Product 1 Views
    private ImageView productImageView1;
    private TextView productNameTextView1;
    private TextView productVolumeTextView1;
    private TextView productPriceTextView1;

    // Hardcoded Product 2 Views (if you add more products, declare them here)
    private ImageView productImageView2;
    private TextView productNameTextView2;
    private TextView productVolumeTextView2;
    private TextView productPriceTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);






        // Nút Back
        backButton.setOnClickListener(v -> finish());

        // Nút "Sao chép mã"
        copyCodeButton.setOnClickListener(v -> {
            String orderCodeToCopy = orderCodeTextView.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order Code", orderCodeToCopy);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Đã sao chép mã đơn hàng: " + orderCodeToCopy, Toast.LENGTH_SHORT).show();
            }
        });

        // Nút "HỦY ĐƠN HÀNG"
        cancelOrderButton.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đã nhấn HỦY ĐƠN HÀNG!", Toast.LENGTH_SHORT).show();
            // Không có logic hủy đơn hàng thực sự ở đây, chỉ là thông báo Toast
        });
    }
}