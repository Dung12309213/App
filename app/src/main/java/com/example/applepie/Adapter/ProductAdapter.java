package com.example.applepie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Model.Product;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.UI.ProductDetail;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private List<String> productIds; // Để lưu trữ ID tài liệu cho ProductDetail
    private Context context;
    private FirebaseFirestore db;

    public ProductAdapter(Context context, List<Product> productList, List<String> productIds) {
        this.context = context;
        this.productList = productList;
        this.productIds = productIds;
        this.db = FirebaseConnector.getInstance(); // Lấy thể hiện của Firestore
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout của từng item sản phẩm
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String documentId = productIds.get(position);

        // Đặt tên sản phẩm
        holder.tvName.setText(product.getName());

        List<String> imageUrls = product.getImageUrl();
        Glide.with(context).load(imageUrls.get(0)).into(holder.img);

        // Lấy dữ liệu biến thể (variant) từ Firestore
        db.collection("Product")
                .document(documentId)
                .collection("Variant")
                .document("V1") // Giả sử V1 là biến thể mặc định
                .get()
                .addOnSuccessListener(variantDoc -> {
                    if (variantDoc.exists()) {
                        Variant v = variantDoc.toObject(Variant.class);
                        if (v != null) {
                            // Định dạng và đặt giá
                            holder.tvPrice.setText(String.format("%,d đ", v.getPrice()));
                            holder.tvSecondPrice.setText(String.format("%,d đ", v.getSecondprice()));

                            // Xử lý hiển thị giá khuyến mãi và phần trăm giảm giá
                            if (v.getSecondprice() > 0) {
                                holder.tvSecondPrice.setVisibility(View.VISIBLE);
                                holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // Gạch ngang giá gốc

                                int discountPercent = (v.getPrice() - v.getSecondprice()) * 100 / v.getPrice();
                                holder.tvProductDiscountPercent.setText(discountPercent + "%");
                                holder.tvProductDiscountPercent.setVisibility(View.VISIBLE);
                            } else {
                                holder.tvSecondPrice.setVisibility(View.GONE);
                                holder.tvPrice.setPaintFlags(0); // Bỏ gạch ngang nếu không có khuyến mãi
                                holder.tvProductDiscountPercent.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        // Thiết lập sự kiện click cho toàn bộ item sản phẩm
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("productId", documentId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ViewHolder để giữ các view của mỗi item sản phẩm
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvPrice, tvSecondPrice, tvProductDiscountPercent;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProductListItem);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvSecondPrice = itemView.findViewById(R.id.tvProductSecondPrice);
            tvProductDiscountPercent = itemView.findViewById(R.id.tvProductDiscountPercent);
        }
    }
}
