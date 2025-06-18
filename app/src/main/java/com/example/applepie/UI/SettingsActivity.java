package com.example.applepie.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.applepie.Connector.FirebaseConnector;
import com.example.applepie.Connector.SQLiteHelper;
import com.example.applepie.R;
import com.example.applepie.Service.EmailSender;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class SettingsActivity extends AppCompatActivity {

    SQLiteHelper dbHelper;

    private ImageButton btnBack;
    private ConstraintLayout itemSettingNotification, itemSettingPassword, itemSettingDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbHelper = new SQLiteHelper(this);

        addViews();
        addEvents();

        checkLoggedIn();
    }

    private void addViews() {
        btnBack = findViewById(R.id.imageButton2);
        itemSettingNotification = findViewById(R.id.itemSettingNotification);
        itemSettingPassword = findViewById(R.id.itemSettingPassword);
        itemSettingDeleteAccount = findViewById(R.id.itemSettingDeleteAccount);
    }

    private void addEvents() {
        // Nút back
        btnBack.setOnClickListener(v -> finish());

        // Password Manager
        itemSettingPassword.setOnClickListener(v -> {
                startActivity(new Intent(this, ChangePasswordActivity.class));
        });

        // Delete Account (hiện dialog xác nhận)
        itemSettingDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xoá tài khoản")
                    .setMessage("Bạn có chắc muốn xoá tài khoản này không?")
                    .setPositiveButton("Có", (dialog, which) -> {

                        Cursor cursor = dbHelper.getUser();
                        if (cursor != null && cursor.moveToFirst()) {
                            // Kiểm tra cột 'id' có tồn tại không
                            int idColumnIndex = cursor.getColumnIndex("id");
                            if (idColumnIndex != -1) {
                                String userId = cursor.getString(idColumnIndex);
                                Log.d("SettingsActivity", "UserId: " + userId);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("User").document(userId).get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                String email = document.getString("email");

                                                Log.d("SettingsActivity", "UserId: " + userId);

                                                // Kiểm tra nếu email là null hoặc rỗng
                                                if (email == null || email.isEmpty()) {
                                                    Log.e("SettingsActivity", "Email is null or empty");
                                                    // Có thể xử lý thêm ở đây, ví dụ hiển thị thông báo lỗi
                                                    return;  // Nếu email không hợp lệ, dừng lại
                                                }

                                                // Tạo mã OTP
                                                String otpCode = generateOtp();

                                                // Gửi email với mã OTP
                                                EmailSender.sendDeleteOTP(email, otpCode);

                                                // Tạo Intent để chuyển đến DeleteOtpActivity và truyền userId và otpCode
                                                Intent intent = new Intent(this, DeleteOtpActivity.class);
                                                intent.putExtra("userId", userId);
                                                intent.putExtra("otpCode", otpCode);
                                                intent.putExtra("email", email);  // Truyền email từ mảng

                                                startActivity(intent);

                                            } else {
                                                Log.d("Firestore", "Get failed with ", task.getException());
                                            }
                                        });
                            } else {
                                Log.e("SettingsActivity", "Cột 'id' không tồn tại trong Cursor");
                            }
                        } else {
                            Log.e("SettingsActivity", "Cursor không có dữ liệu hoặc moveToFirst thất bại");
                        }


                    })
                    .setNegativeButton("Không", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }


    @SuppressLint("Range")
    private void checkLoggedIn() {
        // Lấy dữ liệu người dùng từ SQLite
        Cursor cursor = dbHelper.getUser();

        // Kiểm tra nếu có dữ liệu trong bảng User
        if (cursor != null && cursor.moveToFirst()) {
            // Nếu có, tức là người dùng đã đăng nhập

            itemSettingPassword.setVisibility(View.VISIBLE);
            itemSettingDeleteAccount.setVisibility(View.VISIBLE);
        } else {

            itemSettingPassword.setVisibility(View.GONE);
            itemSettingDeleteAccount.setVisibility(View.GONE);

        }
        cursor.close();
    }

    private String generateOtp() {
        // Tạo mã OTP ngẫu nhiên 4 chữ số
        int otp = 1000 + new Random().nextInt(9000);
        return String.valueOf(otp);
    }
}
