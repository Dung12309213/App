package com.example.applepie.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.Model.NotificationModel;
import com.example.applepie.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SECTION = 0;
    private static final int TYPE_ITEM = 1;

    private final List<Object> itemList;

    public NotificationAdapter(List<Object> itemList) {
        this.itemList = itemList;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position) instanceof String ? TYPE_SECTION : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_section, parent, false);
            return new SectionViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
            return new NotificationViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SectionViewHolder) {
            ((SectionViewHolder) holder).txtSectionTitle.setText((String) itemList.get(position));
        } else if (holder instanceof NotificationViewHolder) {
            NotificationModel model = (NotificationModel) itemList.get(position);
            ((NotificationViewHolder) holder).txtTitle.setText(model.getTitle());
            ((NotificationViewHolder) holder).txtMessage.setText(model.getMessage());
            ((NotificationViewHolder) holder).txtTime.setText(model.getTime());
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView txtSectionTitle;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSectionTitle = itemView.findViewById(R.id.txtSectionTitle);
        }
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtMessage, txtTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtNotiTitle);
            txtMessage = itemView.findViewById(R.id.txtNotiMessage);
            txtTime = itemView.findViewById(R.id.txtNotiTime);
        }
    }
}

