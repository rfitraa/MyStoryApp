package com.dicoding.mystoryapp.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.mystoryapp.utils.createCustomeTempFile
import com.dicoding.mystoryapp.utils.reduceFileImage
import com.dicoding.mystoryapp.utils.uriToFile
import com.dicoding.mystoryapp.viewmodel.AddStoryViewModel
import com.dicoding.mystoryapp.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var viewModelFactory: ViewModelFactory
    private val addStoryViewModel: AddStoryViewModel by viewModels { viewModelFactory }
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var myLocation: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                myFile.let { file ->
                    getFile = file
                    binding.ivPreview.setImageURI(uri)
                }
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == RESULT_OK){
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModelFactory = ViewModelFactory.getInstance(binding.root.context)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnCamera.setOnClickListener {startCamera()}
        binding.btnGallery.setOnClickListener {startGallery()}
        binding.btnUpload.setOnClickListener { uploadImage() }
        binding.switchLocation.setOnCheckedChangeListener{ _, isChecked ->
            if (isChecked){
                getMyLastLocation()
            }else{
                myLocation = null
            }
        }
    }

    private fun getMyLastLocation() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    myLocation = location
                } else {
                    binding.switchLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadImage() {
        if (getFile != null){
            if (binding.edAddDescription.text.toString().isEmpty()){
                binding.edAddDescription.error = getString(R.string.desc_required)
            }else{
                val file = reduceFileImage(getFile as File)
                val desc = binding.edAddDescription.text
                    .toString()
                    .trim()
                    .toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                val lat = myLocation?.latitude?.toString()?.toRequestBody("text/plain".toMediaType())
                val lon = myLocation?.longitude?.toString()?.toRequestBody("text/plain".toMediaType())
                addStoryViewModel.uploadStory(imageMultipart, desc, lat, lon).observe(this){
                    when(it){
                        is com.dicoding.mystoryapp.data.Result.Loading -> {
                            showLoading(true)
                        }
                        is com.dicoding.mystoryapp.data.Result.Error->{
                            showLoading(false)
                            Toast.makeText(this, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                        }
                        is com.dicoding.mystoryapp.data.Result.Success->{
                            showLoading(false)
                            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this, getString(R.string.input_image), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_picture))
        launcherIntentGallery.launch(chooser)
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomeTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity, "com.dicoding.mystoryapp.ui", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (!allPermissionsGranted()){
                Toast.makeText(this, getString(R.string.permission_not_allowed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}