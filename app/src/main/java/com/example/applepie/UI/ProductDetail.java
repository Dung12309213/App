package com.example.applepie.UI;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class ProductDetail extends AppCompatActivity {

    private TextView tvDesc;
    private TextView tvSeeMore;
    private LinearLayout headerIngredients;
    private TextView tvIngredientsDetail;
    private ImageView arrowIngredients;
    private LinearLayout headerInstruction;
    private TextView tvInstructionDetail;
    private ImageView arrowInstruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        tvDesc = findViewById(R.id.DetailProductDesc);
        tvSeeMore = findViewById(R.id.tvSeeMore);
        headerIngredients = findViewById(R.id.headerIngredients);
        tvIngredientsDetail = findViewById(R.id.tvIngredientsDetail);
        arrowIngredients = findViewById(R.id.arrowIngredients);
        headerInstruction = findViewById(R.id.headerInstruction);
        tvInstructionDetail = findViewById(R.id.tvInstructionDetail);
        arrowInstruction = findViewById(R.id.arrowInstruction);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sự kiện "Xem thêm"
        tvSeeMore.setOnClickListener(v -> {
            boolean isExpanded = tvDesc.getMaxLines() == Integer.MAX_VALUE;
            if (isExpanded) {
                tvDesc.setMaxLines(2);
                tvDesc.setEllipsize(TextUtils.TruncateAt.END);
                tvSeeMore.setText("Xem thêm");
            } else {
                tvDesc.setMaxLines(Integer.MAX_VALUE);
                tvDesc.setEllipsize(null);
                tvSeeMore.setText("Thu gọn");
            }
        });

        // Toggle nội dung "Thành phần"
        headerIngredients.setOnClickListener(v -> {
            if (tvIngredientsDetail.getVisibility() == View.GONE) {
                tvIngredientsDetail.setVisibility(View.VISIBLE);
                arrowIngredients.setRotation(180);
            } else {
                tvIngredientsDetail.setVisibility(View.GONE);
                arrowIngredients.setRotation(0);
            }
        });
        //nút back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Toggle nội dung "Hướng dẫn"
        headerInstruction.setOnClickListener(v -> {
            if (tvInstructionDetail.getVisibility() == View.GONE) {
                tvInstructionDetail.setVisibility(View.VISIBLE);
                arrowInstruction.setRotation(180);
            } else {
                tvInstructionDetail.setVisibility(View.GONE);
                arrowInstruction.setRotation(0);
            }
        });findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            View quantityPopup = LayoutInflater.from(this).inflate(R.layout.product_buy_now_popup, null);
            BottomSheetDialog dialog = new BottomSheetDialog(ProductDetail.this);
            dialog.setContentView(quantityPopup);
            dialog.show();

            // Khởi tạo các view trong popup
            Button btn140 = quantityPopup.findViewById(R.id.btn140ml);
            Button btn360 = quantityPopup.findViewById(R.id.btn360ml);
            ImageButton btnMinus = quantityPopup.findViewById(R.id.btnMinus);
            ImageButton btnPlus = quantityPopup.findViewById(R.id.btnPlus);
            TextView tvQuantity = quantityPopup.findViewById(R.id.tvQuantity);
            Button btnConfirm = quantityPopup.findViewById(R.id.btnProductBuyConfirm);

            // Xử lý chọn dung tích 140ml
            btn140.setOnClickListener(vol -> {
                btn140.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C5221")));
                btn360.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            });

            // Xử lý chọn dung tích 360ml
            btn360.setOnClickListener(vol -> {
                btn360.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C5221")));
                btn140.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            });

            // Giảm số lượng
            btnMinus.setOnClickListener(vol -> {
                int current = Integer.parseInt(tvQuantity.getText().toString());
                if (current > 1) {
                    tvQuantity.setText(String.valueOf(current - 1));
                }
            });

            // Tăng số lượng
            btnPlus.setOnClickListener(vol -> {
                int current = Integer.parseInt(tvQuantity.getText().toString());
                tvQuantity.setText(String.valueOf(current + 1));
            });

            // Xác nhận mua hàng
            btnConfirm.setOnClickListener(vol -> {
                // TODO: Gửi thông tin đặt hàng hoặc thêm vào giỏ hàng ở đây
                dialog.dismiss();
            });
        });
    }
}
