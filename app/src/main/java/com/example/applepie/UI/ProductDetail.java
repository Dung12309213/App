package com.example.applepie.UI;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Model.Product;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductDetail extends AppCompatActivity {

    FirebaseFirestore db;

    private TextView tvProductCategory;
    private TextView tvProductDetailName;
    private TextView tvProductDetailRating;
    private TextView tvDesc;
    private TextView tvSeeMore;
    private LinearLayout headerIngredients, sameCateProductLayout;
    private TextView tvIngredientsDetail;
    private ImageView arrowIngredients;
    private LinearLayout headerInstruction;
    private TextView tvInstructionDetail;
    private ImageView arrowInstruction, imgAvrStar, imgProduct;
    private TextView tvDiscountedPrice, tvOriginalPrice;
    private TextView txtUses1,txtUses2,txtUses3,txtUses4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);

        db = FirebaseConnector.getInstance();
        String productId = getIntent().getStringExtra("productId");

        if (productId != null) {
            loadProductDetails(productId);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addViews();
        addEvents();
    }

    private void addEvents() {
        TextView originalPrice = findViewById(R.id.tvOriginalPrice);
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        // Sự kiện "Xem thêm"
        tvSeeMore.setOnClickListener(v -> {
            boolean isExpanded = tvDesc.getMaxLines() == Integer.MAX_VALUE;
            if (isExpanded) {
                tvDesc.setMaxLines(2);
                tvDesc.setEllipsize(TextUtils.TruncateAt.END);
                tvSeeMore.setText("Xem thêm");
            } else {
                tvDesc.setMaxLines(Integer.MAX_VALUE);
                tvDesc.setEllipsize(null);
                tvSeeMore.setText("Thu gọn");
            }
        });

        // Toggle nội dung "Thành phần"
        headerIngredients.setOnClickListener(v -> {
            if (tvIngredientsDetail.getVisibility() == View.GONE) {
                tvIngredientsDetail.setVisibility(View.VISIBLE);
                arrowIngredients.setRotation(180);
            } else {
                tvIngredientsDetail.setVisibility(View.GONE);
                arrowIngredients.setRotation(0);
            }
        });
        //nút back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        // Toggle nội dung "Hướng dẫn"
        headerInstruction.setOnClickListener(v -> {
            if (tvInstructionDetail.getVisibility() == View.GONE) {
                tvInstructionDetail.setVisibility(View.VISIBLE);
                arrowInstruction.setRotation(180);
            } else {
                tvInstructionDetail.setVisibility(View.GONE);
                arrowInstruction.setRotation(0);
            }
        });
        findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            View quantityPopup = LayoutInflater.from(this).inflate(R.layout.product_buy_now_popup, null);
            BottomSheetDialog dialog = new BottomSheetDialog(ProductDetail.this);
            dialog.setContentView(quantityPopup);
            dialog.show();

            // Khởi tạo các view trong popup
            Button btn140 = quantityPopup.findViewById(R.id.btn140ml);
            Button btn360 = quantityPopup.findViewById(R.id.btn360ml);
            ImageButton btnMinus = quantityPopup.findViewById(R.id.btnMinus);
            ImageButton btnPlus = quantityPopup.findViewById(R.id.btnPlus);
            TextView tvQuantity = quantityPopup.findViewById(R.id.tvQuantity);
            Button btnConfirm = quantityPopup.findViewById(R.id.btnProductBuyConfirm);

            // Xử lý chọn dung tích 140ml
            btn140.setOnClickListener(vol -> {
                btn140.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C5221")));
                btn360.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            });

            // Xử lý chọn dung tích 360ml
            btn360.setOnClickListener(vol -> {
                btn360.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C5221")));
                btn140.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            });

            // Giảm số lượng
            btnMinus.setOnClickListener(vol -> {
                int current = Integer.parseInt(tvQuantity.getText().toString());
                if (current > 1) {
                    tvQuantity.setText(String.valueOf(current - 1));
                }
            });

            // Tăng số lượng
            btnPlus.setOnClickListener(vol -> {
                int current = Integer.parseInt(tvQuantity.getText().toString());
                tvQuantity.setText(String.valueOf(current + 1));
            });

            // Xác nhận mua hàng
            btnConfirm.setOnClickListener(vol -> {
                // TODO: Gửi thông tin đặt hàng hoặc thêm vào giỏ hàng ở đây
                dialog.dismiss();
            });
        });
    }

    private void addViews() {
        tvProductCategory = findViewById(R.id.tvProductDetailCategory);
        tvProductDetailName = findViewById(R.id.tvProductDetailName);
        tvProductDetailRating = findViewById(R.id.tvProductDetailRating);
        tvDesc = findViewById(R.id.tvProductDetailDesc);
        tvSeeMore = findViewById(R.id.tvProductDetailSeeMore);
        headerIngredients = findViewById(R.id.headerIngredients);
        tvIngredientsDetail = findViewById(R.id.tvProductDetailIngredient);
        arrowIngredients = findViewById(R.id.arrowIngredients);
        headerInstruction = findViewById(R.id.headerInstruction);
        tvInstructionDetail = findViewById(R.id.tvProductDetailInstruction);
        arrowInstruction = findViewById(R.id.arrowInstruction);
        imgAvrStar = findViewById(R.id.imgAvrStar);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscountedPrice = findViewById(R.id.tvDiscountedPrice);
        imgProduct = findViewById(R.id.imgProduct);
        txtUses1 = findViewById(R.id.txtUses1);
        txtUses2 = findViewById(R.id.txtUses2);
        txtUses3 = findViewById(R.id.txtUses3);
        txtUses4 = findViewById(R.id.txtUses4);
        sameCateProductLayout = findViewById(R.id.sameCateProduct);
    }

    private void loadProductDetails(String productId) {
        db.collection("Product")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            displayProductDetails(product);
                            String cateid = product.getCateid();
                            loadCategoryDetails(cateid);
                        }
                    }
                });
    }
    private void displayProductDetails(Product product) {
        Glide.with(this).load(product.getImageUrl()).into(imgProduct);

        tvProductDetailName.setText(product.getName());
        float rating = product.getRating();
        if (rating == 0) {
            tvProductDetailRating.setText("Chưa có đánh giá");
            imgAvrStar.setVisibility(View.GONE);
        } else {
            tvProductDetailRating.setText(String.format("%.1f", rating));
        }
        tvDesc.setText(product.getDescription());
        tvIngredientsDetail.setText(product.getIngredient());
        tvInstructionDetail.setText(product.getInstruction());

        db.collection("Product")
                .document(product.getId())
                .collection("Variant")
                .document("V1")
                .get()
                .addOnSuccessListener(variantDoc -> {
                    if (variantDoc.exists()) {
                        Variant v = variantDoc.toObject(Variant.class);
                        if (v != null) {
                            // Hiển thị giá từ biến thể
                            if (v.getSecondprice() == 0) {
                                tvOriginalPrice.setVisibility(View.GONE);
                                tvDiscountedPrice.setText(String.format("%,d đ", v.getPrice()));
                            } else {
                                tvOriginalPrice.setVisibility(View.VISIBLE);
                                tvOriginalPrice.setText(String.format("%,d đ", v.getPrice()));
                                tvDiscountedPrice.setText(String.format("%,d đ", v.getSecondprice()));
                            }
                        }
                    }
                });
        txtUses1.setText(product.getUses1());
        txtUses2.setText(product.getUses2());
        txtUses3.setText(product.getUses3());
        txtUses4.setText(product.getUses4());

    }
    private void loadCategoryDetails(String cateid) {
        db.collection("Category")
                .document(cateid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String mcValue = documentSnapshot.getString("MC");
                        tvProductCategory.setText(mcValue);
                    }
                });
        loadSameCategoryProducts(cateid);
    }
    private void loadSameCategoryProducts(String cateid) {
        db.collection("Product")
                .whereEqualTo("cateid", cateid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> sameCateProducts = new ArrayList<>();
                    String currentProductId = getIntent().getStringExtra("productId");

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Product p = doc.toObject(Product.class);
                        p.setId(doc.getId());

                        // Kiểm tra null trước khi so sánh
                        if (p != null && p.getId() != null && currentProductId != null && !p.getId().equals(currentProductId)) {
                            sameCateProducts.add(p);
                        }
                    }

                    displaySameCategoryProducts(sameCateProducts);
                });
    }
    private void displaySameCategoryProducts(List<Product> productList) {
        sameCateProductLayout.removeAllViews(); // Xóa cũ trước khi thêm mới
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Product p : productList) {
            View itemView = inflater.inflate(R.layout.item_same_cate_product, sameCateProductLayout, false);

            ImageView img = itemView.findViewById(R.id.imgSameCateProduct);
            TextView name = itemView.findViewById(R.id.txtSameCateProduct);

            Glide.with(this).load(p.getImageUrl()).into(img);
            name.setText(p.getName());

            // Optional: set onClick to đi đến chi tiết
            itemView.setOnClickListener(v -> {
                // Load lại activity với sản phẩm mới
                Intent intent = new Intent(this, ProductDetail.class);
                intent.putExtra("productId", p.getId());
                startActivity(intent);
            });

            sameCateProductLayout.addView(itemView);
        }
    }
}
