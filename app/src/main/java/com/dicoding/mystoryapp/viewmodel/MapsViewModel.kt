package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getMapStory() = storyRepository.getMapStory()
}