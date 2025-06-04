package com.example.applepie.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.R;

public class ProductDetail extends AppCompatActivity {

    private TextView tvDesc;
    private TextView tvSeeMore;
    private LinearLayout headerIngredients;
    private TextView tvIngredientsDetail;
    private ImageView arrowIngredients;
    private LinearLayout headerInstruction;
    private TextView tvInstructionDetail;
    private ImageView arrowInstruction;
    private LinearLayout bottomBar;  // Khung chứa phần MUA NGAY

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        // Khởi tạo view sau khi set layout
        tvDesc = findViewById(R.id.DetailProductDesc);
        tvSeeMore = findViewById(R.id.tvSeeMore);
        headerIngredients = findViewById(R.id.headerIngredients);
        tvIngredientsDetail = findViewById(R.id.tvIngredientsDetail);
        arrowIngredients = findViewById(R.id.arrowIngredients);
        headerInstruction = findViewById(R.id.headerInstruction);
        tvInstructionDetail = findViewById(R.id.tvInstructionDetail);
        arrowInstruction = findViewById(R.id.arrowInstruction);
        bottomBar = findViewById(R.id.bottomBar); // Nút MUA NGAY

        // Lắng nghe sự kiện nhấn MUA NGAY
        findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            // Tạo một popup mới từ layout riêng
            LayoutInflater inflater = LayoutInflater.from(ProductDetail.this);
            LinearLayout quantityPopup = (LinearLayout) inflater.inflate(R.layout.product_buy_now_popup, null);

            // Thêm popup vào giao diện hiện tại (đè lên nút MUA NGAY)
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            quantityPopup.setLayoutParams(params);

            // Đảm bảo nó xuất hiện trên cùng của màn hình (hoặc đè lên bottomBar)
            bottomBar.addView(quantityPopup);
        });

        // Áp dụng padding cho system bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Sự kiện click "Xem thêm"
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

        // Sự kiện click "Thành phần sản phẩm"
        headerIngredients.setOnClickListener(v -> {
            if (tvIngredientsDetail.getVisibility() == View.GONE) {
                tvIngredientsDetail.setVisibility(View.VISIBLE);
                arrowIngredients.setRotation(180);  // Xoay mũi tên xuống
            } else {
                tvIngredientsDetail.setVisibility(View.GONE);
                arrowIngredients.setRotation(0);  // Xoay mũi tên lên
            }
        });

        // Sự kiện click "Hướng dẫn sử dụng"
        headerInstruction.setOnClickListener(v -> {
            if (tvInstructionDetail.getVisibility() == View.GONE) {
                tvInstructionDetail.setVisibility(View.VISIBLE);
                arrowInstruction.setRotation(180);  // Xoay mũi tên xuống
            } else {
                tvInstructionDetail.setVisibility(View.GONE);
                arrowInstruction.setRotation(0);  // Xoay mũi tên lên
            }
        });


    }
}
