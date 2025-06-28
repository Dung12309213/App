package com.example.applepie.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Model.OrderItem;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;
import com.example.applepie.UI.Order_Detail;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OnReorderClickListener {
        void onReorder(OrderModel order);
    }

    private final List<OrderModel> orderList;
    private final OnReorderClickListener reorderListener;
    private FirebaseFirestore db;

    public OrderAdapter(List<OrderModel> orderList, OnReorderClickListener listener) {
        this.orderList = orderList;
        this.reorderListener = listener;
        this.db = FirebaseFirestore.getInstance();
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
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

        holder.txtOrderId.setText("Mã Đơn: " + order.getId());

        fetchOrderDetailsForDisplay(order, holder.imgProduct, holder.txtQuantity, context);

        String price = numberFormat.format(order.getTotal()) + " đ";

        holder.txtPrice.setText(price);

        // Ẩn tất cả nút trước
        holder.btnReview.setVisibility(View.GONE);
        holder.btnReorder.setVisibility(View.GONE);

        // Hiển thị tuỳ loại đơn
        switch (order.getStatus()) {
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
            case "Đang xử lý": // Chuỗi này phải khớp chính xác với trạng thái bạn lưu trong Firestore
                holder.btnReorder.setText("Theo dõi");
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
            default:
                // Nếu có trạng thái khác, bạn có thể xử lý ở đây hoặc không hiển thị nút
                holder.btnReorder.setText("Xem chi tiết");
                holder.btnReorder.setVisibility(View.VISIBLE);
                break;
        }

        // Gán sự kiện click cho btnReorder nếu là "Mua lại"
        holder.btnReorder.setOnClickListener(v -> {
            if (reorderListener != null) {
                // Logic cho nút "Mua lại"
                if (holder.btnReorder.getText().toString().equalsIgnoreCase("Mua lại")) {
                    reorderListener.onReorder(order); // Kích hoạt sự kiện mua lại
                }
                // Logic cho nút "Theo dõi" hoặc "Xem chi tiết"
                else if (holder.btnReorder.getText().toString().equalsIgnoreCase("Theo dõi") ||
                        holder.btnReorder.getText().toString().equalsIgnoreCase("Xem chi tiết")) {
                    // TODO: Triển khai intent để mở OrderDetailActivity và truyền order.getId()
                    Intent detailIntent = new Intent(context, Order_Detail.class);
                    detailIntent.putExtra("orderId", order.getId());
                    context.startActivity(detailIntent);
                }
            }
        });
    }
    private void fetchOrderDetailsForDisplay(OrderModel order, ImageView imgProduct, TextView txtQuantity, Context context) {
        // 1. Truy vấn subcollection "Items" của đơn hàng hiện tại
        db.collection("Order").document(order.getId()).collection("Item")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalQuantity = 0;
                    final String[] firstProductId = {null};

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot itemDoc : queryDocumentSnapshots) {
                            OrderItem item = itemDoc.toObject(OrderItem.class);
                            totalQuantity += item.getQuantity(); // Cộng dồn số lượng
                            if (firstProductId[0] == null) { // Truy cập và gán giá trị qua phần tử mảng
                                firstProductId[0] = item.getProductid();
                            }
                        }
                    } else {
                        Log.d("OrderAdapter", "Không có mặt hàng nào trong đơn hàng " + order.getId());
                    }

                    // 2. Cập nhật TextView tổng số lượng
                    txtQuantity.setText(context.getString(R.string.quantity_format, totalQuantity));
                    // Lưu tổng số lượng vào OrderModel trong bộ nhớ (để dùng lại nếu cần)
                    order.setTotalItemCount(totalQuantity);

                    // 3. Truy vấn collection "Product" để lấy URL ảnh bằng firstProductId
                    if (firstProductId[0] != null && !firstProductId[0].isEmpty()) {
                        db.collection("Product").document(firstProductId[0])
                                .get()
                                .addOnSuccessListener(productDoc -> {
                                    if (productDoc.exists()) {
                                        // Đảm bảo trường "imageUrl" trong Product là một List<String>
                                        List<String> imageUrls = (List<String>) productDoc.get("imageUrl");
                                        if (imageUrls != null && !imageUrls.isEmpty()) {
                                            Glide.with(context)
                                                    .load(imageUrls.get(0)) // Tải ảnh đầu tiên
                                                    //.placeholder(R.drawable.placeholder_image) // Ảnh tạm thời khi đang tải
                                                    //.error(R.drawable.placeholder_image) // Ảnh khi có lỗi
                                                    .into(imgProduct);
                                            // Lưu URL ảnh vào OrderModel trong bộ nhớ
                                            order.setFirstProductImageUrl(imageUrls.get(0));
                                        } else {
                                            //imgProduct.setImageResource(R.drawable.placeholder_image); // Mặc định nếu không có URL ảnh
                                        }
                                    } else {
                                        Log.w("OrderAdapter", "Không tìm thấy tài liệu sản phẩm với ID: " + firstProductId[0]);
                                        //imgProduct.setImageResource(R.drawable.placeholder_image); // Mặc định nếu không tìm thấy sản phẩm
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("OrderAdapter", "Lỗi khi tải ảnh sản phẩm cho " + firstProductId[0] + ": " + e.getMessage());
                                    //imgProduct.setImageResource(R.drawable.placeholder_image); // Mặc định khi lỗi
                                });
                    } else {
                        //imgProduct.setImageResource(R.drawable.placeholder_image); // Mặc định nếu không có product ID hoặc không có item nào
                    }

                })
                .addOnFailureListener(e -> {
                    Log.e("OrderAdapter", "Lỗi khi lấy các mặt hàng của đơn hàng " + order.getId() + ": " + e.getMessage());
                    txtQuantity.setText(context.getString(R.string.quantity_format, 0)); // Đặt về 0 nếu có lỗi
                    //imgProduct.setImageResource(R.drawable.placeholder_image); // Mặc định khi lỗi
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