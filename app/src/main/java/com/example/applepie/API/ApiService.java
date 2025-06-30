package com.example.applepie.API;

import com.example.applepie.Model.RegisterTokenRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface ApiService {
    @POST("register_fcm_token") // FastAPI endpoint của bạn
    Call<Void> registerToken(@Body RegisterTokenRequest request);

    // Có thể thêm các API khác ở đây nếu cần, ví dụ:
    // @GET("chat")
    // Call<ChatResponse> getChatMessage(@Query("user_id") String userId);
}
