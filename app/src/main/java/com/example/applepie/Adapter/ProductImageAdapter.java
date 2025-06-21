package com.example.applepie.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.R;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private List<Integer> imageResIds; // Danh sách các ID tài nguyên hình ảnh (ví dụ: R.drawable.hinh1)

    // Constructor để truyền danh sách hình ảnh vào Adapter
    public ProductImageAdapter(List<Integer> imageResIds) {
        this.imageResIds = imageResIds;
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
        holder.imageView.setImageResource(imageResIds.get(position));
    }

    // Trả về tổng số lượng hình ảnh
    @Override
    public int getItemCount() {
        return imageResIds.size();
    }

    // ViewHolder để giữ các View con của mỗi item hình ảnh
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImageItem);
        }
    }
}


