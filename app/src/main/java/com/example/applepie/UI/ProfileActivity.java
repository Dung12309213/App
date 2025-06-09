package com.example.applepie.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import  com.example.applepie.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private ShapeableImageView profileImage;
    private TextView txtProfileTitle, txtUserName;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

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

        findViewById(R.id.itemLogin).setOnClickListener(v ->
                startActivity(new Intent(this, LoginScreen1.class)));



        BottomNavHelper.setupBottomNav(this);
    }
}
