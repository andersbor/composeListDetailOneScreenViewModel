package com.example.listdetailonescreenviewmodel

sealed class BooksUIState {
    data object Adding : BooksUIState()
    data object Details : BooksUIState()
}