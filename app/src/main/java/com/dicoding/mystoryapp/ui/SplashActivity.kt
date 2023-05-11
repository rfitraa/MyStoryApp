package com.dicoding.mystoryapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.Preference
import com.dicoding.mystoryapp.response.LoginResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME:Long = 3000
    private lateinit var preference: Preference
    private lateinit var loginResult: LoginResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        preference = Preference(this)
        loginResult = preference.getData()
        lifecycleScope.launch {
            delay(SPLASH_TIME)
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}