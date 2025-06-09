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

import com.example.applepie.UI.BottomNavHelper;
import com.example.applepie.Model.Product;

import java.util.ArrayList;
import java.util.List;

import com.example.applepie.Adapter.FlashSaleAdapter;

public class MainActivity extends AppCompatActivity {

    // Các thành phần UI
    private EditText edtSearch;
    private LinearLayout filterPanel;
    private Button btnCloseFilter;
    private HorizontalScrollView scrollHomepage;
    private RecyclerView rvFlashSale;

    // Adapter và dữ liệu cho danh sách sản phẩm flash sale
    private FlashSaleAdapter flashSaleAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Kích hoạt hiển thị toàn màn hình (không viền)
        setContentView(R.layout.activity_main); // Gắn layout chính cho activity

        // Thiết lập padding tránh bị che khuất bởi thanh trạng thái và điều hướng
        View rootView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Scroll tự động cho danh mục (scrollHomepage)
        scrollHomepage = findViewById(R.id.scrollHomepage);
        scrollHomepage.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(scrollHomepage, "scrollX", 0, 500);
            animator.setDuration(5000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE); // quay lại sau khi scroll xong
            animator.start();
        });

        // Scroll tự động cho phần triết lý (trietLyScrollView)
        HorizontalScrollView trietLyScrollView = findViewById(R.id.trietlyscrollhome);
        trietLyScrollView.post(() -> {
            int maxScroll = trietLyScrollView.getChildAt(0).getWidth() - trietLyScrollView.getWidth();
            if (maxScroll < 0) maxScroll = 0; // tránh lỗi nếu nội dung ngắn hơn khung
            ObjectAnimator animator = ObjectAnimator.ofInt(trietLyScrollView, "scrollX", 0, maxScroll);
            animator.setDuration(10000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        });

        // Giao diện tìm kiếm & bộ lọc
        edtSearch = findViewById(R.id.edt_search);
        filterPanel = findViewById(R.id.filter_panel);
        btnCloseFilter = findViewById(R.id.btn_close_filter);

        // Mở panel lọc khi nhấn vào ô tìm kiếm
        edtSearch.setOnClickListener(v -> filterPanel.setVisibility(View.VISIBLE));

        // Đóng panel lọc
        btnCloseFilter.setOnClickListener(v -> filterPanel.setVisibility(View.GONE));

        // Thiết lập danh sách sản phẩm flash sale
        rvFlashSale = findViewById(R.id.rvFlashSale);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFlashSale.setLayoutManager(layoutManager);

        // Dữ liệu giả lập – sẽ được thay thế bằng dữ liệu thật trong tương lai
        productList = new ArrayList<>();
        productList.add(new Product("Nước dưỡng tóc tinh dầu bưởi 140ml", "165.000 đ", "256.000đ", "10", ""));
        productList.add(new Product("Sản phẩm 2", "120.000 đ", "200.000đ", "15", ""));
        productList.add(new Product("Sản phẩm 3", "99.000 đ", "150.000đ", "20", ""));

        // Gắn adapter và hiển thị lên RecyclerView
        flashSaleAdapter = new FlashSaleAdapter(this, productList);
        rvFlashSale.setAdapter(flashSaleAdapter);

        // Gắn thanh điều hướng (bottom navigation)
        BottomNavHelper.setupBottomNav(this);
    }
}

