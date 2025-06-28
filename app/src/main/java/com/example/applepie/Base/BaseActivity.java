package com.example.applepie.Base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Util.NetworkUtils;

public class BaseActivity extends AppCompatActivity {

    private final BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, android.content.Intent intent) {
            boolean isConnected = NetworkUtils.isNetworkAvailable(context);
            if (!isConnected) {
                Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Đăng ký receiver khi activity được tạo
        registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy đăng ký khi activity bị huỷ
        unregisterReceiver(networkReceiver);
    }
}
