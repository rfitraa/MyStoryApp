package com.dicoding.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.*
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    fun getAllStories(): LiveData<PagingData<ListStoryItem>>{
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, preference)
            }
        ).liveData
    }

    fun getMapStory(): LiveData<Result<StoriesResponse>> = liveData {
        emit(Result.Loading)
        val token = preference.getData().token
        try {
            val response = apiService.getStory(
                token = "Bearer $token",
                page = 1,
                size = 50,
                location = 1
            )
            if (response.error){
                emit(Result.Error(response.message))
            }else{
                emit(Result.Success(response))
            }
        }catch (e: Exception){
            Log.d("StoryRepository", "MapStory : ${e.message.toString()}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(imageFile: MultipartBody.Part, desc: RequestBody, lat: RequestBody?, lon: RequestBody?): LiveData<Result<UploadStoryResponse>> = liveData {
        emit(Result.Loading)
        val token = preference.getData().token
        try {
            val response = apiService.uploadStory(token = "Bearer $token", imageFile, desc, lat, lon)
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