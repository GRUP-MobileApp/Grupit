package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.ProfileTextField
import com.grup.ui.viewmodel.CreateGroupViewModel
import kotlinx.coroutines.launch

internal class CreateGroupView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val createGroupViewModel = rememberScreenModel { CreateGroupViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CreateGroupLayout(
            createGroupViewModel = createGroupViewModel,
            navigator = navigator
        )
    }
}

@Composable
private fun CreateGroupLayout(
    createGroupViewModel: CreateGroupViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val showErrorMessage: (String) -> Unit = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    var groupName: String by remember { mutableStateOf("") }
    var error: String? by remember { mutableStateOf(null) }

    BackPressScaffold(
        scaffoldState = scaffoldState,
        onBackPress = { navigator.pop() },
        title = { H1Header(text = "New Group", fontWeight = FontWeight.SemiBold) }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            ProfileTextField(
                placeholder = "Group Name",
                value = groupName,
                onValueChange = { groupName = it },
                error = error
            )
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Create",
                onClick = {
                    createGroupViewModel.createGroup(
                        groupName = groupName,
                        onSuccess = { navigator.pop() },
                        onValidationError = { error = it },
                        onError = {
                            error = null
                            it?.let(showErrorMessage)
                        }
                    )
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
