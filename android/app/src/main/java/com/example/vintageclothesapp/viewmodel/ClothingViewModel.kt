package com.example.vintageclothesapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vintageclothesapp.ClothingItem
import com.example.vintageclothesapp.auth.TokenExpiredException
import com.example.vintageclothesapp.repository.ClothingRepository
import kotlinx.coroutines.launch

class ClothingViewModel(
    private val repository: ClothingRepository
) : ViewModel() {

    var items by mutableStateOf<List<ClothingItem>>(emptyList())
        private set
    var item by mutableStateOf<ClothingItem?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set
    var saveSuccess by mutableStateOf(false)
        private set

    fun getClothes(type: String?, decade: String?, size: String?, brand: String?, onTokenExpired: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            items = emptyList()
            try {
                items = repository.getClothes(type, decade, size, brand)
            } catch (e: TokenExpiredException) {
                onTokenExpired()
            } catch (e: Exception) {
                errorMessage = "Failed to load data. Please check your connection."
            } finally {
                isLoading = false
            }
        }
    }

    fun getClothingById(id: Int, onTokenExpired: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                item = repository.getClothingById(id)
            } catch (e: TokenExpiredException) {
                onTokenExpired()
            } catch (e: Exception) {
                errorMessage = "Failed to load item."
            } finally {
                isLoading = false
            }
        }
    }

    fun addClothing(newItem: ClothingItem) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                repository.addClothing(newItem)
                saveSuccess = true
            } catch (e: Exception) {
                errorMessage = "Failed to save item."
            } finally {
                isLoading = false
            }
        }
    }

    fun resetSaveSuccess() {
        saveSuccess = false
    }
}