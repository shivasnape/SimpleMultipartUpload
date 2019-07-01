package com.snape.shivichu.simpleretrofitupload.util;


import android.app.Activity;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by ShivaSnape on 31/7/17.
 */

public class NetworkTask {

    Activity activity;
    Retrofit retrofit;
    ApiCalls apiCalls;

    /*Build Retrofit Here*/
    public NetworkTask(Activity activity) {

        this.activity = activity;

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        apiCalls = retrofit.create(ApiCalls.class);
    }


    /*public method for upload*/
    public Call<String> uploadImageToServer(String url, MultipartBody.Part part) {
        return apiCalls.uploadImage(url, part);
    }


    public Call<String> uploadVideo(String url, MultipartBody.Part fbody) {
        return apiCalls.uploadVideo(url, fbody);
    }

    /*Retrofit Interface*/
    public interface ApiCalls {

        @Multipart
        @POST("{path}")
        Call<String> uploadVideo(@Path(value = "path") String path, @Part MultipartBody.Part body);

        @Multipart
        @POST("{path}")
            // Call<String> uploadImageToServer(@Path(value = "path") String path, @Part("image\"; filename=\"image.png\"") RequestBody body, String filename);
        Call<String> uploadImage(@Path(value = "path") String path, @Part MultipartBody.Part file);
    }

}
