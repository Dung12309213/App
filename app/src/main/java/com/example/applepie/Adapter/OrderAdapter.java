package com.example.applepie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnReorderClickListener {
        void onReorder(OrderModel order);
    }

    private final List<OrderModel> orderList;
    private final OnReorderClickListener reorderListener;

    public OrderAdapter(List<OrderModel> orderList, OnReorderClickListener listener) {
        this.orderList = orderList;
        this.reorderListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_card, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);
        Context context = holder.itemView.getContext();

        holder.imgProduct.setImageResource(order.getImageResId());
        holder.txtOrderId.setText(order.getOrderCode());

        String quantity = context.getString(R.string.quantity_format, order.getItemCount());
        String price = context.getString(R.string.price_format, order.getTotalPrice());

        holder.txtQuantity.setText(quantity);
        holder.txtPrice.setText(price);

        // Ẩn tất cả nút trước
        holder.btnReview.setVisibility(View.GONE);
        holder.btnReorder.setVisibility(View.GONE);

        // Hiển thị tuỳ loại đơn
        switch (order.getOrderType()) {
            case "completed":
                holder.btnReview.setText("Đánh giá");
                holder.btnReview.setVisibility(View.VISIBLE);
                holder.btnReorder.setText("Mua lại");
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
            case "canceled":
                holder.btnReorder.setText("Mua lại");
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
            case "ongoing":
                holder.btnReorder.setText("Theo dõi");
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
        }

        // Gán sự kiện click cho btnReorder nếu là "Mua lại"
        holder.btnReorder.setOnClickListener(v -> {
            if (reorderListener != null && holder.btnReorder.getText().toString().equalsIgnoreCase("Mua lại")) {
                reorderListener.onReorder(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtOrderId, txtQuantity, txtPrice;
        Button btnReview, btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnReview = itemView.findViewById(R.id.btnReview);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}