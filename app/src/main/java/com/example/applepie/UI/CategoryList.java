package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.UI.ProductListActivity;
import com.example.applepie.R;

public class CategoryList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_list);

        // Xử lý để tránh che UI bởi thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Thiết lập thanh điều hướng dưới (custom bằng include + ImageButton)
        BottomNavHelper.setupBottomNav(this);
    }

    // Xử lý sự kiện click vào từng danh mục
    public void onCategoryClick(View v) {
        int id = v.getId();

        if (id == R.id.ImgCategory1) {
            Intent intent = new Intent(this, com.example.applepie.UI.ProductListActivity.class);
            startActivity(intent);
        }

        // TODO: Thêm các điều kiện cho ImgCategory2, ImgCategory3 nếu có
        /*
        else if (id == R.id.ImgCategory2) {
            // mở category khác
        }
        */
    }
}
