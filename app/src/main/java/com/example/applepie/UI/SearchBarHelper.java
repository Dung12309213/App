package com.example.applepie.UI;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.applepie.R;

public class SearchBarHelper {

    public interface OnSearchTextChangedListener {
        void onSearchTextChanged(String text);
    }

    public static void setupSearchBar(Activity activity, OnSearchTextChangedListener listener) {
        EditText edtSearch = activity.findViewById(R.id.edt_search);
        ImageButton btnNotification = activity.findViewById(R.id.btn_notification);
        TextView badge = activity.findViewById(R.id.notification_badge);

        // Gọi callback khi người dùng gõ vào ô tìm kiếm
        if (edtSearch != null && listener != null) {
            edtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.onSearchTextChanged(s.toString().trim());
                }

                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        // Badge thông báo (giả lập)
        if (badge != null) {
            int unreadCount = 5; // sau này có thể dynamic
            if (unreadCount > 0) {
                badge.setText(String.valueOf(unreadCount));
                badge.setVisibility(TextView.VISIBLE);
            } else {
                badge.setVisibility(TextView.GONE);
            }
        }

        // Click vào notification
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> {
                // Bạn có thể start NotificationActivity tại đây nếu cần
                // activity.startActivity(new Intent(activity, NotificationActivity.class));
            });
        }
    }
}
