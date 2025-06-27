package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText edtReceiverName, edtPhone, edtDetail, edtCity, edtDistrict, edtWard;
    private CheckBox checkDefault;
    Button btnSave;
    ImageButton btnBack;
    UserSessionManager userSessionManager;
    FirebaseFirestore db;
    private String currentAddressId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addViews();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CHANGE_ADDRESS")) {
            // Có dữ liệu được truyền, đây là chế độ chỉnh sửa
            AddressModel addressToEdit = (AddressModel) intent.getSerializableExtra("CHANGE_ADDRESS");
                currentAddressId = addressToEdit.getAddressid();

                // Điền dữ liệu vào các EditText và CheckBox
                edtReceiverName.setText(addressToEdit.getName());
                edtPhone.setText(addressToEdit.getPhone());
                edtDetail.setText(addressToEdit.getStreet());
                edtWard.setText(addressToEdit.getWard());
                edtDistrict.setText(addressToEdit.getDistrict());
                edtCity.setText(addressToEdit.getProvince());
                checkDefault.setChecked(addressToEdit.isDefaultCheck());
        }
        addEvents();
    }

    private void addEvents() {
        // Quay lại
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Báo hiệu là không có thay đổi nào được lưu
            finish();
        });

        // Lưu địa chỉ
        btnSave.setOnClickListener(v -> {
            String name = edtReceiverName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String detail = edtDetail.getText().toString().trim();
            String province = edtCity.getText().toString().trim();
            String district = edtDistrict.getText().toString().trim();
            String ward = edtWard.getText().toString().trim();
            boolean isDefault = checkDefault.isChecked(); // Lấy trạng thái của checkbox

            if (name.isEmpty() || phone.isEmpty() || detail.isEmpty() || province.isEmpty() || ward.isEmpty() || district.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = userSessionManager.getUserId();

            // Tạo Map dữ liệu cho địa chỉ mới
            Map<String, Object> addressData  = new HashMap<>();
            addressData.put("name", name);
            addressData.put("phone", phone);
            addressData.put("street", detail);
            addressData.put("province", province);
            addressData.put("district", district);
            addressData.put("ward", ward);
            addressData.put("defaultCheck", isDefault);

            if (currentAddressId != null) {
                // Đây là chế độ chỉnh sửa địa chỉ đã có
                if (isDefault) {
                    // Nếu địa chỉ này được đặt làm mặc định, cập nhật các địa chỉ cũ và sau đó cập nhật địa chỉ này
                    updatePreviousDefaultsAndThenUpdateCurrent(userId, currentAddressId, addressData, detail);
                } else {
                    // Chỉ cập nhật địa chỉ này (không đặt làm mặc định)
                    updateAddressInFirestore(userId, currentAddressId, addressData, detail);
                }
            } else {
                // Đây là chế độ thêm địa chỉ mới
                if (isDefault) {
                    // Nếu địa chỉ mới này được đặt làm mặc định, cập nhật các địa chỉ cũ và sau đó thêm mới
                    updatePreviousDefaultsAndAddNew(userId, addressData, detail);
                } else {
                    // Chỉ thêm địa chỉ mới (không đặt làm mặc định)
                    addNewAddressToFirestore(userId, addressData, detail);
                }
            }
        });
    }
    // Hàm riêng để thêm địa chỉ mới vào Firestore
    private void addNewAddressToFirestore(String userId, Map<String, Object> addressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .add(addressData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddAddressActivity.this, "Đã lưu địa chỉ mới thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Báo hiệu đã lưu thành công
                    finish(); // Kết thúc Activity này
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAddressActivity.this, "Có lỗi khi lưu địa chỉ mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddAddressActivity", "Lỗi khi lưu địa chỉ mới: ", e);
                });
    }

    // Hàm để cập nhật địa chỉ hiện có trong Firestore
    private void updateAddressInFirestore(String userId, String addressId, Map<String, Object> addressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address").document(addressId)
                .update(addressData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddAddressActivity.this, "Đã cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Báo hiệu đã lưu thành công
                    finish(); // Kết thúc Activity này
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAddressActivity.this, "Lỗi khi cập nhật địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ: ", e);
                });
    }

    // Hàm để cập nhật các địa chỉ cũ thành không mặc định, sau đó thêm địa chỉ mới
    private void updatePreviousDefaultsAndAddNew(String userId, Map<String, Object> newAddressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Cập nhật từng địa chỉ mặc định cũ thành false
                            Task<Void> updateTask = db.collection("User").document(userId)
                                    .collection("Address")
                                    .document(document.getId())
                                    .update("defaultCheck", false);
                            updateTasks.add(updateTask);
                        }

                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(allUpdatesTask -> {
                                    if (allUpdatesTask.isSuccessful()) {
                                        // Nếu tất cả địa chỉ cũ đã được cập nhật thành công, thì thêm địa chỉ mới
                                        addNewAddressToFirestore(userId, newAddressData, fullAddress);
                                    } else {
                                        Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ mặc định cũ: " + allUpdatesTask.getException());
                                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi cập nhật địa chỉ mặc định cũ. Vẫn thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                                        addNewAddressToFirestore(userId, newAddressData, fullAddress); // Vẫn thêm địa chỉ mới
                                    }
                                });
                    } else {
                        Log.e("AddAddressActivity", "Lỗi khi truy vấn địa chỉ mặc định cũ: " + task.getException());
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy địa chỉ mặc định cũ để cập nhật. Vẫn thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                        addNewAddressToFirestore(userId, newAddressData, fullAddress); // Vẫn thêm địa chỉ mới
                    }
                });
    }

    // Hàm để cập nhật các địa chỉ cũ thành không mặc định, sau đó cập nhật địa chỉ hiện tại
    private void updatePreviousDefaultsAndThenUpdateCurrent(String userId, String addressId, Map<String, Object> updatedAddressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            // Chỉ cập nhật những địa chỉ khác với địa chỉ hiện tại đang chỉnh sửa
                            if (!document.getId().equals(addressId)) {
                                Task<Void> updateTask = db.collection("User").document(userId)
                                        .collection("Address")
                                        .document(document.getId())
                                        .update("defaultCheck", false);
                                updateTasks.add(updateTask);
                            }
                        }

                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(allUpdatesTask -> {
                                    if (allUpdatesTask.isSuccessful()) {
                                        // Nếu tất cả địa chỉ cũ đã được cập nhật thành công, thì cập nhật địa chỉ hiện tại
                                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress);
                                    } else {
                                        Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ mặc định cũ (khi sửa): " + allUpdatesTask.getException());
                                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi cập nhật địa chỉ mặc định cũ. Vẫn cập nhật địa chỉ này.", Toast.LENGTH_SHORT).show();
                                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress); // Vẫn cố gắng cập nhật địa chỉ hiện tại
                                    }
                                });
                    } else {
                        Log.e("AddAddressActivity", "Lỗi khi truy vấn địa chỉ mặc định cũ (khi sửa): " + task.getException());
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy địa chỉ mặc định cũ để cập nhật. Vẫn cập nhật địa chỉ này.", Toast.LENGTH_SHORT).show();
                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress); // Vẫn cố gắng cập nhật địa chỉ hiện tại
                    }
                });
    }

    private void addViews() {
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDetail = findViewById(R.id.edtDetail);
        checkDefault = findViewById(R.id.checkDefault);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        edtCity = findViewById(R.id.edtDetailCity);
        edtDistrict = findViewById(R.id.edtDetailDistrict);
        edtWard = findViewById(R.id.edtDetailWard);
    }
}
