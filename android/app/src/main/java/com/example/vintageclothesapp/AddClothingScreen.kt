// AddClothingScreen.kt

package com.example.vintageclothesapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClothingScreen(
    api: ClothingApiService,
    onSaved: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var decade by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val photoUri = remember {
        val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            val stream = context.contentResolver.openInputStream(photoUri)
            capturedBitmap = BitmapFactory.decodeStream(stream)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val stream = context.contentResolver.openInputStream(it)
            capturedBitmap = BitmapFactory.decodeStream(stream)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) cameraLauncher.launch(photoUri)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.add_clothing_title)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (capturedBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = stringResource(R.string.captured_photo),
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    val permission = Manifest.permission.CAMERA
                    if (ContextCompat.checkSelfPermission(context, permission)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraLauncher.launch(photoUri)
                    } else {
                        permissionLauncher.launch(permission)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (capturedBitmap == null)
                        stringResource(R.string.take_photo)
                    else
                        stringResource(R.string.retake_photo)
                )
            }

            Button(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (capturedBitmap == null) "Pick from Gallery" else "Change from Gallery")
            }

            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text(stringResource(R.string.brand_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = size,
                onValueChange = { size = it },
                label = { Text(stringResource(R.string.size_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = decade,
                onValueChange = { decade = it },
                label = { Text(stringResource(R.string.decade_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text(stringResource(R.string.video_url_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                stringResource(R.string.type_label),
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("jacket", "dress", "shirt").forEach { type ->
                    val selected = selectedType == type
                    Button(
                        onClick = { selectedType = type },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(type, fontSize = 11.sp)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("pants", "shoes").forEach { type ->
                    val selected = selectedType == type
                    Button(
                        onClick = { selectedType = type },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(type, fontSize = 11.sp)
                    }
                }
            }

            if (errorMessage != null) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        errorMessage = null
                        try {
                            val imageBase64 = capturedBitmap?.let { bitmap ->
                                val stream = java.io.ByteArrayOutputStream()
                                bitmap.compress(
                                    android.graphics.Bitmap.CompressFormat.JPEG,
                                    80,
                                    stream
                                )
                                android.util.Base64.encodeToString(
                                    stream.toByteArray(),
                                    android.util.Base64.DEFAULT
                                )
                            }
                            android.util.Log.d("SAVE", "Sending: title=$title, type=$selectedType, brand=$brand, size=$size, decade=$decade")
                            api.addClothing(
                                ClothingItemDto(
                                    title = title,
                                    brand = brand,
                                    size = size,
                                    decade = decade,
                                    clothing_type = selectedType,
                                    description = description,
                                    video_url = videoUrl,
                                    image = imageBase64
                                )
                            )
                            onSaved()
                        } catch (e: retrofit2.HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            errorMessage = errorBody
                            android.util.Log.e("SAVE", "Error body: $errorBody")
                        } catch (e: Exception) {
                            errorMessage = e.message
                            android.util.Log.e("SAVE", "Error: ${e.message}")
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && selectedType.isNotBlank() && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text(stringResource(R.string.save))
                }
            }

            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back))
            }
        }
    }
}