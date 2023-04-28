package com.dicoding.mystoryapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.data.Preference
import com.dicoding.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.mystoryapp.response.LoginResult
import com.dicoding.mystoryapp.viewmodel.MainViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private lateinit var preference: Preference
    private lateinit var loginResult: LoginResult
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val preference = Preference(this)
        val data = preference.getData()
        if (data.token.isNullOrEmpty()){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)
    }

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

            R.id.logout -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle(getString(R.string.logout))
                dialog.setMessage(getString(R.string.sure))
                dialog.setPositiveButton("Yes") {_,_ ->
                    clearData()
                    Toast.makeText(this, "Logout Success", Toast.LENGTH_SHORT).show()
                }
                dialog.setNegativeButton("No") {_,_ ->
                    Toast.makeText(this, "Logout Failed", Toast.LENGTH_SHORT).show()
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
}