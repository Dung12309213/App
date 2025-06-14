package com.example.applepie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.CartItem;
import com.example.applepie.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> itemList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartUpdated();
        void onRequestRemove(int position);
    }

    public CartItemAdapter(Context context, List<CartItem> itemList, OnCartChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = itemList.get(position);
        holder.txtName.setText(item.getName());
        holder.txtSize.setText(item.getSize());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        holder.txtPrice.setText(formatCurrency(item.getPrice() * item.getQuantity()));
        holder.imgProduct.setImageResource(item.getImageResId());

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() == 1) {
                listener.onRequestRemove(position);
            } else {
                item.setQuantity(item.getQuantity() - 1);
                notifyItemChanged(position);
                listener.onCartUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void removeItem(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
        listener.onCartUpdated();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtSize, txtPrice, txtQuantity;
        ImageView imgProduct;
        ImageButton btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
        }
    }

    private String formatCurrency(int number) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(number) + " đ";
    }
}
