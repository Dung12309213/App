package com.example.applepie.UI;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Calendar;

public class YourProfileActivity extends AppCompatActivity {

    private ShapeableImageView imageView;
    private TextView textViewName, tvDefaultAddress;
    private EditText edtFullName, edtPhone, edtEmail, edtDob;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);

        // ÁNH XẠ VIEW
        imageView = findViewById(R.id.imgProfile);
        textViewName = findViewById(R.id.textView2);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtDob = findViewById(R.id.edtDob);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        rbOther = findViewById(R.id.rbOther);
        tvDefaultAddress = findViewById(R.id.tvDefaultAddress);
        Button btnSave = findViewById(R.id.btnSave);
        ImageButton btnBack = findViewById(R.id.btnBack);
        ImageButton btnEditAvatar = findViewById(R.id.imageButton3);

        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // HIỂN THỊ DỮ LIỆU
        loadUserInfo();

        // CHỌN NGÀY SINH
        edtDob.setOnClickListener(v -> showDatePicker());

        // CHỌN ẢNH ĐẠI DIỆN
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageView.setImageURI(selectedImageUri);
                            prefs.edit().putString("user_avatar_uri", selectedImageUri.toString()).apply();
                        }
                    }
                });

        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // NÚT LƯU
        btnSave.setOnClickListener(v -> saveUserInfo());

        // NÚT QUAY LẠI
        btnBack.setOnClickListener(v -> finish());

        // CHUYỂN ĐẾN MÀN ĐỊA CHỈ
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserInfo() {
        String name = prefs.getString("user_name", "");
        String phone = prefs.getString("user_phone", "");
        String email = prefs.getString("user_email", "");
        String dob = prefs.getString("user_dob", "");
        String gender = prefs.getString("user_gender", "");
        String avatarUri = prefs.getString("user_avatar_uri", "");
        String address = prefs.getString("user_address", "Chưa có địa chỉ");

        edtFullName.setText(name);
        edtPhone.setText(phone);
        edtEmail.setText(email);
        edtDob.setText(dob);
        tvDefaultAddress.setText(address);
        textViewName.setText(name);

        if (gender.equals("Nam")) rbMale.setChecked(true);
        else if (gender.equals("Nữ")) rbFemale.setChecked(true);
        else if (gender.equals("Khác")) rbOther.setChecked(true);

        if (!avatarUri.isEmpty()) {
            imageView.setImageURI(Uri.parse(avatarUri));
        }
    }

    private void saveUserInfo() {
        String name = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();
        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        if (selectedGenderId != -1) {
            RadioButton selected = findViewById(selectedGenderId);
            gender = selected.getText().toString();
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", name);
        editor.putString("user_phone", phone);
        editor.putString("user_email", email);
        editor.putString("user_dob", dob);
        editor.putString("user_gender", gender);
        editor.apply();

        textViewName.setText(name);
        Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, day) -> edtDob.setText(String.format("%02d/%02d/%04d", day, month + 1, year)),
                y, m, d
        );
        dialog.show();
    }
}
