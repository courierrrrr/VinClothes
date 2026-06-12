package com.example.vintageclothesapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onSearch: (String, String, String, String) -> Unit,
    onAddClothing: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(40.dp).padding(4.dp)
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            var clothingType by remember { mutableStateOf("") }
            TextField(
                value = clothingType,
                onValueChange = { clothingType = it },
                label = { Text(stringResource(R.string.type_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            var decade by remember { mutableStateOf("") }
            TextField(
                value = decade,
                onValueChange = { decade = it },
                label = { Text(stringResource(R.string.decade_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            var size by remember { mutableStateOf("") }
            TextField(
                value = size,
                onValueChange = { size = it },
                label = { Text(stringResource(R.string.size_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            var brand by remember { mutableStateOf("") }
            TextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text(stringResource(R.string.brand_hint)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSearch(clothingType, decade, size, brand) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.search))
            }

            Button(
                onClick = onAddClothing,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_clothing))
            }

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                )
            ) {
                Text("Log Out")
            }
        }
    }
}