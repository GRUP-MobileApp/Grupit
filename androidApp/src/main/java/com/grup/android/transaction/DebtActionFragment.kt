package com.grup.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.grup.android.R
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.AppTheme
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
                        transactionViewModel = transactionViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class,
    ExperimentalPagerApi::class
)
@Composable
fun DebtActionLayout(
    debtActionAmount: Double,
    transactionViewModel: TransactionViewModel,
    navController: NavController
) {
    val addDebtorBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()
    var debtors: List<UserInfo> by remember { mutableStateOf(emptyList()) }
    var splitStrategy: TransactionViewModel.SplitStrategy
        by remember { mutableStateOf(TransactionViewModel.SplitStrategy.EvenSplit) }
    var debtAmounts: List<Double> by remember { mutableStateOf(emptyList()) }
    var message: String by remember { mutableStateOf("") }

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
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AppTheme.colors.primary)
            ) {
                h1Text(
                    text = "$$debtActionAmount",
                    color = AppTheme.colors.onSecondary,
                    fontSize = 100.sp
                )
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                ) {
                    HorizontalPager(
                        count = 2,
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { page ->
                        when (page) {
                            0 ->
                                DebtAmountsScreen(
                                    debtors = debtors,
                                    debtAmounts = debtAmounts,
                                    splitStrategy = splitStrategy,
                                    onSplitStrategyChange = { splitStrategy = it },
                                    addDebtorsOnClick = {
                                        scope.launch { addDebtorBottomSheetState.show() }
                                    },
                                    onClickContinue = {
                                        scope.launch { pagerState.animateScrollToPage(1) }
                                    }
                                )
                            1 -> AddMessageScreen(
                                    message = message,
                                    onMessageChange = { message = it },
                                    createDebtActionOnClick = {
                                        transactionViewModel.createDebtAction(
                                            debtors,
                                            debtAmounts,
                                            message
                                        )
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
fun DebtAmountsScreen(
    debtors: List<UserInfo>,
    debtAmounts: List<Double>,
    splitStrategy: TransactionViewModel.SplitStrategy,
    onSplitStrategyChange: (TransactionViewModel.SplitStrategy) -> Unit,
    addDebtorsOnClick: () -> Unit,
    onClickContinue: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement
            .spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        DebtActionSettings(
            splitStrategy = splitStrategy,
            onSplitStrategyChange = onSplitStrategyChange,
            addDebtorsOnClick = addDebtorsOnClick
        )
        SelectedDebtorsList(
            debtors = debtors,
            debtAmounts = debtAmounts,
            onClickContinue = onClickContinue
        )
    }
}

@Composable
fun DebtActionSettings(
    splitStrategy: TransactionViewModel.SplitStrategy,
    onSplitStrategyChange: (TransactionViewModel.SplitStrategy) -> Unit,
    addDebtorsOnClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement
            .spacedBy(AppTheme.dimensions.spacingSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = AppTheme.colors.onSecondary
                )
            ) {
                append("by ")
            }
            pushStringAnnotation(
                tag = "SplitStrategy",
                annotation = "Split Strategy"
            )
            withStyle(
                style = SpanStyle(
                    color = AppTheme.colors.onSecondary,
                    background = AppTheme.colors.primary
                )
            ) {
                append(splitStrategy.name)
            }
            pop()
            withStyle(
                style = SpanStyle(
                    color = AppTheme.colors.onSecondary
                )
            ) {
                append(" between:")
            }
        }.let { annotatedText ->
            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "Split Strategy",
                        start = offset,
                        end = offset
                    )[0].let { _ ->
                        /* TODO: Split strategy menu select */
                    }
                }
            )
        }
        AddDebtorButton(addDebtorsOnClick = addDebtorsOnClick)
    }
}

@Composable
fun SelectedDebtorsList(
    debtors: List<UserInfo>,
    debtAmounts: List<Double>,
    onClickContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize(0.95f)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(debtors) { index, userInfo ->
                UserInfoRowCard(
                    userInfo = userInfo,
                    sideContent = {
                        Text(
                            text = "pays $${debtAmounts[index]}",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                )
            }
        }
        Button(
            onClick = onClickContinue,
            shape = AppTheme.shapes.CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.colors.confirm
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Continue",
                color = AppTheme.colors.onSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddDebtorBottomSheet(
    userInfos: List<UserInfo>,
    addDebtorsOnClick: (List<UserInfo>) -> Unit,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    var selectedUsers: List<UserInfo> by remember { mutableStateOf(emptyList()) }
    var usernameSearchQuery: String by remember { mutableStateOf("") }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                h1Text(text = "Add Debtors", color = textColor, fontSize = 50.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onQueryChange = { usernameSearchQuery = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { addDebtorsOnClick(selectedUsers) },
                        shape = AppTheme.shapes.CircleShape,
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
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingSmall))
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
        sheetBackgroundColor = backgroundColor,
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
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(
            userInfos.filter { userInfo ->
                userInfo.nickname!!.contains(usernameSearchQuery, ignoreCase = true)
            }
        ) { _, userInfo ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserInfoRowCard(
                    userInfo = userInfo,
                    mainContent = {
                        Column(verticalArrangement = Arrangement.Center) {
                            h1Text(text = it.nickname!!)
                            caption(text = "Balance: ${it.userBalance}")
                        }
                    },
                    sideContent = { userInfo ->
                        Checkbox(
                            checked = selectedUsers.contains(userInfo),
                            onCheckedChange = { isChecked -> onCheckedChange(userInfo, isChecked) },
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = AppTheme.colors.onPrimary
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

@Composable
fun AddMessageScreen(
    message: String,
    onMessageChange: (String) -> Unit,
    createDebtActionOnClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement
            .spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        h1Text(text = "What is this for?", fontSize = 40.sp)
        TextField(
            value = message,
            onValueChange = onMessageChange,
            shape = RectangleShape,
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.secondary)
                .padding(all = AppTheme.dimensions.paddingMedium)
                .height(200.dp),
            colors = TextFieldDefaults.textFieldColors(
                textColor = AppTheme.colors.primary,
                disabledTextColor = Color.Transparent,
                backgroundColor = AppTheme.colors.onPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = createDebtActionOnClick,
                shape = AppTheme.shapes.CircleShape,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.confirm
                ),
                modifier = Modifier
                    .width(175.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "Confirm Request",
                    color = AppTheme.colors.onSecondary
                )
            }
        }
    }
}
