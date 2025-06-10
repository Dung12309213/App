package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Model.Category;
import com.example.applepie.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CategoryList extends AppCompatActivity {

    GridLayout gridLayout;
    LayoutInflater inflater;
    FirebaseFirestore db;

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

        // Firestore
        db= FirebaseConnector.getInstance();
        gridLayout=findViewById(R.id.gridCategory);
        inflater=LayoutInflater.from(this);

        loadCategory();
    }

    private void loadCategory() {
        db.collection("Category")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Category category = doc.toObject(Category.class);
                addCategoryTogrid(category);
            }
        });
    }

    private void addCategoryTogrid(Category category) {
        View itemView = inflater.inflate(R.layout.item_category, gridLayout, false);

        ImageView img = itemView.findViewById(R.id.imgCategory);
        TextView txt = itemView.findViewById(R.id.txtCategoryName);

        txt.setText(category.getName());
        Glide.with(this).load(category.getImageUrl()).into(img);

        img.setOnClickListener(v -> {
            // Xử lý khi click vào category, ví dụ: mở Activity mới
        });

        gridLayout.addView(itemView);
    }

    // Xử lý sự kiện click vào từng danh mục
//    public void onCategoryClick(View v) {
//        int id = v.getId();
//
//        if (id == R.id.ImgCategory1) {
//            Intent intent = new Intent(this, com.example.applepie.UI.ProductListActivity.class);
//            startActivity(intent);
//        }
//
//        // TODO: Thêm các điều kiện cho ImgCategory2, ImgCategory3 nếu có
//        /*
//        else if (id == R.id.ImgCategory2) {
//            // mở category khác
//        }
//        */
//    }
}
