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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Adapter.ProductAdapter;
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.MainActivity;
import com.example.applepie.Model.Product;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.UI.ProductDetail;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends BaseActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView; // Đã thay đổi thành RecyclerView
    ProductAdapter productAdapter; // Khai báo ProductAdapter
    List<Product> productList; // Danh sách để giữ dữ liệu sản phẩm
    List<String> productIds;
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

        SearchBarHelper.setupSearchBar(this, keyword -> {
            SearchResultHelper.searchAndShow(this, keyword, SearchResultHelper.SearchMode.PRODUCT_AND_CATEGORY);
        });

        // Dùng custom bottom_menu
        BottomNavHelper.setupBottomNav(this);

        cateId = getIntent().getStringExtra("cateId");
        recyclerView = findViewById(R.id.rvProducts);
        db = FirebaseConnector.getInstance();

        // Khởi tạo danh sách
        productList = new ArrayList<>();
        productIds = new ArrayList<>();

        // Thiết lập RecyclerView với GridLayoutManager (2 cột)
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(this, productList, productIds);
        recyclerView.setAdapter(productAdapter);

        if (cateId != null) {
            loadProductsByCategory(cateId);
        }
    }
    private void loadProductsByCategory(String cateId) {
        db.collection("Product")
                .whereEqualTo("cateid", cateId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear(); // Xóa dữ liệu hiện có
                    productIds.clear(); // Xóa ID hiện có
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                        if (product != null) {
                            productList.add(product);
                            productIds.add(doc.getId());
                        }
                    }
                    productAdapter.notifyDataSetChanged();
                });
    }
}
