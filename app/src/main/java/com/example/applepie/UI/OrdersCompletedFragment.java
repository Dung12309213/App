package com.example.applepie.UI; // Đảm bảo đúng package

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.OrderAdapter;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;
import com.example.applepie.Storage.CartStorage;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersCompletedFragment extends Fragment implements OrderAdapter.OnReorderClickListener { // Implement listener

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore db;
    private UserSessionManager userSessionManager;

    public OrdersCompletedFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_completed, container, false); // Giữ nguyên layout của bạn

        recyclerView = view.findViewById(R.id.recyclerOrdersCompleted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        userSessionManager = new UserSessionManager(getContext());
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this); // 'this' vì fragment này implement OnReorderClickListener
        recyclerView.setAdapter(orderAdapter);

        loadOrders("completed"); // Tải đơn hàng với trạng thái "completed"
        return view;
    }

    // Phương thức để tải đơn hàng từ Firestore
    private void loadOrders(String status) {
        String userId = userSessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn hàng đã giao của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Order")
                .whereEqualTo("userid", userId) // Lọc theo ID người dùng hiện tại
                .whereEqualTo("status", status) // Lọc theo trạng thái "completed"
                .orderBy("purchasedate", Query.Direction.DESCENDING) // Sắp xếp theo ngày mua hàng gần nhất
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderModel order = document.toObject(OrderModel.class);
                            order.setId(document.getId());
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged();
                        if (orderList.isEmpty()) {
                            Toast.makeText(getContext(), "Không có đơn hàng đã giao.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("CompletedOrdersFragment", "Lỗi khi tải đơn hàng đã giao: ", task.getException());
                        Toast.makeText(getContext(), "Lỗi khi tải đơn hàng đã giao.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onReorder(OrderModel order) {
        // Xử lý logic mua lại đơn hàng ở đây
        // Hiện tại chỉ thêm vào giỏ và chuyển đến CartActivity
        CartStorage.addItem(order); // Lưu ý: CartStorage.addItem(OrderModel) có thể cần điều chỉnh để xử lý OrderModel
        startActivity(new Intent(getContext(), CartActivity.class));
        Toast.makeText(getContext(), "Bạn muốn mua lại đơn hàng " + order.getId(), Toast.LENGTH_SHORT).show();
    }
}