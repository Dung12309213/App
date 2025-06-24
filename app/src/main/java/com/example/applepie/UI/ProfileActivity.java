package com.example.applepie.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import  com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

   ImageButton btnEdit;
   ShapeableImageView profileImage;
    TextView tvLoginLogout, tvUserName;
    ImageView imgLoginLogout;
    ConstraintLayout itemYourProfile, itemPaymentMethods, itemCoupon, itemMyorders;
    private UserSessionManager sessionManager;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new UserSessionManager(this);

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
        findViewById(R.id.itemCoupon).setOnClickListener(v ->
                startActivity(new Intent(this, Coupon.class)));

        findViewById(R.id.itemPolicy).setOnClickListener(v ->
                startActivity(new Intent(this, PolicyActivity.class)));

        findViewById(R.id.itemMyorders).setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));


        findViewById(R.id.itemCoupon).setOnClickListener(v ->
                startActivity(new Intent(this, Coupon.class)));

        findViewById(R.id.itemYourprofile).setOnClickListener(v ->
                startActivity(new Intent(this, YourProfileActivity.class)));
    }

    private void addViews() {
        btnEdit = findViewById(R.id.imgChangeProfileImage);
        profileImage = findViewById(R.id.imgProfile);
        tvUserName = findViewById(R.id.tvUsername);
        tvLoginLogout=findViewById(R.id.tvLoginLogout);
        imgLoginLogout = findViewById(R.id.imgLoginLogout);
        itemPaymentMethods = findViewById(R.id.itemPaymentMethods);
        itemYourProfile = findViewById(R.id.itemYourprofile);
        itemCoupon = findViewById(R.id.itemCoupon);
        itemMyorders = findViewById(R.id.itemMyorders);

    }

    @SuppressLint("Range")
    private void checkLoggedIn() {
        String userName = sessionManager.getUserName();

        if (!userName.equals("Guest")) {
            tvLoginLogout.setText("Logout");
            tvUserName.setText(userName);
            imgLoginLogout.setImageResource(R.drawable.ic_logout);
            itemYourProfile.setVisibility(View.VISIBLE);
            itemPaymentMethods.setVisibility(View.VISIBLE);
            itemMyorders.setVisibility(View.VISIBLE);
            findViewById(R.id.itemLogin).setOnClickListener(v -> logout());
        } else {
            tvLoginLogout.setText("Login");
            tvUserName.setText("Guest");
            imgLoginLogout.setImageResource(R.drawable.ic_login);
            itemYourProfile.setVisibility(View.GONE);
            itemPaymentMethods.setVisibility(View.GONE);
            itemMyorders.setVisibility(View.GONE);
            itemCoupon.setVisibility(View.GONE);
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
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    sessionManager.logout();
                    tvLoginLogout.setText("Login");
                    imgLoginLogout.setImageResource(R.drawable.ic_login);
                    tvUserName.setText("Guest");
                    itemYourProfile.setVisibility(View.GONE);
                    itemPaymentMethods.setVisibility(View.GONE);
                    itemMyorders.setVisibility(View.GONE);
                    findViewById(R.id.itemLogin).setOnClickListener(v -> login());
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
