package com.as4.mrvalet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by appleuser on 5/15/17.
 */

public interface ApiInterface {

    @Multipart
    @POST("UploadToServer.php")
    Call<JsonObject> uploadImage2(@Part MultipartBody.Part file, @Part("name") RequestBody name);

}
