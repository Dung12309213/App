//
package com.example.applepie.UI;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.Delivery;
import com.example.applepie.Model.OrderItem;
import com.example.applepie.Model.OrderModel;
import com.example.applepie.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Order_Detail extends BaseActivity {

    // Header and main info
    private ImageView backButton;
    private TextView orderStatusTextView;
    private TextView paymentMethodTextView, productSecondPriceTextView;
    private TextView deliveryNameTextView;
    private TextView deliveryAddressDetailTextView;
    private TextView orderCodeTextView;
    private TextView copyCodeButton;
    private TextView deliveryTimeTextView;
    private TextView totalAmountTextView;
    private Button cancelOrderButton;

    private static final String TAG = "OrderDetailActivity";
    private FirebaseFirestore db;
    private String orderId;

    private LinearLayout productsLayoutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        db = FirebaseFirestore.getInstance();
        
        addViews();
        addEvents();

        if (getIntent() != null && getIntent().hasExtra("orderId")) {
            orderId = getIntent().getStringExtra("orderId");
            if (orderId != null) {
                Log.d(TAG, "Đã nhận Order ID: " + orderId);
                fetchOrderDetail(orderId); // Bắt đầu tải dữ liệu chi tiết đơn hàng
            } else {
                Toast.makeText(this, "Không tìm thấy ID đơn hàng.", Toast.LENGTH_SHORT).show();
                finish(); // Đóng activity nếu không có ID
            }
        } else {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng trong Intent.", Toast.LENGTH_SHORT).show();
            finish(); // Đóng activity nếu không có ID
        }
    }

    private void addEvents() {
        backButton.setOnClickListener(v -> finish());

        // Nút "Sao chép mã"
        copyCodeButton.setOnClickListener(v -> {
            // Lấy mã đơn hàng từ TextView, bỏ đi phần "Mã đơn hàng: "
            String codeToCopy = orderCodeTextView.getText().toString()
                    .replace(getString(R.string.order_code_label) + " ", "");
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Order Code", codeToCopy);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Đã sao chép mã đơn hàng: " + codeToCopy, Toast.LENGTH_SHORT).show();
            }
        });

        // Nút "HỦY ĐƠN HÀNG"
        cancelOrderButton.setOnClickListener(v -> {
            Toast.makeText(this, "Bạn đã nhấn HỦY ĐƠN HÀNG!", Toast.LENGTH_SHORT).show();
            // Không có logic hủy đơn hàng thực sự ở đây, chỉ là thông báo Toast
        });
    }

    private void addViews() {
        backButton = findViewById(R.id.backButton);
        orderStatusTextView = findViewById(R.id.orderStatusTextView);
        paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        deliveryNameTextView = findViewById(R.id.txtUserName);
        deliveryAddressDetailTextView = findViewById(R.id.txtUserAddress);

        orderCodeTextView = findViewById(R.id.orderCodeTextView);
        deliveryTimeTextView = findViewById(R.id.deliveryTimeTextView);
        totalAmountTextView = findViewById(R.id.totalAmountTextView);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        copyCodeButton = findViewById(R.id.copyCodeButton);

        productsLayoutContainer = findViewById(R.id.products_list_container);
        productSecondPriceTextView = findViewById(R.id.productSecondPriceTextView);
    }
    private void fetchOrderDetail(String id) {
        db.collection("Order").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            OrderModel order = document.toObject(OrderModel.class);
                            if (order != null) {
                                order.setId(document.getId());
                                displayOrderSummary(order);
                                // Fetch delivery information
                                fetchDeliveryInfo(order.getId());
                                fetchOrderItems(order.getId()); // Keep fetching order items
                            } else {
                                Log.e(TAG, "Không thể chuyển đổi tài liệu đơn hàng thành OrderModel.");
                                Toast.makeText(Order_Detail.this, "Lỗi: Không thể tải chi tiết đơn hàng.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.d(TAG, "Không tìm thấy tài liệu đơn hàng với ID: " + id);
                            Toast.makeText(Order_Detail.this, "Không tìm thấy đơn hàng.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi lấy tài liệu đơn hàng: ", task.getException());
                        Toast.makeText(Order_Detail.this, "Lỗi khi tải dữ liệu đơn hàng.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchDeliveryInfo(String orderId) {
        db.collection("Order").document(orderId).collection("Delivery") // Replace "Deliveries" with your actual collection name
                .limit(1) // Assuming one delivery per order
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Delivery delivery = document.toObject(Delivery.class);
                            displayDeliveryInfo(delivery);
                            return; // Exit after finding the first delivery
                        }
                        // If no delivery is found, handle the case (e.g., show "N/A")
                        displayNoDeliveryInfo();
                        Log.d(TAG, "Không tìm thấy thông tin giao hàng cho đơn hàng: " + orderId);
                    } else {
                        Log.e(TAG, "Lỗi khi lấy thông tin giao hàng: ", task.getException());
                        displayNoDeliveryInfo(); // Handle error gracefully
                    }
                });
    }
    private void displayDeliveryInfo(Delivery delivery) {
        if (delivery != null) {
            deliveryNameTextView.setText(delivery.getDeliveryName() != null ? delivery.getDeliveryName() : "N/A");
            deliveryAddressDetailTextView.setText(
                    (delivery.getAddressStreet() != null ? delivery.getAddressStreet() + ", " : "") +
                            (delivery.getAddressWard() != null ? delivery.getAddressWard() + ", " : "") +
                            (delivery.getAddressDistrict() != null ? delivery.getAddressDistrict() + ", " : "") +
                            (delivery.getAddressProvince() != null ? delivery.getAddressProvince() : "N/A")
                    +"\n"+delivery.getDeliveryPhone()
            );
        } else {
            displayNoDeliveryInfo();
        }
    }
    private void displayNoDeliveryInfo() {
        deliveryNameTextView.setText("N/A");
        deliveryAddressDetailTextView.setText("N/A");
    }
    // Phương thức hiển thị thông tin tổng quan của đơn hàng
    private void displayOrderSummary(OrderModel order) {
        // Hiển thị trạng thái đơn hàng
        String status = order.getStatus();
        orderStatusTextView.setText(mapOrderStatusToDisplayString(status));
        orderStatusTextView.setBackgroundResource(getBackgroundResourceForStatus(status));

        // Hiển thị phương thức thanh toán
        paymentMethodTextView.setText(order.getPaymentMethod());


        // Hiển thị mã đơn hàng
        orderCodeTextView.setText(order.getId());

        // Hiển thị thời gian giao hàng (giả định purchasedate là thời gian tạo đơn)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        if (order.getPurchasedate() != null) {
            deliveryTimeTextView.setText(sdf.format(order.getPurchasedate()));
        } else {
            deliveryTimeTextView.setText("Không rõ thời gian");
        }

        // Hiển thị tổng số tiền
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        totalAmountTextView.setText(numberFormat.format(order.getTotal()) + " đ");

        // Cập nhật trạng thái nút hủy đơn hàng dựa trên trạng thái đơn hàng
        if (order.getStatus().equals("completed") || order.getStatus().equals("canceled")) {
            cancelOrderButton.setVisibility(View.GONE); // Ẩn nút hủy nếu đã hoàn thành hoặc hủy
        } else {
            cancelOrderButton.setVisibility(View.VISIBLE); // Hiển thị nút hủy
        }
    }

    // Phương thức tải các mặt hàng trong đơn hàng
    private void fetchOrderItems(String orderId) {
        db.collection("Order").document(orderId).collection("Item")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productsLayoutContainer.removeAllViews();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OrderItem orderItem = document.toObject(OrderItem.class);
                            if (orderItem != null) {
                                fetchProductDetails(orderItem);
                            }
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi lấy các mặt hàng đơn hàng: ", task.getException());
                        Toast.makeText(Order_Detail.this, "Lỗi khi tải danh sách sản phẩm.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Phương thức tải chi tiết từng sản phẩm
    private void fetchProductDetails(OrderItem orderItem) {
        db.collection("Product").document(orderItem.getProductid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String productName = document.getString("name");
                            String productVolume = document.getString("volume");
                            List<String> imageUrls = (List<String>) document.get("imageUrl");
                            int productPrice = orderItem.getPrice();
                            int productSecondPrice = orderItem.getSecondPrice();

                            addProductItemToLayout(productName, productVolume, productPrice, productSecondPrice, orderItem.getQuantity(),
                                    imageUrls != null && !imageUrls.isEmpty() ? imageUrls.get(0) : null);

                        } else {
                            Log.w(TAG, "Không tìm thấy sản phẩm với ID: " + orderItem.getProductid());
                            addProductItemToLayout("Sản phẩm không tồn tại", "", 0,0, orderItem.getQuantity(), null);
                        }
                    } else {
                        Log.e(TAG, "Lỗi khi lấy chi tiết sản phẩm: ", task.getException());
                        addProductItemToLayout("Lỗi tải sản phẩm", "", 0,0, orderItem.getQuantity(), null);
                    }
                });
    }

    // Phương thức thêm một item sản phẩm vào layout một cách động
    private void addProductItemToLayout(String name, String volume, int price,int secondprice, int quantity, String imageUrl) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View productItemView = inflater.inflate(R.layout.item_order_detail_product, productsLayoutContainer, false);

        ImageView productImageView = productItemView.findViewById(R.id.productImageView);
        TextView productNameTextView = productItemView.findViewById(R.id.productNameTextView);
        TextView productVolumeTextView = productItemView.findViewById(R.id.productVolumeTextView);
        TextView productPriceTextView = productItemView.findViewById(R.id.productPriceTextView);
        TextView productQuantityTextView = productItemView.findViewById(R.id.productQuantityTextView);

        productNameTextView.setText(name);
        productVolumeTextView.setText(volume);
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        productPriceTextView.setText(numberFormat.format(price) + " đ");
        productQuantityTextView.setText("x" + quantity);

        if (secondprice == 0) {
            productPriceTextView.setText(String.format("%,d đ", price));
        } else {
            productPriceTextView.setText(String.format("%,d đ", secondprice));
            productSecondPriceTextView.setText(String.format("%,d đ", price));
            productSecondPriceTextView.setVisibility(View.VISIBLE);
            productSecondPriceTextView.setPaintFlags(productSecondPriceTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }


        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    //.placeholder(R.drawable.placeholder_image)
                    //.error(R.drawable.placeholder_image)
                    .into(productImageView);
        } else {
            //productImageView.setImageResource(R.drawable.placeholder_image);
        }

        productsLayoutContainer.addView(productItemView);

        // Thêm một đường kẻ phân cách sau mỗi sản phẩm (trừ cái cuối cùng)
        // Nếu bạn muốn đường kẻ phân cách luôn có, bỏ kiểm tra này
        // View divider = new View(this);
        // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        //         LinearLayout.LayoutParams.MATCH_PARENT,
        //         (int) (0.5 * getResources().getDisplayMetrics().density)
        // );
        // params.setMargins((int) (96 * getResources().getDisplayMetrics().density), 0, 0, 0);
        // divider.setLayoutParams(params);
        // divider.setBackgroundColor(0xFFE0E0E0);
        // productsLayoutContainer.addView(divider);
    }

    // Phương thức trợ giúp để ánh xạ trạng thái sang chuỗi hiển thị
    private String mapOrderStatusToDisplayString(String status) {
        switch (status) {
            case "completed":
                return getString(R.string.order_status_completed);
            case "canceled":
                return getString(R.string.order_status_canceled);
            case "pending":
            case "Đang xử lý":
                return getString(R.string.order_status_packaging);
            case "shipped":
                return getString(R.string.order_status_shipped);
            default:
                return getString(R.string.order_status_unknown) + ": " + status;
        }
    }

    // Phương thức trợ giúp để chọn màu nền cho trạng thái
    private int getBackgroundResourceForStatus(String status) {
        /*switch (status) {
            case "completed":
                return R.drawable.bg_status_completed;
            case "canceled":
                return R.drawable.bg_status_canceled;
            case "pending":
            case "Đang xử lý":
                return R.drawable.bg_user_message;
            case "shipped":
                return R.drawable.bg_status_shipped;
            default:
                return R.drawable.bg_user_message;
        }*/
        return R.drawable.bg_user_message;
    }
}