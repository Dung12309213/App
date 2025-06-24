package com.example.applepie.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Adapter.CartItemAdapter;
import com.example.applepie.MainActivity;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<Variant> cartItems;
    private EditText editDiscountCode;
    private TextView txtOriginalPrice, txtDiscountAmount, txtFinalPrice;
    private final int DISCOUNT_AMOUNT = 60000;
    private boolean isDiscountApplied = false;
    ImageButton btnBack;
    Button btnApplyDiscount;
    Button btnCheckout;
    private UserSessionManager userSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userSessionManager = new UserSessionManager(this);
        // Khởi tạo cartItems rỗng
        cartItems = new ArrayList<Variant>();

        // Gọi phương thức getCartData và sử dụng listener để xử lý dữ liệu sau khi tải xong
        getCartData(cartItems -> {
            if (cartItems != null) {
                this.cartItems = cartItems;  // Cập nhật danh sách giỏ hàng sau khi lấy dữ liệu
                Log.d("CartData", "Giỏ hàng sau khi cập nhật: " + cartItems.toString());
            } else {
                this.cartItems = new ArrayList<>();  // Nếu không có dữ liệu, khởi tạo giỏ hàng rỗng
            }
            addViews();
            addEvents();
            updatePrice();  // Cập nhật giá trị giỏ hàng
            adapter.notifyDataSetChanged();  // Làm mới RecyclerView
        });
    }

    private void addEvents() {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnApplyDiscount.setOnClickListener(v -> {
            String code = editDiscountCode.getText().toString().trim();
            if (!TextUtils.isEmpty(code) && code.equalsIgnoreCase("GIAM60")) {
                isDiscountApplied = true;
                updatePrice();
            }
        });

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            // Nếu cần truyền dữ liệu giỏ hàng qua (chưa serialize), bạn có thể dùng singleton tạm
            startActivity(intent);
        });
    }

    private void addViews() {
        btnBack = findViewById(R.id.imageButton2);

        recyclerView = findViewById(R.id.recyclerCartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(this, cartItems, new CartItemAdapter.OnCartChangeListener() {
            @Override
            public void onCartUpdated() {
                updatePrice();
            }

            @Override
            public void onRequestRemove(int position) {
                showCustomDeleteDialog(position);
            }
        });
        recyclerView.setAdapter(adapter);

        editDiscountCode = findViewById(R.id.editDiscountCode);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtDiscountAmount = findViewById(R.id.txtDiscountAmount);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);

        btnApplyDiscount = findViewById(R.id.btnApplyDiscount);
        btnCheckout = findViewById(R.id.btnCheckout);
    }

    private void updatePrice() {
        if (cartItems == null || cartItems.isEmpty()) {
            // Nếu giỏ hàng trống hoặc null, cập nhật giá trị mặc định hoặc hiển thị thông báo
            txtOriginalPrice.setText("Giỏ hàng trống");
            txtDiscountAmount.setText("Giảm giá: -0 đ");
            txtFinalPrice.setText("Tổng cộng: 0 đ");
            return;  // Dừng hàm nếu giỏ hàng trống
        }

        int original = 0;
        for (Variant item : cartItems) {
            int price = item.getPrice();
            int secondprice = item.getSecondprice();

            // Ưu tiên secondprice nếu có
            int itemPrice = (secondprice > 0) ? secondprice : price;

            // Tính tổng giá của item
            original += itemPrice * item.getQuantity();
        }
        txtOriginalPrice.setText("Giá tiền: " + formatCurrency(original));

        int discount = isDiscountApplied ? DISCOUNT_AMOUNT : 0;
        txtDiscountAmount.setText("Giảm giá: -" + formatCurrency(discount));

        int finalPrice = original - discount;
        txtFinalPrice.setText("Giá tiền: " + formatCurrency(finalPrice));
    }

    private void showCustomDeleteDialog(int position) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_cart);

        Variant item = cartItems.get(position);

        ImageView imgProduct = dialog.findViewById(R.id.imgProductDialog);
        TextView txtName = dialog.findViewById(R.id.txtNameDialog);
        TextView txtSize = dialog.findViewById(R.id.txtSizeDialog);
        TextView txtPrice = dialog.findViewById(R.id.txtPriceDialog);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        // Lấy productId từ Variant
        String productId = item.getProductid();

        // Truy vấn Firestore để lấy tên sản phẩm và ảnh
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Product")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy tên sản phẩm và ảnh
                        String productName = documentSnapshot.getString("name");
                        List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrl");

                        // Cập nhật tên sản phẩm
                        if (productName != null) {
                            txtName.setText(productName);
                        }

                        // Cập nhật ảnh sản phẩm
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            // Lấy ảnh đầu tiên trong mảng imageUrl
                            String firstImageUrl = imageUrls.get(0);

                            // Dùng Glide để tải ảnh vào ImageView
                            Glide.with(CartActivity.this)  // Sử dụng CartActivity context
                                    .load(firstImageUrl)
                                    .into(imgProduct);  // Đặt ảnh vào ImageView
                        }
                    }
                });

        txtSize.setText(item.getVariant());
        int priceToUse = (item.getSecondprice() > 0) ? item.getSecondprice() : item.getPrice();
        txtPrice.setText(formatCurrency(priceToUse));

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            cartItems.remove(position);
            adapter.notifyItemRemoved(position);
            updatePrice();
            dialog.dismiss();
        });

        dialog.show();

// Bổ sung dòng này để ép dialog chiếm tối đa chiều ngang (có margin)
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

    }

    private String formatCurrency(int number) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(number) + " đ";
    }
    // Lấy giỏ hàng từ Firestore theo userId
    private void getCartData(OnCartDataFetchedListener listener) {
        String userId = userSessionManager.getUserId(); // Lấy userId từ UserSessionManager

        if (userId.isEmpty()) {
            // Người dùng chưa đăng nhập, không lấy dữ liệu từ Firestore
            listener.onCartDataFetched(new ArrayList<>()); // Trả về giỏ hàng rỗng
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("User") // Giả sử bạn đang dùng collection "users"
                .document(userId)
                .collection("Cart") // Giả sử giỏ hàng của người dùng được lưu trong subcollection "Cart"
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Variant> cartItems = queryDocumentSnapshots.toObjects(Variant.class);

                    // Duyệt qua từng item trong cartItems và cập nhật giá trị
                    for (Variant item : cartItems) {
                        String productId = item.getProductid();
                        String variantId = item.getId();  // Assuming variant has its own id

                        // Truy vấn thông tin về price, quantity, variant từ subcollection "Variants"
                        db.collection("Product")
                                .document(productId)
                                .collection("Variant")
                                .document(variantId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        // Cập nhật thông tin price, quantity, và variant vào item
                                        int price = documentSnapshot.contains("price") ? documentSnapshot.getLong("price").intValue() : 0;
                                        int secondprice = documentSnapshot.contains("secondprice") ? documentSnapshot.getLong("secondprice").intValue() : 0;
                                        String variant = documentSnapshot.getString("variant");

                                        item.setPrice(price);
                                        item.setSecondprice(secondprice);
                                        item.setVariant(variant);
                                        listener.onCartDataFetched(cartItems);
                                    }
                                });
                    }

                });

    }
    public interface OnCartDataFetchedListener {
        void onCartDataFetched(List<Variant> cartItems);
    }
}
