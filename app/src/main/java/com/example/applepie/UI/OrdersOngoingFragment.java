package com.example.applepie.UI;

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

import java.util.ArrayList;
import java.util.List;

public class OrdersOngoingFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders_ongoing, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerOrdersOngoing);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<OrderModel> mockOrders = new ArrayList<>();
        mockOrders.add(new OrderModel("DH001", 2, "450.000", R.drawable.ic_homepage_mau2, "Mua lại", "ongoing"));
        mockOrders.add(new OrderModel("DH002", 1, "300.000", R.drawable.ic_homepage_mau2, "Mua lại", "ongoing"));

        OrderAdapter adapter = new OrderAdapter(mockOrders, null);

        recyclerView.setAdapter(adapter);

        return view;
    }
}

