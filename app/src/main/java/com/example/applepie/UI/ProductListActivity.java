package com.example.applepie.UI;

import android.content.Intent;
import android.graphics.Paint;
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
import com.example.applepie.MainActivity;
import com.example.applepie.Model.Product;
import com.example.applepie.R;
import com.example.applepie.UI.ProductDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductListActivity extends AppCompatActivity {

    FirebaseFirestore db;
    GridLayout gridLayout;
    LayoutInflater inflater;
    String cateId;

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

        cateId = getIntent().getStringExtra("cateId");
        gridLayout = findViewById(R.id.gridProducts);
        inflater = LayoutInflater.from(this);
        db = FirebaseConnector.getInstance();

        if (cateId != null) {
            loadProductsByCategory(cateId);
        }
    }
    private void loadProductsByCategory(String cateId) {
        db.collection("Product")
                .whereEqualTo("cateid", cateId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            addProductToGrid(product, doc.getId());
                        }
                    }
                });
    }

    private void addProductToGrid(Product product, String documentId) {
        View itemView = inflater.inflate(R.layout.item_product, gridLayout, false);

        ImageView img = itemView.findViewById(R.id.imgProductListItem);
        TextView tvName = itemView.findViewById(R.id.tvProductName);
        TextView tvPrice = itemView.findViewById(R.id.tvPrice);
        TextView tvSecondPrice = itemView.findViewById(R.id.tvSecondPrice);
        TextView tvProductDiscountPercent = itemView.findViewById(R.id.tvProductDiscountPercent);

        // Set dữ liệu
        tvName.setText(product.getName());
        tvPrice.setText(String.format("%,d đ", product.getPrice()));
        tvSecondPrice.setText(String.format("%,d đ", product.getSecondprice()));
        if (product.getDiscountPercent() > 0 ) {
            tvProductDiscountPercent.setText(product.getDiscountPercent() + "%");
            tvProductDiscountPercent.setVisibility(View.VISIBLE);
        }
        else {
            tvProductDiscountPercent.setVisibility(View.GONE);
        }


        // Kiểm tra nếu secondPrice có giá trị
        if (product.getSecondprice() > 0) {
            tvSecondPrice.setVisibility(View.VISIBLE);
            tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvSecondPrice.setVisibility(View.GONE);
        }

        Glide.with(this).load(product.getImageUrl()).into(img);

        img.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductDetail.class);
            intent.putExtra("productId", documentId);
            startActivity(intent);
        });

        gridLayout.addView(itemView);
    }
}
