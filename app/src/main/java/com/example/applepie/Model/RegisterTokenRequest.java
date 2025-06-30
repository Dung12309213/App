package com.example.applepie.Model;

import com.google.gson.annotations.SerializedName;

public class RegisterTokenRequest {
    @SerializedName("user_id")
    private String userId;
    @SerializedName("fcm_token")
    private String fcmToken;

    public RegisterTokenRequest(String userId, String fcmToken) {
        this.userId = userId;
        this.fcmToken = fcmToken;
    }

    // Gson sẽ tự động serialize các trường này, không cần getter/setter nếu chỉ gửi đi.
    // Tuy nhiên, nếu bạn muốn dùng chúng ở nơi khác, có thể thêm:
    public String getUserId() {
        return userId;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}
