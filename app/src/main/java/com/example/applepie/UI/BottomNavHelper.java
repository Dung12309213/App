package com.example.applepie.UI;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.applepie.MainActivity;
import com.example.applepie.R;
import com.example.applepie.UI.CategoryList;
import com.example.applepie.UI.ChatBotActivity;
import com.example.applepie.UI.CartActivity;
import com.example.applepie.UI.ProfileActivity;
import com.example.applepie.Util.UserSessionManager;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity) {
        // Gán ID mới theo layout
        ImageButton btnHome     = activity.findViewById(R.id.btn_home);
        ImageButton btnCategory = activity.findViewById(R.id.btn_category);
        ImageButton btnBuy      = activity.findViewById(R.id.btn_buy);
        ImageButton btnChat     = activity.findViewById(R.id.btn_chat);
        ImageButton btnProfile  = activity.findViewById(R.id.btn_profile);

        // Lấy tag xác định trang hiện tại (dùng để highlight)
        Intent currentIntent = activity.getIntent();
        String current = currentIntent != null ? currentIntent.getStringExtra("current") : null;

        // Tô màu cho nút đang được chọn
        highlightSelected(activity, current);

        // Gán sự kiện điều hướng nếu chưa ở đúng trang
        if (btnHome != null && !(activity instanceof MainActivity)) {
            btnHome.setOnClickListener(v -> startNewActivity(activity, MainActivity.class, "home"));
        }

        if (btnCategory != null && !(activity instanceof CategoryList)) {
            btnCategory.setOnClickListener(v -> startNewActivity(activity, CategoryList.class, "category"));
        }

        if (btnBuy != null && !(activity instanceof CartActivity)) {
            btnBuy.setOnClickListener(v -> {
                UserSessionManager userSessionManager = new UserSessionManager(activity);
                String userId = userSessionManager.getUserId();

                if (userId.isEmpty()) {
                    startNewActivity(activity, LoginScreen1.class, "login");
                    Toast.makeText(activity, "Vui lòng đăng nhập để tiếp tục", Toast.LENGTH_SHORT).show();
                } else {
                    // Nếu đã đăng nhập, chuyển đến CartActivity
                    startNewActivity(activity, CartActivity.class, "cart");
                }
            });
        }

        if (btnChat != null && !(activity instanceof ChatBotActivity)) {
            btnChat.setOnClickListener(v -> startNewActivity(activity, ChatBotActivity.class, "chat"));
        }

        if (btnProfile != null && !(activity instanceof ProfileActivity)) {
            btnProfile.setOnClickListener(v -> startNewActivity(activity, ProfileActivity.class, "profile"));
        }
    }

    private static void startNewActivity(Activity currentActivity, Class<?> target, String tag) {
        Intent intent = new Intent(currentActivity, target);
        intent.putExtra("current", tag);
        currentActivity.startActivity(intent);
        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        currentActivity.finish();
    }

    private static void highlightSelected(Activity activity, String current) {
        int defaultBg = R.drawable.bg_tab_default;
        int selectedBg = R.drawable.bg_tab_selected;

        // Reset tất cả về mặc định
        int[] buttonIds = {
                R.id.btn_home,
                R.id.btn_category,
                R.id.btn_buy,
                R.id.btn_chat,
                R.id.btn_profile
        };

        for (int id : buttonIds) {
            ImageButton btn = activity.findViewById(id);
            if (btn != null) {
                btn.setBackgroundResource(defaultBg);
            }
        }

        if (current == null) return;

        switch (current) {
            case "home":
                setSelected(activity, R.id.btn_home, selectedBg);
                break;
            case "category":
                setSelected(activity, R.id.btn_category, selectedBg);
                break;
            case "buy":
                setSelected(activity, R.id.btn_buy, selectedBg);
                break;
            case "chat":
                setSelected(activity, R.id.btn_chat, selectedBg);
                break;
            case "profile":
                setSelected(activity, R.id.btn_profile, selectedBg);
                break;
        }
    }

    private static void setSelected(Activity activity, int buttonId, int backgroundRes) {
        ImageButton btn = activity.findViewById(buttonId);
        if (btn != null) {
            btn.setBackgroundResource(backgroundRes);
        }
    }
}
