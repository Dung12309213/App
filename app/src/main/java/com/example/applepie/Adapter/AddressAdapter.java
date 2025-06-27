package com.example.applepie.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.UI.AddAddressActivity;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final List<AddressModel> addressList;
    private int selectedPosition = -1;

    public AddressAdapter(List<AddressModel> addressList) {
        this.addressList = addressList;
        // Nếu có sẵn địa chỉ được chọn, đặt selectedPosition ban đầu
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefaultCheck()) {
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
        holder.txtFullName.setText(model.getName());
        holder.txtAddress.setText(
                (model.getStreet() != null && !model.getStreet().isEmpty() ? model.getStreet() + " " : "") +
                        (model.getWard() != null && !model.getWard().isEmpty() ? model.getWard() + " " : "") +
                        (model.getDistrict() != null && !model.getDistrict().isEmpty() ? model.getDistrict() + " " : "") +
                        (model.getProvince() != null && !model.getProvince().isEmpty() ? model.getProvince() : "")
        );
        holder.txtPhone.setText(model.getPhone());

        holder.radioSelected.setChecked(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
        holder.txtChangeAddressDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddAddressActivity.class);
                intent.putExtra("CHANGE_ADDRESS", model);
                v.getContext().startActivity(intent);
            }
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
        TextView txtFullName, txtAddress, txtPhone, txtChangeAddressDetail;
        RadioButton radioSelected;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            radioSelected = itemView.findViewById(R.id.radioSelected);
            txtChangeAddressDetail = itemView.findViewById(R.id.txtChangeAddressDetail);
        }
    }
}
