package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.data.StoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun uploadStory(imageFile: MultipartBody.Part, desc: RequestBody) = storyRepository.uploadStory(imageFile, desc)
}