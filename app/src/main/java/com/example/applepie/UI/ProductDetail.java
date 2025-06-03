package com.example.applepie.UI;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            // TODO: Xử lý nút MUA NGAY
        });

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

        headerIngredients.setOnClickListener(v -> {
            if (tvIngredientsDetail.getVisibility() == View.GONE) {
                tvIngredientsDetail.setVisibility(View.VISIBLE);
                arrowIngredients.setRotation(180);  // Xoay mũi tên xuống
            } else {
                tvIngredientsDetail.setVisibility(View.GONE);
                arrowIngredients.setRotation(0);  // Xoay mũi tên lên
            }
        });

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
