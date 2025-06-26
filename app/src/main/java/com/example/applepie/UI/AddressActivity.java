package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.AddressAdapter;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<AddressModel> addressList;
    ImageButton btnBack;
    Button btnChange;
    TextView btnAdd;

    UserSessionManager userSessionManager;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addressList = new ArrayList<>();

        loadAddresses();

        addViews();
        addEvents();

    }

    private void addEvents() {
        btnBack.setOnClickListener(v -> finish());
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

    private void addViews() {
        btnBack = findViewById(R.id.btnBack);
        btnChange = findViewById(R.id.btnChange);
        btnAdd = findViewById(R.id.btnAddAddress);
        recyclerView = findViewById(R.id.recyclerAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList);
        recyclerView.setAdapter(adapter);
    }

    private void loadAddresses() {
        String userId = userSessionManager.getUserId();

        if (userId != null && !userId.isEmpty()) {
            // Truy vấn Firestore để lấy địa chỉ từ subcollection "Address"
            db.collection("User").document(userId).collection("Address")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Lấy danh sách địa chỉ từ query snapshot
                            for (DocumentSnapshot document : task.getResult()) {
                                // Chuyển DocumentSnapshot thành AddressModel
                                AddressModel address = document.toObject(AddressModel.class);
                                if (address != null) {
                                    address.setAddressid(document.getId());
                                    addressList.add(address);  // Thêm địa chỉ vào danh sách
                                }
                            }
                            // Sau khi dữ liệu đã được tải xong, cập nhật adapter
                            adapter.notifyDataSetChanged();
                        } else {
                            // Nếu có lỗi khi lấy dữ liệu, bạn có thể thông báo cho người dùng
                            Toast.makeText(AddressActivity.this, "Có lỗi khi lấy địa chỉ", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Nếu không có userId, thông báo lỗi hoặc xử lý phù hợp
            Toast.makeText(AddressActivity.this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
        }
    }
}
