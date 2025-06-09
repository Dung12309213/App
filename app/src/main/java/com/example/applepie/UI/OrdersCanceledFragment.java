package com.example.applepie.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import  com.example.applepie.R;

/**
 * Fragment hiển thị danh sách đơn hàng đã hủy (Canceled Orders)
 */
public class OrdersCanceledFragment extends Fragment {

    public OrdersCanceledFragment() {
        // Bắt buộc phải có constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Gắn layout cho fragment đơn hàng đã hủy
        return inflater.inflate(R.layout.fragment_orders_canceled, container, false);
    }
}
