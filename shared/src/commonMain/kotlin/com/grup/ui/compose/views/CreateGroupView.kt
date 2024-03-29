package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.InvisibleTextField
import com.grup.ui.viewmodel.CreateGroupViewModel

internal class CreateGroupView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            val createGroupViewModel = rememberScreenModel { CreateGroupViewModel() }
            val navigator = LocalNavigator.currentOrThrow

            CreateGroupLayout(
                createGroupViewModel = createGroupViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun CreateGroupLayout(
    createGroupViewModel: CreateGroupViewModel,
    navigator: Navigator
) {
    var groupName: String by remember { mutableStateOf("") }

    BackPressScaffold(onBackPress = { navigator.pop() }, title = "New Group") { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            InvisibleTextField(
                placeholder = "Name",
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier
                    .padding(5.dp)
                    .clip(shape = AppTheme.shapes.medium)
            )
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Create",
                onClick = {
                    createGroupViewModel.createGroup(
                        groupName = groupName,
                        onSuccess = {
                            navigator.pop()
                        },
                        onError = { }
                    )
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
