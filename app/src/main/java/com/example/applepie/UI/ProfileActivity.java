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

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.material.imageview.ShapeableImageView;

// THÊM IMPORT FIREBASE AUTH
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends BaseActivity {

    ImageButton btnEdit;
    ShapeableImageView profileImage;
    TextView tvLoginLogout, tvUserName;
    ImageView imgLoginLogout;
    ConstraintLayout itemYourProfile, itemPaymentMethods, itemCoupon, itemMyorders;
    private UserSessionManager sessionManager;

    // KHAI BÁO FIREBASE AUTH
    private FirebaseAuth mAuth;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new UserSessionManager(this);
        mAuth = FirebaseAuth.getInstance(); // KHỞI TẠO FIREBASE AUTH

        addViews();
        addEvents();

        BottomNavHelper.setupBottomNav(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoggedIn(); // Gọi lại để cập nhật trạng thái đăng nhập mỗi khi activity hiển thị
    }

    private void addEvents() {

        // Các sự kiện mở các mục khác
        findViewById(R.id.itemHelpcenter).setOnClickListener(v ->
                startActivity(new Intent(this, HelpCenterActivity.class)));

        findViewById(R.id.itemPaymentMethods).setOnClickListener(v ->
                startActivity(new Intent(this, CardPaymentActivity.class)));

        findViewById(R.id.itemSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        // Bạn có hai lần gán click listener cho itemCoupon, chỉ cần 1 là đủ
        findViewById(R.id.itemCoupon).setOnClickListener(v ->
                startActivity(new Intent(this, Coupon.class)));

        findViewById(R.id.itemPolicy).setOnClickListener(v ->
                startActivity(new Intent(this, PolicyActivity.class)));

        findViewById(R.id.itemMyorders).setOnClickListener(v ->
                startActivity(new Intent(this, MyOrdersActivity.class)));

        findViewById(R.id.itemYourprofile).setOnClickListener(v ->
                startActivity(new Intent(this, YourProfileActivity.class)));
    }

    private void addViews() {
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
        // Kiểm tra trạng thái đăng nhập bằng UserSessionManager
        String userName = sessionManager.getUserName();

        if (!userName.equals("Guest")) {
            // Đã đăng nhập
            tvLoginLogout.setText("Logout");
            tvUserName.setText(userName); // Hiển thị tên người dùng đã lưu
            imgLoginLogout.setImageResource(R.drawable.ic_logout);
            itemYourProfile.setVisibility(View.VISIBLE);
            itemMyorders.setVisibility(View.VISIBLE);
            findViewById(R.id.itemLogin).setOnClickListener(v -> logout()); // Đổi chức năng thành Logout
        } else {
            // Chưa đăng nhập
            tvLoginLogout.setText("Login");
            tvUserName.setText("Guest");
            imgLoginLogout.setImageResource(R.drawable.ic_login);
            itemYourProfile.setVisibility(View.GONE);
            itemMyorders.setVisibility(View.GONE);
            findViewById(R.id.itemLogin).setOnClickListener(v -> login()); // Đổi chức năng thành Login
        }
    }

    // Phương thức xử lý đăng nhập
    private void login() {
        Intent intent = new Intent(ProfileActivity.this, LoginScreen1.class);
        startActivity(intent);
    }

    // Phương thức xử lý đăng xuất
    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Đăng xuất khỏi Firebase Authentication
                    mAuth.signOut();

                    // Đăng xuất khỏi session cục bộ
                    sessionManager.logout();

                    // Cập nhật lại giao diện
                    Toast.makeText(ProfileActivity.this, "Bạn đã đăng xuất.", Toast.LENGTH_SHORT).show();
                    checkLoggedIn(); // Gọi lại để cập nhật giao diện ngay lập tức
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}