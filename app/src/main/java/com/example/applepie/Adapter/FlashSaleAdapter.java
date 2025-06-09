package com.example.applepie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

import com.example.applepie.Model.Product;
import com.example.applepie.R;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public FlashSaleAdapter(Context context, List<Product> products) {
        this.context = context;
        this.productList = products;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvDiscount, tvPrice, tvOldPrice, tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
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
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice());
        holder.tvOldPrice.setText(product.getOldPrice());
        holder.tvOldPrice.setPaintFlags(holder.tvOldPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvDiscount.setText(product.getDiscountPercent() + "%");

        // Nếu ảnh là url, bạn có thể dùng thư viện Glide/Picasso để load ảnh:
        // Glide.with(context).load(product.getImageUrl()).into(holder.imgProduct);

        // Tạm thời dùng ảnh mẫu trong drawable
        holder.imgProduct.setImageResource(com.example.applepie.R.drawable.ic_homepage_mau2);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
