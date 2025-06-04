package com.duung.applepieapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private List<FaqItem> originalList;
    private List<FaqItem> filteredList;

    public FaqAdapter(List<FaqItem> list) {
        this.originalList = new ArrayList<>(list);
        this.filteredList = list;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FaqViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_faq, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem item = filteredList.get(position);
        holder.question.setText(item.getQuestion());
        holder.answer.setText(item.getAnswer());
        holder.answer.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // FILTER – HỖ TRỢ KEYWORD + CATEGORY
    public void filter(String keyword, String category) {
        List<FaqItem> temp = new ArrayList<>();

        for (FaqItem item : originalList) {
            boolean matchesKeyword = keyword == null || keyword.isEmpty()
                    || item.getQuestion().toLowerCase().contains(keyword.toLowerCase());

            boolean matchesCategory = category.equals("All")
                    || item.getCategory().equalsIgnoreCase(category);

            if (matchesKeyword && matchesCategory) {
                temp.add(item);
            }
        }

        filteredList = temp;
        notifyDataSetChanged();
    }

    // VIEW HOLDER
    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.faq_question);
            answer = itemView.findViewById(R.id.faq_answer);
        }
    }
}
