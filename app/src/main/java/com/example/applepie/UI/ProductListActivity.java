package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.MainActivity;
import com.example.applepie.UI.ProductDetail;

import com.example.applepie.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class ProductListActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intentHome = new Intent(ProductListActivity.this, MainActivity.class);
                startActivity(intentHome);
                return true;
            } else if (id == R.id.nav_category) {
                Intent intentCategory = new Intent(ProductListActivity.this, CategoryList.class);
                startActivity(intentCategory);
                return true;
            } else if (id == R.id.nav_cart) {
                // Xử lý chuyển màn Cart nếu có
                return true;
            } else if (id == R.id.nav_chat) {
                // Xử lý chuyển màn Chat nếu có
                return true;
            } else if (id == R.id.nav_profile) {
                // Xử lý chuyển màn Profile nếu có
                return true;
            }
            return false;
        });

    }
    /**
     * Hàm này nên đặt ở Activity khác (như ProductListActivity) để mở ProductDetail
     */
    public void onProductClick(View v) {
        Log.d("ProductListActivity", "Clicked view id: " + v.getId());
        if (v.getId() == R.id.imgProductDetail2) {
            Log.d("ProductListActivity", "Opening ProductDetail Activity");
            Intent intent = new Intent(ProductListActivity.this, ProductDetail.class);
            startActivity(intent);
        }
    }

    }
