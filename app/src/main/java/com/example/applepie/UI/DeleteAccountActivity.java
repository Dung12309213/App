package com.example.applepie.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccountActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private UserSessionManager sessionManager;

    private ImageButton btnBack;
    private EditText edtDeleteAccountPassword; // Đổi tên để phản ánh mục đích nhập mật khẩu
    private Button btnStartDelete; // Đổi tên để phản ánh mục đích bắt đầu xóa
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account); // Sử dụng layout bạn đã cung cấp

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addViews();
        addEvents();
    }

    private void addViews() {
        btnBack = findViewById(R.id.btnBack);
        edtDeleteAccountPassword = findViewById(R.id.edtDeleteAccountPassword); // Đây sẽ là ô nhập mật khẩu
        btnStartDelete = findViewById(R.id.btnStartDelete);
    }

    private void addEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnStartDelete.setOnClickListener(v -> {
            String password = edtDeleteAccountPassword.getText().toString().trim();

            if (password.isEmpty()) {
                Toast.makeText(DeleteAccountActivity.this, "Vui lòng nhập mật khẩu của bạn.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi phương thức xác thực lại và xóa
            reauthenticateAndDeleteUser(password);
        });
    }

    private void reauthenticateAndDeleteUser(String password) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Không có người dùng nào đang đăng nhập. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            // Chuyển hướng về màn hình đăng nhập nếu không có người dùng
            Intent intent = new Intent(DeleteAccountActivity.this, LoginScreen1.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        String email = user.getEmail();
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Không thể lấy thông tin email. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            Log.e("ReauthenticateActivity", "User email is null or empty.");
            return;
        }

        // Tạo thông tin xác thực từ email và mật khẩu
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);

        // Thực hiện xác thực lại
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ReauthenticateActivity", "User re-authenticated successfully.");
                        // Nếu xác thực lại thành công, tiến hành xóa tài khoản
                        deleteUserAccount(user);
                    } else {
                        Log.e("ReauthenticateActivity", "Re-authentication failed: " + task.getException().getMessage());
                        Toast.makeText(DeleteAccountActivity.this, "Xác thực lại thất bại. Sai mật khẩu hoặc tài khoản không hợp lệ.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void deleteUserAccount(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("ReauthenticateActivity", "User account deleted successfully.");
                        Toast.makeText(DeleteAccountActivity.this, "Tài khoản của bạn đã được xóa thành công.", Toast.LENGTH_LONG).show();

                        db.collection("User").document(user.getUid()).delete();

                        // Đăng xuất khỏi Firebase và xóa phiên cục bộ
                        mAuth.signOut();
                        sessionManager.logout(); // Xóa phiên đăng nhập của ứng dụng

                        // Chuyển hướng người dùng về màn hình đăng nhập và xóa sạch lịch sử hoạt động
                        Intent intent = new Intent(DeleteAccountActivity.this, LoginScreen1.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // Kết thúc ReauthenticateActivity
                    } else {
                        Log.e("ReauthenticateActivity", "Error deleting user account: " + task.getException().getMessage());
                        Toast.makeText(DeleteAccountActivity.this, "Lỗi khi xóa tài khoản: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}