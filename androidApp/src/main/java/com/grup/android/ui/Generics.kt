package com.grup.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.android.GroupItem
import com.grup.android.MenuItem
import com.grup.android.NotificationsButton
import com.grup.android.asMoneyAmount
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.UserInfo

@Composable
fun h1Text(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
    )
}


@Composable
fun caption(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        style = AppTheme.typography.smallFont,
        fontSize = fontSize
    )
}

@Composable
fun SmallIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = AppTheme.colors.onPrimary,
        modifier = modifier.size(AppTheme.dimensions.iconSize)
    )
}

@Composable
fun ProfileIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String = "Profile Picture",
    iconSize: Dp = 70.dp
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(iconSize)
    )
}

@Composable
fun SmallIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = {  },
        modifier = Modifier
            .size(AppTheme.dimensions.borderIconSize)
            .shadow(
                elevation = AppTheme.dimensions.shadowElevationSize,
                shape = AppTheme.shapes.CircleShape,
                clip = false
            )
            .clip(AppTheme.shapes.CircleShape)
            .background(color = AppTheme.colors.caption)
            .border(
                border = BorderStroke(1.dp, AppTheme.colors.secondary),
                shape = AppTheme.shapes.CircleShape
            )
    ) {
        SmallIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier.clip(AppTheme.shapes.CircleShape)
        )
    }
}

@Composable
fun IconRowCard(
    icon: ImageVector = Icons.Default.Face,
    iconSize: Dp = 70.dp,
    mainContent: @Composable () -> Unit,
    sideContent: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 3.dp)
            .padding(end = 5.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)
        ) {
            ProfileIcon(
                imageVector = icon,
                iconSize = iconSize
            )
            mainContent()
        }
        sideContent()
    }
}

@Composable
fun UserInfoRowCard(
    userInfo: UserInfo,
    mainContent: @Composable (UserInfo) -> Unit = {
        Column(verticalArrangement = Arrangement.Center) {
            h1Text(text = it.nickname!!)
        }
    },
    sideContent: @Composable (UserInfo) -> Unit = {
        Text(text = it.userBalance.asMoneyAmount())
    },
    onClick: () -> Unit = {}
) {
    IconRowCard(
        mainContent = { mainContent(userInfo) },
        sideContent = { sideContent(userInfo) },
        onClick = onClick
    )
}

@Composable
fun DrawerHeader(
    navigateNotificationsOnClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        Text(text = "Groups", fontSize = 40.sp, color = AppTheme.colors.onPrimary)

        Spacer(modifier = Modifier.weight(1f))

        NotificationsButton(navigateNotificationsOnClick = navigateNotificationsOnClick)
    }
}

@Composable
fun DrawerBody(
    items: List<GroupItem>,
    itemTextStyle: TextStyle = TextStyle(fontSize = 25.sp),
    onItemClick: (GroupItem) -> Unit
) {
    LazyColumn {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onItemClick(item)
                    }
                    .padding(20.dp)
            ) {
                SmallIcon(
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
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.contentDescription,
                    tint = AppTheme.colors.onPrimary
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

@Composable
fun UsernameSearchBar(
    modifier: Modifier = Modifier,
    usernameSearchQuery: String,
    onQueryChange: (String) -> Unit,
    border: Color = Color.Transparent
) {
    Row(modifier = modifier) {
        TextField(
            value = usernameSearchQuery,
            onValueChange = onQueryChange,
            label = { Text("Search", color = AppTheme.colors.primary) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "SearchIcon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.secondary),
            colors = TextFieldDefaults.textFieldColors(
                textColor = AppTheme.colors.primary,
                disabledTextColor = Color.Transparent,
                backgroundColor = AppTheme.colors.onPrimary,
                focusedIndicatorColor = border,
                unfocusedIndicatorColor = border,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}
