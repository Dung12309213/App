package com.example.applepie.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserSessionManager {

    private static final String PREF_NAME = "UserSession";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public UserSessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    // Lưu thông tin người dùng
    public void saveUser(String userId, String userName) {
        editor.putString("id", userId);
        editor.putString("name", userName);
        editor.apply();  // Dùng apply để lưu bất đồng bộ
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

}
