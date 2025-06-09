package com.example.applepie.UI;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import  com.example.applepie.R;

public class PolicyActivity extends AppCompatActivity {

    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        // Gán nút back
        btnBack = findViewById(R.id.imageButton2);
        btnBack.setOnClickListener(v -> finish());
    }
}
