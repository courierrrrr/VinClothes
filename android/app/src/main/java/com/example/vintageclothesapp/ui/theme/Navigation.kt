package com.example.vintageclothesapp

import android.app.Application
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.navigation.toRoute
import com.example.vintageclothesapp.auth.AuthViewModel
import com.example.vintageclothesapp.auth.LoginScreen
import com.example.vintageclothesapp.auth.TokenStore
import com.example.vintageclothesapp.repository.DatabaseProvider
import com.example.vintageclothesapp.repository.RestClothingRepository
import com.example.vintageclothesapp.viewmodel.ClothingViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
@Serializable
object Login

@Serializable
data class Detail(val itemId: Int)

@Serializable
object Home

@Serializable
object AddClothing

@Serializable
data class Results(
    val type: String,
    val decade: String,
    val size: String,
    val brand: String
)

fun ClothingItemDto.toClothingItem() = ClothingItem(
    id = id ?: 0,
    title = title,
    decade = decade,
    brand = brand,
    size = size,
    type = clothing_type,
    description = description,
    videoUrl = video_url,
    imageBase64 = image,
    averageRating = average_rating
)

@Composable
fun AppNavHost() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val api = remember { RetrofitInstance.buildApi(context) }
    val authViewModel = remember {
        AuthViewModel(context.applicationContext as Application, api)
    }
    val database = remember { DatabaseProvider.getDatabase(context) }
    val repository = remember { RestClothingRepository(api, database.clothingItemDao()) }

    val startDestination = if (TokenStore.get(context) != null) Home else Login

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Home)
                }
            )
        }
        composable<Home> {
            HomeScreen(
                onSearch = { type, decade, size, brand ->
                    navController.navigate(Results(type, decade, size, brand))
                },
                onAddClothing = {
                    navController.navigate(AddClothing)
                },
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(Login) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<Results> { backStackEntry ->
            val route = backStackEntry.toRoute<Results>()
            val vm = remember { ClothingViewModel(repository) }

            LaunchedEffect(Unit) {
                vm.getClothes(
                    type = route.type.ifBlank { null },
                    decade = route.decade.ifBlank { null },
                    size = route.size.ifBlank { null },
                    brand = route.brand.ifBlank { null }
                )
            }

            when {
                vm.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                vm.errorMessage != null -> Text(
                    text = vm.errorMessage!!,
                    modifier = Modifier.padding(16.dp)
                )
                else -> ResultsScreen(
                    items = vm.items,
                    onItemClick = { item ->
                        navController.navigate(Detail(item.id))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable<Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<Detail>()
            val vm = remember { ClothingViewModel(repository) }

            LaunchedEffect(Unit) {
                vm.getClothingById(route.itemId)
            }

            val currentItem = vm.item
            when {
                vm.isLoading && currentItem == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                currentItem != null -> DetailScreen(
                    item = currentItem,
                    api = api,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable<AddClothing> {
            AddClothingScreen(
                api = api,
                onSaved = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}