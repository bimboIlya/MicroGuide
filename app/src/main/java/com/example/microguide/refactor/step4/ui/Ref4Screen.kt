package com.example.microguide.refactor.step4.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.microguide.data.model.CommentModel
import com.example.microguide.refactor.step4.presentation.ScreenState

@Composable
fun Ref4Screen(
    screenState: ScreenState,
    onUpdateComments: (String) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Компост") }) },
        bottomBar = { PinnedBar(onUpdateComments = onUpdateComments) }
    ) { padding ->
        Body(screenState = screenState, modifier = Modifier.padding(padding))
    }
}

@Composable
private fun Body(
    screenState: ScreenState,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (screenState) {
            ScreenState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            ScreenState.Error -> Unit
            is ScreenState.Content -> {
                val comments = screenState.comments
                if (comments.isEmpty()) {
                    Text(text = "EMPTY", modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn {
                        items(comments) { comment ->
                            Comment(comment)
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PinnedBar(
    onUpdateComments: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Surface(elevation = 6.dp, modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
            )
            Button(onClick = { onUpdateComments(text) }) {
                Text(text = "UPDATE")
            }
        }
    }
}

@Composable
private fun Comment(comment: CommentModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = comment.name,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
        )
        Text(
            text = comment.body,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview
@Composable
private fun ScreenPreview() {
    val comments = listOf(
        CommentModel(
            postId = 1,
            id = 1,
            name = "poop",
            email = "poop@poop.com",
            body = "Lorem Ipsum"
        ),
        CommentModel(
            postId = 2,
            id = 2,
            name = "poop",
            email = "poop@poop.com",
            body = "Lorem Ipsum"
        ),
        CommentModel(
            postId = 3,
            id = 3,
            name = "poop",
            email = "poop@poop.com",
            body = "Lorem Ipsum"
        ),
    )
    val state = ScreenState.Content(comments)

    Ref4Screen(
        screenState = state,
        onUpdateComments = {},
    )
}

@Preview
@Composable
private fun CommentPreview() {
    val comment = CommentModel(
        postId = 1,
        id = 1,
        name = "poop",
        email = "poop@poop.com",
        body = "Lorem Ipsum"
    )

    Comment(comment = comment)
}