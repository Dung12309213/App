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

    private AddressModel initialSelectedAddress; // THÊM: Biến để lưu địa chỉ được truyền từ Activity gọi

    // SỬA: Thêm tham số initialSelectedAddress vào constructor
    public AddressAdapter(List<AddressModel> addressList, AddressModel initialSelectedAddress) {
        this.addressList = addressList;
        this.initialSelectedAddress = initialSelectedAddress; // Gán địa chỉ được truyền

        // THÊM: Logic để xác định selectedPosition ban đầu
        if (initialSelectedAddress != null) {
            // Nếu có địa chỉ được truyền, tìm vị trí của nó trong danh sách
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).getAddressid().equals(initialSelectedAddress.getAddressid())) {
                    selectedPosition = i;
                    break;
                }
            }
        } else {
            // Nếu không có địa chỉ nào được truyền, tìm địa chỉ mặc định
            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).isDefaultCheck()) {
                    selectedPosition = i;
                    break;
                }
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
            int oldSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Chỉ gọi notifyDataSetChanged() cho các vị trí bị ảnh hưởng để tối ưu hiệu suất
            if (oldSelectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldSelectedPosition);
            }
            notifyItemChanged(selectedPosition);
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
        if (selectedPosition != -1 && selectedPosition < addressList.size()) {
            return addressList.get(selectedPosition);
        }
        return null;
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
