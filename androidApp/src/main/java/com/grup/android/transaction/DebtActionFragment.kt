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
import com.grup.android.R
import com.grup.android.UsernameSearchBar
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.h1Text
import com.grup.android.ui.SmallIcon
import com.grup.android.ui.UserInfoRowCard
import com.grup.android.ui.caption
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

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun DebtActionLayout(
    debtActionAmount: Double,
    transactionViewModel: TransactionViewModel,
    navController: NavController
) {
    val addDebtorBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()
    var debtors: List<UserInfo> by remember { mutableStateOf(emptyList()) }

    AddDebtorBottomSheet(
        userInfos = userInfos,
        addDebtorOnClick = { selectedUsers ->
            debtors = selectedUsers
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
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
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
                                    append("Even Split")
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
                                            tag = "SignUp",
                                            start = offset,
                                            end = offset
                                        )[0].let { _ ->
                                            //do your stuff when it gets clicked
                                        }
                                    }
                                )
                            }
                            AddDebtorButton(
                                addDebtorOnClick = {
                                    scope.launch { addDebtorBottomSheetState.show() }
                                }
                            )
                        }
                        SelectedDebtorsList(
                            debtActionAmount = debtActionAmount,
                            debtors = debtors,
                            createDebtActionOnClick = { userInfos, debtAmounts ->
                                transactionViewModel.createDebtAction(userInfos, debtAmounts)
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
fun SelectedDebtorsList(
    debtActionAmount: Double,
    debtors: List<UserInfo>,
    createDebtActionOnClick: (List<UserInfo>, List<Double>) -> Unit,
    modifier: Modifier = Modifier
) {
    val debtAmounts: MutableList<Double> =
        debtors.map { debtActionAmount / debtors.size }.toMutableStateList()
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
            onClick = { createDebtActionOnClick(debtors, debtAmounts) },
            shape = AppTheme.shapes.CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.colors.confirm
            ),
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Add Selected Users",
                color = AppTheme.colors.onSecondary
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddDebtorBottomSheet(
    userInfos: List<UserInfo>,
    addDebtorOnClick: (List<UserInfo>) -> Unit,
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
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                h1Text(text = "Add Debtors", color = textColor, fontSize = 50.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onUsernameChange = { usernameSearchQuery = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { addDebtorOnClick(selectedUsers) },
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
                Spacer(modifier = Modifier.height(8.dp))
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
                            onCheckedChange = { isChecked -> onCheckedChange(userInfo, isChecked) }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun AddDebtorButton(
    addDebtorOnClick: () -> Unit
) {
    IconButton(onClick = addDebtorOnClick) {
        SmallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a debtor"
        )
    }
}
