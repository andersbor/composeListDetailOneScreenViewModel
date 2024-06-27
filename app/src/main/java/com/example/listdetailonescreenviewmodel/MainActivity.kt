package com.example.listdetailonescreenviewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listdetailonescreenviewmodel.ui.theme.ListDetailOneScreenViewModelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListDetailOneScreenViewModelTheme {
                BooksScaffold()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // TopAppBar
@Composable
fun BooksScaffold(modifier: Modifier = Modifier, mainViewModel: MainViewModel = viewModel()) {
    Scaffold(
        modifier = modifier,
        // https://developer.android.com/develop/ui/compose/components/app-bars
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = { mainViewModel.uiStateChange(BooksUIState.Adding) }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Call, contentDescription = "Delete")
                    }
                },
                title = { Text("Books") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mainViewModel.uiStateChange(BooksUIState.Adding) }
            ) { Icon(Icons.Default.Add, contentDescription = "Add") }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            val uiState = mainViewModel.uiState.collectAsState().value
            when (uiState) {
                is BooksUIState.Details -> BookDetailsPanel(mainViewModel.selectedBook)
                is BooksUIState.Adding -> AddBookPanel(
                    mainViewModel.addBook,
                    mainViewModel.uiStateChange
                )
            }
            BookListPanel(
                mainViewModel.books,
                onClickItem = mainViewModel.selectBook,
                onClickDelete = mainViewModel.deleteBook
            )
        }
    }
}

@Composable
fun BookListPanel(
    books: List<Book>,
    modifier: Modifier = Modifier,
    onClickItem: (Book) -> Unit = {},
    onClickDelete: (Book) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(books) { book ->
            BookItem(book, onClickItem = onClickItem, onClickDelete = onClickDelete)
        }
    }
}

@Composable
fun BookDetailsPanel(book: Book?, modifier: Modifier = Modifier) {
    if (book != null) {
        Column(modifier = modifier) {
            Text(text = book.title)
            Text(text = book.author)
            Text(text = book.price.toString())
        }
    } else {
        Text(text = "No book selected", modifier = modifier)
    }
}

@Composable
fun AddBookPanel(
    addBook: (Book) -> Unit,
    uiStateChange: (BooksUIState) -> Unit,
    modifier: Modifier = Modifier
) {
    var author by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }

    val localAdd = {
        val price = priceStr.toDoubleOrNull() ?: 0.0
        val book = Book(0, title, author, price)
        addBook(book)
        // TODO is this the right place to declare state change?
        uiStateChange(BooksUIState.Details)
    }
    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = author,
            //keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            //modifier = Modifier.fillMaxWidth(),
            onValueChange = { author = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            label = { Text("Author") }
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(modifier = Modifier.weight(2f),
                value = priceStr,
                onValueChange = { priceStr = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                label = { Text("Price") }
            )
            Button(modifier = Modifier
                .weight(1f)
                .padding(2.dp),
                // TODO is this the right place to declare state change?
                onClick = { uiStateChange(BooksUIState.Details) }) {
                Text("Cancel")
            }
            Button(modifier = Modifier
                .weight(1f)
                .padding(2.dp), onClick = {
                localAdd()
            }) {
                Text("Add")
            }
        }
    }
}

@Composable
fun BookItem(
    book: Book, modifier: Modifier = Modifier,
    onClickItem: (Book) -> Unit = {},
    onClickDelete: (Book) -> Unit = {}
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(5.dp),
        onClick = { onClickItem(book) }) {
        Row(modifier = Modifier.padding(2.dp)) {
            Text(
                text = book.author,
                modifier = Modifier.padding(8.dp),
                fontWeight = FontWeight.Bold
            )
            Text(text = book.title, modifier = Modifier.padding(8.dp), fontStyle = FontStyle.Italic)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = { onClickDelete(book) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BooksPreview() {
    ListDetailOneScreenViewModelTheme {
        BooksScaffold()
    }
}