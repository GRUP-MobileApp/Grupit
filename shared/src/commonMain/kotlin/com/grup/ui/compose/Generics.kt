package com.grup.ui.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.*
import com.grup.ui.apptheme.AppTheme
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.other.asMoneyAmount
import com.grup.other.isoFullDate
import com.grup.other.profilePicturePainter
import com.grup.ui.models.TransactionActivity

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f

@Composable
internal fun H1Text(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        color = color,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        modifier = modifier
    )
}

@Composable
internal fun H1Text(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        color = color,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        modifier = modifier
    )
}

@Composable
fun AutoSizingH1Text(
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
internal fun Caption(
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
internal fun SmallIcon(
    imageVector: ImageVector,
    contentDescription: String,
    iconSize: Dp = AppTheme.dimensions.smallIconSize,
    tint: Color = AppTheme.colors.onSecondary
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier.size(iconSize)
    )
}

@Composable
internal fun H1ConfirmTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = 150.dp,
    height: Dp = 45.dp,
    fontSize: TextUnit = 20.sp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.colors.confirm,
            disabledBackgroundColor = AppTheme.colors.caption
        ),
        modifier = modifier
            .width(width.times(scale))
            .height(height.times(scale)),
        shape = AppTheme.shapes.circleShape,
        enabled = enabled,
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
internal fun AcceptRejectRow(
    acceptOnClick: () -> Unit,
    rejectOnClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxHeight()
    ) {
        AcceptCheckButton(acceptOnClick)
        RejectButton(rejectOnClick)
    }
}

@Composable
internal fun AcceptCheckButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(AppTheme.shapes.circleShape)
            .background(AppTheme.colors.caption)
            .clickable(onClick = onClick)
            .padding(AppTheme.dimensions.paddingSmall)
    ) {
        SmallIcon(
            imageVector = Icons.Default.Check,
            contentDescription = "Accept",
            tint = AppTheme.colors.confirm,
            iconSize = AppTheme.dimensions.smallButtonSize
        )
    }
}

@Composable
internal fun RejectButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(AppTheme.shapes.circleShape)
            .background(AppTheme.colors.caption)
            .clickable(onClick = onClick)
            .padding(AppTheme.dimensions.paddingSmall)
    ) {
        SmallIcon(
            imageVector = Icons.Default.Close,
            contentDescription = "Reject",
            tint = AppTheme.colors.deny,
            iconSize = AppTheme.dimensions.smallButtonSize
        )
    }
}

@Composable
internal fun ProfileIcon(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String = "Profile Picture",
    iconSize: Dp = 70.dp
) {
    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .clip(AppTheme.shapes.circleShape)
            .size(iconSize)
    )
}

@Composable
internal fun SimpleLazyListPage(
    pageName: String,
    onBackPress: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { H1Text(text = pageName, color = AppTheme.colors.onSecondary) },
                backgroundColor = AppTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            Modifier.background(AppTheme.colors.primary)
                        )
                    }
                }
            )
        },
        backgroundColor = AppTheme.colors.primary
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            content = content
        )
    }
}

@Composable
internal fun IconRowCard(
    modifier: Modifier = Modifier,
    painter: Painter = rememberVectorPainter(image = Icons.Default.Face),
    iconSize: Dp = 70.dp,
    mainContent: @Composable () -> Unit,
    sideContent: (@Composable () -> Unit)? = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(end = AppTheme.dimensions.paddingSmall)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = AppTheme.dimensions.spacing)
                .weight(1f, false)
        ) {
            ProfileIcon(
                painter = painter,
                iconSize = iconSize
            )
            mainContent()
        }
        if (sideContent != null) {
            sideContent()
        }
    }
}

