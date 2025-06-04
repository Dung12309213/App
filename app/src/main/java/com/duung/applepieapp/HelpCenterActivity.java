package com.duung.applepieapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.button.MaterialButton;

import android.text.TextWatcher;
import android.text.Editable;
import android.content.Context;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HelpCenterActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private LinearLayout filterContainer, faqLayout, contactLayout;
    private FaqAdapter faqAdapter;
    private List<FaqItem> faqList;
    private List<String> filters = Arrays.asList("All", "Services", "General", "Account");

    private String currentFilter = "All";
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        tabLayout = findViewById(R.id.tabLayout);
        recyclerView = findViewById(R.id.faq_recycler);
        searchInput = findViewById(R.id.search_input);
        filterContainer = findViewById(R.id.filter_container);
        faqLayout = findViewById(R.id.faq_layout);
        contactLayout = findViewById(R.id.contact_layout);

        setupTabs();
        setupFilters();
        setupRecycler();
        setupSearch();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("FAQ"));
        tabLayout.addTab(tabLayout.newTab().setText("Contact Us"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    faqLayout.setVisibility(View.VISIBLE);
                    contactLayout.setVisibility(View.GONE);
                } else {
                    faqLayout.setVisibility(View.GONE);
                    contactLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFilters() {
        Context context = this;
        for (String filter : filters) {
            MaterialButton btn = new MaterialButton(context, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            btn.setText(filter);
            btn.setStrokeColorResource(R.color.brown);
            btn.setTextColor(ContextCompat.getColor(context, R.color.brown));
            btn.setCornerRadius(40);
            btn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            ((LinearLayout.LayoutParams) btn.getLayoutParams()).setMargins(8, 0, 8, 0);

            btn.setOnClickListener(v -> {
                currentFilter = filter;
                filterFaqList();
            });

            filterContainer.addView(btn);
        }
    }

    private void setupRecycler() {
        faqList = getMockFaqs();
        faqAdapter = new FaqAdapter(faqList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(faqAdapter);
    }

    private void setupSearch() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentKeyword = s.toString();
                filterFaqList();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterFaqList() {
        faqAdapter.filter(currentKeyword, currentFilter);
    }

    private List<FaqItem> getMockFaqs() {
        List<FaqItem> list = new ArrayList<>();
        list.add(new FaqItem("Can I track my order's delivery status?", "Yes, you can track...", "Services"));
        list.add(new FaqItem("Is there a return policy?", "You can return items within...", "General"));
        list.add(new FaqItem("How do I change my account password?", "Go to account settings...", "Account"));
        list.add(new FaqItem("Can I save my favorite items for later?", "Yes, just tap the heart icon...", "General"));
        return list;
    }
}
