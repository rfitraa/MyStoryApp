package com.dicoding.mystoryapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dicoding.mystoryapp.data.StoryRepository

class AuthViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun login(email: String, pass: String) = storyRepository.login(email, pass)

    fun register(name: String, email: String, pass: String) = storyRepository.register(name, email, pass)
}