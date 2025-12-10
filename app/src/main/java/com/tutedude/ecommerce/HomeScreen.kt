package com.tutedude.ecommerce

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tutedude.ecommerce.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenDetails: (Long) -> Unit,
    onOpenFavorites: () -> Unit,
    onOpenUpload: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    LaunchedEffect(Unit) {
        viewModel.refresh()
        viewModel.loadRecommended()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TuteDude Store") },
                actions = {
                    val authVm: AuthViewModel = hiltViewModel()
                    TextButton(onClick = onOpenFavorites) { Text("Favorites") }
                    TextButton(onClick = onOpenUpload) { Text("Upload") }
                    TextButton(onClick = { authVm.signOut() }) { Text("Sign out") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {

            val categories by viewModel.categories.collectAsState()
            var query by remember { mutableStateOf("") }

            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    viewModel.setQuery(it)
                },
                label = { Text("Search products") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            if (categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { c ->
                        TextButton(onClick = { viewModel.setCategory(c) }) { Text(c) }
                    }
                    item { TextButton(onClick = { viewModel.setCategory(null) }) { Text("All") } }
                }
            }

            val recs by viewModel.recommended.collectAsState()
            if (recs.isNotEmpty()) {
                Text(
                    "Recommended",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recs) { p ->
                        Card(onClick = { onOpenDetails(p.id) }) {
                            Column(Modifier.padding(8.dp)) {
                                if (p.images.isNotEmpty()) {
                                    AsyncImage(
                                        model = p.images.first(),
                                        contentDescription = null,
                                        modifier = Modifier.height(88.dp)
                                    )
                                }
                                Text(p.title, maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            ProductList(
                products = products,
                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                onOpenDetails = onOpenDetails,
                modifier = Modifier
            )

            val hasMore by viewModel.hasMore.collectAsState()
            if (hasMore) {
                TextButton(
                    onClick = { viewModel.loadMore() },
                    modifier = Modifier.padding(16.dp)
                ) { Text("Load more") }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
private fun ProductList(
    products: List<Product>,
    onToggleFavorite: (Long) -> Unit,
    onOpenDetails: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products, key = { it.id }) { p ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpenDetails(p.id) }
            ) {
                Row(Modifier.padding(16.dp)) {
                    if (p.images.isNotEmpty()) {
                        AsyncImage(
                            model = p.images.first(),
                            contentDescription = null,
                            modifier = Modifier
                                .height(72.dp)
                                .weight(0.3f, fill = false)
                        )
                        Spacer(Modifier.height(0.dp))
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(p.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            p.description,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("₹" + String.format("%.2f", p.price))
                            TextButton(onClick = { onToggleFavorite(p.id) }) { Text("Favorite") }
                        }
                    }
                }
            }
        }
    }
}
