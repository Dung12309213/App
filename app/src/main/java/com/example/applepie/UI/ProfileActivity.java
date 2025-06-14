package com.example.applepie.UI;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.applepie.Connector.SQLiteHelper;
import  com.example.applepie.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private ShapeableImageView profileImage;
    private TextView tvLoginLogout, tvUserName;
    private ImageView imgLoginLogout;
    private ConstraintLayout itemYourProfile;
    SQLiteHelper dbHelper;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new SQLiteHelper(this);

        addViews();
        addEvents();

        BottomNavHelper.setupBottomNav(this);
        checkLoggedIn();
    }

    private void addEvents() {
        // Khởi tạo launcher chọn ảnh
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            profileImage.setImageURI(selectedImageUri);
                        }
                    } else {
                        Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                });

        // Bấm nút chỉnh sửa ảnh đại diện => mở thư viện ảnh
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Các sự kiện mở các mục khác
        findViewById(R.id.itemHelpcenter).setOnClickListener(v ->
                startActivity(new Intent(this, HelpCenterActivity.class)));

        findViewById(R.id.itemPaymentMethods).setOnClickListener(v ->
                startActivity(new Intent(this, PaymentMethodsActivity.class)));

        findViewById(R.id.itemSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        findViewById(R.id.itemPolicy).setOnClickListener(v ->
                startActivity(new Intent(this, PolicyActivity.class)));

        findViewById(R.id.itemMyorders).setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));


        findViewById(R.id.itemMyCoupons).setOnClickListener(v ->
                startActivity(new Intent(this, Coupon.class)));
    }

    private void addViews() {
        btnEdit = findViewById(R.id.imgChangeProfileImage);
        profileImage = findViewById(R.id.imgProfile);
        tvUserName = findViewById(R.id.tvUsername);
        tvLoginLogout=findViewById(R.id.tvLoginLogout);
        imgLoginLogout = findViewById(R.id.imgLoginLogout);
        itemYourProfile = findViewById(R.id.itemYourprofile);
    }

    private void checkLoggedIn() {
        // Lấy dữ liệu người dùng từ SQLite
        Cursor cursor = dbHelper.getUser();

        // Kiểm tra nếu có dữ liệu trong bảng User
        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có, tức là người dùng đã đăng nhập
            tvLoginLogout.setText("Logout");
            tvUserName.setText(cursor.getString(cursor.getColumnIndex("name")));  // Hiển thị tên người dùng
            imgLoginLogout.setImageResource(R.drawable.ic_logout);
            itemYourProfile.setVisibility(View.VISIBLE);
            // Thay đổi nút thành "Logout"
            findViewById(R.id.itemLogin).setOnClickListener(v -> logout());
        } else {
            // Nếu không có, tức là chưa đăng nhập
            tvLoginLogout.setText("Login");
            tvUserName.setText("Guest");
            imgLoginLogout.setImageResource(R.drawable.ic_login);
            itemYourProfile.setVisibility(View.INVISIBLE);
            // Thay đổi nút thành "Login"
            findViewById(R.id.itemLogin).setOnClickListener(v -> login());
        }
    }

    // Phương thức xử lý đăng nhập
    private void login() {
        // Xử lý đăng nhập, ví dụ mở màn hình đăng nhập và lưu trạng thái khi đăng nhập thành công
        Intent intent = new Intent(ProfileActivity.this, LoginScreen1.class);
        startActivity(intent);
    }

    // Phương thức xử lý đăng xuất
    private void logout() {
        // Xóa thông tin người dùng trong SQLite khi đăng xuất
        dbHelper.logoutUser();

        // Cập nhật giao diện
        tvLoginLogout.setText("Login");
        tvUserName.setText("Guest");
        itemYourProfile.setVisibility(View.INVISIBLE);

        // Cập nhật nút Login
        findViewById(R.id.itemLogin).setOnClickListener(v -> login());
    }
}
