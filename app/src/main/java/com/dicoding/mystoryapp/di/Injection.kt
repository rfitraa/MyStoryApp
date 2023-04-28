package com.dicoding.mystoryapp.di

import android.content.Context
import com.dicoding.mystoryapp.api.ApiConfig
import com.dicoding.mystoryapp.data.Preference
import com.dicoding.mystoryapp.data.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val preference = Preference(context)
        return StoryRepository.getInstance(apiService, preference)
    }
}