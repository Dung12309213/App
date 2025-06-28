package com.example.applepie.UI; // Đảm bảo đúng package

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Thêm import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast; // Thêm import Toast

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.OrderAdapter;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;
import com.example.applepie.Storage.CartStorage;
import com.example.applepie.Util.UserSessionManager; // Thêm import UserSessionManager
import com.google.firebase.firestore.FirebaseFirestore; // Thêm import FirebaseFirestore
import com.google.firebase.firestore.Query; // Thêm import Query (nếu dùng orderBy)
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrdersCanceledFragment extends Fragment implements OrderAdapter.OnReorderClickListener { // Implement listener

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<OrderModel> orderList;
    private FirebaseFirestore db; // Khai báo Firestore
    private UserSessionManager userSessionManager; // Khai báo UserSessionManager

    public OrdersCanceledFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_canceled, container, false); // Giữ nguyên layout của bạn

        recyclerView = view.findViewById(R.id.recyclerOrdersCanceled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance(); // Khởi tạo Firestore
        userSessionManager = new UserSessionManager(getContext()); // Khởi tạo UserSessionManager
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this); // 'this' vì fragment này implement OnReorderClickListener
        recyclerView.setAdapter(orderAdapter);

        loadOrders("canceled"); // Tải đơn hàng với trạng thái "canceled"
        return view;
    }

    // Phương thức để tải đơn hàng từ Firestore
    private void loadOrders(String status) {
        String userId = userSessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập để xem đơn hàng đã hủy của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Order")
                .whereEqualTo("userid", userId) // Lọc theo ID người dùng hiện tại
                .whereEqualTo("status", status) // Lọc theo trạng thái "canceled"
                .orderBy("purchasedate", Query.Direction.DESCENDING) // Sắp xếp theo ngày mua hàng gần nhất
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        orderList.clear(); // Xóa danh sách hiện có
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderModel order = document.toObject(OrderModel.class);
                            order.setId(document.getId()); // Gán ID tài liệu Firestore vào OrderModel
                            orderList.add(order);
                        }
                        orderAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                        if (orderList.isEmpty()) {
                            Toast.makeText(getContext(), "Không có đơn hàng đã hủy.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("CanceledOrdersFragment", "Lỗi khi tải đơn hàng đã hủy: ", task.getException());
                        Toast.makeText(getContext(), "Lỗi khi tải đơn hàng đã hủy.", Toast.LENGTH_SHORT).show();
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