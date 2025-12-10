package com.tutedude.ecommerce

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutedude.ecommerce.domain.model.Product
import com.tutedude.ecommerce.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ProductRepository,
    private val recs: com.tutedude.ecommerce.domain.repository.RecommendationRepository
) : ViewModel() {

    private val allProducts: StateFlow<List<Product>> = repo.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Search and filters
    private val _query = MutableStateFlow("")
    private val _category = MutableStateFlow<String?>(null)

    val categories: StateFlow<List<String>> = allProducts
        .map { list -> list.mapNotNull { it.category }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val filtered = combine(allProducts, _query, _category) { list, q, cat ->
        val qn = q.trim().lowercase()
        list.filter { p ->
            (cat == null || p.category == cat) &&
            (qn.isBlank() || p.title.lowercase().contains(qn) || p.description.lowercase().contains(qn))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // Pagination
    private val pageSize = 10
    private val _page = MutableStateFlow(1)
    val hasMore: StateFlow<Boolean> = combine(filtered, _page) { list, page -> list.size > page * pageSize }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val products: StateFlow<List<Product>> = combine(filtered, _page) { list, page ->
        list.take(page * pageSize)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _recommended = MutableStateFlow<List<Product>>(emptyList())
    val recommended: StateFlow<List<Product>> = _recommended.asStateFlow()

    fun setQuery(q: String) { _page.value = 1; _query.value = q }
    fun setCategory(cat: String?) { _page.value = 1; _category.value = cat }
    fun clearFilters() { _page.value = 1; _query.value = ""; _category.value = null }
    fun loadMore() { _page.value = _page.value + 1 }

    fun loadRecommended() {
        viewModelScope.launch {
            runCatching { recs.fetchRecommended() }
                .onSuccess { _recommended.value = it }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch { repo.toggleFavorite(id) }
    }

    fun refresh() {
        viewModelScope.launch {
            runCatching { repo.refreshFromRemote() }
            repo.ensureSeeded()
        }
    }
}
