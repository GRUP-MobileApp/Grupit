package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.grup.ui.NavigationController
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.viewmodel.MainViewModel

@Composable
fun CreateGroupView(
    mainViewModel: MainViewModel,
    navController: NavigationController
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onSecondary
    ) {
        CreateGroupLayout(
            mainViewModel = mainViewModel,
            navController = navController
        )
    }
}

@Composable
private fun CreateGroupLayout(
    mainViewModel: MainViewModel,
    navController: NavigationController
) {
    var groupName: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CreateGroupTopBar(
                onBackPress = { navController.onBackPress() },
                createGroupOnClick = {
                    mainViewModel.onSelectedGroupChange(mainViewModel.createGroup(groupName))
                    navController.onBackPress()
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
