package com.example.applepie.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private Context context;
    private List<Variant> itemList;
    private OnCartChangeListener listener;
    private UserSessionManager userSessionManager;

    public interface OnCartChangeListener {
        void onCartUpdated();
        void onRequestRemove(int position);
    }

    public CartItemAdapter(Context context, List<Variant> itemList, OnCartChangeListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.listener = listener;
        this.userSessionManager = new UserSessionManager(context);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Variant item = itemList.get(position);

        holder.txtSize.setText(item.getVariant());
        holder.txtQuantity.setText(String.valueOf(item.getQuantity()));
        int priceToUse = (item.getSecondprice() > 0) ? item.getSecondprice() : item.getPrice();

        holder.txtPrice.setText(formatCurrency(priceToUse * item.getQuantity()));

        // Lấy productId từ Variant
        String productId = item.getProductid();

        // Truy vấn Firestore để lấy tên sản phẩm và ảnh
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Product")
                .document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy tên sản phẩm và ảnh
                        String productName = documentSnapshot.getString("name");
                        List<String> imageUrls = (List<String>) documentSnapshot.get("imageUrl");

                        // Cập nhật thông tin vào các View
                        if (productName != null) {
                            holder.txtName.setText(productName);
                        }

                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            // Lấy ảnh đầu tiên trong mảng imageUrl
                            String firstImageUrl = imageUrls.get(0);
                            Glide.with(context)
                                    .load(firstImageUrl)  // Dùng Glide để tải ảnh
                                    .into(holder.imgProduct);  // Đặt ảnh vào ImageView
                        }
                    }
                });

        holder.btnIncrease.setOnClickListener(v -> {
            // Tăng số lượng
            item.setQuantity(item.getQuantity() + 1);

            // Cập nhật RecyclerView
            notifyItemChanged(position);

            // Cập nhật Firestore
            updateCartInFirestore(item);

            // Thông báo cho listener
            listener.onCartUpdated();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() == 1) {
                // Nếu số lượng là 1, yêu cầu xóa sản phẩm khỏi giỏ hàng
                listener.onRequestRemove(position);
            } else {
                // Giảm số lượng
                item.setQuantity(item.getQuantity() - 1);

                // Cập nhật RecyclerView
                notifyItemChanged(position);

                // Cập nhật Firestore
                updateCartInFirestore(item);

                // Thông báo cho listener
                listener.onCartUpdated();
            }
        });
    }

    private void updateCartInFirestore(Variant item) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Lấy userId từ UserSessionManager
        String userId = userSessionManager.getUserId();

        // Cập nhật số lượng trong Firestore
        db.collection("User")
                .document(userId)
                .collection("Cart")
                .whereEqualTo("productid", item.getProductid())
                .whereEqualTo("id", item.getId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Chỉ lấy document đầu tiên
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Cập nhật số lượng trong document đã tìm thấy
                        documentSnapshot.getReference()
                                .update("quantity", item.getQuantity());
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
