package com.example.applepie.UI;

import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADDRESS = 100;

    private TextView txtUserName, txtUserAddress, txtChangeAddress;
    private Spinner spinnerPaymentMethod;
    private TextView txtDeliveryDate;
    private LinearLayout cartItemContainer;
    private EditText edtDiscountCode;
    private TextView txtSubtotal, txtDiscount, txtShippingFee, txtTotal;
    private Button btnApplyDiscount, btnCheckout;
    private ImageView imgCheckOutSummaryProduct;

    private int subtotal = 0;
    private int discountAmount = 0;
    private int shippingFee = 15000;

    private ActivityResultLauncher<Intent> paymentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ArrayList<Variant> selectedVariants = (ArrayList<Variant>) getIntent().getSerializableExtra("selectedVariants");

        addViews();
        addEvents();

        // Load sản phẩm & cập nhật giá
        displayCartItems(selectedVariants);
        updatePriceSummary(selectedVariants, discountAmount);

    }

    private void addEvents() {
        // Nút back
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

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
    }

    private void addViews() {
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

    }

    private void applyDiscount() {
        /*if (!isDiscountApplied && edtDiscountCode.getText().toString().equalsIgnoreCase("GIAM60K")) {
            isDiscountApplied = true;
            updatePriceSummary(selectedVariants, discountAmount);*/
            Toast.makeText(this, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
        /*} else {
            Toast.makeText(this, "Mã không hợp lệ hoặc đã áp dụng", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void updatePriceSummary(ArrayList<Variant> selectedVariants, int discountAmount) {
        for (Variant variant : selectedVariants) {
            int price = variant.getPrice();
            int secondPrice = variant.getSecondprice();
            int quantity = variant.getQuantity();

            // Tính giá của từng sản phẩm
            int itemTotalPrice = (secondPrice > 0) ? secondPrice : price;

            // Cộng vào subtotal
            subtotal += itemTotalPrice * quantity;
        }

        // Tính tổng số tiền sau khi giảm giá và vận chuyển
        int total = subtotal - discountAmount + shippingFee;

        // Khởi tạo đối tượng NumberFormat để định dạng số
        NumberFormat numberFormat = NumberFormat.getInstance();

        // Cập nhật giao diện với định dạng có dấu chấm
        txtSubtotal.setText("Giá tiền: " + numberFormat.format(subtotal) + " đ");
        txtDiscount.setText("Giảm giá: - " + numberFormat.format(discountAmount) + " đ");
        txtShippingFee.setText("Vận chuyển: " + numberFormat.format(shippingFee) + " đ");
        txtTotal.setText("Tổng cộng: " + numberFormat.format(total) + " đ");
    }


    private void displayCartItems(ArrayList<Variant> selectedVariants) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout cartItemContainer = findViewById(R.id.cartItemContainer);  // Giả sử container là LinearLayout

        for (Variant variant : selectedVariants) {
            View itemView = inflater.inflate(R.layout.item_checkout_summary, cartItemContainer, false);

            // Ánh xạ các TextView trong item layout
            TextView txtName = itemView.findViewById(R.id.txtName);
            TextView txtSize = itemView.findViewById(R.id.txtSize);
            TextView txtSummaryPrice = itemView.findViewById(R.id.txtSummaryPrice);
            TextView txtSummarySecondPrice = itemView.findViewById(R.id.txtSummarySecondPrice);
            TextView txtQuantity = itemView.findViewById(R.id.txtQuantity);

            FirebaseFirestore.getInstance().collection("Product")
                    .document(variant.getProductid())  // Lấy document dựa trên productId từ Variant
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String pname = documentSnapshot.getString("name");
                        txtName.setText(pname);

                        List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrl");
                        imgCheckOutSummaryProduct= findViewById(R.id.imgCheckOutSummaryProduct);
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            // Dùng Glide để tải ảnh đầu tiên từ danh sách
                            Glide.with(this)  // 'this' là context, có thể là 'ProductDetail.this' hoặc 'getContext()'
                                    .load(imageUrls.get(0))  // Lấy ảnh từ URL đầu tiên trong danh sách
                                    .into(imgCheckOutSummaryProduct);  // Đặt ảnh vào ImageView
                        }
                    });


            txtSize.setText("Biến thể: " + variant.getVariant());
            txtSummaryPrice.setText(String.format("%,d đ", variant.getPrice()));

            // Kiểm tra xem có giá giảm (secondprice) hay không
            if (variant.getSecondprice() > 0) {
                txtSummarySecondPrice.setText(String.format("%,d đ", variant.getSecondprice()));  // Gán giá giảm
                txtSummarySecondPrice.setVisibility(View.VISIBLE);  // Hiển thị giá giảm
                txtSummaryPrice.setPaintFlags(txtSummarySecondPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Gạch ngang giá giảm
            } else {
                txtSummarySecondPrice.setVisibility(View.GONE);  // Ẩn giá giảm nếu không có
            }

            // Gán số lượng
            txtQuantity.setText("x" + variant.getQuantity());

            // Thêm item vào container
            cartItemContainer.addView(itemView);
        }
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
