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
import com.example.applepie.R;
import com.example.applepie.UI.ProductDetail;

public class ProductListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_list);

        // Đảm bảo phần tử gốc layout có id là "main"
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dùng custom bottom_menu
        BottomNavHelper.setupBottomNav(this);
    }

    /**
     * Mở trang chi tiết sản phẩm khi người dùng click
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
