package com.grup.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.caption
import com.grup.android.ui.h1Text
import com.grup.android.ui.smallIcon
import com.grup.models.Group
import com.grup.models.GroupInvite
import kotlinx.coroutines.launch

class MembersFragment : Fragment() {
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
                    MembersLayout(
                        mainViewModel = mainViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MembersLayout(
    mainViewModel: MainViewModel,
    navController: NavController
) {

    val scaffoldState = rememberScaffoldState()
    var username: String by remember { mutableStateOf("") }
    val MemberInfoBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val selectedGroup: Group? by mainViewModel.selectedGroup.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val sampleList = mapOf(
        "4/20" to listOf("test1", "test2", "test3", "test4"),
        "6/9" to listOf("test5", "test6", "test7", "test8")
    )

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->

        AddToGroupBottomSheetLayout(
            selectedGroup = selectedGroup!!,
            state = addToGroupBottomSheetState,
            inviteUsernameToGroup = { username, group ->
                mainViewModel.inviteUserToGroup(username, group)
            }
        ) {
            MemberInfoBottomSheet(state = MemberInfoBottomSheetState ) {
                content()

            }
        }

    }

    modalSheets{
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Members", color = AppTheme.colors.onSecondary) },
                    backgroundColor = AppTheme.colors.primary,
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                Modifier.background(AppTheme.colors.primary)
                            )
                        }
                    },
                    actions = {
                        AddToGroupButton(
                            addToGroupOnClick = {
                                scope.launch { addToGroupBottomSheetState.show() }
                            }
                        )
                    }
                )
            },
            backgroundColor = AppTheme.colors.primary,
            drawerContent = { Text(text = "drawerContent") },
            bottomBar = { /* TODO */ },
            content = {
                Box(
                    modifier = Modifier
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                        .fillMaxSize()
                ) {

                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SearchBar(
                                username = username,
                                onUsernameChange = {}
                            )
                        }

                        MembersList()
                    }
                }
            },
        )
    }
}

@Composable
fun MembersList() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.paddingMedium)
    ) {
        Icon(
            imageVector = Icons.Default.Face,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(70.dp)
                .padding(horizontal = AppTheme.dimensions.paddingSmall)
        )

        Column(verticalArrangement = Arrangement.Center) {
            h1Text(text = "Name")
            caption(text = "This is a description")
        }
    }

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MemberInfoBottomSheet(
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimensions.paddingLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(98.dp)
                    )
                    h1Text(
                        text = "Member",
                        modifier = Modifier.padding(top = AppTheme.dimensions.paddingLarge)
                    )
                }
            },
        sheetBackgroundColor = backgroundColor,
        content = content,
        sheetShape = AppTheme.shapes.large
    )
}

@Composable
fun AddToGroupButton(
    addToGroupOnClick: () -> Unit
) {
    IconButton(onClick = addToGroupOnClick) {
        smallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add to Group"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddToGroupBottomSheetLayout(
    selectedGroup: Group,
    inviteUsernameToGroup: (String, Group) -> Unit,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    var username: String by remember { mutableStateOf("") }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                SearchBar(
                    username = username,
                    onUsernameChange = { username = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { inviteUsernameToGroup(username, selectedGroup) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    shape = AppTheme.shapes.CircleShape
                ) {
                    Text(text = "Add to group", color = textColor)
                }
            }
        },
        sheetBackgroundColor = backgroundColor,
        content = content
    )
}