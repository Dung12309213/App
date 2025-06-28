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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Model.AddressModel;
import com.example.applepie.Model.User;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class YourProfileActivity extends AppCompatActivity {

    private ShapeableImageView imageView;
    private TextView textViewName, tvDefaultAddress;
    private EditText edtFullName, edtPhone, edtEmail, edtDob;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale, rbOther;
    private Button btnSave;
    private ImageButton btnBack;
    private SharedPreferences prefs;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private UserSessionManager userSessionManager;
    private FirebaseFirestore db;
    private static final int ADDRESS_ACTIVITY_REQUEST_CODE = 200;

    public static final String MODE_SELECTION_KEY = "address_selection_mode";
    public static final String MODE_SET_DEFAULT = "set_default"; // Mode: đặt làm mặc định
    public static final String MODE_SELECT_ADDRESS = "select_address"; // Mode: chọn địa chỉ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addViews();
        addEvents();

        // HIỂN THỊ DỮ LIỆU
        loadUserInfo();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadUserInfo();
    }

    private void addEvents() {
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


        // NÚT LƯU
        btnSave.setOnClickListener(v -> saveUserInfo());

        // NÚT QUAY LẠI
        btnBack.setOnClickListener(v -> finish());

        // CHUYỂN ĐẾN MÀN ĐỊA CHỈ
        findViewById(R.id.btnEditAddress).setOnClickListener(v -> {
            Intent intent = new Intent(YourProfileActivity.this, AddressActivity.class);
            // Thêm mode vào Intent
            intent.putExtra(MODE_SELECTION_KEY, MODE_SET_DEFAULT); // Báo là mở để đặt địa chỉ mặc định
            startActivityForResult(intent, ADDRESS_ACTIVITY_REQUEST_CODE);
        });
    }

    private void addViews() {
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
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        textViewName.setText(userSessionManager.getUserName());
    }

    private void loadUserInfo() {
        // Lấy userId từ UserSessionManager
        String userId = userSessionManager.getUserId();

        if (!userId.isEmpty()) {
            // Truy vấn Firestore để lấy thông tin người dùng
            DocumentReference userRef = db.collection("User").document(userId);
            userRef.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User user = document.toObject(User.class);

                                if (user != null) {
                                    // Cập nhật thông tin lên UI
                                    edtFullName.setText(user.getName());
                                    edtPhone.setText(user.getPhone());
                                    edtEmail.setText(user.getEmail());
                                    if (user.getDob() != null) {
                                        String formattedDate = formatDate(user.getDob());
                                        edtDob.setText(formattedDate);
                                    }

                                    // Set Gender RadioButton
                                    if ("male".equals(user.getGender())) rbMale.setChecked(true);
                                    else if ("female".equals(user.getGender())) rbFemale.setChecked(true);
                                    else if ("other".equals(user.getGender())) rbOther.setChecked(true);

                                    // Lấy địa chỉ mặc định từ subcollection "Address"
                                    getDefaultAddress(userId);
                                }
                            }
                        }
                    });
        }
    }

    // Phương thức để lấy địa chỉ mặc định từ subcollection "Address"
    private void getDefaultAddress(String userId) {
        // Truy vấn subcollection "Address" của người dùng
        CollectionReference addressRef = db.collection("User").document(userId).collection("Address");
        addressRef.whereEqualTo("defaultCheck", true)  // Tìm địa chỉ có defaultCheck = true
                .limit(1)  // Chỉ lấy một địa chỉ
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Lấy địa chỉ mặc định
                            DocumentSnapshot addressDoc = querySnapshot.getDocuments().get(0);
                            AddressModel address = addressDoc.toObject(AddressModel.class);

                            if (address != null) {
                                // Cập nhật địa chỉ vào UI
                                String addressString = address.toString();  // Chuyển đối tượng thành chuỗi
                                tvDefaultAddress.setText(addressString);
                            }
                        } else {
                            // Không có địa chỉ mặc định, hiển thị "Chưa có địa chỉ"
                            tvDefaultAddress.setText("Chưa có địa chỉ mặc định");
                        }
                    } else {
                        // Trường hợp truy vấn thất bại
                        tvDefaultAddress.setText("Có lỗi khi lấy địa chỉ");
                    }
                });
    }
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(date);
    }

    private void saveUserInfo() {
        String name = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dobString  = edtDob.getText().toString().trim();
        String gender = "";
        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        // Kiểm tra xem nút Radio nào được chọn
        if (rbMale.isChecked()) {
            gender = "male";
        } else if (rbFemale.isChecked()) {
            gender = "female";
        } else if (rbOther.isChecked()) {
            gender = "other";
        }
        Date dob = convertStringToDate(dobString);

        // Lưu thông tin người dùng vào Firestore
        String userId = userSessionManager.getUserId();
        DocumentReference userRef = db.collection("User").document(userId);
        userRef.update(
                "name", name,
                "phone", phone,
                "email", email,
                "dob", dob,
                "gender", gender
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
                textViewName.setText(name);
                userSessionManager.saveUser(userId, name, "");
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi lưu thông tin", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private Date convertStringToDate(String dobString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            return dateFormat.parse(dobString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADDRESS_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String userId = userSessionManager.getUserId();
                if (userId != null && !userId.isEmpty()) {
                    getDefaultAddress(userId);
                }
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
}
