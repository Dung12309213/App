package com.example.applepie.UI;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.R;

public class ProductReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar1;
    private EditText editReview1;
    private ImageView btnBack, imgUpload1;
    private Button btnSubmit;

    private float rating1 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_review_screen);

        btnBack = findViewById(R.id.btn_back);
        btnSubmit = findViewById(R.id.btn_submit);

        View reviewBlock1 = findViewById(R.id.include_review_1);
        ratingBar1 = reviewBlock1.findViewById(R.id.ratingBar1);
        editReview1 = reviewBlock1.findViewById(R.id.edit_review1);
        imgUpload1 = reviewBlock1.findViewById(R.id.img_upload1);

        ratingBar1.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            rating1 = rating;
            Toast.makeText(this, "Rated: " + rating + " stars", Toast.LENGTH_SHORT).show();
        });

        imgUpload1.setOnClickListener(v ->
                Toast.makeText(this, "Select image for product", Toast.LENGTH_SHORT).show());

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            String review = editReview1.getText().toString();

            if (review.length() < 50) {
                Toast.makeText(this, "Review must be at least 50 characters.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Submitted\nRating: " + rating1 + "★\nReview: " + review,
                    Toast.LENGTH_LONG).show();
        });
    }
}
