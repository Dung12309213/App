package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class CheckoutActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADDRESS = 100;

    private TextView txtUserName, txtUserAddress, txtChangeAddress;
    private Spinner spinnerPaymentMethod;
    private TextView txtDeliveryDate;
    private LinearLayout cartItemContainer;
    private EditText edtDiscountCode;
    private TextView txtSubtotal, txtDiscount, txtShippingFee, txtTotal;
    private Button btnApplyDiscount, btnCheckout;

    private int subtotal = 1035000;
    private int discountAmount = 60000;
    private int shippingFee = 15000;
    private boolean isDiscountApplied = false;

    private ActivityResultLauncher<Intent> paymentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Nút back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ view
        txtUserName = findViewById(R.id.txtUserName);
        txtUserAddress = findViewById(R.id.txtUserAddress);
        txtChangeAddress = findViewById(R.id.txtChangeAddress);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        txtDeliveryDate = findViewById(R.id.txtDeliveryDate);
        cartItemContainer = findViewById(R.id.cartItemContainer);
        edtDiscountCode = findViewById(R.id.edtDiscountCode);
        txtSubtotal = findViewById(R.id.txtSubtotal);
        txtDiscount = findViewById(R.id.txtDiscount);
        txtShippingFee = findViewById(R.id.txtShippingFee);
        txtTotal = findViewById(R.id.txtTotal);
        btnApplyDiscount = findViewById(R.id.btnApplyDiscount);
        btnCheckout = findViewById(R.id.btnCheckout);

        // Launcher cho CardPaymentActivity
        paymentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String cardHolder = result.getData().getStringExtra("cardHolder");
                        String cardNumber = result.getData().getStringExtra("cardNumber");

                        if (cardHolder != null && cardNumber != null && cardNumber.length() >= 4) {
                            String last4 = cardNumber.substring(cardNumber.length() - 4);
                            Toast.makeText(this, "Đã chọn thẻ: " + cardHolder + " •••• " + last4, Toast.LENGTH_LONG).show();

                            // Sau khi chọn thẻ → chuyển sang màn thành công
                            Intent successIntent = new Intent(this, PaymentSuccessActivity.class);
                            startActivity(successIntent);
                            finish();
                        }
                    }
                }
        );

        // Sự kiện "Thay đổi địa chỉ"
        txtChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        // Sự kiện áp dụng mã giảm
        btnApplyDiscount.setOnClickListener(v -> applyDiscount());

        // Sự kiện CHECK OUT: kiểm tra phương thức thanh toán
        btnCheckout.setOnClickListener(v -> {
            String selectedMethod = spinnerPaymentMethod.getSelectedItem().toString().trim();

            if (selectedMethod.equalsIgnoreCase("Thẻ tín dụng")) {
                Intent intent = new Intent(CheckoutActivity.this, CardPaymentActivity.class);
                paymentLauncher.launch(intent);
            } else {
                // Chuyển đến màn hình thanh toán thành công luôn
                Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
                startActivity(intent);
                finish(); // không quay lại Checkout nữa
            }
        });

        // Load sản phẩm & cập nhật giá
        loadCartItems();
        updatePriceSummary();
    }

    private void applyDiscount() {
        if (!isDiscountApplied && edtDiscountCode.getText().toString().equalsIgnoreCase("GIAM60K")) {
            isDiscountApplied = true;
            updatePriceSummary();
            Toast.makeText(this, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mã không hợp lệ hoặc đã áp dụng", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePriceSummary() {
        txtSubtotal.setText("Giá tiền: " + subtotal + " đ");
        txtDiscount.setText("Giảm giá: - " + (isDiscountApplied ? discountAmount : 0) + " đ");
        txtShippingFee.setText("Vận chuyển: " + shippingFee + " đ");

        int total = subtotal - (isDiscountApplied ? discountAmount : 0) + shippingFee;
        txtTotal.setText("Tổng cộng: " + total + " đ");
    }

    private void loadCartItems() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemView1 = inflater.inflate(R.layout.item_checkout_summary, cartItemContainer, false);

        TextView txtName1 = itemView1.findViewById(R.id.txtName);
        TextView txtSize1 = itemView1.findViewById(R.id.txtSize);
        TextView txtPrice1 = itemView1.findViewById(R.id.txtPrice);
        TextView txtQuantity1 = itemView1.findViewById(R.id.txtQuantity);

        txtName1.setText("Mặt nạ nghệ Hưng Yên");
        txtSize1.setText("Dung tích: 100ml");
        txtPrice1.setText("690.000 đ");
        txtQuantity1.setText("x2");

        cartItemContainer.addView(itemView1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_CODE_ADDRESS) {
                String name = data.getStringExtra("userName");
                String address = data.getStringExtra("userAddress");
                String phone = data.getStringExtra("userPhone");

                if (name != null) txtUserName.setText(name);
                if (address != null && phone != null) {
                    txtUserAddress.setText(address + "\n" + phone);
                }
            }
        }
    }
}
