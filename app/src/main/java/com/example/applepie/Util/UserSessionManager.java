package com.example.applepie.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private static final String PREF_NAME = "UserSession";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private UserSessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }
    public String getLoggedInUserId() {
        return preferences.getString("id", ""); // Lấy giá trị của khóa "id" từ SharedPreferences
    }
    // Lưu thông tin người dùng
    public void saveUser(String userId, String userName, String userEmail) {
        editor.putString("id", userId);
        editor.putString("name", userName);
        editor.putString("email", userEmail);
        editor.apply();
    }

    public String getUserEmail() {
        return preferences.getString("email", "");
    }
    // Lấy tên người dùng
    public String getUserName() {
        return preferences.getString("name", "Guest");  // Nếu chưa đăng nhập, trả về "Guest"
    }

    // Lấy ID người dùng
    public String getUserId() {
        return preferences.getString("id", "");  // Trả về ID nếu đã đăng nhập
    }

    // Xóa thông tin người dùng (Đăng xuất)
    public void logout() {
        editor.clear();
        editor.apply();
    }
    public boolean isLoggedIn() {
        // Người dùng được coi là đã đăng nhập nếu có userId được lưu
        return !preferences.getString("id", "").isEmpty();
    }

    // Instance duy nhất của UserSessionManager (Singleton Pattern)
    private static UserSessionManager instance;

    // Phương thức public để lấy instance của UserSessionManager
    // Đây là phương thức mà MainActivity đang cố gắng gọi
    public static synchronized UserSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserSessionManager(context.getApplicationContext()); // Sử dụng application context để tránh rò rỉ bộ nhớ
        }
        return instance;
    }

}
