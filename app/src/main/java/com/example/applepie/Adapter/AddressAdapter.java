package com.example.applepie.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final List<AddressModel> addressList;
    private int selectedPosition = -1;

    public AddressAdapter(List<AddressModel> addressList) {
        this.addressList = addressList;
        // Nếu có sẵn địa chỉ được chọn, đặt selectedPosition ban đầu
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isSelected()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel model = addressList.get(position);
        holder.txtFullName.setText(model.getFullName());
        holder.txtAddress.setText(model.getAddress());
        holder.txtPhone.setText(model.getPhone());
        holder.radioSelected.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public AddressModel getSelectedAddress() {
        return selectedPosition != -1 ? addressList.get(selectedPosition) : null;
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView txtFullName, txtAddress, txtPhone;
        RadioButton radioSelected;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            radioSelected = itemView.findViewById(R.id.radioSelected);
        }
    }
}
