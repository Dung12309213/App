package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class AddAddressActivity extends AppCompatActivity {

    private EditText edtReceiverName, edtPhone, edtDetail;
    private CheckBox checkDefault;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        // Ánh xạ view
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDetail = findViewById(R.id.edtDetail);
        checkDefault = findViewById(R.id.checkDefault);
        Button btnSave = findViewById(R.id.btnSave);
        ImageButton btnBack = findViewById(R.id.btnBack);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Quay lại
        btnBack.setOnClickListener(v -> finish());

        // Lưu địa chỉ
        btnSave.setOnClickListener(v -> {
            String name = edtReceiverName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String detail = edtDetail.getText().toString().trim();
            boolean isDefault = checkDefault.isChecked();

            // Validate cơ bản
            if (name.isEmpty() || phone.isEmpty() ||detail.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullAddress = detail;

            // Lưu vào SharedPreferences nếu được chọn làm mặc định
            if (isDefault) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_address", fullAddress);
                editor.apply();
            }

            Toast.makeText(this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();

            // Trả về hoặc chuyển tiếp
            Intent result = new Intent();
            result.putExtra("saved_address", fullAddress);
            setResult(RESULT_OK, result);
            finish();
        });
    }
}
