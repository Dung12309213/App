package com.example.applepie.Adapter;

import android.app.Activity;
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
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.UI.ProductDetail;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.ViewHolder> {

    private Context context;
    private List<Variant> variantList;

    public FlashSaleAdapter(Context context, List<Variant> variantList) {
        this.context = context;
        this.variantList = variantList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvDiscount, tvPrice, tvSecond, tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvPrice = itemView.findViewById(R.id.tvVariantPrice);
            tvSecond = itemView.findViewById(R.id.tvVariantSecondPrice);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flash_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Variant variant = variantList.get(position);

        FirebaseFirestore.getInstance()
                .collection("Product")
                .document(variant.getProductid())
                .get()
                .addOnSuccessListener(productDoc -> {
                    if (productDoc.exists()) {
                        // Lấy ảnh
                        List<String> imageUrls = (List<String>) productDoc.get("imageUrl");
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            if (context instanceof Activity && !((Activity) context).isDestroyed()) {
                                Glide.with(context)
                                        .load(imageUrls.get(0))
                                        .placeholder(R.drawable.ic_homepage_mau2)
                                        .into(holder.imgProduct);
                            }
                        } else {
                            holder.imgProduct.setImageResource(R.drawable.ic_homepage_mau2);
                        }

                        // Gộp tên sản phẩm + biến thể
                        String productName = productDoc.getString("name");
                        String variantName = variant.getVariant();
                        String fullName = (productName != null ? productName : "Sản phẩm") +
                                (variantName != null ? " " + variantName : "");

                        holder.tvName.setText(fullName);
                    }
                });

        holder.tvPrice.setText(String.format("%,d đ", variant.getPrice()));
        holder.tvSecond.setText(String.format("%,d đ", variant.getSecondprice()));
        holder.tvSecond.setVisibility(View.VISIBLE);
        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        int discount = (variant.getPrice() - variant.getSecondprice()) * 100 / variant.getPrice();
        holder.tvDiscount.setText(discount + "%");
        holder.tvDiscount.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetail.class);
            intent.putExtra("productId", variant.getProductid()); // Mở đúng sản phẩm
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variantList.size();
    }
}
