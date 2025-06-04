package com.duung.applepieapp;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

public class BottomNavHelper {

    public static void setupBottomNav(Activity activity) {
        ImageButton btnHome = activity.findViewById(R.id.imageButton);
        ImageButton btnCategory = activity.findViewById(R.id.imageButton4);
        ImageButton btnBuy = activity.findViewById(R.id.imageButton5);
        ImageButton btnChat = activity.findViewById(R.id.imageButton7);
        ImageButton btnProfile = activity.findViewById(R.id.imageButton6);

        Intent currentIntent = activity.getIntent();
        String current = currentIntent != null ? currentIntent.getStringExtra("current") : null;

        highlightSelected(activity, current);

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                if (!(activity instanceof MainActivity)) {
                    startNewActivity(activity, MainActivity.class, "home");
                }
            });
        }


        if (btnProfile != null) {
            btnProfile.setOnClickListener(v -> {
                if (!(activity instanceof ProfileActivity)) {
                    startNewActivity(activity, ProfileActivity.class, "profile");
                }
            });
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

        ImageButton[] buttons = {
                activity.findViewById(R.id.imageButton),
                activity.findViewById(R.id.imageButton4),
                activity.findViewById(R.id.imageButton5),
                activity.findViewById(R.id.imageButton7),
                activity.findViewById(R.id.imageButton6)
        };

        for (ImageButton btn : buttons) {
            if (btn != null) {
                btn.setBackgroundResource(defaultBg);
            }
        }

        if (current == null) return;

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

    private static void setSelected(Activity activity, int buttonId, int backgroundRes) {
        ImageButton btn = activity.findViewById(buttonId);
        if (btn != null) {
            btn.setBackgroundResource(backgroundRes);
        }
    }
}
