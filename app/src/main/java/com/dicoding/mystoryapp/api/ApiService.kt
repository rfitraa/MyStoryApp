package com.dicoding.mystoryapp.api

import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.response.RegisterResponse
import com.dicoding.mystoryapp.response.StoriesResponse
import com.dicoding.mystoryapp.response.UploadStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("desc") desc: RequestBody
    ): UploadStoryResponse
}