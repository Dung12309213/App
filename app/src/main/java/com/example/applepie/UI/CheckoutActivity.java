package com.example.applepie.UI;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.Model.Delivery;
import com.example.applepie.Model.OrderItem;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.Model.Variant;
import com.example.applepie.Model.Voucher;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends BaseActivity {

    private static final int REQUEST_CODE_ADDRESS = 100;
    // Mã yêu cầu cho Activity đăng nhập/đăng ký
    private static final int REQUEST_CODE_LOGIN_REGISTER = 101;


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
    public static final String MODE_SELECT_ADDRESS = "select_address";

    private ActivityResultLauncher<Intent> paymentLauncher;
    private AddressModel selectedCheckoutAddress;
    private UserSessionManager userSessionManager;
    private ArrayList<Variant> currentSelectedVariants;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        currentSelectedVariants = (ArrayList<Variant>) getIntent().getSerializableExtra("selectedVariants");

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();
        addViews();

        // Ban đầu, hiển thị thông báo để chọn/nhập địa chỉ
        txtUserName.setText("Bạn chưa chọn địa chỉ");
        txtUserAddress.setText("Vui lòng chọn hoặc thêm địa chỉ giao hàng.");

        addEvents();

        // Load sản phẩm & cập nhật giá
        displayCartItems(currentSelectedVariants);
        updateDeliveryDateDisplay();
        updatePriceSummary(currentSelectedVariants, discountAmount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đảm bảo userSessionManager đã được khởi tạo
        if (userSessionManager == null) {
            userSessionManager = new UserSessionManager(this);
        }

        // Tải địa chỉ mặc định chỉ khi người dùng đã đăng nhập VÀ CHƯA CÓ địa chỉ nào được chọn trong phiên này
        if (selectedCheckoutAddress == null && userSessionManager.isLoggedIn()) {
            loadDefaultAddressForCheckout();
        } else if (selectedCheckoutAddress != null) {
            // Nếu đã có địa chỉ được chọn (từ lần trước hoặc vừa quay lại từ AddressActivity),
            // chỉ cần cập nhật lại hiển thị để đảm bảo nó vẫn đúng
            updateAddressDisplay(selectedCheckoutAddress);
        }
        // Nếu người dùng chưa đăng nhập và cũng chưa có địa chỉ nào được chọn (selectedCheckoutAddress == null),
        // thì không làm gì cả, giữ nguyên text "Bạn chưa chọn địa chỉ"
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
                            // HOÀN TẤT ĐƠN HÀNG Ở ĐÂY
                            finalizeOrder();
                        }
                    }
                }
        );

        // Sự kiện "Thay đổi địa chỉ"
        txtChangeAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
            intent.putExtra(MODE_SELECTION_KEY, MODE_SELECT_ADDRESS);

            // Truyền địa chỉ hiện tại (nếu có) để AddressActivity biết được địa chỉ nào đang được chọn
            if (selectedCheckoutAddress != null) {
                intent.putExtra("currently_selected_address", selectedCheckoutAddress);
            }
            startActivityForResult(intent, REQUEST_CODE_ADDRESS);
        });

        // Sự kiện áp dụng mã giảm
        btnApplyDiscount.setOnClickListener(v -> applyDiscount());

        // Sự kiện CHECK OUT: kiểm tra phương thức thanh toán VÀ ĐĂNG NHẬP/ĐĂNG KÝ
        btnCheckout.setOnClickListener(v -> {
            if (selectedCheckoutAddress == null) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!userSessionManager.isLoggedIn()) {
                // Nếu chưa đăng nhập, chuyển sang màn hình đăng nhập/đăng ký
                Toast.makeText(this, "Vui lòng đăng nhập hoặc đăng ký để hoàn tất đơn hàng.", Toast.LENGTH_LONG).show();
                Intent loginRegisterIntent = new Intent(CheckoutActivity.this, LoginScreen1.class); // Hoặc RegisterActivity
                // Bạn có thể thêm cờ để LoginActivity biết đây là từ Checkout
                loginRegisterIntent.putExtra("from_checkout", true);
                startActivityForResult(loginRegisterIntent, REQUEST_CODE_LOGIN_REGISTER);
                return; // Dừng lại, chờ kết quả đăng nhập
            }

            // Nếu đã đăng nhập, tiến hành xử lý thanh toán
            String selectedMethod = spinnerPaymentMethod.getSelectedItem().toString().trim();
            finalizeOrder();
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
        String voucherCode = edtDiscountCode.getText().toString().trim();

        if (voucherCode.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mã giảm giá.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Truy vấn collection "discount"
        db.collection("Discount")
                .whereEqualTo("code", voucherCode) // Tìm voucher theo mã
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Tìm thấy voucher
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Voucher voucher = document.toObject(Voucher.class);

                        if (voucher != null && voucher.getAmount() > 0) {
                            discountAmount = (int) voucher.getAmount(); // Áp dụng số tiền giảm giá
                            updatePriceSummary(currentSelectedVariants, discountAmount);
                            Toast.makeText(CheckoutActivity.this, "Áp dụng mã giảm giá thành công: -" +
                                    NumberFormat.getInstance(new Locale("vi", "VN")).format(discountAmount) + " đ", Toast.LENGTH_LONG).show();
                        } else {
                            // Voucher không hợp lệ (ví dụ: số tiền giảm giá <= 0)
                            Toast.makeText(CheckoutActivity.this, "Mã giảm giá không hợp lệ.", Toast.LENGTH_SHORT).show();
                            discountAmount = 0; // Đặt lại giảm giá
                            updatePriceSummary(currentSelectedVariants, discountAmount);
                        }
                    } else {
                        // Không tìm thấy voucher
                        Toast.makeText(CheckoutActivity.this, "Mã giảm giá không tồn tại.", Toast.LENGTH_SHORT).show();
                        discountAmount = 0; // Đặt lại giảm giá
                        updatePriceSummary(currentSelectedVariants, discountAmount);
                    }
                })
                .addOnFailureListener(e -> {
                    // Lỗi khi truy vấn Firestore
                    Log.e("CheckoutActivity", "Lỗi khi truy vấn mã giảm giá: " + e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Lỗi khi áp dụng mã giảm giá. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                    discountAmount = 0; // Đặt lại giảm giá khi có lỗi
                    updatePriceSummary(currentSelectedVariants, discountAmount);
                });
    }

    private void updatePriceSummary(ArrayList<Variant> selectedVariants, int currentDiscountAmount) {
        subtotal = 0;
        discountAmount = currentDiscountAmount;

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
        discountAmount = currentDiscountAmount;

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
        cartItemContainer.removeAllViews();

        for (Variant variant : selectedVariants) {
            View itemView = inflater.inflate(R.layout.item_checkout_summary, cartItemContainer, false);

            TextView txtName = itemView.findViewById(R.id.txtName);
            TextView txtSize = itemView.findViewById(R.id.txtSize);
            TextView txtSummaryPrice = itemView.findViewById(R.id.txtSummaryPrice);
            TextView txtSummarySecondPrice = itemView.findViewById(R.id.txtSummarySecondPrice);
            TextView txtQuantity = itemView.findViewById(R.id.txtQuantity);
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
                                    .into(imgProduct);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("CheckoutActivity", "Lỗi tải thông tin sản phẩm: " + e.getMessage());
                    });

            txtSize.setText("Biến thể: " + variant.getVariant());
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
            txtSummaryPrice.setText(numberFormat.format(variant.getPrice()) + " đ");

            if (variant.getSecondprice() > 0) {
                txtSummarySecondPrice.setText(numberFormat.format(variant.getSecondprice()) + " đ");
                txtSummarySecondPrice.setVisibility(View.VISIBLE);
                txtSummaryPrice.setPaintFlags(txtSummaryPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                txtSummarySecondPrice.setVisibility(View.GONE);
                txtSummaryPrice.setPaintFlags(txtSummaryPrice.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            txtQuantity.setText("x" + variant.getQuantity());

            cartItemContainer.addView(itemView);
        }
    }

    private void loadDefaultAddressForCheckout() {
        String userId = userSessionManager.getUserId();

        if (userId == null || userId.isEmpty()) {
            // Không làm gì nếu chưa đăng nhập, để người dùng tự chọn/thêm
            txtUserName.setText("Bạn chưa chọn địa chỉ");
            txtUserAddress.setText("Vui lòng chọn hoặc thêm địa chỉ giao hàng.");
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
                            selectedCheckoutAddress = defaultAddress;
                            updateAddressDisplay(defaultAddress);
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

    private void updateAddressDisplay(AddressModel address) {
        if (address != null) {
            String userNameText = address.getName();
            if (address.getPhone() != null && !address.getPhone().isEmpty()) {
                userNameText += " (" + address.getPhone() + ")";
            }
            txtUserName.setText(userNameText);

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

    // Phương thức chung để hoàn tất đơn hàng
    private void finalizeOrder() {

        String userId = userSessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            // Trường hợp này không xảy ra nếu đã kiểm tra ở nút checkout, nhưng vẫn nên có
            Toast.makeText(this, "Lỗi: Người dùng chưa đăng nhập để lưu đơn hàng.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        OrderModel order = new OrderModel();
        order.setUserid(userId);
        order.setPurchasedate(new Date());
        order.setDiscountid(edtDiscountCode.getText().toString());
        order.setStatus("Đang xử lý");
        order.setPaymentMethod(spinnerPaymentMethod.getSelectedItem().toString());
        order.setTotal(subtotal - discountAmount + shippingFee);

        db.collection("Order")
                .add(order)
                .addOnSuccessListener(documentReference -> {
                    String orderId = documentReference.getId(); // Lấy ID của Order vừa tạo
                    Log.d("CheckoutActivity", "Đơn hàng đã được tạo thành công với ID: " + orderId);

                    // 2. Tạo subcollection "Items" và thêm OrderItem vào
                    if (currentSelectedVariants != null && !currentSelectedVariants.isEmpty()) {
                        for (Variant variant : currentSelectedVariants) {
                            OrderItem orderItem = new OrderItem();
                            orderItem.setOrderId(orderId); // Liên kết OrderItem với Order chính
                            orderItem.setProductid(variant.getProductid());
                            orderItem.setVariantid(variant.getId());
                            orderItem.setQuantity(variant.getQuantity());
                            orderItem.setPrice(variant.getPrice());
                            orderItem.setSecondPrice(variant.getSecondprice());

                            // Thêm OrderItem vào subcollection "Item"
                            documentReference.collection("Item")
                                    .add(orderItem)
                                    .addOnSuccessListener(itemRef -> Log.d("CheckoutActivity", "OrderItem thêm thành công: " + itemRef.getId()))
                                    .addOnFailureListener(e -> Log.e("CheckoutActivity", "Lỗi thêm OrderItem: " + e.getMessage()));
                        }
                    }

                    // 3. Tạo subcollection "Delivery" và thêm Delivery Model vào
                    if (selectedCheckoutAddress != null) {
                        Delivery delivery = new Delivery();
                        delivery.setOrderId(orderId);
                        delivery.setDeliveryStatus("Đang xử lý");
                        delivery.setShippingProvider("Giao hàng nhanh");
                        // Gán các trường địa chỉ từ selectedCheckoutAddress
                        delivery.setAddressStreet(selectedCheckoutAddress.getStreet());
                        delivery.setAddressWard(selectedCheckoutAddress.getWard());
                        delivery.setAddressDistrict(selectedCheckoutAddress.getDistrict());
                        delivery.setAddressProvince(selectedCheckoutAddress.getProvince());
                        delivery.setDeliveryName(selectedCheckoutAddress.getName());
                        delivery.setDeliveryPhone(selectedCheckoutAddress.getPhone());

                        java.util.Calendar calendar = java.util.Calendar.getInstance();
                        calendar.setTime(order.getPurchasedate()); // Sử dụng ngày mua hàng của đơn hàng
                        calendar.add(java.util.Calendar.DAY_OF_YEAR, 5); // Thêm 5 ngày
                        delivery.setDelivery_date_estimated(calendar.getTime());

                        delivery.setDelivery_date_actual(null);

                        // Thêm Delivery vào subcollection "Delivery"
                        documentReference.collection("Delivery")
                                .add(delivery)
                                .addOnSuccessListener(deliveryRef -> Log.d("CheckoutActivity", "Delivery thêm thành công: " + deliveryRef.getId()))
                                .addOnFailureListener(e -> Log.e("CheckoutActivity", "Lỗi thêm Delivery: " + e.getMessage()));
                    }

                    Toast.makeText(CheckoutActivity.this, "Đơn hàng của bạn đã được đặt thành công!", Toast.LENGTH_LONG).show();
                    Intent successIntent = new Intent(CheckoutActivity.this, PaymentSuccessActivity.class);
                    startActivity(successIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("CheckoutActivity", "Lỗi khi tạo đơn hàng: " + e.getMessage());
                    Toast.makeText(CheckoutActivity.this, "Có lỗi xảy ra khi đặt hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADDRESS) {
            if (resultCode == RESULT_OK && data != null) {
                AddressModel selectedAddress = (AddressModel) data.getSerializableExtra("selected_address_object");
                if (selectedAddress != null) {
                    selectedCheckoutAddress = selectedAddress;
                    updateAddressDisplay(selectedAddress);
                    Toast.makeText(this, "Địa chỉ giao hàng đã được cập nhật!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không nhận được dữ liệu địa chỉ.", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bạn đã hủy chọn địa chỉ.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_LOGIN_REGISTER) {
            if (resultCode == RESULT_OK) {

                Toast.makeText(this, "Đăng nhập thành công! Vui lòng nhấn Checkout để hoàn tất đơn hàng.", Toast.LENGTH_LONG).show();
                if (userSessionManager.isLoggedIn() && selectedCheckoutAddress == null) {
                    loadDefaultAddressForCheckout();
                }
                updateDeliveryDateDisplay();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bạn đã hủy đăng nhập/đăng ký.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void updateDeliveryDateDisplay() {
        // Lấy ngày hiện tại
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        // Thêm 5 ngày vào ngày hiện tại để có ngày giao hàng ước tính
        calendar.add(java.util.Calendar.DAY_OF_YEAR, 5);
        java.util.Date estimatedDeliveryDate = calendar.getTime();

        // Định dạng ngày để hiển thị
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(estimatedDeliveryDate);

        txtDeliveryDate.setText("Ngày giao hàng dự kiến: " + formattedDate);
    }
}