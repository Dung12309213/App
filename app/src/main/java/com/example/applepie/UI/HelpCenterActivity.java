package com.example.applepie.UI;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applepie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {

    // Views
    private ImageButton btnBack;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private LinearLayout filterContainer, faqLayout, contactLayout;

    // Adapter + Data
    private FaqAdapter faqAdapter;
    private List<FaqItem> faqList;

    // Filters
    private List<String> filters = Arrays.asList("All", "Services", "General", "Account");
    private String currentFilter = "All";
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        // Bind Views
        btnBack = findViewById(R.id.imageButton2);
        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.faq_recycler);
        searchInput = findViewById(R.id.search_input);
        filterContainer = findViewById(R.id.filter_container);
        faqLayout = findViewById(R.id.faq_layout);
        contactLayout = findViewById(R.id.contact_layout);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Setup UI
        setupTabs();
        setupFilters();
        setupRecycler();
        setupSearch();
        setupContactUs(); // 👈 Gọi hàm tạo nội dung Contact Us
    }

    // Tabs: FAQ & Contact Us
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("FAQ"));
        tabLayout.addTab(tabLayout.newTab().setText("Contact Us"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                boolean isFaq = tab.getPosition() == 0;
                faqLayout.setVisibility(isFaq ? View.VISIBLE : View.GONE);
                contactLayout.setVisibility(isFaq ? View.GONE : View.VISIBLE);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // FAQ Filter buttons
    private void setupFilters() {
        Context context = this;
        for (String filter : filters) {
            MaterialButton btn = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(filter);
            btn.setStrokeColorResource(R.color.brown);
            btn.setTextColor(ContextCompat.getColor(context, R.color.brown));
            btn.setCornerRadius(40);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                currentFilter = filter;
                filterFaqList();
            });

            filterContainer.addView(btn);
        }
    }

    // FAQ list
    private void setupRecycler() {
        faqList = getMockFaqs();
        faqAdapter = new FaqAdapter(faqList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(faqAdapter);
    }

    // Search input
    private void setupSearch() {
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                filterFaqList();
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
    }

    // Filter FAQ based on keyword and category
    private void filterFaqList() {
        faqAdapter.filter(currentKeyword, currentFilter);
    }

    // Sample FAQs
    private List<FaqItem> getMockFaqs() {
        List<FaqItem> list = new ArrayList<>();
        list.add(new FaqItem("Can I track my order's delivery status?", "Yes, you can track...", "Services"));
        list.add(new FaqItem("Is there a return policy?", "You can return items within...", "General"));
        list.add(new FaqItem("How do I change my account password?", "Go to account settings...", "Account"));
        list.add(new FaqItem("Can I save my favorite items for later?", "Yes, just tap the heart icon...", "General"));
        return list;
    }

    // Contact cards setup
    private void setupContactUs() {
        setupContactCard(R.id.contact_customer_service, R.drawable.ic_customer_service, "Customer Service", "Hotline: 1900 1001");
        setupContactCard(R.id.contact_website, R.drawable.ic_website, "Website", "https://applepie.vn");
        setupContactCard(R.id.contact_fb, R.drawable.ic_fb, "Facebook", "facebook.com/applepie");
        setupContactCard(R.id.contact_instagram, R.drawable.ic_instagram, "Instagram", "@applepie.official");
    }

    // Gán icon, title, detail và xử lý ẩn/hiện khi bấm vào từng contact card
    private void setupContactCard(int layoutId, int iconRes, String title, String detailText) {
        View card = findViewById(layoutId);
        if (card == null) return;

        ImageView icon = card.findViewById(R.id.icon);
        TextView titleView = card.findViewById(R.id.title);
        TextView detail = card.findViewById(R.id.detail);
        ImageView arrow = card.findViewById(R.id.arrow);

        icon.setImageResource(iconRes);
        titleView.setText(title);
        detail.setText(detailText);

        card.findViewById(R.id.header).setOnClickListener(v -> {
            boolean isVisible = detail.getVisibility() == View.VISIBLE;
            detail.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            arrow.setRotation(isVisible ? 0 : 90);
        });
    }
}
