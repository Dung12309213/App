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
 * Fragment hiển thị danh sách đơn hàng đã hoàn thành (Completed Orders)
 */
public class OrdersCompletedFragment extends Fragment {

    public OrdersCompletedFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Gắn layout tương ứng cho fragment này
        return inflater.inflate(R.layout.fragment_orders_completed, container, false);
    }
}
