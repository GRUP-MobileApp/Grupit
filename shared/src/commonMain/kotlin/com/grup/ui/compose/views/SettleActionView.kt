package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.KeyPadBottomSheet
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.UserInfoAmountsList
import com.grup.ui.compose.UsernameSearchBar
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

internal class SettleActionView : Screen {
    @Composable
    override fun Content() {
        val transactionViewModel = getScreenModel<TransactionViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            SettleActionLayout(transactionViewModel = transactionViewModel, navigator = navigator)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettleActionLayout(
    transactionViewModel: TransactionViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val keyPadBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()

    val settleActionAmounts:
        SnapshotStateMap<UserInfo, Double> = remember { mutableStateMapOf() }
    var keyPadUserInfo: UserInfo? by remember { mutableStateOf(null) }
    var usernameSearchQuery: String by remember { mutableStateOf("") }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        keyPadUserInfo?.let { userInfo ->
            KeyPadBottomSheet(
                state = keyPadBottomSheetState,
                initialMoneyAmount = settleActionAmounts[userInfo] ?: 0.0,
                maxMoneyAmount = userInfo.userBalance,
                onClick = { settleActionAmount ->
                    settleActionAmounts[userInfo] = settleActionAmount
                },
                onBackPress = { scope.launch { keyPadBottomSheetState.hide() } },
                content = content
            )
        } ?: content()
    }

    modalSheets {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    backgroundColor = AppTheme.colors.primary,
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = AppTheme.colors.onSecondary
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.primary)
                    .padding(padding)
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                MoneyAmount(
                    moneyAmount = settleActionAmounts.values.sum(),
                    fontSize = 100.sp
                )
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                        .padding(AppTheme.dimensions.cardPadding)
                ) {
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        UsernameSearchBar(
                            usernameSearchQuery = usernameSearchQuery,
                            onQueryChange = { usernameSearchQuery = it }
                        )
                        UserInfoAmountsList(
                            userInfoMoneyAmounts = userInfos.filter { userInfo ->
                                userInfo.userBalance > 0
                                        &&
                                userInfo.user.displayName
                                    .contains(usernameSearchQuery, ignoreCase = true)
                            }.associateWith { userInfo ->
                                settleActionAmounts[userInfo] ?: 0.0
                            },
                            userInfoHasSetAmount = { userInfo ->
                                settleActionAmounts[userInfo] != null
                            },
                            userInfoAmountOnClick = { userInfo ->
                                keyPadUserInfo = userInfo
                                scope.launch { keyPadBottomSheetState.show() }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        H1ConfirmTextButton(
                            text = "Pay",
                            enabled = settleActionAmounts.values.sum() > 0.0,
                            onClick = {
                                transactionViewModel.createSettleAction(
                                    settleActionAmounts,
                                    onSuccess = { navigator.pop() },
                                    onFailure = { }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
