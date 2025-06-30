package com.example.applepie.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.applepie.Connector.DatabaseHelper;
import com.example.applepie.MainActivity;
import com.example.applepie.Model.ChatModel;
import com.example.applepie.R;
import com.example.applepie.UI.ChatBotActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String PROMO_CHANNEL_ID = "promo_notification_channel"; // ID riêng cho kênh khuyến mãi
    private static final String PROMO_CHANNEL_NAME = "Tin nhắn khuyến mãi"; // Tên kênh

    @Override
    public void onNewToken(String token) {
        // Được gọi khi token mới được tạo hoặc cập nhật
        Log.d(TAG, "Refreshed token: " + token);

        // Gửi token mới này lên backend của bạn
        // Đây là TRƯỜNG HỢP RẤT QUAN TRỌNG để đảm bảo backend luôn có token mới nhất.
        // Bạn cần một user_id duy nhất để liên kết token với người dùng.
        // Ví dụ: lấy user_id từ SharedPreferences sau khi đăng nhập, hoặc tạo một ID duy nhất.
        String userId = "your_test_user_id"; // <-- THAY THẾ BẰNG USER ID THỰC TẾ CỦA BẠN!
        sendRegistrationToServer(userId, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Được gọi khi nhận được thông báo FCM
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Ưu tiên xử lý dữ liệu (data payload) từ backend của bạn
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String userId = remoteMessage.getData().get("user_id");
            String messageContent = remoteMessage.getData().get("message");
            String messageType = remoteMessage.getData().get("type"); // "churn_promo"

            // Nếu đây là tin nhắn khuyến mãi từ API FastAPI
            if ("churn_promo".equals(messageType) && messageContent != null) {
                ChatModel promoChat = new ChatModel(messageContent, true);
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                dbHelper.addMessage(promoChat);

                // Gửi tin nhắn này tới ChatBotActivity
                sendPromoMessageToChatbot(userId, messageContent);
                // Hiển thị một thông báo hệ thống ngắn gọn cho người dùng biết có tin nhắn mới
                sendNotification("Tin nhắn khuyến mãi mới từ Cocoon", messageContent, ChatBotActivity.class);
            } else {
                // Xử lý các loại tin nhắn dữ liệu khác nếu có
                // Ví dụ: hiển thị thông báo mặc định nếu không phải churn_promo
                sendNotification(
                        remoteMessage.getData().get("title"), // Hoặc một tiêu đề mặc định
                        messageContent,
                        MainActivity.class // Mở MainActivity hoặc Activity mặc định
                );
            }
        }

        // Kiểm tra xem tin nhắn có chứa thông báo (notification payload) hay không
        // (Thông thường, bạn sẽ gửi data payload từ FastAPI để kiểm soát hoàn toàn nội dung)
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Nếu bạn muốn xử lý notification payload (thường là Firebase console gửi)
            // thì hãy gọi sendNotification() với thông tin từ remoteMessage.getNotification()
            sendNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    MainActivity.class
            );
        }
    }

    /**
     * Gửi FCM token lên backend FastAPI.
     * @param userId User ID của người dùng.
     * @param token FCM token mới.
     */
    private void sendRegistrationToServer(String userId, String token) {
        new Thread(() -> {
            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_id", userId);
                jsonBody.put("fcm_token", token);

                RequestBody body = RequestBody.create(
                        jsonBody.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                // CẬP NHẬT URL NÀY MỖI KHI BẠN KHỞI ĐỘNG LẠI NGROK NẾU DÙNG TÀI KHOẢN MIỄN PHÍ!
                String ngrokUrl = "https://4b29-171-250-165-219.ngrok-free.app"; // <-- CẬP NHẬT LẠI URL NÀY!
                Request request = new Request.Builder()
                        .url(ngrokUrl + "/register_fcm_token")
                        .post(body)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    Log.d(TAG, "FCM token sent successfully to backend for user: " + userId);
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No body";
                    Log.e(TAG, "Failed to send FCM token. Response code: " + response.code() + ", Body: " + errorBody);
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON Error in sendRegistrationToServer: " + e.getMessage());
            } catch (Exception e) {
                Log.e(TAG, "Network error in sendRegistrationToServer: " + e.getMessage());
            }
        }).start();
    }


    /**
     * Gửi tin nhắn từ FCM đến ChatBotActivity thông qua LocalBroadcastManager
     * để hiển thị trong giao diện chat.
     * @param userId User ID liên quan đến tin nhắn.
     * @param promoMessage Nội dung tin nhắn khuyến mãi.
     */
    private void sendPromoMessageToChatbot(String userId, String promoMessage) {
        // Tạo Intent với action riêng biệt để ChatBotActivity lắng nghe
        Intent intent = new Intent("PROMO_MESSAGE_RECEIVED");
        intent.putExtra("user_id", userId);
        intent.putExtra("promo_message", promoMessage);

        // Sử dụng LocalBroadcastManager để gửi broadcast chỉ trong ứng dụng của bạn
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "Sent promo message to ChatBotActivity via LocalBroadcast: " + promoMessage);
    }

    /**
     * Hiển thị thông báo hệ thống cho người dùng.
     * @param title Tiêu đề thông báo.
     * @param messageBody Nội dung thông báo.
     * @param targetActivity Lớp Activity sẽ mở khi người dùng nhấp vào thông báo.
     */
    private void sendNotification(String title, String messageBody, Class<?> targetActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Sử dụng kênh thông báo riêng cho tin khuyến mãi
        String channelId = PROMO_CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.mipmap.ic_logo) // Thay bằng icon của app bạn
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody)) // Hiển thị đầy đủ tin nhắn dài
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Đối với Android 8.0 (API level 26) trở lên, cần tạo Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    PROMO_CHANNEL_NAME, // Tên kênh
                    NotificationManager.IMPORTANCE_DEFAULT); // Hoặc IMPORTANCE_HIGH nếu muốn ưu tiên hơn
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID của thông báo */, notificationBuilder.build());
    }
}
