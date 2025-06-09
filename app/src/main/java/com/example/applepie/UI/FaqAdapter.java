package com.example.applepie.UI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách câu hỏi thường gặp (FAQ)
 */
public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    // Danh sách ban đầu (full)
    private List<FaqItem> originalList;
    // Danh sách hiện tại sau khi lọc (hiển thị)
    private List<FaqItem> filteredList;

    // Constructor – nhận danh sách dữ liệu ban đầu
    public FaqAdapter(List<FaqItem> list) {
        this.originalList = new ArrayList<>(list); // sao chép để giữ nguyên
        this.filteredList = list;
    }

    // Tạo ViewHolder – ánh xạ layout item_faq.xml vào từng dòng
    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FaqViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq, parent, false));
    }

    // Gán dữ liệu cho từng dòng (câu hỏi – trả lời)
    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem item = filteredList.get(position);

        // Hiển thị câu hỏi
        holder.question.setText(item.getQuestion());
        // Hiển thị hoặc ẩn phần trả lời tùy vào trạng thái mở rộng
        holder.answer.setText(item.getAnswer());
        holder.answer.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);

        // Bắt sự kiện click để toggle mở/đóng phần trả lời
        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded()); // đảo trạng thái
            notifyItemChanged(position); // cập nhật lại dòng
        });
    }

    // Trả về số lượng item đang hiển thị
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    /**
     * Hàm filter – lọc dữ liệu theo từ khóa (keyword) và danh mục (category)
     */
    public void filter(String keyword, String category) {
        List<FaqItem> temp = new ArrayList<>();

        for (FaqItem item : originalList) {
            // Kiểm tra keyword (nếu có)
            boolean matchesKeyword = keyword == null || keyword.isEmpty()
                    || item.getQuestion().toLowerCase().contains(keyword.toLowerCase());

            // Kiểm tra category (nếu không phải "All" thì so sánh)
            boolean matchesCategory = category.equals("All")
                    || item.getCategory().equalsIgnoreCase(category);

            // Nếu cả hai điều kiện đều đúng thì thêm vào danh sách lọc
            if (matchesKeyword && matchesCategory) {
                temp.add(item);
            }
        }

        // Cập nhật danh sách hiển thị
        filteredList = temp;
        notifyDataSetChanged(); // thông báo dữ liệu thay đổi
    }

    /**
     * ViewHolder: đại diện cho từng dòng trong danh sách
     */
    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView question, answer;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.faq_question); // ID câu hỏi
            answer = itemView.findViewById(R.id.faq_answer);     // ID câu trả lời
        }
    }
}
