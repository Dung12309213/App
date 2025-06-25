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
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.applepie.UI.BottomNavHelper;
import com.example.applepie.UI.CartActivity;
import com.example.applepie.UI.CategoryList;
import com.example.applepie.UI.ChatBotActivity;
import com.example.applepie.UI.LoginScreen1;
import com.example.applepie.UI.NotificationActivity;
import com.example.applepie.UI.ProductDetail;
import com.example.applepie.UI.ProfileActivity;
import com.example.applepie.UI.SearchBarHelper;
import com.example.applepie.UI.SearchResultHelper;
import com.example.applepie.Util.UserSessionManager;

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

        SearchBarHelper.setupSearchBar(this, keyword -> {
            SearchResultHelper.searchAndShow(this, keyword, SearchResultHelper.SearchMode.PRODUCT_AND_CATEGORY);
        });

        BottomNavHelper.setupBottomNav(this);
        BottomNavHelper.highlightSelected(this, "home");

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

        ImageButton btnNotification = findViewById(R.id.btn_notification);

        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
        TextView badge = findViewById(R.id.notification_badge);

// Ví dụ giả định có 5 thông báo mới
        int unreadCount = 5;

        if (unreadCount > 0) {
            badge.setText(String.valueOf(unreadCount));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }



        /*// Flash Sale
        rvFlashSale = findViewById(R.id.rvFlashSale);
        rvFlashSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        flashSaleAdapter = new FlashSaleAdapter(this, productList);
        rvFlashSale.setAdapter(flashSaleAdapter);*/
    }
}
