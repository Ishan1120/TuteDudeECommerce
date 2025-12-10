package com.tutedude.ecommerce

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.domain.repository.AuthRepository
import com.tutedude.ecommerce.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val products: ProductRepository,
    private val auth: AuthRepository
) : ViewModel() {

    fun create(
        title: String,
        desc: String,
        price: Double,
        imageUris: List<String>,
        category: String?,
        onDone: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                val user = auth.currentUser.firstOrNull()
                products.createProduct(
                    title = title,
                    description = desc,
                    price = price,
                    imageUris = imageUris,
                    uploaderUid = user?.uid,
                    category = category
                )
                products.refreshFromRemote()
            }.onSuccess { onDone(Result.success(Unit)) }
                .onFailure { onDone(Result.failure(it)) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBack: () -> Unit, viewModel: UploadViewModel = hiltViewModel()) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var picked by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var category by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        picked = uris
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Upload Product") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Description") })
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (optional)") })
            Spacer(Modifier.height(8.dp))

            Row {
                TextButton(onClick = { picker.launch(arrayOf("image/*")) }) { Text("Pick Images (3+)") }
                Text("  Selected: ${picked.size}")
            }

            Spacer(Modifier.height(16.dp))

            Button(onClick = {
                val p = price.toDoubleOrNull()
                if (title.isBlank() || desc.isBlank() || p == null || picked.size < 3) {
                    error = "Fill all fields and pick at least 3 images"
                } else {
                    viewModel.create(
                        title = title,
                        desc = desc,
                        price = p,
                        imageUris = picked.map { it.toString() },
                        category = category.ifBlank { null }
                    ) { res ->
                        if (res.isSuccess) onBack()
                        else error = res.exceptionOrNull()?.message
                    }
                }
            }) { Text("Upload") }
        }
    }
}
