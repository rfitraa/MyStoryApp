package com.dicoding.mystoryapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.mystoryapp.response.ListStoryItem

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    companion object{
        const val EXTRA_DETAIL = "extra_detail"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showDetail()
    }

    private fun showDetail() {
        val data = intent?.getParcelableExtra<ListStoryItem>(EXTRA_DETAIL)
        binding.apply {
            tvName.text = data?.name
            tvDesc.text = data?.description
            Glide.with(this@DetailActivity)
                .load(data?.photoUrl)
                .into(ivImage)
        }
    }
}