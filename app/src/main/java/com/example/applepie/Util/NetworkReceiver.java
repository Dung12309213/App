package com.example.applepie.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = NetworkUtils.isNetworkAvailable(context);

        if (isConnected) {

        } else {
            Toast.makeText(context, "Không có kết nối Internet", Toast.LENGTH_SHORT).show();
        }
    }
}
