package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.AddressAdapter;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private static final int ADD_EDIT_ADDRESS_REQUEST = 1; // Mã yêu cầu
    private String currentSelectionMode; // Biến để lưu trữ mode hiện tại

    public static final String MODE_SELECTION_KEY = "address_selection_mode";
    public static final String MODE_SET_DEFAULT = "set_default";
    public static final String MODE_SELECT_ADDRESS = "select_address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addressList = new ArrayList<>();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(MODE_SELECTION_KEY)) {
            currentSelectionMode = intent.getStringExtra(MODE_SELECTION_KEY);
        } else {
            // Nếu không có mode được truyền, đặt một mode mặc định (ví dụ: chỉ để hiển thị hoặc set default)
            currentSelectionMode = MODE_SET_DEFAULT;
        }

        loadAddresses();

        addViews();
        addEvents();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Clear the existing list to avoid duplicates before reloading
        addressList.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged(); // Cập nhật adapter để hiển thị rỗng hoặc làm mới trạng thái
        }
        loadAddresses(); // Tải lại địa chỉ mỗi khi Activity được resume
    }

    private void addEvents() {
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        btnChange.setOnClickListener(v -> {
            AddressModel selected = adapter.getSelectedAddress();
            if (selected == null) {
                Toast.makeText(AddressActivity.this, "Vui lòng chọn một địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- Logic phân biệt 2 dạng của nút Change ---
            if (MODE_SET_DEFAULT.equals(currentSelectionMode)) {
                // Dạng 1: Đặt địa chỉ được chọn làm mặc định (khi mở từ YourProfileActivity)
                updateDefaultAddress(selected);
            } else if (MODE_SELECT_ADDRESS.equals(currentSelectionMode)) {
                // Dạng 2: Chọn địa chỉ và quay về Activity gọi nó (khi mở từ CheckoutActivity)
                returnSelectedAddress(selected);
            } else {
                Toast.makeText(AddressActivity.this, "Chế độ không xác định cho nút Change", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            startActivityForResult(intent, ADD_EDIT_ADDRESS_REQUEST);
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
                            addressList.clear();
                            // Lấy danh sách địa chỉ từ query snapshot
                            for (DocumentSnapshot document : task.getResult()) {
                                // Chuyển DocumentSnapshot thành AddressModel
                                AddressModel address = document.toObject(AddressModel.class);
                                if (address != null) {
                                    address.setAddressid(document.getId());
                                    addressList.add(address);
                                }
                            }
                            adapter.notifyDataSetChanged();

                            adapter = new AddressAdapter(addressList); // Tạo lại adapter để cập nhật selectedPosition
                            recyclerView.setAdapter(adapter);
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

    private void updateDefaultAddress(AddressModel selectedAddress) {
        String userId = userSessionManager.getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
                        // 1. Bỏ mặc định các địa chỉ cũ
                        for (DocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals(selectedAddress.getAddressid())) {
                                updateTasks.add(db.collection("User").document(userId)
                                        .collection("Address").document(document.getId())
                                        .update("defaultCheck", false));
                            }
                        }

                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(allUpdatesTask -> {
                                    if (allUpdatesTask.isSuccessful()) {
                                        // 2. Đặt địa chỉ được chọn làm mặc định mới
                                        db.collection("User").document(userId).collection("Address")
                                                .document(selectedAddress.getAddressid())
                                                .update("defaultCheck", true)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(AddressActivity.this, "Đã đặt địa chỉ mặc định thành công", Toast.LENGTH_SHORT).show();
                                                    // Báo hiệu cho YourProfileActivity rằng có thay đổi
                                                    setResult(RESULT_OK);
                                                    finish(); // Quay lại YourProfileActivity
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(AddressActivity.this, "Lỗi khi đặt địa chỉ mặc định: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(AddressActivity.this, "Lỗi khi bỏ mặc định địa chỉ cũ: " + allUpdatesTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(AddressActivity.this, "Lỗi truy vấn địa chỉ mặc định cũ: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- Phương thức xử lý Dạng 2: Trả về địa chỉ đã chọn ---
    private void returnSelectedAddress(AddressModel selectedAddress) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_address_object", selectedAddress); // Đặt khóa là "selected_address_object"
        setResult(RESULT_OK, resultIntent); // Báo hiệu thành công và gửi dữ liệu
        finish(); // Quay lại CheckoutActivity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_ADDRESS_REQUEST) {
            if (resultCode == RESULT_OK) {
                // AddAddressActivity đã lưu/cập nhật thành công, tải lại AddressActivity
                Toast.makeText(this, "Địa chỉ đã được cập nhật từ AddAddressActivity!", Toast.LENGTH_SHORT).show();
                addressList.clear();
                loadAddresses();
                // Báo hiệu cho Activity gọi nó (YourProfileActivity hoặc CheckoutActivity) rằng có thay đổi
                setResult(RESULT_OK); // Để YourProfileActivity/CheckoutActivity cũng refresh
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Thao tác thêm/sửa địa chỉ bị hủy.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED); // Báo hiệu cho Activity gọi nó
            }
        }
    }
}
