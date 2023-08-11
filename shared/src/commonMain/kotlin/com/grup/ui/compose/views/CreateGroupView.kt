package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme

internal class CreateGroupView(
    private val createGroupOnClick: (groupName: String) -> Unit
) : Screen {
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            val navigator = LocalNavigator.currentOrThrow

            CreateGroupLayout(
                createGroupOnClick = createGroupOnClick,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun CreateGroupLayout(
    createGroupOnClick: (groupName: String) -> Unit,
    navigator: Navigator
) {
    var groupName: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CreateGroupTopBar(
                onBackPress = { navigator.pop() },
                createGroupOnClick = {
                    createGroupOnClick(groupName)
                    navigator.pop()
                }
            )
        },
        backgroundColor = AppTheme.colors.primary,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.primary)
        ) {
            TextField(
                label = { Text("Group Name", color = AppTheme.colors.onSecondary) },
                value = groupName,
                onValueChange = { groupName = it },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier
                    .padding(5.dp)
                    .clip(shape = AppTheme.shapes.medium)
            )
        }
    }
}

@Composable
private fun CreateGroupTopBar(
    onBackPress: () -> Unit,
    createGroupOnClick: () -> Unit
) {
    TopAppBar(
        title = { Text("Create a group", color = AppTheme.colors.onSecondary) },
        actions = {
            ClickableText(
                text = AnnotatedString(text = "Create"),
                style = TextStyle(color = AppTheme.colors.onSecondary),
                onClick = { createGroupOnClick() }
            )
        },
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}
