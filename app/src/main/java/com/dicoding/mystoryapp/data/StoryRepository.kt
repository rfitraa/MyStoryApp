package com.dicoding.mystoryapp.data

import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.response.*
import com.dicoding.mystoryapp.ui.AddStoryActivity
import com.dicoding.mystoryapp.ui.MainActivity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryRepository(private val apiService: ApiService, private val preference: Preference) {
    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            preference: Preference
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, preference)
            }.also { instance = it }
    }

    fun login(email: String, pass: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, pass)
            if (response.error){
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "login : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(name: String, email: String, pass: String): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, pass)
            if (response.error){
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "register : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getAllStories(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        val token = preference.getData().token
        try {
            val response = apiService.getAllStories(token = "Bearer $token")
            if (response.error){
                emit(Result.Error(response.message))
            } else {
                emit(Result.Success(response))
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "listStory : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(imageFile: MultipartBody.Part, desc: RequestBody): LiveData<Result<UploadStoryResponse>> = liveData {
        emit(Result.Loading)
        val token = preference.getData().token
        try {
            val response = apiService.uploadStory(token = "Bearer $token", imageFile, desc)
            if (response.error){
                emit(Result.Error(response.message))
            }else{
                emit(Result.Success(response))
            }
        }catch (e : Exception){
            Log.d("StoryRepository", "uploadStory : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }
}