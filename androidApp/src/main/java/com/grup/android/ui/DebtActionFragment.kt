package com.grup.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.asMoneyAmount
import com.grup.android.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.models.UserInfo
import kotlinx.coroutines.launch

class DebtActionFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.main_graph)

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
                    DebtActionLayout(
                        debtActionAmount = requireArguments().getDouble("amount"),
                        message = requireArguments().getString("message")!!,
                        transactionViewModel = transactionViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DebtActionLayout(
    debtActionAmount: Double,
    message: String,
    transactionViewModel: TransactionViewModel,
    navController: NavController
) {
    val addDebtorBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()
    var debtors: List<UserInfo> by remember { mutableStateOf(emptyList()) }
    var splitStrategy: TransactionViewModel.SplitStrategy
        by remember { mutableStateOf(TransactionViewModel.SplitStrategy.EvenSplit) }
    var debtAmounts: List<Double> by remember { mutableStateOf(emptyList()) }

    AddDebtorBottomSheet(
        userInfos = userInfos,
        addDebtorsOnClick = { selectedUsers ->
            debtors = selectedUsers
            debtAmounts = splitStrategy.generateSplit(debtActionAmount, debtors.size)
            scope.launch { addDebtorBottomSheetState.hide() }
        },
        state = addDebtorBottomSheetState
    ) {
        Scaffold(
            topBar = {
                DebtActionTopBar(
                    onBackPress = { navController.popBackStack() }
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
                    moneyAmount = debtActionAmount,
                    fontSize = 100.sp
                )
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                ) {
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = AppTheme.dimensions.cardPadding)
                            .padding(horizontal = AppTheme.dimensions.cardPadding)
                    ) {
                        DebtActionSettings(
                            splitStrategy = splitStrategy,
                            onSplitStrategyChange = { splitStrategy = it },
                            addDebtorsOnClick = {
                                scope.launch { addDebtorBottomSheetState.show() }
                            }
                        )
                        SelectedDebtorsList(
                            debtors = debtors,
                            debtAmounts = debtAmounts,
                            modifier = Modifier.weight(1f)
                        )
                        H1ConfirmTextButton(
                            text = "Create",
                            enabled = debtAmounts.sum() == debtActionAmount,
                            onClick = {
                                transactionViewModel.createDebtAction(debtors, debtAmounts, message)
                                navController.popBackStack()
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DebtActionTopBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = { },
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onBackPress
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

@Composable
fun DebtActionSettings(
    splitStrategy: TransactionViewModel.SplitStrategy,
    onSplitStrategyChange: (TransactionViewModel.SplitStrategy) -> Unit,
    addDebtorsOnClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            H1Text(text = "by ", fontSize = 20.sp)
            Box(
                modifier = Modifier
                    .padding(vertical = AppTheme.dimensions.spacing)
                    .clip(AppTheme.shapes.medium)
                    .background(AppTheme.colors.primary)
                    .clickable {  }
            ) {
                H1Text(
                    text = splitStrategy.name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(AppTheme.dimensions.spacingSmall)
                )
            }
            H1Text(text = " between:", fontSize = 20.sp)
        }
        AddDebtorButton(addDebtorsOnClick = addDebtorsOnClick)
    }
}

@Composable
fun SelectedDebtorsList(
    debtors: List<UserInfo>,
    debtAmounts: List<Double>,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            itemsIndexed(debtors) { index, userInfo ->
                UserInfoRowCard(
                    userInfo = userInfo,
                    sideContent = {
                        Text(
                            text = "pays ${debtAmounts[index].asMoneyAmount()}",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddDebtorBottomSheet(
    userInfos: List<UserInfo>,
    addDebtorsOnClick: (List<UserInfo>) -> Unit,
    state: ModalBottomSheetState,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    var selectedUsers: List<UserInfo> by remember { mutableStateOf(emptyList()) }
    var usernameSearchQuery: String by remember { mutableStateOf("") }

    BackPressModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                H1Text(text = "Add Debtors", color = textColor, fontSize = 50.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onQueryChange = { usernameSearchQuery = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { addDebtorsOnClick(selectedUsers) },
                        shape = AppTheme.shapes.circleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.confirm
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Add",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                SelectDebtorsChecklist(
                    usernameSearchQuery = usernameSearchQuery,
                    userInfos = userInfos,
                    selectedUsers = selectedUsers,
                    onCheckedChange = { userInfo, isSelected ->
                        selectedUsers = if (isSelected) {
                            selectedUsers + userInfo
                        } else {
                            selectedUsers - userInfo
                        }
                    }
                )
            }
        },
        content = content
    )
}

@Composable
fun SelectDebtorsChecklist(
    usernameSearchQuery: String,
    userInfos: List<UserInfo>,
    selectedUsers: List<UserInfo>,
    onCheckedChange: (UserInfo, Boolean) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            userInfos.filter { userInfo ->
                userInfo.nickname!!.contains(usernameSearchQuery, ignoreCase = true)
            }
        ) { userInfo ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserInfoRowCard(
                    userInfo = userInfo,
                    mainContent = {
                        H1Text(text = userInfo.nickname!!)
                        Caption(text = "Balance: ${userInfo.userBalance.asMoneyAmount()}")
                    },
                    sideContent = {
                        Checkbox(
                            checked = selectedUsers.contains(userInfo),
                            onCheckedChange = { isChecked -> onCheckedChange(userInfo, isChecked) },
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = AppTheme.colors.onSecondary
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AddDebtorButton(
    addDebtorsOnClick: () -> Unit
) {
    IconButton(onClick = addDebtorsOnClick) {
        SmallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a debtor"
        )
    }
}
