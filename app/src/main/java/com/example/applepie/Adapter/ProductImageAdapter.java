package com.example.applepie.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.R;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private List<String> imageUrls; // Sử dụng List<String> để chứa các URL ảnh

    // Constructor để truyền danh sách URL ảnh vào Adapter
    public ProductImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    // Tạo ViewHolder mới (tức là tạo layout cho mỗi item hình ảnh)
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_product_image.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_image, parent, false);
        return new ImageViewHolder(view);
    }

    // Gán dữ liệu (hình ảnh) vào ViewHolder tại vị trí cụ thể
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Sử dụng Glide để tải ảnh từ URL vào ImageView
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.itemView.getContext())  // Context của itemView
                .load(imageUrl)  // URL của ảnh
                .into(holder.imageView);  // Đặt ảnh vào ImageView
    }

    // Trả về tổng số lượng hình ảnh
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    // ViewHolder để giữ các View con của mỗi item hình ảnh
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImageItem);  // Đảm bảo ID đúng với layout của bạn
        }
    }
}


