package com.grup.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.grup.android.MoneyRequestActivity
import com.grup.android.R
import com.grup.android.ui.*
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainLayout()
            }
        }
    }
}

@Composable
fun MainLayout() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = AppTheme.colors.primary,
        topBar = {
            HomeAppBar(
                onNavigationIconClick = {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                }
            )
        },
        drawerBackgroundColor = AppTheme.colors.secondary,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerContent = {
            DrawerHeader()
            DrawerBody(
                items = listOf(
                    GroupItem(
                        id = "group1",
                        title = "Group 1",
                        contentDescription = "",
                        icon = Icons.Default.Home
                    ),
                    GroupItem(
                        id = "group2",
                        title = "Group 2",
                        contentDescription = "",
                        icon = Icons.Default.Home
                    ),
                    GroupItem(
                        id = "group3",
                        title = "Group 3",
                        contentDescription = "",
                        icon = Icons.Default.Home
                    )
                ),
                onItemClick = {
                    println("Clicked on ${it.title}")
                }
            )
            Spacer(modifier = Modifier.weight(1.0f))
            DrawerSettings(
                items = listOf(
                    MenuItem(
                        id = "home",
                        title = "Create New Group",
                        contentDescription = "Go to the home screen",
                        icon = Icons.Default.AddCircle
                    ),
                    MenuItem(
                        id = "home",
                        title = "Settings",
                        contentDescription = "Go to the settings screen",
                        icon = Icons.Default.Settings
                    ),
                    MenuItem(
                        id = "home",
                        title = "Sign Out",
                        contentDescription = "Go to the home screen",
                        icon = Icons.Default.ExitToApp
                    ),
                ),
                onItemClick = {
                    println("Clicked on ${it.title}")
                }
            )
        },
        bottomBar = {
            /* TODO */
        },
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
    ) {
        Column (
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

@Composable
fun HomeAppBar(
    onNavigationIconClick: () -> Unit
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
        actions = {
            IconButton(
                onClick = { /* TODO: Open notifications */ }
            ) {
                smallIcon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notifications"
                )
            }
        }
    )
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
                    painter = painterResource(id = R.drawable.ic_profile_icon),
                    contentDescription = "Profile Picture",
                    modifier = Modifier.size(98.dp)
                )
                h1Text(
                    text = "Group Name",
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
                text = "Make a Transaction",
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
