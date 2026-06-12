package com.example.vintageclothesapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    item: ClothingItem,
    api: ClothingApiService,
    onBack: () -> Unit,
    onDeleted: () -> Unit = onBack
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentItem by remember { mutableStateOf(item) }
    var userRating by remember { mutableStateOf(0) }
    var ratingSubmitted by remember { mutableStateOf(false) }
    var isRating by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete") },
            text = { Text("Are you sure you want to delete this item?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        isDeleting = true
                        scope.launch {
                            try {
                                api.deleteClothing(currentItem.id)
                                onDeleted()
                            } catch (e: Exception) {
                                android.util.Log.e("DELETE", "Error: ${e.message}")
                                isDeleting = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(currentItem.title) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            currentItem.imageBase64?.let { base64 ->
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap?.let {
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = currentItem.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(text = stringResource(R.string.detail_type, currentItem.type))
            Text(text = stringResource(R.string.detail_decade, currentItem.decade))
            Text(text = stringResource(R.string.detail_brand, currentItem.brand))
            Text(text = stringResource(R.string.detail_size, currentItem.size))
            Text(text = stringResource(R.string.detail_description, currentItem.description))

            Text(
                text = "Average Rating: ${"%.1f".format(currentItem.averageRating)}/5",
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = stringResource(R.string.rate_this_item),
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    Button(
                        onClick = {
                            if (!isRating) {
                                userRating = star
                                isRating = true
                                scope.launch {
                                    try {
                                        api.rateClothing(
                                            id = currentItem.id,
                                            rating = RatingRequest(stars = star)
                                        )
                                        val updated = api.getClothingById(currentItem.id)
                                        currentItem = updated.toClothingItem()
                                        ratingSubmitted = true
                                    } catch (e: Exception) {
                                        android.util.Log.e("RATING", "Error: ${e.message}")
                                    } finally {
                                        isRating = false
                                    }
                                }
                            }
                        },
                        enabled = !isRating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (star <= userRating)
                                colorResource(R.color.star_gold)
                            else
                                colorResource(R.color.star_inactive)
                        ),
                        modifier = Modifier.size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("★", fontSize = 20.sp)
                    }
                }
            }

            if (isRating) {
                CircularProgressIndicator()
            }

            if (userRating > 0 && !isRating) {
                Text(
                    text = if (ratingSubmitted)
                        stringResource(R.string.rating_saved, userRating)
                    else
                        stringResource(R.string.rating_local, userRating)
                )
            }

            if (currentItem.videoUrl.isNotBlank()) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, currentItem.videoUrl.toUri())
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.watch_video))
                }
            }

            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isDeleting,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                )
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Delete")
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