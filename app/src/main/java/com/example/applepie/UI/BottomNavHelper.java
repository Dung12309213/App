package com.example.applepie.UI;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageButton;

import com.example.applepie.MainActivity;
import  com.example.applepie.R;

public class BottomNavHelper {

    // Hàm chính: thiết lập sự kiện cho các nút trong thanh điều hướng dưới (bottom nav)
    public static void setupBottomNav(Activity activity) {
        // Gán ID các nút từ layout
        ImageButton btnHome = activity.findViewById(R.id.imageButton);
        ImageButton btnCategory = activity.findViewById(R.id.imageButton4);
        ImageButton btnBuy = activity.findViewById(R.id.imageButton5);
        ImageButton btnChat = activity.findViewById(R.id.imageButton7);
        ImageButton btnProfile = activity.findViewById(R.id.imageButton6);

        // Lấy tag xác định activity hiện tại (dùng để tô màu chọn)
        Intent currentIntent = activity.getIntent();
        String current = currentIntent != null ? currentIntent.getStringExtra("current") : null;

        // Tô màu cho nút đang được chọn
        highlightSelected(activity, current);

        // Nếu nút HOME tồn tại và đang không ở trang HOME thì mới gán sự kiện chuyển trang
        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                if (!(activity instanceof MainActivity)) {
                    startNewActivity(activity, MainActivity.class, "home");
                }
            });
        }

        // Nếu nút PROFILE tồn tại và đang không ở trang PROFILE thì mới gán sự kiện chuyển trang
        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                if (!(activity instanceof ProfileActivity)) {
                    startNewActivity(activity, ProfileActivity.class, "profile");
                }
            });
        }

        // Nếu nút CHAT tồn tại và đang không ở trang CHAT thì gán sự kiện
        if (btnChat != null) {
            btnChat.setOnClickListener(v -> {
                if (!(activity instanceof ChatBotActivity)) {
                    startNewActivity(activity, ChatBotActivity.class, "chat");
                }
            });
        }
        if (btnBuy != null) {
            btnBuy.setOnClickListener(v -> {
                if (!(activity instanceof CartActivity)) {
                    startNewActivity(activity, CartActivity.class, "buy");
                }
            });
        }

    }

    // Hàm chuyển trang với hiệu ứng fade và truyền thêm thông tin trang hiện tại
    private static void startNewActivity(Activity currentActivity, Class<?> target, String tag) {
        Intent intent = new Intent(currentActivity, target);
        intent.putExtra("current", tag); // truyền "current" để biết đang ở trang nào
        currentActivity.startActivity(intent);

        // Tạo hiệu ứng chuyển fade
        currentActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        currentActivity.finish(); // đóng activity hiện tại để tránh chồng lên nhau
    }

    // Hàm tô màu lại nút đang được chọn (nền selected khác với default)
    private static void highlightSelected(Activity activity, String current) {
        int defaultBg = R.drawable.bg_tab_default;
        int selectedBg = R.drawable.bg_tab_selected;

        // Danh sách tất cả các nút để reset về mặc định
        ImageButton[] buttons = {
                activity.findViewById(R.id.imageButton),   // Home
                activity.findViewById(R.id.imageButton4),  // Category
                activity.findViewById(R.id.imageButton5),  // Buy
                activity.findViewById(R.id.imageButton7),  // Chat
                activity.findViewById(R.id.imageButton6)   // Profile
        };

        // Reset tất cả các nút về background mặc định
        for (ImageButton btn : buttons) {
            if (btn != null) {
                btn.setBackgroundResource(defaultBg);
            }
        }

        // Nếu không có tag current thì không làm gì cả
        if (current == null) return;

        // Tô lại màu nền cho nút tương ứng với trang hiện tại
        switch (current) {
            case "home":
                setSelected(activity, R.id.imageButton, selectedBg);
                break;
            case "category":
                setSelected(activity, R.id.imageButton4, selectedBg);
                break;
            case "buy":
                setSelected(activity, R.id.imageButton5, selectedBg);
                break;
            case "chat":
                setSelected(activity, R.id.imageButton7, selectedBg);
                break;
            case "profile":
                setSelected(activity, R.id.imageButton6, selectedBg);
                break;
        }
    }

    // Hàm hỗ trợ tô nền cho nút được chọn
    private static void setSelected(Activity activity, int buttonId, int backgroundRes) {
        ImageButton btn = activity.findViewById(buttonId);
        if (btn != null) {
            btn.setBackgroundResource(backgroundRes);
        }
    }
}
