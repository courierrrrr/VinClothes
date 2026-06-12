package com.example.vintageclothesapp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SortOption(val label: String) {
    NONE("Default"),
    DECADE_ASC("Decade ↓"),
    DECADE_DESC("Decade ↑"),
    TITLE_ASC("Title A-Z")
}

@Composable
fun ClothingItemRow(item: ClothingItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = stringResource(R.string.detail_decade, item.decade))
            Text(text = stringResource(R.string.detail_brand, item.brand))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    items: List<ClothingItem>,
    onItemClick: (ClothingItem) -> Unit,
    onBack: () -> Unit
) {
    var selectedSort by remember { mutableStateOf(SortOption.NONE) }
    var expanded by remember { mutableStateOf(false) }

    val sortedItems = remember(items, selectedSort) {
        when (selectedSort) {
            SortOption.NONE        -> items
            SortOption.DECADE_ASC  -> items.sortedBy { it.decade }
            SortOption.DECADE_DESC -> items.sortedByDescending { it.decade }
            SortOption.TITLE_ASC   -> items.sortedBy { it.title.lowercase() }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.results_title)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = onBack) {
                    Text(stringResource(R.string.back))
                }

                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Sort: ${selectedSort.label}", fontSize = 13.sp)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    selectedSort = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            if (sortedItems.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_results),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sortedItems) { item ->
                        ClothingItemRow(
                            item = item,
                            onClick = { onItemClick(item) }
                        )
                    }
                }
            }
        }
    }
}