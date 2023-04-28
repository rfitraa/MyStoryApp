package com.dicoding.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.dicoding.mystoryapp.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIME:Long = 3000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        isLogin()
    }

    private fun isLogin() {
        TODO("Not yet implemented")
    }

    private fun splash(intent: Intent){
        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_TIME)
    }
}