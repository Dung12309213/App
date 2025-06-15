package com.example.applepie.UI;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.applepie.Adapter.CartItemAdapter;
import com.example.applepie.MainActivity;
import com.example.applepie.Model.CartItem;
import com.example.applepie.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItem> cartItems;
    private EditText editDiscountCode;
    private TextView txtOriginalPrice, txtDiscountAmount, txtFinalPrice;
    private final int DISCOUNT_AMOUNT = 60000;
    private boolean isDiscountApplied = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ImageButton btnBack = findViewById(R.id.imageButton2);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerCartItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        editDiscountCode = findViewById(R.id.editDiscountCode);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtDiscountAmount = findViewById(R.id.txtDiscountAmount);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        Button btnApplyDiscount = findViewById(R.id.btnApplyDiscount);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        cartItems = getMockData();

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


        updatePrice();
    }

    private void updatePrice() {
        int original = 0;
        for (CartItem item : cartItems) {
            original += item.getPrice() * item.getQuantity();
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

        CartItem item = cartItems.get(position);

        ImageView imgProduct = dialog.findViewById(R.id.imgProductDialog);
        TextView txtName = dialog.findViewById(R.id.txtNameDialog);
        TextView txtSize = dialog.findViewById(R.id.txtSizeDialog);
        TextView txtPrice = dialog.findViewById(R.id.txtPriceDialog);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        imgProduct.setImageResource(item.getImageResId());
        txtName.setText(item.getName());
        txtSize.setText(item.getSize());
        txtPrice.setText(formatCurrency(item.getPrice()));

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

    private List<CartItem> getMockData() {
        List<CartItem> list = new ArrayList<>();
        list.add(new CartItem("Mặt nạ nghệ Hưng Yên", "Dung tích: 100ml", 1, 345000, R.drawable.ic_homepage_mau2));
        list.add(new CartItem("Toner hoa cúc nguyên chất", "Dung tích: 150ml", 1, 295000, R.drawable.ic_homepage_mau2));
        return list;
    }
}
