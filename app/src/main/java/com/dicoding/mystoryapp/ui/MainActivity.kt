package com.dicoding.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.adapter.LoadingStateAdapter
import com.dicoding.mystoryapp.adapter.StoryAdapter
import com.dicoding.mystoryapp.data.Preference
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.response.ListStoryItem
import com.dicoding.mystoryapp.viewmodel.MainViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)

        val preference = Preference(this)
        val token = preference.getData().token
        if (token.isNullOrEmpty()){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        showList()
    }

    private fun showList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager

        val storyAdapter = StoryAdapter()
        binding.rvStory.adapter = storyAdapter.withLoadStateHeaderAndFooter(
            header = LoadingStateAdapter{storyAdapter.retry()},
            footer = LoadingStateAdapter{storyAdapter.retry()}
        )

        mainViewModel.getAllStories.observe(this) {
            showLoading(false)
            storyAdapter.submitData(lifecycle, it)
        }

        storyAdapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListStoryItem) {
                Intent(this@MainActivity, DetailActivity::class.java).also { detail ->
                    detail.putExtra(DetailActivity.EXTRA_DETAIL, data)
                    startActivity(detail)
                }
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }

            R.id.logout -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(getString(R.string.logout))
                dialog.setMessage(getString(R.string.sure))
                dialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                    clearData()
                    Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                }
                dialog.setNegativeButton(getString(R.string.no)) { _, _ ->
                    Toast.makeText(this, getString(R.string.logout_failed), Toast.LENGTH_SHORT).show()
                }
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun clearData() {
        val preference = Preference(this)
        preference.clearData()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }
}