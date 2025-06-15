package com.example.applepie.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private ShapeableImageView profileImage;
    private TextView txtProfileTitle, txtUserName, txtLoginButton;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private SharedPreferences prefs;
    private boolean isLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Ánh xạ view
        btnBack = findViewById(R.id.imageButton2);
        btnEdit = findViewById(R.id.imageButton3);
        profileImage = findViewById(R.id.imageView);
        txtProfileTitle = findViewById(R.id.textView);
        txtUserName = findViewById(R.id.textView2);
        txtLoginButton = findViewById(R.id.itemLogin);

        // Đọc trạng thái đăng nhập
        prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);

        updateUI();

        // Chọn ảnh đại diện
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

        btnEdit.setOnClickListener(v -> {
            if (!isLoggedIn) {
                Toast.makeText(this, "Bạn cần đăng nhập để chỉnh sửa ảnh", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Các mục cần đăng nhập mới được truy cập
        findViewById(R.id.itemHelpcenter).setOnClickListener(v ->
                startActivity(new Intent(this, HelpCenterActivity.class)));

        findViewById(R.id.itemPaymentMethods).setOnClickListener(v -> {
            if (checkLogin()) startActivity(new Intent(this, PaymentMethodsActivity.class));
        });

        findViewById(R.id.itemSettings).setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class)); // sẽ check login riêng trong Settings
        });

        findViewById(R.id.itemPolicy).setOnClickListener(v ->
                startActivity(new Intent(this, PolicyActivity.class)));

        findViewById(R.id.itemMyorders).setOnClickListener(v -> {
            if (checkLogin()) startActivity(new Intent(this, MyOrdersActivity.class));
        });

        txtLoginButton.setOnClickListener(v -> {
            if (isLoggedIn) {
                new AlertDialog.Builder(this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất?")
                        .setPositiveButton("Đăng xuất", (dialog, which) -> {
                            prefs.edit().clear().apply();
                            isLoggedIn = false;
                            updateUI();
                        })
                        .setNegativeButton("Huỷ", null)
                        .show();
            } else {
                startActivity(new Intent(this, LoginScreen1.class));
            }
        });

        BottomNavHelper.setupBottomNav(this);
    }

    private void updateUI() {
        if (isLoggedIn) {
            String name = prefs.getString("user_name", "User");
            txtUserName.setText(name);
            txtLoginButton.setText("Log out");
            // Có thể load avatar từ URL nếu lưu link
        } else {
            txtUserName.setText("");
            txtLoginButton.setText("Login");
            profileImage.setImageResource(R.mipmap.ic_launcher); // avatar mặc định
        }
    }

    private boolean checkLogin() {
        if (!isLoggedIn) {
            startActivity(new Intent(this, LoginScreen1.class));
            return false;
        }
        return true;
    }
}
