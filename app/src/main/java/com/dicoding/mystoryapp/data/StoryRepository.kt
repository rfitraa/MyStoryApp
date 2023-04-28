package com.dicoding.mystoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.mystoryapp.api.ApiService
import com.dicoding.mystoryapp.response.LoginResponse
import com.dicoding.mystoryapp.response.RegisterResponse

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
}