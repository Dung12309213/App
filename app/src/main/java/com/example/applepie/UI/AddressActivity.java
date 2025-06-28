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
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends BaseActivity {

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

    private AddressModel addressFromCallingActivity;

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
        if (intent != null) {
            if (intent.hasExtra(MODE_SELECTION_KEY)) {
                currentSelectionMode = intent.getStringExtra(MODE_SELECTION_KEY);
            } else {
                currentSelectionMode = MODE_SET_DEFAULT;
            }

            // Đọc địa chỉ được truyền từ Activity gọi nó (CheckoutActivity)
            if (intent.hasExtra("currently_selected_address")) {
                addressFromCallingActivity = (AddressModel) intent.getSerializableExtra("currently_selected_address");
            }
        } else {
            currentSelectionMode = MODE_SET_DEFAULT;
        }

        addViews(); // Khởi tạo UI trước khi tải dữ liệu

        // --- ĐIỀU CHỈNH QUAN TRỌNG Ở ĐÂY ---
        // Chỉ tải địa chỉ từ Firestore nếu người dùng đã đăng nhập
        if (userSessionManager.isLoggedIn()) {
            loadAddresses(); // Tải địa chỉ từ Firestore
        } else {
            // Nếu chưa đăng nhập, và có địa chỉ tạm thời từ CheckoutActivity, thêm vào danh sách
            if (addressFromCallingActivity != null) {
                addressList.add(addressFromCallingActivity);
                Toast.makeText(this, "Bạn đang sử dụng địa chỉ tạm thời.", Toast.LENGTH_LONG).show();
            }
            // Khởi tạo adapter ngay cả khi danh sách rỗng (hoặc chỉ có địa chỉ tạm thời)
            adapter = new AddressAdapter(addressList, addressFromCallingActivity);
            recyclerView.setAdapter(adapter);
        }

        addEvents();

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Cần tải lại địa chỉ chỉ khi người dùng đã đăng nhập
        // và không phải là trường hợp sau khi quay lại từ AddAddressActivity với địa chỉ khách.
        if (userSessionManager.isLoggedIn()) {
            addressList.clear(); // Xóa list cũ để tránh trùng lặp
            loadAddresses(); // Tải lại địa chỉ từ Firestore
        } else {
            // Nếu chưa đăng nhập, không tải lại từ Firestore.
            // Danh sách địa chỉ đã được setup trong onCreate hoặc onActivityResult.
            if (adapter != null) {
                adapter.notifyDataSetChanged(); // Chỉ cập nhật adapter nếu danh sách đã thay đổi thủ công
            }
        }
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

            if (MODE_SET_DEFAULT.equals(currentSelectionMode)) {
                // Chỉ cho phép đặt mặc định nếu đã đăng nhập và địa chỉ có ID thật sự
                if (userSessionManager.isLoggedIn() && selected.getAddressid() != null && !selected.getAddressid().isEmpty()) {
                    updateDefaultAddress(selected);
                } else {
                    Toast.makeText(this, "Không thể đặt địa chỉ mặc định khi chưa đăng nhập hoặc địa chỉ chưa được lưu.", Toast.LENGTH_SHORT).show();
                }
            } else if (MODE_SELECT_ADDRESS.equals(currentSelectionMode)) {
                returnSelectedAddress(selected);
            } else {
                Toast.makeText(AddressActivity.this, "Chế độ không xác định cho nút Change", Toast.LENGTH_SHORT).show();
            }
        });

        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
            // Không truyền userId hay thông tin đăng nhập ở đây.
            // AddAddressActivity sẽ tự kiểm tra trạng thái đăng nhập.
            startActivityForResult(intent, ADD_EDIT_ADDRESS_REQUEST);
        });
    }

    private void addViews() {
        btnBack = findViewById(R.id.btnBack);
        btnChange = findViewById(R.id.btnChange);
        btnAdd = findViewById(R.id.btnAddAddress);
        recyclerView = findViewById(R.id.recyclerAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadAddresses() {
        String userId = userSessionManager.getUserId();

        if (userId != null && !userId.isEmpty()) {
            db.collection("User").document(userId).collection("Address")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            addressList.clear(); // Xóa danh sách cũ
                            // Thêm địa chỉ đang được chọn nếu nó không có trong Firestore (trường hợp khách thêm mới)
                            if (addressFromCallingActivity != null &&
                                    (addressFromCallingActivity.getAddressid() == null || addressFromCallingActivity.getAddressid().isEmpty())) {
                                addressList.add(addressFromCallingActivity);
                            }

                            for (DocumentSnapshot document : task.getResult()) {
                                AddressModel address = document.toObject(AddressModel.class);
                                if (address != null) {
                                    address.setAddressid(document.getId());
                                    // Tránh thêm trùng lặp nếu địa chỉ khách đã được lưu sau khi đăng nhập
                                    boolean alreadyExists = false;
                                    for(AddressModel existingAddress : addressList) {
                                        if (existingAddress.getAddressid() != null && existingAddress.getAddressid().equals(address.getAddressid())) {
                                            alreadyExists = true;
                                            break;
                                        }
                                    }
                                    if (!alreadyExists) {
                                        addressList.add(address);
                                    }
                                }
                            }
                            adapter = new AddressAdapter(addressList, addressFromCallingActivity);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(AddressActivity.this, "Có lỗi khi lấy địa chỉ", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Không làm gì nếu chưa đăng nhập, danh sách sẽ được khởi tạo rỗng
            // hoặc chứa địa chỉ tạm thời từ CheckoutActivity (đã xử lý trong onCreate)
        }
    }

    private void updateDefaultAddress(AddressModel selectedAddress) {
        String userId = userSessionManager.getUserId();
        if (userId == null || userId.isEmpty() || selectedAddress.getAddressid() == null || selectedAddress.getAddressid().isEmpty()) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng hoặc địa chỉ không hợp lệ.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
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
                                        db.collection("User").document(userId).collection("Address")
                                                .document(selectedAddress.getAddressid())
                                                .update("defaultCheck", true)
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(AddressActivity.this, "Đã đặt địa chỉ mặc định thành công", Toast.LENGTH_SHORT).show();
                                                    setResult(RESULT_OK);
                                                    finish();
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

    private void returnSelectedAddress(AddressModel selectedAddress) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selected_address_object", selectedAddress);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_ADDRESS_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                AddressModel newGuestAddress = (AddressModel) data.getSerializableExtra("new_guest_address");
                if (newGuestAddress != null) {
                    // Nếu người dùng CHƯA đăng nhập và thêm địa chỉ mới,
                    // thêm địa chỉ này vào danh sách tạm thời
                    if (!userSessionManager.isLoggedIn()) {
                        addressList.clear(); // Xóa các địa chỉ cũ (nếu có địa chỉ tạm thời khác)
                        addressList.add(newGuestAddress);
                        // Cập nhật addressFromCallingActivity để nó được chọn trong adapter
                        addressFromCallingActivity = newGuestAddress;
                        adapter = new AddressAdapter(addressList, addressFromCallingActivity);
                        recyclerView.setAdapter(adapter);
                        Toast.makeText(this, "Địa chỉ mới đã được thêm tạm thời.", Toast.LENGTH_SHORT).show();
                    }
                    // Nếu đã đăng nhập, AddAddressActivity đã tự lưu vào Firestore và refresh rồi.
                    // Không cần làm gì thêm ở đây ngoài việc gọi loadAddresses() ở onResume
                    // (hoặc nếu muốn hiển thị ngay, gọi loadAddresses() luôn).
                } else {
                    // Nếu không phải địa chỉ khách mới, có thể là đã có thay đổi ở Firestore (khi đã đăng nhập)
                    // Hoặc là một chỉnh sửa địa chỉ khách mà không thay đổi trạng thái
                    // Sẽ được refresh ở onResume nếu user đã đăng nhập.
                }

                Toast.makeText(this, "Địa chỉ đã được cập nhật từ AddAddressActivity!", Toast.LENGTH_SHORT).show();
                // Báo hiệu cho Activity gọi nó (CheckoutActivity) rằng có thay đổi
                setResult(RESULT_OK);
                //finish(); // Không finish ở đây để người dùng có thể chọn địa chỉ vừa thêm
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Thao tác thêm/sửa địa chỉ bị hủy.", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
        }
    }
}
