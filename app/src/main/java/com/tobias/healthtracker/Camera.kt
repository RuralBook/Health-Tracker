package com.tobias.healthtracker

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.tobias.healthtracker.ui.theme.HealthTrackerTheme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Camera : AppCompatActivity() {
// activity
private lateinit var outputDirectory: File
private lateinit var cameraExecutor: ExecutorService

private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

private lateinit var photoUri: Uri
private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    if (isGranted) {
        Log.i("kilo", "Permission granted")
        shouldShowCamera.value = true
    } else {
        Log.i("kilo", "Permission denied")
    }
}

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
        HealthTrackerTheme() {


            if (shouldShowCamera.value) {
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("kilo", "View error:", it) }
                )
            }

           if (shouldShowPhoto.value) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    requestCameraPermission()

    outputDirectory = getOutputDirectory()
    cameraExecutor = Executors.newSingleThreadExecutor()
}

private fun requestCameraPermission() {
    when {
        ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED -> {
            Log.i("kilo", "Permission previously granted")
            shouldShowCamera.value = true
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            android.Manifest.permission.CAMERA
        ) -> Log.i("kilo", "Show camera permissions dialog")

        else -> requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
    }
}

private fun handleImageCapture(uri: Uri) {
    Log.i("kilo", "Image captured: $uri")
    shouldShowCamera.value = false

    photoUri = uri
    shouldShowPhoto.value = true
}

private fun getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }

    return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
}

override fun onDestroy() {
    super.onDestroy()
    cameraExecutor.shutdown()
}
}