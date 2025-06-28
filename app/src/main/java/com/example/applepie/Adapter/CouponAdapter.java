package com.example.applepie.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.Voucher;
import com.example.applepie.R;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Voucher> voucherList;
    private Context context;

    public CouponAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        holder.tvVoucherCode.setText(voucher.getCode());
        holder.tvVoucherCondition.setText(voucher.getDescription());

        // Format amount: nếu là số nguyên, hiển thị không có thập phân
        String formattedAmount = (voucher.getAmount() % 1 == 0)
                ? String.format("%.0f", voucher.getAmount())
                : String.format("%.2f", voucher.getAmount());

        holder.tvVoucherDiscount.setText(formattedAmount + "đ");

        // Xử lý nút sao chép
        holder.btnCopyCode.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Voucher Code", voucher.getCode());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Đã sao chép mã: " + voucher.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvVoucherCode, tvVoucherCondition, tvVoucherDiscount, btnCopyCode;

        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvVoucherCondition = itemView.findViewById(R.id.tvVoucherCondition);
            tvVoucherDiscount = itemView.findViewById(R.id.tvVoucherDiscount);
            btnCopyCode = itemView.findViewById(R.id.btnCopyCode);
        }
    }
}
