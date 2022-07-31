package com.example.grup.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grup.android.ui.AppTheme


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
    Scaffold(
        topBar = {
            HomeAppBar(
                AppTheme.colors.background,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(AppTheme.dimensions.topBarSize)
            )
        },
        backgroundColor = AppTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupDetails()
            Divider(thickness = AppTheme.dimensions.divider)
            PublicRequestsDetails()
        }
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {},
        backgroundColor = backgroundColor,
        navigationIcon = {
            IconButton(
                onClick = { /* TODO: Open search */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open notifications */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun PublicRequestsList(content: List<String>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxHeight()
    ) {
        items(content.size) {
            index ->
            Text(
                text = content[index],
                style = AppTheme.typography.h1,
                color = AppTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
fun GroupDetails() {
    Column (
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .size(AppTheme.dimensions.groupDetailsSize)
            .padding(top = AppTheme.dimensions.paddingLarge)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppTheme.dimensions.paddingLarge)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_profile_icon),
                contentDescription = "Profile",
                modifier = Modifier.size(98.dp)
            )
            Text(
                text = "Group Name",
                style = AppTheme.typography.h1,
                color = AppTheme.colors.textPrimary,
            )
        }
        Text(
            "$10",
            style = AppTheme.typography.h1,
            color = AppTheme.colors.textPrimary,
            fontSize = 100.sp
        )
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.button),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Money Request",
                color = AppTheme.colors.background,
            )
        }
    }
}

@Composable
fun PublicRequestsDetails() {
    val sampleList = listOf("test1", "test2", "test3")

    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(
                text = "Public Requests",
                style = AppTheme.typography.h1,
                color = AppTheme.colors.textPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search List"
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Filter List"
                )
            }
        }
        PublicRequestsList(content = sampleList)
    }
}
