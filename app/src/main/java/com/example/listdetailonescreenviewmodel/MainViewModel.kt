package com.example.listdetailonescreenviewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    val books = mutableStateListOf(
        Book(1, "Kotlin", "Anders B", 9.95),
        Book(2, "More Kotlin", "Bobby C", 19.95),
        Book(3, "Even more Kotlin", "Bobby C", 4.95),
        Book(4, "Kotlin for the win", "Anders B", 29.95),
        Book(5, "Kotlin for the win", "Anders B", 29.95),
        Book(6, "Kotlin for the win", "Anders B", 29.95),
    )

    var selectedBook by mutableStateOf<Book?>(null)

    val selectBook : (Book) -> Unit = {
        selectedBook = it
    }

    val addBook: (Book) -> Unit = {
        books.add(it)
    }

    val deleteBook: (Book) -> Unit = {
        books.remove(it)
        if (it == selectedBook) {
            selectedBook = null
        }
    }

    private val mutableUIState = MutableStateFlow<BooksUIState>(BooksUIState.Details)
    val uiState = mutableUIState.asStateFlow()
    val uiStateChange: (BooksUIState) -> Unit = {
        mutableUIState.value = it
    }
}