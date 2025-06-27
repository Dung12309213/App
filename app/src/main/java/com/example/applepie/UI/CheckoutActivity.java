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
import com.example.applepie.Model.AddressModel;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public static final String MODE_SELECTION_KEY = "address_selection_mode";
    public static final String MODE_SELECT_ADDRESS = "select_address"; // Mode: chọn địa chỉ

    private ActivityResultLauncher<Intent> paymentLauncher;
    private AddressModel selectedCheckoutAddress;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ArrayList<Variant> selectedVariants = (ArrayList<Variant>) getIntent().getSerializableExtra("selectedVariants");

        userSessionManager = new UserSessionManager(this);
        addViews();
        txtUserName.setText("Tên người nhận (Số điện thoại)");
        txtUserAddress.setText("Địa chỉ giao hàng");
        addEvents();

        // Load sản phẩm & cập nhật giá
        displayCartItems(selectedVariants);
        updatePriceSummary(selectedVariants, discountAmount);
        if (selectedCheckoutAddress == null) {
            loadDefaultAddressForCheckout();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo userSessionManager đã được khởi tạo
        if (userSessionManager == null) {
            userSessionManager = new UserSessionManager(this);
        }

        // QUAN TRỌNG: Gọi loadDefaultAddressForCheckout() chỉ khi CHƯA có địa chỉ nào được chọn.
        // Nếu đã có địa chỉ được chọn (từ onActivityResult), ta chỉ cập nhật hiển thị.
        if (selectedCheckoutAddress == null) {
            loadDefaultAddressForCheckout();
        } else {
            // Nếu đã có địa chỉ được chọn (ví dụ: từ lần trước hoặc vừa quay lại từ AddressActivity),
            // chỉ cần cập nhật lại hiển thị để đảm bảo nó vẫn đúng
            updateAddressDisplay(selectedCheckoutAddress);
        }
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
            Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
            intent.putExtra(MODE_SELECTION_KEY, MODE_SELECT_ADDRESS);
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        // Sự kiện áp dụng mã giảm
        btnApplyDiscount.setOnClickListener(v -> applyDiscount());

        // Sự kiện CHECK OUT: kiểm tra phương thức thanh toán
        btnCheckout.setOnClickListener(v -> {
            if (selectedCheckoutAddress == null) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return; // Ngăn không cho tiếp tục nếu chưa có địa chỉ
            }
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

    private void updatePriceSummary(ArrayList<Variant> selectedVariants, int currentDiscountAmount) {
        subtotal = 0; // SỬA: Reset subtotal về 0 để tính toán lại chính xác

        if (selectedVariants != null) {
            for (Variant variant : selectedVariants) {
                int price = variant.getPrice();
                int secondPrice = variant.getSecondprice();
                int quantity = variant.getQuantity();

                int itemTotalPrice = (secondPrice > 0) ? secondPrice : price;
                subtotal += itemTotalPrice * quantity;
            }
        }

        int total = subtotal - currentDiscountAmount + shippingFee;
        discountAmount = currentDiscountAmount; // SỬA: Cập nhật biến discountAmount của lớp

        // SỬA: Định dạng tiền tệ theo Locale Việt Nam
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        txtSubtotal.setText("Giá tiền: " + numberFormat.format(subtotal) + " đ");
        txtDiscount.setText("Giảm giá: - " + numberFormat.format(discountAmount) + " đ");
        txtShippingFee.setText("Vận chuyển: " + numberFormat.format(shippingFee) + " đ");
        txtTotal.setText("Tổng cộng: " + numberFormat.format(total) + " đ");
    }


    private void displayCartItems(ArrayList<Variant> selectedVariants) {
        if (selectedVariants == null || selectedVariants.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào để hiển thị trong giỏ hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        cartItemContainer.removeAllViews(); // SỬA: Xóa các view cũ trước khi thêm view mới

        for (Variant variant : selectedVariants) {
            View itemView = inflater.inflate(R.layout.item_checkout_summary, cartItemContainer, false);

            TextView txtName = itemView.findViewById(R.id.txtName);
            TextView txtSize = itemView.findViewById(R.id.txtSize);
            TextView txtSummaryPrice = itemView.findViewById(R.id.txtSummaryPrice);
            TextView txtSummarySecondPrice = itemView.findViewById(R.id.txtSummarySecondPrice);
            TextView txtQuantity = itemView.findViewById(R.id.txtQuantity);
            // SỬA: Ánh xạ ImageView riêng cho TỪNG ITEM
            ImageView imgProduct = itemView.findViewById(R.id.imgCheckOutSummaryProduct);

            FirebaseFirestore.getInstance().collection("Product")
                    .document(variant.getProductid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String pname = documentSnapshot.getString("name");
                        txtName.setText(pname);

                        List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrl");
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrls.get(0))
                                    .into(imgProduct); // SỬA: Tải ảnh vào ImageView CỦA ITEM hiện tại
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CheckoutActivity", "Lỗi tải thông tin sản phẩm: " + e.getMessage());
                    });

            txtSize.setText("Biến thể: " + variant.getVariant());
            // SỬA: Sử dụng NumberFormat cho giá
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtSummaryPrice.setText(numberFormat.format(variant.getPrice()) + " đ");

            if (variant.getSecondprice() > 0) {
                txtSummarySecondPrice.setText(numberFormat.format(variant.getSecondprice()) + " đ");
                txtSummarySecondPrice.setVisibility(View.VISIBLE);
                // SỬA: Gạch ngang giá gốc
                txtSummaryPrice.setPaintFlags(txtSummaryPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                txtSummarySecondPrice.setVisibility(View.GONE);
                // SỬA: Bỏ gạch ngang nếu không có giá giảm
                txtSummaryPrice.setPaintFlags(txtSummaryPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            txtQuantity.setText("x" + variant.getQuantity());

            cartItemContainer.addView(itemView);
        }
    }

    private void loadDefaultAddressForCheckout() {
        // SỬA: Lấy userId từ UserSessionManager đã khởi tạo
        String userId = userSessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            txtUserName.setText("Bạn chưa đăng nhập.");
            txtUserAddress.setText("Vui lòng đăng nhập để xem hoặc thêm địa chỉ.");
            return;
        }

        FirebaseFirestore.getInstance().collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        AddressModel defaultAddress = querySnapshot.getDocuments().get(0).toObject(AddressModel.class);
                        if (defaultAddress != null) {
                            selectedCheckoutAddress = defaultAddress; // Lưu địa chỉ mặc định
                            updateAddressDisplay(defaultAddress); // Cập nhật hiển thị UI
                        }
                    } else {
                        txtUserName.setText("Chưa có địa chỉ mặc định");
                        txtUserAddress.setText("Vui lòng thêm địa chỉ giao hàng.");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "Lỗi khi tải địa chỉ mặc định: " + e.getMessage());
                    txtUserName.setText("Lỗi tải địa chỉ.");
                    txtUserAddress.setText("Vui lòng thử lại sau.");
                });
    }

    // --- Hàm mới để cập nhật hiển thị địa chỉ trên UI ---
    private void updateAddressDisplay(AddressModel address) {
        if (address != null) {
            String userNameText = address.getName();
            // THÊM: Chỉ hiển thị số điện thoại nếu có và không rỗng
            if (address.getPhone() != null && !address.getPhone().isEmpty()) {
                userNameText += " (" + address.getPhone() + ")";
            }
            txtUserName.setText(userNameText);

            // THÊM: Xây dựng chuỗi địa chỉ đầy đủ một cách cẩn thận
            StringBuilder addressDetailBuilder = new StringBuilder();
            if (address.getStreet() != null && !address.getStreet().isEmpty()) {
                addressDetailBuilder.append(address.getStreet());
            }
            if (address.getWard() != null && !address.getWard().isEmpty()) {
                if (addressDetailBuilder.length() > 0) addressDetailBuilder.append(", ");
                addressDetailBuilder.append(address.getWard());
            }
            if (address.getDistrict() != null && !address.getDistrict().isEmpty()) {
                if (addressDetailBuilder.length() > 0) addressDetailBuilder.append(", ");
                addressDetailBuilder.append(address.getDistrict());
            }
            if (address.getProvince() != null && !address.getProvince().isEmpty()) {
                if (addressDetailBuilder.length() > 0) addressDetailBuilder.append(", ");
                addressDetailBuilder.append(address.getProvince());
            }
            txtUserAddress.setText(addressDetailBuilder.toString());
        } else {
            txtUserName.setText("Chưa có địa chỉ được chọn");
            txtUserAddress.setText("Vui lòng chọn địa chỉ giao hàng.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK && data != null) {
                // SỬA: Nhận đối tượng AddressModel đã chọn từ AddressActivity
                AddressModel selectedAddress = (AddressModel) data.getSerializableExtra("selected_address_object");
                if (selectedAddress != null) {
                    // QUAN TRỌNG: Gán địa chỉ MỚI ĐƯỢC CHỌN vào biến của lớp
                    selectedCheckoutAddress = selectedAddress;
                    // Cập nhật hiển thị UI với địa chỉ MỚI NÀY
                    updateAddressDisplay(selectedAddress);
                    Toast.makeText(this, "Địa chỉ giao hàng đã được cập nhật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không nhận được dữ liệu địa chỉ.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bạn đã hủy chọn địa chỉ.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}