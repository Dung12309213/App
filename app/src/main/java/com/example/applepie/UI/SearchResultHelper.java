package com.example.applepie.UI;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Adapter.ProductAdapter;
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Model.Category;
import com.example.applepie.Model.Product;
import com.example.applepie.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SearchResultHelper {

    public enum SearchMode {
        PRODUCT_ONLY,
        CATEGORY_ONLY,
        PRODUCT_AND_CATEGORY
    }

    public static void searchAndShow(Activity activity, String keyword, SearchMode mode) {
        if (keyword.isEmpty()) {
            hideResults(activity);
            return;
        }

        showResults(activity);

        if (mode == SearchMode.PRODUCT_ONLY || mode == SearchMode.PRODUCT_AND_CATEGORY) {
            searchProducts(activity, keyword);
        }

        if (mode == SearchMode.CATEGORY_ONLY || mode == SearchMode.PRODUCT_AND_CATEGORY) {
            searchCategories(activity, keyword);
        }
    }

    public static void searchAndShow(Activity activity, String keyword) {
        if (keyword.isEmpty()) {
            hideResults(activity);
            return;
        }

        showResults(activity);
        searchProducts(activity, keyword);
        searchCategories(activity, keyword);
    }

    private static void hideResults(Activity activity) {
        activity.findViewById(R.id.rvSearchProducts).setVisibility(View.GONE);
        activity.findViewById(R.id.gridSearchCategory).setVisibility(View.GONE);

        View topBar = activity.findViewById(R.id.layout_top_bar);
        if (topBar != null) {
            topBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
            topBar.requestLayout();
        }
    }

    private static void showResults(Activity activity) {
        activity.findViewById(R.id.rvSearchProducts).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.gridSearchCategory).setVisibility(View.VISIBLE);

        View topBar = activity.findViewById(R.id.layout_top_bar);
        if (topBar != null) {
            topBar.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            topBar.requestLayout();
        }
    }

    private static void searchProducts(Activity activity, String keyword) {
        FirebaseFirestore db = FirebaseConnector.getInstance();
        RecyclerView rv = activity.findViewById(R.id.rvSearchProducts);
        List<Product> productList = new ArrayList<>();
        List<String> productIds = new ArrayList<>();
        ProductAdapter adapter = new ProductAdapter(activity, productList, productIds);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new GridLayoutManager(activity, 2));

        db.collection("Product")
                .get()
                .addOnSuccessListener(snapshot -> {
                    productList.clear();
                    productIds.clear();
                    for (DocumentSnapshot doc : snapshot) {
                        Product p = doc.toObject(Product.class);
                        if (p != null && p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            productList.add(p);
                            productIds.add(doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private static void searchCategories(Activity activity, String keyword) {
        FirebaseFirestore db = FirebaseConnector.getInstance();
        GridLayout gridLayout = activity.findViewById(R.id.gridSearchCategory);
        LayoutInflater inflater = LayoutInflater.from(activity);
        gridLayout.removeAllViews();

        db.collection("Category")
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot) {
                        Category c = doc.toObject(Category.class);
                        if (c != null && c.getName().toLowerCase().contains(keyword.toLowerCase())) {
                            View itemView = inflater.inflate(R.layout.item_category, gridLayout, false);
                            ImageView img = itemView.findViewById(R.id.imgCategory);
                            TextView txt = itemView.findViewById(R.id.txtCategoryName);

                            txt.setText(c.getName());
                            Glide.with(activity).load(c.getImageUrl()).into(img);

                            String cateId = doc.getId();
                            img.setOnClickListener(v -> {
                                Intent i = new Intent(activity, ProductListActivity.class);
                                i.putExtra("cateId", cateId);
                                activity.startActivity(i);
                            });

                            gridLayout.addView(itemView);
                        }
                    }
                });
    }
}