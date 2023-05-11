package com.dicoding.mystoryapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityRegisterBinding
import com.dicoding.mystoryapp.viewmodel.AuthViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val registerViewModel: AuthViewModel by viewModels { viewModelFactory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)

        playAnimation()

        binding.signIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegister.setOnClickListener {
            registerButtonClicked()
        }
    }

    private fun registerButtonClicked() {
        val name = binding.edRegisterName.text?.trim().toString()
        val email = binding.edRegisterEmail.text?.trim().toString()
        val pass = binding.edRegisterPassword.text?.trim().toString()

        if (binding.edRegisterName.text.isNullOrEmpty() ||binding.edRegisterEmail.text.isNullOrEmpty() || binding.edRegisterPassword.text.isNullOrEmpty()){
            Toast.makeText(this, getString(R.string.input_first), Toast.LENGTH_SHORT).show()
        }
        if (binding.edRegisterName.error != null ||binding.edRegisterEmail.error != null || binding.edRegisterPassword.error != null){
            Toast.makeText(this, getString(R.string.input_correctly), Toast.LENGTH_SHORT).show()
        }else{
            registerViewModel.register(name, email, pass).observe(this){register ->
                if (register != null){
                    when(register){
                        is com.dicoding.mystoryapp.data.Result.Loading -> {
                            showLoading(true)
                        }
                        is com.dicoding.mystoryapp.data.Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this, getString(R.string.register_failed), Toast.LENGTH_SHORT).show()
                        }
                        is com.dicoding.mystoryapp.data.Result.Success -> {
                            showLoading(false)
                            startActivity(Intent(this, LoginActivity::class.java))
                            Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)
        val tagline = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 1f).setDuration(500)
        val username = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val pass = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btn = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(login, tagline, username, email, pass, btn)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}