package com.example.applepie.UI;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Adapter.CouponAdapter;
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.Voucher;
import com.example.applepie.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Coupon extends BaseActivity {

    private RecyclerView recyclerViewVouchers;
    private CouponAdapter couponAdapter;
    private List<Voucher> voucherList;
    private ImageView backButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        recyclerViewVouchers = findViewById(R.id.recyclerViewVouchers);
        backButton = findViewById(R.id.backButton);
        recyclerViewVouchers.setLayoutManager(new LinearLayoutManager(this));

        voucherList = new ArrayList<>();
        couponAdapter = new CouponAdapter(voucherList, this);
        recyclerViewVouchers.setAdapter(couponAdapter);

        db = FirebaseFirestore.getInstance();
        loadVouchersFromFirestore();

        backButton.setOnClickListener(v -> finish());
    }

    private void loadVouchersFromFirestore() {
        db.collection("Discount")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    voucherList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Voucher voucher = doc.toObject(Voucher.class);
                        voucher.setId(doc.getId());
                        voucherList.add(voucher);
                    }
                    couponAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Optional: Log lỗi hoặc Toast
                });
    }
}
