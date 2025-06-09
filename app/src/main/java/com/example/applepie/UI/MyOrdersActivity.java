package com.example.applepie.UI;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import  com.example.applepie.R;
import  com.example.applepie.Adapter.OrdersPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Màn hình My Orders gồm 3 tab:
 * - Active
 * - Completed
 * - Cancelled
 */
public class MyOrdersActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrdersPagerAdapter pagerAdapter;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // Gán view
        tabLayout = findViewById(R.id.tabLayoutOrders);
        viewPager = findViewById(R.id.viewPagerOrders);
        btnBack = findViewById(R.id.btnBack);

        // Gán sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thiết lập ViewPager + TabLayout
        pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Gắn tiêu đề cho tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Active");
                    break;
                case 1:
                    tab.setText("Completed");
                    break;
                case 2:
                    tab.setText("Cancelled");
                    break;
            }
        }).attach();
    }
}
