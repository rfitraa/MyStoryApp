package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.mystoryapp.data.StoryRepository
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.response.StoriesResponse
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository): ViewModel() {
    val getAllStories: LiveData<PagingData<ListStoryItem>> = storyRepository.getAllStories().cachedIn(viewModelScope)
}