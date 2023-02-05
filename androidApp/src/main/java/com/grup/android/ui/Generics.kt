package com.grup.android.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.UserInfo

@Composable
fun h1Text(
    text: String,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
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
    sideContent: @Composable () -> Unit,
    onClick: () -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(end = 5.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraSmall)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(iconSize)
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
            caption(text = "This is a description")
        }
    },
    sideContent: @Composable (UserInfo) -> Unit = {
        Text(text = "$${it.userBalance}")
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
