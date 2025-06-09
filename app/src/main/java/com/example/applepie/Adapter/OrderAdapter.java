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

import com.example.applepie.R;
import com.example.applepie.Model.OrderModel;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderModel> orderList;

    public OrderAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
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

        // Format đúng cách
        String quantity = context.getString(R.string.quantity_format, order.getItemCount());
        String price = context.getString(R.string.price_format, order.getTotalPrice());

        holder.txtQuantity.setText(quantity);
        holder.txtPrice.setText(price);
        holder.btnAction.setText(order.getActionText());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView txtOrderId, txtQuantity, txtPrice;
        Button btnAction;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtOrderId = itemView.findViewById(R.id.txtOrderId);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
