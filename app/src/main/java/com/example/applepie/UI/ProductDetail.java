package com.example.applepie.UI;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; // Import thêm ViewGroup cho Adapter
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2; // Quan trọng cho ViewPager2
import androidx.recyclerview.widget.RecyclerView; // Quan trọng cho Adapter của ViewPager2

import com.bumptech.glide.Glide;
import com.example.applepie.Adapter.ProductImageAdapter;
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

    // Khai báo các Views
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
    private ImageView arrowInstruction, imgAvrStar; // imgProduct sẽ được thay bằng ViewPager2
    private TextView tvDiscountedPrice, tvOriginalPrice;
    private TextView txtUses1, txtUses2, txtUses3, txtUses4;
    LayoutInflater inflate; // Biến này nên được khởi tạo trong onCreate hoặc constructor

    // Biến cho ViewPager2
    private ViewPager2 productImageViewPager;
    private ImageView btnBack; // Đã có ở trên, nhưng khai báo lại để rõ ràng hơn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail); // Đảm bảo đây là layout chính của bạn

        // Khởi tạo Firebase
        db = FirebaseConnector.getInstance();
        String productId = getIntent().getStringExtra("productId");

        // Áp dụng insets cho hệ thống (ví dụ: thanh trạng thái, thanh điều hướng)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo LayoutInflater
        inflate = LayoutInflater.from(this); // Khởi tạo ở đây

        // Ánh xạ các Views từ layout
        addViews();

        // Thiết lập ViewPager2 cho hình ảnh sản phẩm
        // Tìm ViewPager2 trong layout
        productImageViewPager = findViewById(R.id.productImageViewPager);
        // Chuẩn bị dữ liệu hình ảnh (ví dụ với 3 hình ảnh)
        List<Integer> imageList = new ArrayList<>();
        // Bạn cần thay thế bằng các tài nguyên drawable thực tế của mình trong res/drawable/
        imageList.add(R.drawable.ic_homepage_mau1);
        imageList.add(R.drawable.ic_homepage_mau2);
        imageList.add(R.drawable.ic_homepage_mau3);
        // Bạn có thể thêm bao nhiêu hình tùy thích vào đây
        // Tạo Adapter và gán cho ViewPager2
        ProductImageAdapter adapter = new ProductImageAdapter(imageList);
        productImageViewPager.setAdapter(adapter);

        // Load chi tiết sản phẩm nếu có productId
        if (productId != null) {
            loadProductDetails(productId);
        }

        // Thêm các sự kiện cho Views
        addEvents();
    }

    // Phương thức để ánh xạ các Views
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
        // imgProduct đã được thay bằng productImageViewPager nên không cần ánh xạ ở đây
        txtUses1 = findViewById(R.id.txtUses1);
        txtUses2 = findViewById(R.id.txtUses2);
        txtUses3 = findViewById(R.id.txtUses3);
        txtUses4 = findViewById(R.id.txtUses4);
        sameCateProductLayout = findViewById(R.id.sameCateProduct);
        btnBack = findViewById(R.id.btnBack); // Ánh xạ nút back ở đây
    }

    // Phương thức để thêm các sự kiện click, v.v.
    private void addEvents() {
        TextView originalPrice = findViewById(R.id.tvOriginalPrice);
        originalPrice.setPaintFlags(originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Sự kiện "Xem thêm" cho mô tả sản phẩm
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

        // Nút back
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

        // Xử lý sự kiện khi nhấn nút "MUA NGAY"
        findViewById(R.id.btnBuyNow).setOnClickListener(v -> {
            // Khởi tạo BottomSheetDialog và popup
            View quantityPopup = inflate.inflate(R.layout.product_buy_now_popup, null); // Sử dụng biến inflate đã khởi tạo
            BottomSheetDialog dialog = new BottomSheetDialog(ProductDetail.this);
            dialog.setContentView(quantityPopup);
            dialog.show();

            // Khởi tạo container cho các biến thể (item_variant)
            GridLayout variantContainer = quantityPopup.findViewById(R.id.gridVariant);
            String productId = getIntent().getStringExtra("productId");

            // Lấy thông tin biến thể từ Firestore
            db.collection("Product")
                    .document(productId)
                    .collection("Variant")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot variantDoc : queryDocumentSnapshots.getDocuments()) {
                            Variant variant = variantDoc.toObject(Variant.class);
                            variant.setId(variantDoc.getId());

                            if (variant != null) {
                                // Inflate item_variant và cập nhật thông tin
                                View variantItem = LayoutInflater.from(ProductDetail.this)
                                        .inflate(R.layout.item_variant, variantContainer, false);

                                // Lấy Button trong item_variant và cập nhật với tên biến thể
                                Button btnVariant = variantItem.findViewById(R.id.btnVariant);
                                btnVariant.setText(variant.getVariant());

                                // Đặt sự kiện click cho btnVariant
                                btnVariant.setOnClickListener(v1 -> {
                                    // Đặt lại màu cho tất cả các nút variant
                                    for (int i = 0; i < variantContainer.getChildCount(); i++) {
                                        View variantItemChild = variantContainer.getChildAt(i);
                                        Button btn = variantItemChild.findViewById(R.id.btnVariant);

                                        // Đặt lại màu mặc định cho các nút chưa được chọn
                                        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                                    }

                                    // Thay đổi màu cho nút đã được chọn
                                    btnVariant.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9C5221")));

                                    // Hiển thị thông báo khi người dùng chọn biến thể
                                    Toast.makeText(ProductDetail.this, "Chọn biến thể: " + variant.getVariant(), Toast.LENGTH_SHORT).show();
                                });

                                // Thêm item vào container
                                variantContainer.addView(variantItem);
                            }
                        }
                    });

            // Khởi tạo các view trong popup
            ImageButton btnMinus = quantityPopup.findViewById(R.id.btnMinus);
            ImageButton btnPlus = quantityPopup.findViewById(R.id.btnPlus);
            TextView tvQuantity = quantityPopup.findViewById(R.id.tvQuantity);
            Button btnConfirm = quantityPopup.findViewById(R.id.btnProductBuyConfirm);

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
                int quantity = Integer.parseInt(tvQuantity.getText().toString());

                // Lấy thông tin về biến thể đã chọn (nếu cần)
                // Có thể lấy tên biến thể và giá từ các nút đã chọn.

                Log.d("ProductDetail", "Quantity: " + quantity);
                dialog.dismiss();
            });
        });
    }

    // Phương thức để tải chi tiết sản phẩm từ Firestore
    private void loadProductDetails(String productId) {
        db.collection("Product")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        product.setId(productId);
                        if (product != null) {
                            displayProductDetails(product);
                            String cateid = product.getCateid();
                            loadCategoryDetails(cateid);
                        }
                    } else {
                        // Xử lý trường hợp không tìm thấy sản phẩm
                        Toast.makeText(this, "Không tìm thấy sản phẩm này.", Toast.LENGTH_SHORT).show();
                        finish(); // Đóng Activity nếu sản phẩm không tồn tại
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetail", "Error loading product details: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tải chi tiết sản phẩm.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    // Phương thức để hiển thị chi tiết sản phẩm lên giao diện
    private void displayProductDetails(Product product) {
        // imgProduct đã được thay thế bằng ViewPager2, nên không cần Glide.with(...).into(imgProduct) ở đây
        // ViewPager2 sẽ được set Adapter với danh sách ảnh trong onCreate.

        tvProductDetailName.setText(product.getName());
        float rating = product.getRating();
        if (rating == 0) {
            tvProductDetailRating.setText("Chưa có đánh giá");
            imgAvrStar.setVisibility(View.GONE);
        } else {
            tvProductDetailRating.setText(String.format("%.1f", rating));
            imgAvrStar.setVisibility(View.VISIBLE); // Đảm bảo sao hiển thị nếu có rating
        }
        tvDesc.setText(product.getDescription());
        tvIngredientsDetail.setText(product.getIngredient());
        tvInstructionDetail.setText(product.getInstruction());

        // Load thông tin biến thể đầu tiên (V1) để hiển thị giá
        db.collection("Product")
                .document(product.getId())
                .collection("Variant")
                .document("V1") // Giả sử V1 là biến thể mặc định để hiển thị giá
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
                    } else {
                        Log.w("ProductDetail", "Variant V1 not found for product: " + product.getId());
                        // Xử lý trường hợp không có biến thể V1, ví dụ: ẩn giá
                        tvOriginalPrice.setVisibility(View.GONE);
                        tvDiscountedPrice.setText("Đang cập nhật giá");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetail", "Error loading variant V1: " + e.getMessage());
                    // Xử lý lỗi khi tải biến thể
                    tvOriginalPrice.setVisibility(View.GONE);
                    tvDiscountedPrice.setText("Lỗi tải giá");
                });

        txtUses1.setText(product.getUses1());
        txtUses2.setText(product.getUses2());
        txtUses3.setText(product.getUses3());
        txtUses4.setText(product.getUses4());
    }

    // Phương thức để tải chi tiết danh mục và các sản phẩm cùng danh mục
    private void loadCategoryDetails(String cateid) {
        db.collection("Category")
                .document(cateid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String mcValue = documentSnapshot.getString("MC");
                        tvProductCategory.setText(mcValue);
                    } else {
                        Log.w("ProductDetail", "Category not found for ID: " + cateid);
                        tvProductCategory.setText("Danh mục không xác định");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetail", "Error loading category details: " + e.getMessage());
                    tvProductCategory.setText("Lỗi tải danh mục");
                });
        loadSameCategoryProducts(cateid); // Gọi để tải các sản phẩm cùng danh mục
    }

    // Phương thức để tải các sản phẩm cùng danh mục
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

                        // Kiểm tra null trước khi so sánh và loại bỏ sản phẩm hiện tại
                        if (p != null && p.getId() != null && currentProductId != null && !p.getId().equals(currentProductId)) {
                            sameCateProducts.add(p);
                        }
                    }
                    displaySameCategoryProducts(sameCateProducts);
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetail", "Error loading same category products: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi tải sản phẩm cùng danh mục.", Toast.LENGTH_SHORT).show();
                });
    }

    // Phương thức để hiển thị các sản phẩm cùng danh mục
    private void displaySameCategoryProducts(List<Product> productList) {
        sameCateProductLayout.removeAllViews(); // Xóa các View cũ trước khi thêm mới
        LayoutInflater inflater = LayoutInflater.from(this); // Sử dụng LayoutInflater cục bộ

        for (Product p : productList) {
            View itemView = inflater.inflate(R.layout.item_same_cate_product, sameCateProductLayout, false);

            ImageView img = itemView.findViewById(R.id.imgSameCateProduct);
            TextView name = itemView.findViewById(R.id.txtSameCateProduct);

            Glide.with(this).load(p.getImageUrl()).into(img);
            name.setText(p.getName());

            // Thiết lập sự kiện click cho mỗi sản phẩm tương tự
            itemView.setOnClickListener(v -> {
                // Tải lại Activity với sản phẩm mới
                Intent intent = new Intent(this, ProductDetail.class);
                intent.putExtra("productId", p.getId());
                startActivity(intent);
                finish(); // Đóng Activity hiện tại để tránh chồng chất
            });

            sameCateProductLayout.addView(itemView);
        }
    }
}