package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.AddressAdapter;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<AddressModel> addressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnChange = findViewById(R.id.btnChange);
        TextView btnAdd = findViewById(R.id.btnAddAddress);

        recyclerView = findViewById(R.id.recyclerAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMockAddresses();

        adapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(adapter);

        btnChange.setOnClickListener(v -> {
            AddressModel selected = adapter.getSelectedAddress();
            if (selected != null) {
                // TODO: Gửi về màn hình trước hoặc lưu
            }
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivity(intent);
        });

    }

    private void loadMockAddresses() {
        addressList = new ArrayList<>();
        addressList.add(new AddressModel("Quốc Trịnh", "Trường Đại học Kinh tế - Luật", "0123 456 789", true));
        addressList.add(new AddressModel("Quốc Trịnh", "Trường Đại học Kinh tế - Luật", "0123 456 789", false));
        addressList.add(new AddressModel("Quốc Trịnh", "Trường Đại học Kinh tế - Luật", "0123 456 789", false));
        addressList.add(new AddressModel("Quốc Trịnh", "Trường Đại học Kinh tế - Luật", "0123 456 789", false));
    }
}
