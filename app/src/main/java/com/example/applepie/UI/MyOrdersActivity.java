package com.example.applepie.UI;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.applepie.Adapter.OrdersPagerAdapter;
import com.example.applepie.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyOrdersActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private OrdersPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        tabLayout = findViewById(R.id.tabLayoutOrders);
        viewPager = findViewById(R.id.viewPagerOrders);
        ImageButton btnBack = findViewById(R.id.btnBack);

        pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position) {
                            case 0:
                                tab.setText("Đang xử lý");
                                break;
                            case 1:
                                tab.setText("Đã giao");
                                break;
                            case 2:
                                tab.setText("Đã hủy");
                                break;
                        }
                    }
                }).attach();

        btnBack.setOnClickListener(v -> finish());
    }
}

