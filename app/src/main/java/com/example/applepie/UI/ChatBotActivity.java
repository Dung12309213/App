package com.example.applepie.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import  com.example.applepie.MainActivity;
import  com.example.applepie.R;
import  com.example.applepie.Adapter.ChatAdapter;
import  com.example.applepie.Model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatBotActivity extends AppCompatActivity {

    private RecyclerView recyclerChat;
    private EditText edtMessage;
    private ImageButton btnSend, btnBack, btnAdd;
    private ChatAdapter chatAdapter;
    private List<ChatModel> chatList;

    // ActivityResultLauncher để mở thư viện ảnh
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Ánh xạ view
        recyclerChat = findViewById(R.id.recyclerChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd); // Nút mở thư viện

        // Gán sự kiện quay lại về MainActivity
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ChatBotActivity.this, MainActivity.class);
            intent.putExtra("current", "home");
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Gán sự kiện mở thư viện ảnh
        btnAdd.setOnClickListener(v -> openImagePicker());

        // Khởi tạo bộ chọn ảnh
        setupImagePicker();

        // Thiết lập RecyclerView
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerChat.setLayoutManager(new LinearLayoutManager(this));
        recyclerChat.setAdapter(chatAdapter);

        // Sự kiện gửi tin nhắn
        btnSend.setOnClickListener(v -> {
            String msg = edtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                chatList.add(new ChatModel(msg, false)); // Tin nhắn từ user
                chatList.add(new ChatModel("Leaf AI is typing...", true)); // Giả lập phản hồi
                chatAdapter.notifyItemRangeInserted(chatList.size() - 2, 2);
                recyclerChat.scrollToPosition(chatList.size() - 1);
                edtMessage.setText("");
            }
        });
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
                            // Gửi ảnh dưới dạng đường dẫn URI
                            chatList.add(new ChatModel(selectedImageUri.toString(), false, true));
                            chatAdapter.notifyItemInserted(chatList.size() - 1);
                            recyclerChat.scrollToPosition(chatList.size() - 1);
                        }
                    }
                }
        );
    }
}
