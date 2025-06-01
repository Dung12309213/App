package com.example.applepie;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.Product;

import java.util.ArrayList;
import java.util.List;

import com.example.applepie.Adapter.FlashSaleAdapter;

public class MainActivity extends AppCompatActivity {

    private EditText edtSearch;
    private LinearLayout filterPanel;
    private Button btnCloseFilter;
    HorizontalScrollView scrollHomepage;
    private RecyclerView rvFlashSale;
    private FlashSaleAdapter flashSaleAdapter;
    private List<Product> productList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Đảm bảo layout chính là activity_main.xml

        // Fix lỗi setPadding + getInsets + thiếu import View
        View rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Liên kết với HorizontalScrollView trong XML
        scrollHomepage = findViewById(R.id.scrollHomepage);

        // Auto-scroll danh mục
        scrollHomepage.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(scrollHomepage, "scrollX", 0, 500);
            animator.setDuration(5000); // cuộn trong 5 giây
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.start();
        });
        HorizontalScrollView trietLyScrollView = findViewById(R.id.trietlyscrollhome);

        trietLyScrollView.post(() -> {
            int maxScroll = trietLyScrollView.getChildAt(0).getWidth() - trietLyScrollView.getWidth();
            if (maxScroll < 0) maxScroll = 0; // tránh trường hợp ảnh nhỏ hơn khung
            ObjectAnimator animator = ObjectAnimator.ofInt(trietLyScrollView, "scrollX", 0, maxScroll);
            animator.setDuration(10000); // 10 giây
            animator.setRepeatCount(ValueAnimator.INFINITE);

            animator.start();
        });

        edtSearch = findViewById(R.id.edt_search);
        filterPanel = findViewById(R.id.filter_panel);
        btnCloseFilter = findViewById(R.id.btn_close_filter);

        // Khi nhấn vào EditText, hiện filter panel
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPanel.setVisibility(View.VISIBLE);
            }
        });

        // Nút đóng filter panel
        btnCloseFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterPanel.setVisibility(View.GONE);
            }
        });
        // 1. Tìm RecyclerView trong layout
        rvFlashSale = findViewById(R.id.rvFlashSale);

        // 2. Tạo LayoutManager cho RecyclerView, ngang (Horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFlashSale.setLayoutManager(layoutManager);

        // 3. Tạo danh sách sản phẩm mẫu (thay sau sẽ lấy từ database hoặc API)
        productList = new ArrayList<>();
        productList.add(new Product("Nước dưỡng tóc tinh dầu bưởi 140ml", "165.000 đ", "256.000đ", "10", ""));
        productList.add(new Product("Sản phẩm 2", "120.000 đ", "200.000đ", "15", ""));
        productList.add(new Product("Sản phẩm 3", "99.000 đ", "150.000đ", "20", ""));
        // Thêm sản phẩm tuỳ ý

        // 4. Tạo adapter với dữ liệu
        flashSaleAdapter = new FlashSaleAdapter(this, productList);

        // 5. Gán adapter cho RecyclerView
        rvFlashSale.setAdapter(flashSaleAdapter);
    }
}




