package com.grup.android.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import com.grup.android.*
import com.grup.android.transaction.TransactionActivity
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.UserInfo
import kotlinx.coroutines.launch

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f

@Composable
fun H1Text(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        color = color,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
        fontWeight = fontWeight,
        modifier = modifier
    )
}

@Composable
fun H1Text(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1
) {
    var textSize: TextUnit by remember { mutableStateOf(fontSize) }
    var textLength: Int by remember { mutableStateOf(text.length) }

    Text(
        modifier = modifier,
        text = text,
        color = color,
        style = AppTheme.typography.h1,
        fontSize = textSize,
        fontWeight = fontWeight,
        softWrap = false,
        maxLines = maxLines,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                textSize = textSize.times(TEXT_SCALE_REDUCTION_INTERVAL)
            } else if(text.length < textLength) {
                textSize = minOf(
                    textSize.div(TEXT_SCALE_REDUCTION_INTERVAL),
                    fontSize
                ) { font1, font2 ->
                    (font1.value - font2.value).toInt()
                }
            }
            textLength = text.length
        }
    )
}

@Composable
fun Caption(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onPrimary,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
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
        tint = AppTheme.colors.onSecondary,
        modifier = modifier.size(AppTheme.dimensions.iconSize)
    )
}

@Composable
fun H1ConfirmTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = 150.dp,
    height: Dp = 45.dp,
    fontSize: TextUnit = 20.sp,
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
        modifier = modifier
            .width(width.times(scale))
            .height(height.times(scale)),
        shape = AppTheme.shapes.CircleShape,
        onClick = onClick
    ) {
        H1Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = fontSize.times(scale),
            color = AppTheme.colors.onSecondary,
        )
    }
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
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Face,
    iconSize: Dp = 70.dp,
    mainContent: @Composable () -> Unit,
    sideContent: @Composable () -> Unit = {},
    onClick: (() -> Unit)? = null
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)
        ) {
            ProfileIcon(
                imageVector = icon,
                iconSize = iconSize
            )
            mainContent()
        }
        Row(modifier = Modifier.padding(horizontal = AppTheme.dimensions.paddingSmall)) {
            sideContent()
        }
    }
}

@Composable
fun UserInfoRowCard(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    mainContent: @Composable (UserInfo) -> Unit = {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            H1Text(text = it.nickname!!)
        }
    },
    sideContent: @Composable (UserInfo) -> Unit = {
        MoneyAmount(
            moneyAmount = userInfo.userBalance,
            fontSize = 24.sp
        )
    },
    iconSize: Dp = 70.dp,
    onClick: (() -> Unit)? = null
) {
    IconRowCard(
        mainContent = { mainContent(userInfo) },
        sideContent = { sideContent(userInfo) },
        iconSize = iconSize,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun MoneyAmount(
    moneyAmount: Double,
    fontSize: TextUnit = 30.sp
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .height(IntrinsicSize.Min)
    ) {
        H1Text(
            text = moneyAmount.asMoneyAmount().substring(0, if (moneyAmount >= 0) 1 else 2),
            fontSize = fontSize.times(0.5),
            modifier = Modifier.padding(top = 4.dp)
        )
        H1Text(
            text = moneyAmount.asMoneyAmount().substring(if (moneyAmount >= 0) 1 else 2),
            fontSize = fontSize
        )
    }
}

fun LazyListScope.recentActivityList(
    groupActivity: List<TransactionActivity>,
    transactionActivityOnClick: (TransactionActivity) -> Unit = {}
) {
    item {
        H1Text(
            text = "Recent Transactions",
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
    }
    groupActivity.groupBy {
        isoDate(it.date)
    }.let { groupActivityByDate ->
        groupActivityByDate.keys.forEachIndexed { index, date ->
            item {
                if (index == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.dimensions.cardPadding)
                            .clip(AppTheme.shapes.listShape)
                            .background(AppTheme.colors.secondary)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.secondary)
                        .padding(horizontal = AppTheme.dimensions.cardPadding)
                        .padding(bottom = AppTheme.dimensions.spacing)
                ) {
                    Caption(text = date)
                }
            }
            items(groupActivityByDate[date]!!) { transactionActivity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.secondary)
                        .padding(horizontal = AppTheme.dimensions.cardPadding)
                        .padding(bottom = AppTheme.dimensions.spacing)
                        .clickable { transactionActivityOnClick(transactionActivity) }
                ) {
                    H1Text(
                        text = transactionActivity.displayText(),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun RecentActivityList(
    modifier: Modifier = Modifier,
    groupActivity: List<TransactionActivity>
) {
    val groupActivityByDate: Map<String, List<TransactionActivity>> =
        groupActivity.groupBy { isoDate(it.date) }
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier.fillMaxWidth()
    ) {
        H1Text(text = "Recent Transactions", fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(AppTheme.shapes.listShape)
                .background(AppTheme.colors.secondary)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppTheme.dimensions.cardPadding)
            ) {
                groupActivityByDate.keys.forEach { date ->
                    Caption(text = date)
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacingSmall),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        groupActivityByDate[date]!!.forEach { transactionActivity ->
                            H1Text(
                                text = transactionActivity.displayText(),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
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
        Text(text = "Groups", fontSize = 40.sp, color = AppTheme.colors.onSecondary)

        Spacer(modifier = Modifier.weight(1f))

        NotificationsButton(navigateNotificationsOnClick = navigateNotificationsOnClick)
    }
}

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun DrawerSettings(
    items: List<MenuItem>,
    itemTextStyle: TextStyle = TextStyle(fontSize = 15.sp)
) {
    LazyColumn(
        verticalArrangement = Arrangement.Bottom)
    {
        items(items) { menuItem ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = menuItem.onClick)
                    .padding(13.dp)
            ) {
                Icon(
                    imageVector = menuItem.icon,
                    contentDescription = menuItem.contentDescription,
                    tint = AppTheme.colors.onSecondary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = menuItem.title,
                    style = itemTextStyle,
                    color = AppTheme.colors.onSecondary,
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
                backgroundColor = AppTheme.colors.onSecondary,
                focusedIndicatorColor = border,
                unfocusedIndicatorColor = border,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TransparentTextField(
    modifier: Modifier = Modifier,
    value: String,
    textColor: Color = AppTheme.colors.onSecondary,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = textColor),
        singleLine = true,
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource  = remember { MutableInteractionSource() },
                label = { H1Text(text = "Message") }
            )
        },
        modifier = modifier.width(IntrinsicSize.Min)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BackPressModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = AppTheme.colors.primary,
    sheetContentColor: Color = AppTheme.colors.onSecondary,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    ModalBottomSheetLayout(
        sheetContent = sheetContent,
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor,
    ) {
        BackHandler(enabled = sheetState.isVisible) {
            scope.launch { sheetState.hide() }
        }
        content()
    }
}
