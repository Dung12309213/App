package com.example.applepie.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.OrderItem;
import com.example.applepie.Model.Review;
import com.example.applepie.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductReviewActivity extends BaseActivity {

    private FirebaseFirestore db;
    private LinearLayout reviewBlockContainer;
    private Button btnSubmit;
    private ImageView btnBack;

    private String receivedOrderId;
    private String userId = "";
    private static boolean isMediaManagerInitialized = false;

    private final List<ReviewBlockHolder> reviewBlocks = new ArrayList<>();
    private static final int REQUEST_IMAGE_PICK = 101;
    private ImageView currentUploadImageView;
    private ReviewBlockHolder currentReviewHolder;

    // Track pending uploads
    private AtomicInteger pendingUploads = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_review_screen);
        if (!isMediaManagerInitialized) {
            Map config = new HashMap();
            config.put("cloud_name", "dngjnodf5");
            config.put("api_key", "997483168222317");
            config.put("api_secret", "5JZU0m-E4PZU9K5uySOSgBNm_-Q");
            config.put("secure", true);

            MediaManager.init(this, config);
            isMediaManagerInitialized = true;
        }

        db = FirebaseFirestore.getInstance();
        reviewBlockContainer = findViewById(R.id.reviewBlockContainer);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.btn_back);

        // Initially disable submit button
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang tải dữ liệu...");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("orderId")) {
            receivedOrderId = intent.getStringExtra("orderId");
            fetchOrderProductsForReview(receivedOrderId);
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng!", Toast.LENGTH_LONG).show();
            finish();
        }

        btnBack.setOnClickListener(v -> finish());

        // This listener will now only execute when the button is enabled
        btnSubmit.setOnClickListener(v -> handleSubmit());
    }

    private void fetchOrderProductsForReview(String orderId) {
        db.collection("Order").document(orderId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        userId = documentSnapshot.getString("userid");
                    }
                });
        db.collection("Order").document(orderId).collection("Item")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderItem item = document.toObject(OrderItem.class);
                            item.setId(document.getId());
                            if (item != null) {
                                createReviewBlock(item);
                            }
                        }
                        // Enable submit button after all review blocks are created (initially, no images are uploaded)
                        // This assumes reviews can be submitted without images. If images are mandatory,
                        // you'd keep it disabled until all *expected* images are uploaded.
                        // For now, let's enable it once blocks are loaded.
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Gửi đánh giá");
                    } else {
                        Log.e("Review", "Lỗi khi lấy item: ", task.getException());
                        Toast.makeText(this, "Lỗi khi tải sản phẩm đánh giá.", Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(false); // Keep disabled on error
                        btnSubmit.setText("Lỗi tải");
                    }
                });
    }

    private void createReviewBlock(OrderItem item) {
        View reviewBlock = getLayoutInflater().inflate(R.layout.review_block, reviewBlockContainer, false);

        ImageView imgProduct = reviewBlock.findViewById(R.id.imgProductReview);
        TextView tvName = reviewBlock.findViewById(R.id.tvProductReviewName);
        TextView tvVariant = reviewBlock.findViewById(R.id.tvProductReviewVariant);
        ImageView imgUpload = reviewBlock.findViewById(R.id.img_upload1);
        RatingBar ratingBar = reviewBlock.findViewById(R.id.ratingBar1);
        EditText editReview = reviewBlock.findViewById(R.id.edit_review1);

        String productId = item.getProductid();
        String variantId = item.getVariantid();

        db.collection("Product").document(productId)
                .get()
                .addOnSuccessListener(productDoc -> {
                    if (productDoc.exists()) {
                        String name = productDoc.getString("name");
                        tvName.setText(name != null ? name : "Tên sản phẩm");

                        List<String> imageUrls = (List<String>) productDoc.get("imageUrl");
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            Glide.with(this)
                                    .load(imageUrls.get(0))
                                    .into(imgProduct);
                        }
                    } else {
                        tvName.setText("Tên sản phẩm");
                    }

                    db.collection("Product").document(productId)
                            .collection("Variant").document(variantId)
                            .get()
                            .addOnSuccessListener(variantDoc -> {
                                if (variantDoc.exists()) {
                                    String variant = variantDoc.getString("variant");
                                    tvVariant.setText(variant != null ? variant : "Phiên bản");
                                } else {
                                    tvVariant.setText("Phiên bản");
                                }
                            })
                            .addOnFailureListener(e -> {
                                tvVariant.setText("Phiên bản");
                                Log.e("Variant", "Lỗi lấy variant: " + e.getMessage());
                            });
                });
        ReviewBlockHolder holder = new ReviewBlockHolder(productId, item.getId(), ratingBar, editReview, imgUpload);

        imgUpload.setOnClickListener(v -> {
            currentUploadImageView = imgUpload;
            currentReviewHolder = holder;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_PICK);
        });

        reviewBlocks.add(holder);
        reviewBlockContainer.addView(reviewBlock);
    }

    private void handleSubmit() {
        if (pendingUploads.get() > 0) {
            Toast.makeText(this, "Vui lòng chờ ảnh được tải lên hoàn tất trước khi gửi!", Toast.LENGTH_LONG).show();
            return;
        }

        // Use a list to hold all Firestore tasks
        List<Task<Void>> firestoreTasks = new ArrayList<>();

        for (ReviewBlockHolder holder : reviewBlocks) {
            int score = (int) holder.ratingBar.getRating();
            String comment = holder.editText.getText().toString();
            List<String> imageUrls = holder.getImageUrls(); // This should now contain URLs if uploads completed
            Date now = new Date();

            Review review = new Review();
            review.setOrderId(receivedOrderId);
            review.setUserId(userId);
            review.setScore(score);
            review.setComment(comment);
            review.setImageUrl(imageUrls);
            review.setTimestamp(now);

            String reviewId = db.collection("Review").document().getId();
            review.setId(reviewId);

            Task<Void> task = db.collection("Order")
                    .document(receivedOrderId)
                    .collection("Item")
                    .document(holder.itemId)
                    .collection("Review")
                    .document(reviewId)
                    .set(review)
                    .addOnSuccessListener(unused -> Log.d("Review", "Đã lưu vào Item " + holder.itemId))
                    .addOnFailureListener(e -> Log.e("Review", "Lỗi lưu review vào Item: " + e.getMessage()));
            firestoreTasks.add(task);
        }

        // Wait for all Firestore tasks to complete
        Tasks.whenAllComplete(firestoreTasks)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Gửi đánh giá thành công!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Có lỗi xảy ra khi gửi đánh giá. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
                        Log.e("ReviewSubmit", "Error submitting all reviews: ", task.getException());
                    }
                });
    }

    static class ReviewBlockHolder {
        String productId;
        String itemId;
        RatingBar ratingBar;
        EditText editText;
        ImageView uploadImage;
        List<String> imageUrls = new ArrayList<>();

        public List<String> getImageUrls() {
            return imageUrls;
        }

        public void setImageUrls(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        public ReviewBlockHolder(String productId, String itemId, RatingBar ratingBar, EditText editText, ImageView uploadImage) {
            this.productId = productId;
            this.itemId = itemId;
            this.ratingBar = ratingBar;
            this.editText = editText;
            this.uploadImage = uploadImage;
        }
    }

    private void uploadImageToCloudinary(Uri imageUri, OnImageUploadCallback callback) {
        // Increment the counter when an upload starts
        pendingUploads.incrementAndGet();
        updateSubmitButtonState(); // Update button state

        MediaManager.get().upload(imageUri)
                .option("folder", "ApplePieImage/Review")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = (String) resultData.get("secure_url");
                        callback.onUploadSuccess(url);
                        // Decrement the counter when an upload succeeds
                        pendingUploads.decrementAndGet();
                        updateSubmitButtonState(); // Update button state
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onUploadError(error.getDescription());
                        // Decrement the counter when an upload fails
                        pendingUploads.decrementAndGet();
                        updateSubmitButtonState(); // Update button state
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }

    public interface OnImageUploadCallback {
        void onUploadSuccess(String url);
        void onUploadError(String errorMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null && currentReviewHolder != null) {
                currentUploadImageView.setImageURI(selectedImageUri); // Display temporary image
                uploadImageToCloudinary(selectedImageUri, new OnImageUploadCallback() {
                    @Override
                    public void onUploadSuccess(String url) {
                        Log.d("Cloudinary", "Image URL uploaded: " + url);
                        currentReviewHolder.imageUrls.add(url);
                        Log.d("ImageUpload", "Đã thêm URL vào holder: " + url);
                        Log.d("ImageUpload", "Tổng imageUrls của holder: " + currentReviewHolder.imageUrls);
                        Toast.makeText(ProductReviewActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUploadError(String errorMessage) {
                        Toast.makeText(ProductReviewActivity.this, "Lỗi upload: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // New method to update the submit button state
    private void updateSubmitButtonState() {
        runOnUiThread(() -> {
            if (pendingUploads.get() > 0) {
                btnSubmit.setEnabled(false);
                btnSubmit.setText("Đang tải ảnh...");
            } else {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi đánh giá");
            }
        });
    }
}