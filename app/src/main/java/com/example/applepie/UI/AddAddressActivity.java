package com.example.applepie.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.applepie.Base.BaseActivity;
import com.example.applepie.Model.AddressModel;
import com.example.applepie.R;
import com.example.applepie.Util.UserSessionManager;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddAddressActivity extends BaseActivity {

    private EditText edtReceiverName, edtPhone, edtDetail;
    Spinner spnCity, spnDistrict, spnWard;
    private CheckBox checkDefault;
    Button btnSave;
    ImageButton btnBack;
    UserSessionManager userSessionManager;
    FirebaseFirestore db;
    private String currentAddressId = null;

    // Danh sách các tên tỉnh/thành phố
    private List<String> provinceNames = new ArrayList<>();
    // Map lưu trữ Document ID của tỉnh theo tên (để dễ dàng lấy subcollection District)
    private Map<String, String> provinceNameToIdMap = new HashMap<>();

    // Map lưu trữ danh sách tên quận/huyện theo tên tỉnh
    private Map<String, List<String>> districtsByProvinceName = new HashMap<>();
    // Map lưu trữ Document ID của quận/huyện theo tên quận/huyện (để dễ dàng lấy mảng Ward)
    private Map<String, String> districtNameToIdMap = new HashMap<>();

    // Map lưu trữ danh sách phường/xã theo tên quận/huyện
    private Map<String, List<String>> wardsByDistrictName = new HashMap<>();

    // Các ArrayAdapter
    private ArrayAdapter<String> cityAdapter;
    private ArrayAdapter<String> districtAdapter;
    private ArrayAdapter<String> wardAdapter;
    private boolean isSettingSelection = false;
    // Dữ liệu địa chỉ cũ bạn muốn set vào Spinner
    private String addressToSetProvince = null;
    private String addressToSetDistrict = null;
    private String addressToSetWard = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        userSessionManager = new UserSessionManager(this);
        db = FirebaseFirestore.getInstance();

        addViews();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("CHANGE_ADDRESS")) {
            // Có dữ liệu được truyền, đây là chế độ chỉnh sửa
            AddressModel addressToEdit = (AddressModel) intent.getSerializableExtra("CHANGE_ADDRESS");
            currentAddressId = addressToEdit.getAddressid();

            edtReceiverName.setText(addressToEdit.getName());
            edtPhone.setText(addressToEdit.getPhone());
            edtDetail.setText(addressToEdit.getStreet());
            checkDefault.setChecked(addressToEdit.isDefaultCheck());

            isSettingSelection = true;
            addressToSetProvince = addressToEdit.getProvince();
            addressToSetDistrict = addressToEdit.getDistrict();
            addressToSetWard = addressToEdit.getWard();

        }
        loadProvinces();
        setupSpinnerListeners();

        addEvents();
    }

    private void addEvents() {
        // Quay lại
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Báo hiệu là không có thay đổi nào được lưu
            finish();
        });

        // Lưu địa chỉ
        btnSave.setOnClickListener(v -> {
            String name = edtReceiverName.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String street = edtDetail.getText().toString().trim(); // Đổi tên biến để dễ hiểu hơn
            String province = "";
            String district = "";
            String ward = "";

            if (spnCity.getSelectedItem() != null) {
                province = spnCity.getSelectedItem().toString().trim();
            }

            if (spnDistrict.getSelectedItem() != null) {
                district = spnDistrict.getSelectedItem().toString().trim();
            }

            if (spnWard.getSelectedItem() != null) {
                ward = spnWard.getSelectedItem().toString().trim();
            }
            boolean isDefault = checkDefault.isChecked();

            if (name.isEmpty() || phone.isEmpty() || street.isEmpty() || province.isEmpty() || ward.isEmpty() || district.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            String cleanedPhone = phone.replaceAll("\\s+", "");

            if (cleanedPhone.length() != 10) {
                Toast.makeText(this, "Số điện thoại phải có đúng 10 chữ số.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cleanedPhone.startsWith("0")) {
                Toast.makeText(this, "Số điện thoại phải bắt đầu bằng số 0.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!cleanedPhone.matches("\\d+")) {
                Toast.makeText(this, "Số điện thoại chỉ được chứa các chữ số.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng AddressModel từ dữ liệu nhập vào
            AddressModel newOrEditedAddress = new AddressModel(
                    currentAddressId != null ? currentAddressId : "", // Giữ ID nếu là sửa, rỗng nếu là thêm mới tạm thời
                    name, phone, street, ward, district, province, isDefault
            );

            // --- ĐIỀU CHỈNH QUAN TRỌNG Ở ĐÂY ---
            if (userSessionManager.isLoggedIn()) {
                // Người dùng đã đăng nhập, tiến hành lưu/cập nhật lên Firestore
                String userId = userSessionManager.getUserId();
                Map<String, Object> addressData = new HashMap<>();
                addressData.put("name", name);
                addressData.put("phone", phone);
                addressData.put("street", street);
                addressData.put("province", province);
                addressData.put("district", district);
                addressData.put("ward", ward);
                addressData.put("defaultCheck", isDefault);

                if (currentAddressId != null) {
                    if (isDefault) {
                        updatePreviousDefaultsAndThenUpdateCurrent(userId, currentAddressId, addressData, street);
                    } else {
                        updateAddressInFirestore(userId, currentAddressId, addressData, street);
                    }
                } else {
                    if (isDefault) {
                        updatePreviousDefaultsAndAddNew(userId, addressData, street);
                    } else {
                        addNewAddressToFirestore(userId, addressData, street);
                    }
                }
            } else {
                // Người dùng CHƯA đăng nhập, KHÔNG lưu lên Firestore ngay
                // Trả về địa chỉ này cho AddressActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_guest_address", newOrEditedAddress);
                setResult(RESULT_OK, resultIntent);
                finish();
                Toast.makeText(this, "Địa chỉ đã được chọn (chưa lưu vào tài khoản).", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // Hàm riêng để thêm địa chỉ mới vào Firestore
    private void addNewAddressToFirestore(String userId, Map<String, Object> addressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .add(addressData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddAddressActivity.this, "Đã lưu địa chỉ mới thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAddressActivity.this, "Có lỗi khi lưu địa chỉ mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddAddressActivity", "Lỗi khi lưu địa chỉ mới: ", e);
                });
    }

    // Hàm để cập nhật địa chỉ hiện có trong Firestore
    private void updateAddressInFirestore(String userId, String addressId, Map<String, Object> addressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address").document(addressId)
                .update(addressData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddAddressActivity.this, "Đã cập nhật địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddAddressActivity.this, "Lỗi khi cập nhật địa chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ: ", e);
                });
    }

    // Hàm để cập nhật các địa chỉ cũ thành không mặc định, sau đó thêm địa chỉ mới
    private void updatePreviousDefaultsAndAddNew(String userId, Map<String, Object> newAddressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Task<Void> updateTask = db.collection("User").document(userId)
                                    .collection("Address")
                                    .document(document.getId())
                                    .update("defaultCheck", false);
                            updateTasks.add(updateTask);
                        }

                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(allUpdatesTask -> {
                                    if (allUpdatesTask.isSuccessful()) {
                                        addNewAddressToFirestore(userId, newAddressData, fullAddress);
                                    } else {
                                        Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ mặc định cũ: " + allUpdatesTask.getException());
                                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi cập nhật địa chỉ mặc định cũ. Vẫn thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                                        addNewAddressToFirestore(userId, newAddressData, fullAddress);
                                    }
                                });
                    } else {
                        Log.e("AddAddressActivity", "Lỗi khi truy vấn địa chỉ mặc định cũ: " + task.getException());
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy địa chỉ mặc định cũ để cập nhật. Vẫn thêm địa chỉ mới.", Toast.LENGTH_SHORT).show();
                        addNewAddressToFirestore(userId, newAddressData, fullAddress);
                    }
                });
    }

    // Hàm để cập nhật các địa chỉ cũ thành không mặc định, sau đó cập nhật địa chỉ hiện tại
    private void updatePreviousDefaultsAndThenUpdateCurrent(String userId, String addressId, Map<String, Object> updatedAddressData, String fullAddress) {
        db.collection("User").document(userId).collection("Address")
                .whereEqualTo("defaultCheck", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> updateTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals(addressId)) {
                                Task<Void> updateTask = db.collection("User").document(userId)
                                        .collection("Address")
                                        .document(document.getId())
                                        .update("defaultCheck", false);
                                updateTasks.add(updateTask);
                            }
                        }

                        Tasks.whenAllComplete(updateTasks)
                                .addOnCompleteListener(allUpdatesTask -> {
                                    if (allUpdatesTask.isSuccessful()) {
                                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress);
                                    } else {
                                        Log.e("AddAddressActivity", "Lỗi khi cập nhật địa chỉ mặc định cũ (khi sửa): " + allUpdatesTask.getException());
                                        Toast.makeText(AddAddressActivity.this, "Có lỗi khi cập nhật địa chỉ mặc định cũ. Vẫn cập nhật địa chỉ này.", Toast.LENGTH_SHORT).show();
                                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress);
                                    }
                                });
                    } else {
                        Log.e("AddAddressActivity", "Lỗi khi truy vấn địa chỉ mặc định cũ (khi sửa): " + task.getException());
                        Toast.makeText(AddAddressActivity.this, "Không tìm thấy địa chỉ mặc định cũ để cập nhật. Vẫn cập nhật địa chỉ này.", Toast.LENGTH_SHORT).show();
                        updateAddressInFirestore(userId, addressId, updatedAddressData, fullAddress);
                    }
                });
    }

    private void addViews() {
        edtReceiverName = findViewById(R.id.edtReceiverName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDetail = findViewById(R.id.edtDetail);
        checkDefault = findViewById(R.id.checkDefault);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        spnCity = findViewById(R.id.spnDetailCity);
        spnDistrict = findViewById(R.id.spnDetailDistrict);
        spnWard = findViewById(R.id.spnDetailWard);

        // Khởi tạo các Adapter rỗng hoặc với item "Chọn..." ban đầu
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCity.setAdapter(cityAdapter);

        districtAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDistrict.setAdapter(districtAdapter);

        wardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnWard.setAdapter(wardAdapter);
    }
    private void setupSpinnerListeners() {
        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Lấy tên tỉnh/thành phố được chọn
                String selectedProvinceName = (String) parent.getItemAtPosition(position);

                // --- QUAN TRỌNG: Xử lý reset Spinner con ---
                // Luôn reset District và Ward khi City thay đổi (trừ khi đang trong quá trình set tự động)
                if (!isSettingSelection) { // Chỉ reset nếu KHÔNG phải là quá trình set tự động
                    districtAdapter.clear();
                    districtAdapter.add("Chọn Quận/Huyện");
                    districtAdapter.notifyDataSetChanged();

                    wardAdapter.clear();
                    wardAdapter.add("Chọn Phường/Xã");
                    wardAdapter.notifyDataSetChanged();

                    // Nếu người dùng chọn thủ công, xóa các biến addressToSet...
                    addressToSetProvince = null;
                    addressToSetDistrict = null;
                    addressToSetWard = null;
                }


                // Kiểm tra nếu là placeholder "Chọn Tỉnh/Thành phố"
                if (selectedProvinceName != null &&
                        (selectedProvinceName.equals("Chọn Tỉnh/Thành phố") || selectedProvinceName.equals("Không có Tỉnh/Thành phố"))) {
                    // Nếu chọn placeholder, không cần tải thêm dữ liệu, chỉ cần reset như trên.
                    // Logic reset đã được đưa lên trên, nên ở đây chỉ cần return.
                    return;
                }

                // Nếu là một tỉnh/thành phố hợp lệ được chọn
                String provinceId = provinceNameToIdMap.get(selectedProvinceName);
                if (provinceId != null) {
                    loadDistricts(provinceId); // Tải danh sách quận/huyện cho tỉnh đã chọn
                } else {
                    Log.w("AddAddressActivity", "Không tìm thấy ID cho tỉnh: " + selectedProvinceName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrictName = (String) parent.getItemAtPosition(position);

                // --- QUAN TRỌNG: LUÔN RESET WARD KHI DISTRICT THAY ĐỔI ---
                // Trừ khi đang trong quá trình set tự động (isSettingSelection là true)
                if (!isSettingSelection) {
                    wardAdapter.clear();
                    wardAdapter.add("Chọn Phường/Xã");
                    wardAdapter.notifyDataSetChanged();

                    // Nếu người dùng chọn thủ công, xóa các biến addressToSet...
                    addressToSetDistrict = null; // Xóa dữ liệu district cũ nếu người dùng thao tác
                    addressToSetWard = null;
                }

                // Kiểm tra nếu là placeholder "Chọn Quận/Huyện" hoặc không có quận/huyện
                if (selectedDistrictName != null &&
                        (selectedDistrictName.equals("Chọn Quận/Huyện") || selectedDistrictName.equals("Không có Quận/Huyện"))) {
                    // Nếu là placeholder, không cần tải thêm dữ liệu. Việc reset đã làm ở trên.
                    return;
                }

                // Nếu là một quận/huyện hợp lệ được chọn
                updateWardSpinner(selectedDistrictName); // Cập nhật danh sách phường/xã cho quận/huyện đã chọn
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    // --- Phương thức tải Tỉnh/Thành phố ---
    private void loadProvinces() {
        db.collection("Province") // Đảm bảo tên collection khớp với Firestore của bạn
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    provinceNames.clear();
                    provinceNameToIdMap.clear();
                    provinceNames.add("Chọn Tỉnh/Thành phố"); // Thêm item hướng dẫn

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String provinceName = document.getString("province"); // Field tên tỉnh
                        if (provinceName != null) {
                            provinceNames.add(provinceName);
                            provinceNameToIdMap.put(provinceName, document.getId()); // Lưu ID document
                        }
                    }
                    cityAdapter.clear();
                    cityAdapter.addAll(provinceNames);
                    cityAdapter.notifyDataSetChanged();

                    // --- SAU KHI TẢI TỈNH XONG, CỐ GẮNG SET CHỌN TỈNH NẾU CÓ DỮ LIỆU CŨ ---
                    if (isSettingSelection && addressToSetProvince != null) {
                        setSpinnerSelection(spnCity, cityAdapter, addressToSetProvince);
                        // (Lưu ý: cờ isSettingSelection sẽ được tắt khi chuỗi phụ thuộc cuối cùng hoàn tất)
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddAddressActivity", "Lỗi tải tỉnh/thành phố: " + e.getMessage());
                    Toast.makeText(this, "Không thể tải danh sách tỉnh/thành phố.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadDistricts(String provinceId) {
        db.collection("Province").document(provinceId).collection("District") // Subcollection District
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> currentDistricts = new ArrayList<>();
                    currentDistricts.add("Chọn Quận/Huyện"); // Thêm item hướng dẫn
                    districtNameToIdMap.clear(); // Xóa map cũ của quận/huyện
                    // Xóa dữ liệu phường/xã cũ để tránh dữ liệu sai lệch
                    wardsByDistrictName.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String districtName = document.getString("district"); // Field tên quận/huyện
                        List<String> wards = (List<String>) document.get("ward"); // Lấy mảng ward
                        if (districtName != null) {
                            currentDistricts.add(districtName);
                            districtNameToIdMap.put(districtName, document.getId());
                            if (wards != null) {
                                List<String> tempWards = new ArrayList<>(wards); // Tạo bản sao
                                tempWards.add(0, "Chọn Phường/Xã"); // Thêm item hướng dẫn
                                wardsByDistrictName.put(districtName, tempWards); // Lưu danh sách phường/xã
                            } else {
                                wardsByDistrictName.put(districtName, new ArrayList<>());
                            }
                        }
                    }

                    districtAdapter.clear();
                    districtAdapter.addAll(currentDistricts);
                    districtAdapter.notifyDataSetChanged();

                    // Reset Ward spinner khi danh sách Quận/Huyện được cập nhật
                    wardAdapter.clear();
                    wardAdapter.add("Chọn Phường/Xã");
                    wardAdapter.notifyDataSetChanged();

                    // --- SAU KHI TẢI QUẬN/HUYỆN XONG, CỐ GẮNG SET CHỌN QUẬN/HUYỆN NẾU CÓ DỮ LIỆU CŨ ---
                    if (isSettingSelection && addressToSetDistrict != null) {
                        setSpinnerSelection(spnDistrict, districtAdapter, addressToSetDistrict);
                        // (Lưu ý: cờ isSettingSelection sẽ được tắt khi chuỗi phụ thuộc cuối cùng hoàn tất)
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AddAddressActivity", "Lỗi tải quận/huyện: " + e.getMessage());
                    Toast.makeText(this, "Không thể tải danh sách quận/huyện.", Toast.LENGTH_SHORT).show();
                });
    }

    // --- Phương thức cập nhật Phường/Xã dựa trên tên quận/huyện đã chọn ---
    private void updateWardSpinner(String selectedDistrictName) {
        List<String> wards = wardsByDistrictName.get(selectedDistrictName);
        if (wards == null) {
            wards = new ArrayList<>();
            wards.add("Không có Phường/Xã");
        }

        wardAdapter.clear();
        wardAdapter.addAll(wards);
        wardAdapter.notifyDataSetChanged();

        if (isSettingSelection && addressToSetWard != null && selectedDistrictName.equals(addressToSetDistrict)) {
            setSpinnerSelection(spnWard, wardAdapter, addressToSetWard);
            isSettingSelection = false;
            addressToSetProvince = null;
            addressToSetDistrict = null;
            addressToSetWard = null;
        }
    }

    // --- Các phương thức khác (ví dụ: lấy giá trị đã chọn) ---
    public String getSelectedProvince() {
        if (spnCity.getSelectedItemPosition() > 0) {
            return (String) spnCity.getSelectedItem();
        }
        return null;
    }

    public String getSelectedDistrict() {
        if (spnDistrict.getSelectedItemPosition() > 0) {
            return (String) spnDistrict.getSelectedItem();
        }
        return null;
    }

    public String getSelectedWard() {
        if (spnWard.getSelectedItemPosition() > 0) {
            return (String) spnWard.getSelectedItem();
        }
        return null;
    }
    private boolean setSpinnerSelection(Spinner spinner, ArrayAdapter<String> adapter, String valueToSet) {
        if (adapter == null || valueToSet == null || valueToSet.isEmpty()) {
            return false;
        }

        int position = 0; // Mặc định là mục đầu tiên (ví dụ: "Chọn...")
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(valueToSet)) {
                position = i;
                break;
            }
        }
        spinner.setSelection(position);
        return position != 0; // Trả về true nếu một giá trị cụ thể được tìm thấy và đặt
    }
}
