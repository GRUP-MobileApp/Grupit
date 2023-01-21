package com.grup.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.ui.apptheme.AppTheme

class CreateGroupFragment : Fragment() {
    private val mainViewModel: MainViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onSecondary
                ) {
                    CreateGroupLayout(
                        mainViewModel = mainViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun CreateGroupLayout(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    var groupName: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CreateGroupTopBar(
                onBackPress = { navController.popBackStack() },
                createGroupOnClick = {
                    mainViewModel.selectedGroup.value = mainViewModel.createGroup(groupName)
                    navController.popBackStack()
                }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
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
fun CreateGroupTopBar(
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
