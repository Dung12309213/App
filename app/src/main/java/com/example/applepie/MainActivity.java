package com.example.applepie;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.applepie.Adapter.FlashSaleAdapter;
import com.example.applepie.Model.Product;
import com.example.applepie.Model.Variant;
import com.example.applepie.R;
import com.example.applepie.UI.BottomNavHelper;
import com.example.applepie.UI.CartActivity;
import com.example.applepie.UI.CategoryList;
import com.example.applepie.UI.ChatBotActivity;
import com.example.applepie.UI.LoginScreen1;
import com.example.applepie.UI.NotificationActivity;
import com.example.applepie.UI.ProductDetail;
import com.example.applepie.UI.ProfileActivity;
import com.example.applepie.UI.SearchBarHelper;
import com.example.applepie.UI.SearchResultHelper;
import com.example.applepie.Util.UserSessionManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edtSearch;
    private LinearLayout filterPanel;
    private Button btnCloseFilter;
    private HorizontalScrollView scrollHomepage;
    private RecyclerView rvFlashSale;
    private FlashSaleAdapter flashSaleAdapter;
    private List<Product> productList;
    private LinearLayout scrollImageList;
    private FirebaseFirestore db;
    private List<Variant> flashSaleVariantList;
    private TextView tvFlashCountdown;
    private LinearLayout flashSaleLayout;
    private Handler flashSaleHandler = new Handler();
    private Runnable flashSaleRunnable;
    ImageButton btnNotification;
    HorizontalScrollView trietLyScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        addViews();
        addEvents();


        SearchBarHelper.setupSearchBar(this, keyword -> {
            SearchResultHelper.searchAndShow(this, keyword, SearchResultHelper.SearchMode.PRODUCT_AND_CATEGORY);
        });

        BottomNavHelper.setupBottomNav(this);
        BottomNavHelper.highlightSelected(this, "home");

        fetchAndDisplayImages();
        loadFlashSaleVariants();
        startFlashSaleTimer();
    }

    private void addViews() {
        flashSaleVariantList = new ArrayList<>();
        flashSaleAdapter = new FlashSaleAdapter(this, flashSaleVariantList);

        RecyclerView rvFlashSale = findViewById(R.id.rvFlashSale);
        rvFlashSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvFlashSale.setAdapter(flashSaleAdapter);

        flashSaleLayout = findViewById(R.id.flashSaleLayout);
        tvFlashCountdown = findViewById(R.id.tvFlashCountdown);

        TextView badge = findViewById(R.id.notification_badge);

        // Ví dụ giả định có 5 thông báo mới
        int unreadCount = 5;

        if (unreadCount > 0) {
            badge.setText(String.valueOf(unreadCount));
            badge.setVisibility(View.VISIBLE);
        } else {
            badge.setVisibility(View.GONE);
        }
        btnNotification = findViewById(R.id.btn_notification);
        scrollImageList = findViewById(R.id.scroll_image_list);

        trietLyScrollView = findViewById(R.id.trietlyscrollhome);
        trietLyScrollView.post(() -> {
            int maxScroll = trietLyScrollView.getChildAt(0).getWidth() - trietLyScrollView.getWidth();
            if (maxScroll < 0) maxScroll = 0;
            ObjectAnimator animator = ObjectAnimator.ofInt(trietLyScrollView, "scrollX", 0, maxScroll);
            animator.setDuration(10000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.start();
        });

        scrollHomepage = findViewById(R.id.scrollHomepage);
        scrollHomepage.post(() -> {
            ObjectAnimator animator = ObjectAnimator.ofInt(scrollHomepage, "scrollX", 0, 500);
            animator.setDuration(5000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.start();
        });
    }
    private void addEvents() {
        btnNotification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });
    }

    private void loadFlashSaleVariants() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        flashSaleVariantList.clear();

        db.collection("Product")
                .get()
                .addOnSuccessListener(productSnapshots -> {
                    for (QueryDocumentSnapshot productDoc : productSnapshots) {
                        String productId = productDoc.getId();

                        db.collection("Product")
                                .document(productId)
                                .collection("Variant")
                                .get()
                                .addOnSuccessListener(variantSnapshots -> {
                                    for (QueryDocumentSnapshot variantDoc : variantSnapshots) {
                                        Variant v = variantDoc.toObject(Variant.class);
                                        if (v != null && v.getSecondprice() > 0 && v.getSecondprice() < v.getPrice()) {
                                            v.setProductid(productId); // lưu productId để mở ProductDetail nếu cần
                                            v.setId(variantDoc.getId()); // lưu id của variant
                                            flashSaleVariantList.add(v);
                                        }
                                    }

                                    // Cập nhật adapter sau mỗi sản phẩm (có thể dùng notify once nếu cần tối ưu)
                                    flashSaleAdapter.notifyDataSetChanged();
                                });
                    }
                });
    }
    private void fetchAndDisplayImages() {
        db.collection("Scroll-Image") // Assuming your collection is named "Image"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> imageUrls = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Assuming your image URL field in Firestore is named "url"
                            List<String> urlsFromDocument = (List<String>) document.get("imageUrl");

                            if (urlsFromDocument != null && !urlsFromDocument.isEmpty()) {
                                imageUrls.addAll(urlsFromDocument); // Add all URLs from the array to your main list
                            }
                        }
                        addImagesToLayout(imageUrls);
                    }
                });
    }

    private void addImagesToLayout(List<String> imageUrls) {
        // Clear any existing views if you want to refresh the list
        scrollImageList.removeAllViews();

        for (String imageUrl : imageUrls) {
            ImageView imageView = new ImageView(this);

            // Set layout parameters for the ImageView
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.image_width), // Define image_width in dimens.xml (e.g., 100dp)
                    (int) getResources().getDimension(R.dimen.image_height) // Define image_height in dimens.xml (e.g., 100dp)
            );
            layoutParams.setMarginEnd((int) getResources().getDimension(R.dimen.image_margin_end)); // Define image_margin_end (e.g., 12dp)
            imageView.setLayoutParams(layoutParams);

            // Set scale type if needed
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Load image using Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_homepage_mau1) // Optional: Placeholder image
                    .error(R.drawable.ic_launcher_background) // Optional: Error image
                    .into(imageView);

            // Add the ImageView to the LinearLayout
            scrollImageList.addView(imageView);
        }
    }
    private void startFlashSaleTimer() {
        flashSaleRunnable = new Runnable() {
            @Override
            public void run() {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY); // 0–23
                int minute = calendar.get(java.util.Calendar.MINUTE);
                int second = calendar.get(java.util.Calendar.SECOND);

                int cycleHour = hour % 4; // 0–3 trong mỗi chu kỳ 4 giờ

                boolean isFlashVisible = cycleHour < 3; // HIỆN nếu trong 3 tiếng đầu

                // HIỆN hoặc ẨN
                flashSaleLayout.setVisibility(isFlashVisible ? View.VISIBLE : View.GONE);

                // Tính đếm ngược đến khi đổi trạng thái
                int hoursLeft, minutesLeft, secondsLeft;

                if (isFlashVisible) {
                    hoursLeft = 2 - cycleHour;
                } else {
                    hoursLeft = 0;
                }

                minutesLeft = 59 - minute;
                secondsLeft = 59 - second;

                // Định dạng thời gian
                String timeFormatted = String.format("%02d : %02d : %02d", hoursLeft, minutesLeft, secondsLeft);
                tvFlashCountdown.setText(timeFormatted);

                // Lặp lại mỗi giây
                flashSaleHandler.postDelayed(this, 1000);
            }
        };

        flashSaleHandler.post(flashSaleRunnable);
    }
}
