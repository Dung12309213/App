package com.example.applepie.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.NotificationAdapter;
import com.example.applepie.Model.NotificationModel;
import com.example.applepie.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationAdapter adapter;
    private final List<Object> notificationItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        recyclerView = findViewById(R.id.recyclerNotifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadMockData();

        adapter = new NotificationAdapter(notificationItems);
        recyclerView.setAdapter(adapter);
    }

    private void loadMockData() {
        notificationItems.add("Today");
        notificationItems.add(new NotificationModel("Order Shipped", "Your order has been shipped", "1h", false, "Today"));
        notificationItems.add(new NotificationModel("Flash Sale Alert", "Get 20% off now", "1h", false, "Today"));

        notificationItems.add("Yesterday");
        notificationItems.add(new NotificationModel("Order Shipped", "Tracking number: 123456", "1d", true, "Yesterday"));
        notificationItems.add(new NotificationModel("Product Review Request", "Please rate your product", "1d", true, "Yesterday"));
    }
}
