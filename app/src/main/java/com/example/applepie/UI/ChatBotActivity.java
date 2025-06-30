package com.example.applepie.UI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Connector.DatabaseHelper;
import  com.example.applepie.MainActivity;
import  com.example.applepie.R;
import  com.example.applepie.Adapter.ChatAdapter;
import  com.example.applepie.Model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends BaseActivity {

    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageButton btnSend, btnBack, btnAdd;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Biến để lưu trữ BroadcastReceiver
    private BroadcastReceiver promoMessageReceiver;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ view
        recyclerChat = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatBotActivity.this, MainActivity.class);
            intent.putExtra("current", "home");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        btnAdd.setOnClickListener(v -> openImagePicker());

        setupImagePicker();

        // Lấy lịch sử chat từ database khi khởi tạo Activity
        chatList = new ArrayList<>(dbHelper.getAllMessages()); // Load all messages from DB
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);
        recyclerChat.scrollToPosition(chatList.size() - 1);

        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                // Tạo ChatModel cho tin nhắn người dùng
                ChatModel userMessage = new ChatModel(msg, false); // isFromBot = false
                // LƯU VÀO DATABASE
                dbHelper.addMessage(userMessage);

                chatList.add(userMessage); // Thêm vào danh sách hiển thị
                chatList.add(new ChatModel("Leaf AI is typing...", true)); // Giả lập phản hồi
                chatAdapter.notifyItemRangeInserted(chatList.size() - 2, 2);
                recyclerChat.scrollToPosition(chatList.size() - 1);
                edtMessage.setText("");

                // TODO: Gửi tin nhắn user đến FastAPI để xử lý (nếu có tính năng chat thường)
                // Hiện tại chỉ là dummy, bạn sẽ cần thêm logic gọi API ở đây.
            }
        });

        // Đăng ký BroadcastReceiver để nhận tin nhắn khuyến mãi
        setupPromoMessageReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Đăng ký receiver khi Activity hoạt động trở lại
        LocalBroadcastManager.getInstance(this).registerReceiver(promoMessageReceiver, new IntentFilter("PROMO_MESSAGE_RECEIVED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hủy đăng ký receiver khi Activity tạm dừng
        LocalBroadcastManager.getInstance(this).unregisterReceiver(promoMessageReceiver);
    }

    private void openImagePicker() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(pickIntent);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            chatList.add(new ChatModel(selectedImageUri.toString(), false, true));
                            chatAdapter.notifyItemInserted(chatList.size() - 1);
                            recyclerChat.scrollToPosition(chatList.size() - 1);
                        }
                    }
                }
        );
    }

    // Phương thức để thêm tin nhắn từ AI vào chatbot
    private void addAiMessageToChat(String message) {
        // Xóa "Leaf AI is typing..." nếu có trong UI
        if (!chatList.isEmpty() && chatList.get(chatList.size() - 1).getMessage().equals("Leaf AI is typing...")) {
            chatList.remove(chatList.size() - 1);
            chatAdapter.notifyItemRemoved(chatList.size());
            // Xóa cả trong DB nếu bạn đã lưu "typing..." vào DB
            dbHelper.deleteTypingMessage();
        }

        // Tạo ChatModel cho tin nhắn AI thực sự
        ChatModel aiMessage = new ChatModel(message, true); // isFromBot = true
        // LƯU VÀO DATABASE
        dbHelper.addMessage(aiMessage);

        chatList.add(aiMessage); // Thêm vào danh sách hiển thị
        chatAdapter.notifyItemInserted(chatList.size() - 1);
        recyclerChat.scrollToPosition(chatList.size() - 1);
    }

    private void setupPromoMessageReceiver() {
        if (promoMessageReceiver == null) { // Đảm bảo chỉ khởi tạo một lần
            promoMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if ("PROMO_MESSAGE_RECEIVED".equals(intent.getAction())) {
                        String promoMessage = intent.getStringExtra("promo_message");
                        if (promoMessage != null) {
                            Log.d("ChatBotActivity", "Received promo message for chat: " + promoMessage);
                            Toast.makeText(context, "Tin nhắn khuyến mãi: " + promoMessage, Toast.LENGTH_SHORT).show(); // Giữ nguyên toast để debug
                            // Thêm tin nhắn khuyến mãi vào danh sách chat (và tự động lưu vào DB trong addAiMessageToChat)
                            addAiMessageToChat(promoMessage);
                        }
                    }
                }
            };
        }
    }
}
