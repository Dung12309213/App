package com.example.applepie.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.R;
import com.example.applepie.Model.ChatModel;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_USER = 0;
    private static final int TYPE_BOT = 1;

    private Context context;
    private List<ChatModel> chatList;

    public ChatAdapter(Context context, List<ChatModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).isFromBot() ? TYPE_BOT : TYPE_USER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel model = chatList.get(position);
        if (holder instanceof BotViewHolder) {
            BotViewHolder botHolder = (BotViewHolder) holder;
            botHolder.txtBotMessage.setText(model.getMessage());
            botHolder.txtBotTime.setText(model.getFormattedTime());
        } else if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.txtUserMessage.setText(model.getMessage());
            userHolder.txtUserTime.setText(model.getFormattedTime());
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView txtBotMessage;
        TextView txtBotTime;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBotMessage = itemView.findViewById(R.id.txtBotMessage);
            txtBotTime = itemView.findViewById(R.id.txtBotTime);
        }
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserMessage;
        TextView txtUserTime;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserMessage = itemView.findViewById(R.id.txtUserMessage);
            txtUserTime = itemView.findViewById(R.id.txtUserTime);
        }
    }
}