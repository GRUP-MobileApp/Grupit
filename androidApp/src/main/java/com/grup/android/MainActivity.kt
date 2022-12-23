package com.grup.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.grup.APIServer
import com.grup.android.ui.*
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.GroupInvite
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Prompt user for user info like username, etc
        try {
            APIServer.user
        } catch (e: UserObjectNotFoundException) {
            TODO("Welcome slideshow")
        }
        setContent {
            AppTheme {
                MainLayout()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainLayout() {
    val scaffoldState = rememberScaffoldState()
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    AddToGroupBottomSheetLayout(state = addToGroupBottomSheetState) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                HomeAppBar(
                    onNavigationIconClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch { addToGroupBottomSheetState.show() }
                        }) {
                            smallIcon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add to Group"
                            )
                        }
                        GroupNotificationsButton()
                    }
                )
            },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerContent = { GroupNavigationMenu() },
            bottomBar = {
                /* TODO */
            },
            backgroundColor = AppTheme.colors.primary,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onPrimary
                ) {
                    GroupDetails()
                    PublicRequestsDetails()
                }
            }
        }
    }
}

@Composable
fun GroupNavigationMenu() {
    DrawerHeader()
    DrawerBody(
        items = listOf(
            MenuItem(
                id = "home",
                title = "Home",
                contentDescription = "Go to the home screen",
                icon = Icons.Default.Home
            ),
            MenuItem(
                id = "groups",
                title = "Groups",
                contentDescription = "Go to the home screen",
                icon = Icons.Default.Home
            ),
            MenuItem(
                id = "settings",
                title = "Settings",
                contentDescription = "Go to the settings screen",
                icon = Icons.Default.Home
            ),
            MenuItem(
                id = "help",
                title = "Help",
                contentDescription = "",
                icon = Icons.Default.Home
            )
        ),
        onItemClick = {
            println("Clicked on ${it.title}")
        }
    )
}

@Composable
fun HomeAppBar(
    onNavigationIconClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        title = {},
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onNavigationIconClick
            ) {
                smallIcon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddToGroupBottomSheetLayout(
    state: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val username = remember { mutableStateOf(TextFieldValue()) }
    val group = APIServer.getGroupById("6234c5ab-5244-4f6d-a93a-314f3113b9a2")

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                TextField(
                    label = {
                        Text(
                            text = "Username to add",
                            color = AppTheme.colors.onSecondary
                        )
                    },
                    textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                    value = username.value,
                    onValueChange = { username.value = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { APIServer.inviteUserToGroup(username.value.text, group) }
                ) {
                    Text("Add to group")
                }
            }
        },
        sheetBackgroundColor = AppTheme.colors.secondary,
        content = content
    )
}

@Composable
fun GroupNotificationsButton() {
    var popupState by remember { mutableStateOf(false) }
    IconButton(
        onClick = { popupState = !popupState }
    ) {
        smallIcon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications"
        )
        if (popupState) {
            Popup(
                alignment = Alignment.BottomEnd,
                offset = IntOffset(0, 100),
            ) {
                val notifications: List<GroupInvite>
                        by APIServer.getAllGroupInvitesAsFlow().collectAsState(
                            initial = emptyList()
                        )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(AppTheme.dimensions.paddingMedium)
                        .background(Color.White)
                ) {
                    Text(text = "NOTIFICATIONS LIST")
                    notifications.forEach { groupInvite ->
                        ClickableText(
                            text = AnnotatedString(
                                "Group Invite to ${groupInvite.groupName!!}"
                            ),
                            onClick = {
                                APIServer.acceptInviteToGroup(groupInvite)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupDetails() {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .size(AppTheme.dimensions.groupDetailsSize)
            .padding(top = AppTheme.dimensions.paddingLarge)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimensions.paddingLarge)
        ) {
            Row (
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
                    text = "GROUP NAME",
                    modifier = Modifier.padding(top = AppTheme.dimensions.paddingLarge)
                )
            }
            h1Text(
                "$10",
                fontSize = 100.sp
            )
        }

        val context = LocalContext.current
        TextButton(
            colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
            modifier = Modifier
                .padding(bottom = AppTheme.dimensions.paddingMedium)
                .width(250.dp)
                .height(45.dp),
            shape = AppTheme.shapes.large,
            onClick = { context.startActivity(Intent(context, MoneyRequestActivity::class.java))}
        ) {
            Text(
                text = "Money Request",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = AppTheme.colors.onPrimary,
            )
        }
    }
}

@Composable
fun PublicRequestsDetails() {
    val sampleList = mapOf(
        "4/20" to listOf("test1", "test2", "test3", "test4"),
        "6/9" to listOf("test5", "test6", "test7", "test8")
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
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = AppTheme.dimensions.paddingMedium)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.smallSpacing)
                ) {
                    IconButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                    ) {

                    }
                    smallIconButton(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search List"
                    )
                    smallIconButton(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Filter List"
                    )
                }
            }
            PublicRequestsList(content = sampleList)
        }
    }
}

@Composable
fun PublicRequestsList(content: Map<String, List<String>>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(content.keys.toList()) { _: Int, filterGroup: String ->
            caption(
                text = "Completed - $filterGroup",
                modifier = Modifier.padding(start = AppTheme.dimensions.paddingExtraLarge)
            )
            Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing / 2))
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                content[filterGroup]!!.forEach { request ->
                    h1Text(
                        text = request,
                    )
                }
            }
        }
    }
}