@Composable
internal fun UserInfoRowCard(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    mainContent: @Composable ColumnScope.() -> Unit = {
        H1Text(text = userInfo.nickname!!)
    },
    sideContent: (@Composable () -> Unit)? = {
        MoneyAmount(
            moneyAmount = userInfo.userBalance,
            fontSize = 24.sp,
        )
    },
    iconSize: Dp = 50.dp
) {
    val pfpPainter = profilePicturePainter(userInfo.profilePictureURL)
    IconRowCard(
        painter = pfpPainter,
        mainContent = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                mainContent()
            }
        },
        sideContent = sideContent,
        iconSize = iconSize,
        modifier = modifier
    )
}

@Composable
internal fun TransactionActivityRowCard(
    modifier: Modifier = Modifier,
    transactionActivity: TransactionActivity
) {
    UserInfoRowCard(
        userInfo = transactionActivity.userInfo,
        mainContent = {
            Caption(
                text = when(transactionActivity.action) {
                    is DebtAction -> "Request"
                    is SettleAction -> "Settle"
                }
            )
            H1Text(text = transactionActivity.displayText(), fontSize = 18.sp)
        },
        sideContent = { },
        modifier = modifier.height(70.dp)
    )
}

@Composable
internal fun MoneyAmount(
    modifier: Modifier = Modifier,
    moneyAmount: Double,
    fontSize: TextUnit = 30.sp
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        H1Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontSize = fontSize)) {
                    withStyle(
                        SpanStyle(
                            fontSize = fontSize.times(0.5f),
                            baselineShift = BaselineShift(0.4f)
                        )
                    ) {
                        append(
                            moneyAmount
                                .asMoneyAmount()
                                .substring(0, if (moneyAmount >= 0) 1 else 2)
                        )
                    }
                    append(moneyAmount.asMoneyAmount().substring(if (moneyAmount >= 0) 1 else 2))
                }
            }
        )
    }
}

internal fun LazyListScope.recentActivityList(
    groupActivity: List<TransactionActivity>,
    transactionActivityOnClick: (TransactionActivity) -> Unit = {}
) {
//    private fun displayMultipleUsers(displayNames: List<String>): String {
//        return when(displayNames.size) {
//            1 -> displayNames[0]
//            2 -> "${displayNames[0]} and + ${displayNames[1]}"
//            3 -> "${displayNames[0]}, ${displayNames[1]}, and ${displayNames[2]}"
//            else ->
//                "${displayNames[0]}, ${displayNames[1]}, ${displayNames[2]}, " +
//                        "and ${displayNames.size - 3} others"
//        }
//    }
    item {
        H1Text(
            text = "Recent Transactions",
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
    groupActivity.groupBy {
        isoFullDate(it.date)
    }.let { groupActivityByDate ->
        groupActivityByDate.keys.forEach { date ->
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.dimensions.appPadding)
                ) {
                    Caption(text = date)
                }
            }
            items(groupActivityByDate[date]!!) { transactionActivity ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppTheme.dimensions.appPadding)
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                        .clickable { transactionActivityOnClick(transactionActivity) }
                        .padding(AppTheme.dimensions.rowCardPadding)
                ) {
                    TransactionActivityRowCard(transactionActivity = transactionActivity)
                }
            }
        }
    }
}

@Composable
internal fun RecentActivityList(
    modifier: Modifier = Modifier,
    groupActivity: List<TransactionActivity>
) {
    val groupActivityByDate: Map<String, List<TransactionActivity>> =
        groupActivity.groupBy { isoFullDate(it.date) }
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

internal data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
internal fun DrawerSettings(
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
internal fun UsernameSearchBar(
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
internal fun TransparentTextField(
    modifier: Modifier = Modifier,
    value: String,
    fontSize: TextUnit = TextUnit.Unspecified,
    textColor: Color = AppTheme.colors.onSecondary,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = textColor, fontSize = fontSize),
        singleLine = true,
        decorationBox = { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                innerTextField = innerTextField,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource  = remember { MutableInteractionSource() }
            )
        },
        modifier = modifier.width(IntrinsicSize.Min)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun BackPressModalBottomSheetLayout(
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
        content()
    }
}
