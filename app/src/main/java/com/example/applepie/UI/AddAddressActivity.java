package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AddAddressActivity extends AppCompatActivity {

    private EditText edtReceiverName, edtPhone, edtDetail, edtCity, edtDistrict, edtWard;
    private CheckBox checkDefault;
    Button btnSave;
    ImageButton btnBack;
    UserSessionManager userSessionManager;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        addViews();
        addEvents();

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        
    }

    private void addEvents() {
        // Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Lưu địa chỉ
        btnSave.setOnClickListener(v -> {
            String name = edtReceiverName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String detail = edtDetail.getText().toString().trim();
            boolean isDefault = checkDefault.isChecked();

            // Validate cơ bản
            if (name.isEmpty() || phone.isEmpty() || detail.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = detail;

            // Tạo đối tượng AddressModel
            AddressModel address = new AddressModel();
            address.setName(name);
            address.setPhone(phone);
            address.setStreet(detail); // Bạn có thể thêm các trường khác như phường, quận...
            address.setDefaultCheck(isDefault);

            // Nếu địa chỉ được đánh dấu là mặc định, cần phải cập nhật các địa chỉ cũ thành không mặc định
            if (isDefault) {
                updatePreviousDefaultAddresses();
            }

            String userId = userSessionManager.getUserId();

            db.collection("User").document(userId).collection("Address")
                    .add(address)
                    .addOnSuccessListener(documentReference -> {
                        // Nếu địa chỉ được lưu thành công, trả về kết quả
                        Toast.makeText(AddAddressActivity.this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();

                        // Trả lại địa chỉ đã lưu cho Activity trước đó
                        Intent result = new Intent();
                        result.putExtra("saved_address", fullAddress);
                        setResult(RESULT_OK, result);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Nếu có lỗi khi lưu vào Firestore
                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi lưu địa chỉ", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    // Cập nhật các địa chỉ mặc định cũ thành false
    private void updatePreviousDefaultAddresses() {
        String userId = "userId"; // Lấy userId từ session hoặc từ Intent

        // Truy vấn tất cả các địa chỉ với defaultCheck = true
        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Lặp qua các địa chỉ có defaultCheck = true và cập nhật chúng
                        QuerySnapshot querySnapshot = task.getResult();
                        for (DocumentSnapshot document : querySnapshot) {
                            // Cập nhật từng địa chỉ thành defaultCheck = false
                            db.collection("User").document(userId)
                                    .collection("Address")
                                    .document(document.getId())
                                    .update("defaultCheck", false)
                                    .addOnSuccessListener(aVoid -> {
                                        // Cập nhật thành công, tiếp tục
                                    })
                                    .addOnFailureListener(e -> {
                                        // Nếu có lỗi trong việc cập nhật
                                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi cập nhật địa chỉ mặc định", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Nếu có lỗi khi truy vấn Firestore
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy địa chỉ mặc định", Toast.LENGTH_SHORT).show();
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
