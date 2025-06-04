package com.duung.applepieapp;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileActivity extends AppCompatActivity {

    private ImageButton btnBack, btnEdit;
    private ShapeableImageView profileImage;
    private TextView txtProfileTitle, txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile); // dùng đúng tên file XML không chứa đuôi .xml

        // Gán ID
        btnBack = findViewById(R.id.imageButton2);
        btnEdit = findViewById(R.id.imageButton3);
        profileImage = findViewById(R.id.imageView);
        txtProfileTitle = findViewById(R.id.textView);
        txtUserName = findViewById(R.id.textView2);

        // Xử lý sự kiện nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Xử lý sự kiện nút chỉnh sửa (ví dụ)
        btnEdit.setOnClickListener(v -> {
            // TODO: Mở activity chỉnh sửa hồ sơ
        });

        // Các mục menu còn lại (ví dụ với "Your Profile")
        View yourProfileItem = findViewById(R.id.title);
        yourProfileItem.setOnClickListener(v -> {
            // TODO: Mở trang "Your Profile"
        });
        BottomNavHelper.setupBottomNav(this);

        // Làm tương tự với các mục còn lại nếu cần xử lý sự kiện
    }
}
