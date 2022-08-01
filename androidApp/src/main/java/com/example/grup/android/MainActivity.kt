package com.example.grup.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.grup.android.ui.h1Text


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
                AppTheme.colors.primary,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(AppTheme.dimensions.topBarSize)
            )
        },
        backgroundColor = AppTheme.colors.primary,
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GroupDetails()
            Divider(
                thickness = AppTheme.dimensions.divider,
                color = AppTheme.colors.secondary
            )
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
                    tint = AppTheme.colors.secondary,
                    contentDescription = "Menu",
                    modifier = Modifier.size(AppTheme.dimensions.iconSize)
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
                        tint = AppTheme.colors.secondary,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(AppTheme.dimensions.iconSize)
                    )
                }
            }
        },
        modifier = modifier
    )
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
                contentDescription = "Profile Picture",
                modifier = Modifier.size(98.dp)
            )
            h1Text(
                text = "Group Name"
            )
        }
        h1Text(
            "$10",
            fontSize = 100.sp
        )
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.secondary),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Money Request",
                color = AppTheme.colors.textSecondary,
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

    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            h1Text(
                text = "Public Requests",
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    tint = AppTheme.colors.secondary,
                    contentDescription = "Search List",
                    modifier = Modifier.size(AppTheme.dimensions.iconSize)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    tint = AppTheme.colors.secondary,
                    contentDescription = "Filter List",
                    modifier = Modifier.size(AppTheme.dimensions.iconSize)
                )
            }
        }
        PublicRequestsList(content = sampleList)
    }
}

@Composable
fun PublicRequestsList(content: Map<String, List<String>>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(content.keys.toList()) {
                _: Int, filterGroup: String ->
            h1Text(
                text = filterGroup,
                modifier = Modifier.padding(start = AppTheme.dimensions.paddingExtraLarge)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                content[filterGroup]!!.forEach {
                        request ->
                    h1Text(
                        text = request,
                    )
                }
            }
        }
    }
}
