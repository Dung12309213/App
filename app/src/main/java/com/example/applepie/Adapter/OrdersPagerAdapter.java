package com.example.applepie.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.applepie.UI.OrdersCanceledFragment;
import com.example.applepie.UI.OrdersCompletedFragment;
import com.example.applepie.UI.OrdersOngoingFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new OrdersOngoingFragment();
            case 1:
                return new OrdersCompletedFragment();
            case 2:
                return new OrdersCanceledFragment();
            default:
                return new OrdersOngoingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // 3 tab: Active, Completed, Cancelled
    }
}
