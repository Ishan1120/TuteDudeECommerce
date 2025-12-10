package com.tutedude.ecommerce

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.map


data class DetailsUi(
    val product: Product?,
    val uploaderName: String?,
    val uploaderEmail: String?
)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val users: com.tutedude.ecommerce.domain.repository.UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Long = savedStateHandle.get<Long>("id") ?: 0L
    val ui: StateFlow<DetailsUi> = repo.observeProduct(id)
        .flatMapLatest { p ->
            val uploaderFlow = p?.uploaderUid?.let { users.user(it) } ?: kotlinx.coroutines.flow.flowOf(null)
            uploaderFlow.map { u -> DetailsUi(product = p, uploaderName = u?.displayName, uploaderEmail = u?.email) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailsUi(null, null, null))

    fun toggleFavorite() {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(productId: Long, onBack: () -> Unit, viewModel: DetailsViewModel = hiltViewModel()) {
    val ui by viewModel.ui.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Details") }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            val product = ui.product
            if (product == null) {
                Text("Loading...")
                return@Column
            }
            Text(product.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            Text("Seller: ${ui.uploaderName ?: "Unknown"} (${ui.uploaderEmail ?: "no email"})")
            Spacer(Modifier.height(8.dp))
            if (product.images.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(product.images) { url ->
                        AsyncImage(model = url, contentDescription = null)
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Text(product.description)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$" + String.format("%.2f", product.price))
                Button(onClick = { viewModel.toggleFavorite() }) { Text("Toggle Favorite") }
            }
        }
    }
}
