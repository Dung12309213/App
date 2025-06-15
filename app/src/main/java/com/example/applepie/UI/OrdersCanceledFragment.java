package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.OrderAdapter;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;
import com.example.applepie.Storage.CartStorage;

import java.util.ArrayList;
import java.util.List;

public class OrdersCanceledFragment extends Fragment {

    public OrdersCanceledFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_canceled, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerOrdersCanceled);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OrderModel> mockOrders = new ArrayList<>();
        mockOrders.add(new OrderModel("CC12345DH999", 1, "345.000 VND", R.drawable.ic_homepage_mau2, "Mua lại", "canceled"));

        OrderAdapter adapter = new OrderAdapter(mockOrders, order -> {
            CartStorage.addItem(order);
            startActivity(new Intent(getContext(), CartActivity.class));
        });

        recyclerView.setAdapter(adapter);
        return view;
    }
}
