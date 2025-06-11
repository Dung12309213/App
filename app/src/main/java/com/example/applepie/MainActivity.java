package com.example.applepie;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.FlashSaleAdapter;
import com.example.applepie.Model.Product;
import com.example.applepie.R;
import com.example.applepie.UI.CartActivity;
import com.example.applepie.UI.CategoryList;
import com.example.applepie.UI.ChatBotActivity;
import com.example.applepie.UI.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtSearch;
    private LinearLayout filterPanel;
    private Button btnCloseFilter;
    private HorizontalScrollView scrollHomepage;
    private RecyclerView rvFlashSale;
    private FlashSaleAdapter flashSaleAdapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 👉 BẮT SỰ KIỆN CHO CÁC NÚT ĐIỀU HƯỚNG
        findViewById(R.id.btn_home).setOnClickListener(v -> {
            // Đang ở trang chủ, không cần chuyển
        });

        findViewById(R.id.btn_category).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CategoryList.class));
        });

        findViewById(R.id.btn_buy).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CartActivity.class));
        });

        findViewById(R.id.btn_chat).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ChatBotActivity.class));
        });

        findViewById(R.id.btn_profile).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        // Scroll ngang danh mục
        scrollHomepage = findViewById(R.id.scrollHomepage);
        scrollHomepage.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(scrollHomepage, "scrollX", 0, 500);
            animator.setDuration(5000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.start();
        });

        // Scroll ngang triết lý
        HorizontalScrollView trietLyScrollView = findViewById(R.id.trietlyscrollhome);
        trietLyScrollView.post(() -> {
            int maxScroll = trietLyScrollView.getChildAt(0).getWidth() - trietLyScrollView.getWidth();
            if (maxScroll < 0) maxScroll = 0;
            ObjectAnimator animator = ObjectAnimator.ofInt(trietLyScrollView, "scrollX", 0, maxScroll);
            animator.setDuration(10000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        });

        // Panel tìm kiếm
        edtSearch = findViewById(R.id.edt_search);
        filterPanel = findViewById(R.id.filter_panel);
        btnCloseFilter = findViewById(R.id.btn_close_filter);

        edtSearch.setOnClickListener(v -> filterPanel.setVisibility(View.VISIBLE));
        btnCloseFilter.setOnClickListener(v -> filterPanel.setVisibility(View.GONE));

        // Flash Sale
        rvFlashSale = findViewById(R.id.rvFlashSale);
        rvFlashSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        productList = new ArrayList<>();
        productList.add(new Product("Nước dưỡng tóc tinh dầu bưởi 140ml", 165000, 256000, "10", ""));
        productList.add(new Product("Sản phẩm 2", 120000, 200000, "15", ""));
        productList.add(new Product("Sản phẩm 3", 99000, 150000, "20", ""));

        flashSaleAdapter = new FlashSaleAdapter(this, productList);
        rvFlashSale.setAdapter(flashSaleAdapter);
    }
}
