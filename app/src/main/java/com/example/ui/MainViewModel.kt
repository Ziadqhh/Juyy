package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.MarbleItem
import com.example.data.MarbleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SortType {
    NAME, AREA, COUNT, DATE
}

class MainViewModel(private val repository: MarbleRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType = _sortType.asStateFlow()

    val items: StateFlow<List<MarbleItem>> = combine(
        repository.allItems,
        _searchQuery,
        _sortType
    ) { allItems, query, sortType ->
        var list = allItems
        if (query.isNotBlank()) {
            list = list.filter { it.type.contains(query, ignoreCase = true) }
        }
        when (sortType) {
            SortType.NAME -> list.sortedBy { it.type }
            SortType.AREA -> list.sortedByDescending { it.totalArea }
            SortType.COUNT -> list.sortedByDescending { it.count }
            SortType.DATE -> list // Already DESC from DAO
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSortType(type: SortType) {
        _sortType.value = type
    }

    fun addItem(type: String, length: Double, width: Double, count: Int) {
        viewModelScope.launch {
            repository.insertItem(
                MarbleItem(
                    type = type,
                    length = length,
                    width = width,
                    count = count
                )
            )
        }
    }

    fun updateItem(item: MarbleItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(item: MarbleItem) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearAllItems()
        }
    }
}

class MainViewModelFactory(private val repository: MarbleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
