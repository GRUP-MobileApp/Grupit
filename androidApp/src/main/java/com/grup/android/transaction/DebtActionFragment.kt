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
import androidx.compose.material.icons.filled.Search
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
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.h1Text
import com.grup.android.ui.SmallIcon
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
    val debtors: MutableList<UserInfo> = remember { mutableStateListOf() }

    AddDebtorBottomSheet(
        userInfos = userInfos,
        addDebtorOnClick = { selectedUsers ->
            debtors.addAll(selectedUsers)
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
                    fontSize = 65.sp
                )
                Box(
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .height(40.dp)
                                .padding(AppTheme.dimensions.paddingMedium)
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
                                            tag = "SignUp",// tag which you used in the buildAnnotatedString
                                            start = offset,
                                            end = offset
                                        )[0].let { annotation ->
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
                        Spacer(modifier = Modifier.height(8.dp))
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
                )
            }
        }
    )
}

@Composable
fun SelectedDebtorsList(
    debtActionAmount: Double,
    debtors: List<UserInfo>,
    createDebtActionOnClick: (List<UserInfo>, List<Double>) -> Unit
) {
    val debtAmounts: MutableList<Double> =
        debtors.map { debtActionAmount / debtors.size }.toMutableStateList()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(debtors) { index, userInfo ->
            Text(
                text = "${userInfo.nickname} pays $${debtAmounts[index]}",
                color = AppTheme.colors.onSecondary
            )
        }
    }
    Button(onClick = { createDebtActionOnClick(debtors, debtAmounts) }) {
        Text(text = "Add selected users")
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
    val selectedUsers: MutableList<UserInfo> = remember { mutableStateListOf() }
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
                Text(text = "Add Debtors", color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = usernameSearchQuery,
                    onValueChange = { usernameSearchQuery = it },
                    trailingIcon = { Icons.Default.Search },
                    modifier = Modifier
                        .padding(5.dp)
                        .background(color = Color.White)
                        .clip(shape = AppTheme.shapes.medium)
                )
                Spacer(modifier = Modifier.height(8.dp))

                SelectDebtorsChecklist(
                    usernameSearchQuery = usernameSearchQuery,
                    userInfos = userInfos,
                    selectedUsers = selectedUsers,
                    onCheckedChange = { userInfo, isSelected ->
                        if (isSelected) {
                            selectedUsers.add(userInfo)
                        } else {
                            selectedUsers.remove(userInfo)
                        }
                    }
                )
                Button(
                    onClick = { addDebtorOnClick(selectedUsers) }) {
                    Text(text = "Add selected users")
                }
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
                Text(
                    text = "${userInfo.nickname}: ${userInfo.userBalance}",
                    color = AppTheme.colors.onSecondary
                )
                Checkbox(
                    checked = selectedUsers.contains(userInfo),
                    onCheckedChange = { isChecked -> onCheckedChange(userInfo, isChecked) }
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
