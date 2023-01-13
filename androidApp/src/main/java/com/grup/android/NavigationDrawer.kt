package com.grup.android

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.android.ui.apptheme.AppTheme

@Composable
fun DrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
            .background(AppTheme.colors.secondary),
        contentAlignment = Alignment.TopStart
    ) {
        Text(text = "Groups", fontSize = 40.sp, color = AppTheme.colors.onPrimary)
    }
}

@Composable
fun DrawerBody(
    items: List<GroupItem>,
    itemTextStyle: TextStyle = TextStyle(fontSize = 25.sp),
    onItemClick: (GroupItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(AppTheme.colors.secondary)
    )
    {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(20.dp)
                    .background(AppTheme.colors.secondary)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = item.groupName,
                    style = itemTextStyle,
                    color = AppTheme.colors.onPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun DrawerSettings(
    items: List<MenuItem>,
    itemTextStyle: TextStyle = TextStyle(fontSize = 15.sp),
    onItemClick: (MenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .background(AppTheme.colors.secondary),
        verticalArrangement = Arrangement.Bottom)
    {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(13.dp)
                    .background(AppTheme.colors.secondary)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    tint = AppTheme.colors.onSecondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    style = itemTextStyle,
                    color = AppTheme.colors.onPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}