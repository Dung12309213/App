package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.content.Intent;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.MainActivity;
import com.example.applepie.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
public class CategoryList extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intentHome = new Intent(CategoryList.this, MainActivity.class);
                startActivity(intentHome);
                return true;
            } else if (id == R.id.nav_category) {
                // Đang ở màn CategoryList rồi, không làm gì
                return true;
            } else if (id == R.id.nav_cart) {
                // Xử lý chuyển sang màn Cart nếu có
                return true;
            } else if (id == R.id.nav_chat) {
                // Xử lý chuyển sang màn Chat nếu có
                return true;
            } else if (id == R.id.nav_profile) {
                // Xử lý chuyển sang màn Profile nếu có
                return true;
            }
            return false;
        });
    }

    // Phương thức xử lý khi click vào category image
    public void onCategoryClick(View v) {
        int id = v.getId();
        if (id == R.id.ImgCategory1) {
            Intent intent = new Intent(this, ProductListActivity.class);
            startActivity(intent);
        }
        // Thêm các điều kiện if-else cho các category khác
    }

}

