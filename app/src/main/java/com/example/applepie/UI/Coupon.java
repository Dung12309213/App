package com.example.applepie.UI;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.example.applepie.Adapter.CouponAdapter;
import com.example.applepie.Model.Voucher;
import com.example.applepie.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class Coupon extends AppCompatActivity { // Tên Activity

    private RecyclerView recyclerViewVouchers;
    private CouponAdapter couponAdapter; // Sử dụng CouponAdapter
    private List<Voucher> voucherList;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon); // Set layout cho Activity này

        // Ánh xạ các View từ layout
        recyclerViewVouchers = findViewById(R.id.recyclerViewVouchers);
        backButton = findViewById(R.id.backButton);

        // Thiết lập LayoutManager cho RecyclerView
        recyclerViewVouchers.setLayoutManager(new LinearLayoutManager(this));

        // Chuẩn bị dữ liệu mẫu cho danh sách voucher
        voucherList = new ArrayList<>();
/*        voucherList.add(new Voucher("GIAM60", "Đơn hàng tối thiểu 600.000 đ", "Giảm 60.000 đ"));
        voucherList.add(new Voucher("FREESHIP", "Đơn hàng tối thiểu 150.000 đ", "Miễn phí vận chuyển"));
        voucherList.add(new Voucher("FLASH50", "Áp dụng cho đơn hàng từ 200.000 đ", "Giảm 50.000 đ"));
        voucherList.add(new Voucher("NEWUSER100", "Dành cho khách hàng mới", "Giảm 100.000 đ"));*/
        // Thêm các voucher khác nếu cần

        // Khởi tạo Adapter và gán cho RecyclerView
        couponAdapter = new CouponAdapter(voucherList, this); // Khởi tạo CouponAdapter
        recyclerViewVouchers.setAdapter(couponAdapter);

        // Xử lý sự kiện click cho nút back
        backButton.setOnClickListener(v -> finish()); // Quay lại màn hình trước đó
    }
}