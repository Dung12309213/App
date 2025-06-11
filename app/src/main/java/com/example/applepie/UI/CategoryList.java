package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
                        addCategoryToGrid(doc);
                    }
                });
    }

    private void addCategoryToGrid(DocumentSnapshot doc) {
        Category category = doc.toObject(Category.class);
        if (category == null) return;

        View itemView = inflater.inflate(R.layout.item_category, gridLayout, false);
        ImageView img = itemView.findViewById(R.id.imgCategory);
        TextView txt = itemView.findViewById(R.id.txtCategoryName);

        txt.setText(category.getName());
        Glide.with(this).load(category.getImageUrl()).into(img);

        String cateId = doc.getId();

        img.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductListActivity.class);
            intent.putExtra("cateId", cateId);
            startActivity(intent);
        });

        gridLayout.addView(itemView);
    }

}
