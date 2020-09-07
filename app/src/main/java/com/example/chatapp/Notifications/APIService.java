package com.example.chatapp.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA6jFzqBM:APA91bFxaEBAAHF3BHphne7Jv_dYpuCEbIPfCUUlJq5hZEHzpRpQfqVtJ6nmvHLYSfWnxTP_AZPD6UftJnxJFI6CG3pi9GiCPcohNC-IUgA-WSgEqtIGTZSl6akvTPT0GPQIUIzUeXw4"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
