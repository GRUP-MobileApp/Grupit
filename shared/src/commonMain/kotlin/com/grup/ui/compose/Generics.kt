package com.grup.ui.compose

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.*
import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.LoginViewModel
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlin.math.roundToInt

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f

@Composable
internal fun H1Text(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
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
internal fun H1Header(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = AppTheme.typography.headerFont,
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
internal fun AutoSizingH1Text(
    modifier: Modifier = Modifier,
    textContent: @Composable (TextUnit) -> AnnotatedString,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit,
    fontWeight: FontWeight? = null,
    maxLines: Int = 1
) {
    var textSize: TextUnit by remember { mutableStateOf(fontSize) }
    val text: AnnotatedString = textContent(textSize)
    var textLength: Int by remember { mutableStateOf(text.length) }

    Text(
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
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun InvisibleTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    indicatorColor: Color = AppTheme.colors.primary,
    interactionSource: InteractionSource = remember { MutableInteractionSource() },
) {
    val colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        backgroundColor = AppTheme.colors.primary,
        focusedIndicatorColor = indicatorColor,
        unfocusedIndicatorColor = indicatorColor,
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .width(IntrinsicSize.Min)
            .indicatorLine(
                enabled = true,
                isError = false,
                interactionSource = interactionSource,
                colors = colors
            ),
        decorationBox = @Composable { innerTextField ->
            TextFieldDefaults.TextFieldDecorationBox(
                value = value,
                visualTransformation = VisualTransformation.None,
                innerTextField = innerTextField,
                placeholder = placeholder?.let {
                    {
                        H1Text(
                            text = it,
                            fontSize = fontSize,
                            color = AppTheme.colors.onPrimary,
                            maxLines = 1,
                        )
                    }
                },
                singleLine = true,
                enabled = true,
                interactionSource = interactionSource,
                colors = colors,
                contentPadding = PaddingValues(AppTheme.dimensions.paddingSmall)
            )
        },
        textStyle = TextStyle(
            color = AppTheme.colors.onSecondary,
            fontFamily = AppTheme.typography.h1.fontFamily,
            fontSize = fontSize
        ),
        singleLine = true,
        cursorBrush = SolidColor(Color.White)
    )
}

@Composable
internal fun Caption(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onPrimary,
    fontSize: TextUnit = AppTheme.typography.smallFont
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = AppTheme.typography.caption,
        fontSize = fontSize,
        maxLines = 1
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
internal expect fun GoogleSignInButton(
    loginResult: LoginViewModel.LoginResult,
    googleSignInManager: GoogleSignInManager,
    signInCallback: (String) -> Unit
)

@Composable
internal fun H1ConfirmTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = 140.dp,
    height: Dp = 42.dp,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
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
internal fun H1DenyTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = 140.dp,
    height: Dp = 42.dp,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppTheme.colors.deny,
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
internal fun H1ErrorTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = 140.dp,
    height: Dp = 42.dp,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.secondary),
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
            color = AppTheme.colors.error,
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
    user: User,
    iconSize: Dp = 70.dp
) {
    val painterResource: Resource<Painter> =
        asyncPainterResource(user.profilePictureURL) {
            // CoroutineContext to be used while loading the image.
            coroutineContext = Job() + Dispatchers.IO
        }
    KamelImage(
        resource = painterResource,
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(AppTheme.shapes.circleShape)
            .size(iconSize)
            .then(modifier)
    )
}

@Composable
internal fun GroupIcon(
    modifier: Modifier = Modifier,
    group: Group,
    iconSize: Dp = 45.dp
) {
    Image(
        imageVector = Icons.Default.Person,
        contentDescription = group.groupName,
        modifier = modifier
            .size(iconSize)
    )
}

@Composable
internal fun BackPressScaffold(
    onBackPress: () -> Unit,
    title: String? = null,
    actions: @Composable RowScope.() -> Unit = { },
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    title?.let { title ->
                        H1Header(text = title, color = AppTheme.colors.onSecondary)
                    }
                },
                actions = actions,
                backgroundColor = AppTheme.colors.primary,
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
        backgroundColor = AppTheme.colors.primary,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        content(padding)
    }
}

@Composable
internal fun SimpleLazyListPage(
    pageName: String,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(0.dp),
    contentPadding: PaddingValues = PaddingValues(AppTheme.dimensions.appPadding),
    content: LazyListScope.() -> Unit
) {
    Scaffold(backgroundColor = AppTheme.colors.primary) { padding ->
        LazyColumn(
            verticalArrangement = verticalArrangement,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                H1Header(
                    text = pageName,
                    color = AppTheme.colors.onSecondary,
                    modifier = Modifier.fillMaxWidth(0.95f)
                )
            }
            content()
        }
    }
}

@Composable
internal fun IconRowCard(
    modifier: Modifier = Modifier,
    mainContent: @Composable () -> Unit,
    sideContent: (@Composable () -> Unit)? = {},
    iconContent: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.rowCardPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, false)
        ) {
            iconContent()
            mainContent()
        }
        if (sideContent != null) {
            sideContent()
        }
    }
}

@Composable
internal fun TransactionRecordRowCard(
    modifier: Modifier = Modifier,
    transactionRecord: TransactionRecord,
    moneyAmountTextColor: Color
) {
    UserRowCard(
        user = transactionRecord.userInfo.user,
        iconSize = 50.dp,
        mainContent = {
            H1Text(text = transactionRecord.userInfo.user.displayName)
            Caption(
                text = with(transactionRecord.status) {
                    if (this is TransactionRecord.Status.Accepted)
                        "Accepted on ${isoDate(date)}"
                    else
                        status
                }
            )
        },
        sideContent = {
            MoneyAmount(
                moneyAmount = transactionRecord.balanceChange,
                fontSize = 24.sp,
                color = moneyAmountTextColor
            )
        },
        modifier = modifier
    )
}

@Composable
internal fun UserInfoRowCard(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    coloredMoneyAmount: Boolean = true,
    mainContent: @Composable ColumnScope.() -> Unit = {
        H1Text(text = userInfo.user.displayName)
    },
    iconSize: Dp = 50.dp,
) {
    UserRowCard(
        user = userInfo.user,
        mainContent = mainContent,
        sideContent = {
            MoneyAmount(
                moneyAmount = userInfo.userBalance,
                fontSize = 24.sp,
                color = if (coloredMoneyAmount) {
                    if (userInfo.userBalance >= 0) AppTheme.colors.confirm else AppTheme.colors.deny
                } else {
                    AppTheme.colors.onSecondary
                }
            )
        },
        iconSize = iconSize,
        modifier = modifier
    )
}

@Composable
internal fun UserRowCard(
    modifier: Modifier = Modifier,
    user: User,
    mainContent: @Composable ColumnScope.() -> Unit = {
        H1Text(text = user.displayName)
    },
    sideContent: (@Composable ColumnScope.() -> Unit)? = null,
    iconSize: Dp = 50.dp,
    iconContent: @Composable () -> Unit = {
        ProfileIcon(
            user = user,
            iconSize = iconSize
        )
    }
) {
    IconRowCard(
        mainContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                mainContent()
            }
        },
        sideContent = sideContent?.let { content ->
            {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    content()
                }
            }
        },
        iconContent = iconContent,
        modifier = modifier
    )
}

@Composable
internal fun TransactionActivityRowCard(
    modifier: Modifier = Modifier,
    transactionActivity: TransactionActivity
) {
    UserRowCard(
        user = transactionActivity.userInfo.user,
        mainContent = {
            Caption(
                text =
                "${transactionActivity.activityName} at ${isoTime(transactionActivity.date)}",
                fontSize = AppTheme.typography.tinyFont,
                modifier = Modifier.padding(bottom = AppTheme.dimensions.spacingSmall)
            )
            H1Text(text = transactionActivity.userInfo.user.displayName)
            H1Text(
                text = transactionActivity.displayText(),
                fontSize = AppTheme.typography.smallFont,
            )
        },
        sideContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                MoneyAmount(moneyAmount = transactionActivity.amount, fontSize = 20.sp)
            }
        },
        modifier = modifier.height(AppTheme.dimensions.itemRowCardHeight)
    )
}

@Composable
internal fun GroupRowCard(
    modifier: Modifier = Modifier,
    group: Group,
    userBalance: Double,
    membersCount: Int
) {
    IconRowCard(
        mainContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                H1Text(text = group.groupName)
                Caption(
                    text = "$membersCount " + when(membersCount) {
                        1 -> {
                            "person"
                        }
                        else -> {
                            "people"
                        }
                    }
                )
            }
        },
        sideContent = {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                MoneyAmount(
                    moneyAmount = userBalance,
                    color = if (userBalance >= 0) AppTheme.colors.confirm
                            else AppTheme.colors.deny,
                    fontSize = 24.sp
                )
            }
        },
        iconContent = { GroupIcon(group = group) },
        modifier = modifier
    )
}

@Composable
internal fun MoneyAmount(
    modifier: Modifier = Modifier,
    moneyAmount: Double,
    fontSize: TextUnit = 30.sp,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = modifier
            .height(IntrinsicSize.Min)
    ) {
        AutoSizingH1Text(
            textContent = { fontSize ->
                buildAnnotatedString {
                    moneyAmount.asCurrencySymbolAndMoneyAmount().let { (symbol, moneyAmount) ->
                        withStyle(
                            SpanStyle(
                                fontSize = fontSize.times(0.5f),
                                fontWeight = fontWeight,
                                color = color,
                                baselineShift = BaselineShift(0.4f)
                            )
                        ) {
                            append(symbol)
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = fontSize,
                                fontWeight = fontWeight,
                                color = color
                            )
                        ) {
                            append(moneyAmount)
                        }
                    }
                }
            },
            fontSize = fontSize
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun KeyPadBottomSheet(
    state: ModalBottomSheetState,
    initialMoneyAmount: Double,
    maxMoneyAmount: Double = Double.MAX_VALUE,
    isEnabled: (Double) -> Boolean = { true },
    onClick: (Double) -> Unit,
    onBackPress: () -> Unit,
    content: @Composable () -> Unit
) {
    var moneyAmount: String by remember { mutableStateOf("0") }
    LaunchedEffect(state.isVisible) {
        moneyAmount = if (initialMoneyAmount % 1 == 0.0)
            initialMoneyAmount.roundToInt().toString()
        else
            initialMoneyAmount.asPureMoneyAmount()
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            KeyPadScreenLayout(
                moneyAmount = moneyAmount,
                onMoneyAmountChange = { newActionAmount ->
                    moneyAmount = if (newActionAmount.toDouble() > maxMoneyAmount) {
                        maxMoneyAmount.toString().trimEnd('0')
                    } else {
                        newActionAmount
                    }
                },
                confirmButton = {
                    val actualMoneyAmount = moneyAmount.toDouble()
                    H1ConfirmTextButton(
                        text = "Confirm",
                        enabled = isEnabled(actualMoneyAmount),
                        onClick = {
                            onClick(actualMoneyAmount)
                            onBackPress()
                        }
                    )
                },
                onBackPress = onBackPress
            )
        },
        content = content
    )
}

@Composable
internal fun KeyPadScreenLayout(
    moneyAmount: String,
    onMoneyAmountChange: (String) -> Unit,
    message: String? = null,
    onMessageChange: ((String) -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    moneyAmountFontSize: TextUnit = 98.sp,
    onBackPress: () -> Unit,
) {
    BackPressScaffold(onBackPress = onBackPress) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f)
                ) {
                    KeyPadMoneyAmount(moneyAmount = moneyAmount, fontSize = moneyAmountFontSize)
                    message?.let { message ->
                        Spacer(modifier = Modifier.weight(1f))
                        TransparentTextField(
                            value = message,
                            placeholder = "What is this for?",
                            onValueChange = {
                                onMessageChange!!(it.take(50))
                            },
                            fontSize = 24.sp,
                            modifier = Modifier.height(IntrinsicSize.Max)
                        )
                    }
                }
            }
            KeyPad(
                modifier = Modifier.padding(top = AppTheme.dimensions.spacing),
                moneyAmount = moneyAmount,
                onMoneyAmountChange = onMoneyAmountChange
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = AppTheme.dimensions.cardPadding)
            ) {
                confirmButton()
            }
        }
    }
}

@Composable
internal fun KeyPadMoneyAmount(
    moneyAmount: String,
    fontSize: TextUnit
) {
    AutoSizingH1Text(
        textContent = { moneyAmountFontSize ->
            buildAnnotatedString {
                withStyle(SpanStyle(color = AppTheme.colors.onSecondary)) {
                    withStyle(
                        SpanStyle(
                            fontSize = moneyAmountFontSize.times(0.5f),
                            baselineShift = BaselineShift(0.4f)
                        )
                    ) {
                        append(getCurrencySymbol())
                    }
                    withStyle(SpanStyle(fontSize = moneyAmountFontSize)) {
                        append(moneyAmount)
                    }
                }
                moneyAmount.indexOf('.').let { index ->
                    if (index != -1) {
                        withStyle(
                            SpanStyle(
                                color = AppTheme.colors.caption,
                                fontSize = moneyAmountFontSize
                            )
                        ) {
                            repeat(2 - (moneyAmount.length - (index + 1))) {
                                append('0')
                            }
                        }
                    }
                }
            }
        },
        fontSize = fontSize,
    )
}

@Composable
internal fun KeyPad(
    modifier: Modifier = Modifier,
    moneyAmount: String,
    onMoneyAmountChange: (String) -> Unit
) {
    val keys: List<List<Char>> = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf('.', '0', '<')
    )
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight(0.4f)
            .fillMaxWidth()
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(80.dp)
                            .height(50.dp)
                            .background(AppTheme.colors.primary)
                            .clickable {
                                when(key) {
                                    '.' -> {
                                        if (!moneyAmount.contains('.')) {
                                            onMoneyAmountChange(moneyAmount + key)
                                        }
                                    }
                                    '<' -> {
                                        onMoneyAmountChange(
                                            if (moneyAmount.length > 1) {
                                                moneyAmount.substring(0, moneyAmount.length - 1)
                                            } else {
                                                "0"
                                            }
                                        )
                                    }
                                    else -> {
                                        if (key.isDigit() &&
                                            (moneyAmount.length < 3 ||
                                                    moneyAmount[moneyAmount.length - 3] != '.')) {
                                            onMoneyAmountChange(
                                                if (moneyAmount == "0") {
                                                    key.toString()
                                                } else {
                                                    moneyAmount + key
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                    ) {
                        H1Text(
                            text = key.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = AppTheme.colors.onSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun UserInfoAmountsList(
    userInfoMoneyAmounts: Map<UserInfo, Double>,
    userInfoHasSetAmount: (UserInfo) -> Boolean,
    userInfoAmountOnClick: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(userInfoMoneyAmounts.toList()) { (userInfo, moneyAmount) ->
                UserRowCard(
                    user = userInfo.user,
                    sideContent = {
                        MoneyAmount(
                            moneyAmount = moneyAmount,
                            color =
                            if (userInfoHasSetAmount(userInfo))
                                AppTheme.colors.onSecondary
                            else
                                AppTheme.colors.caption,
                            fontSize = 26.sp,
                            modifier = Modifier.clickable {
                                userInfoAmountOnClick(userInfo)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
internal fun UsernameSearchBar(
    modifier: Modifier = Modifier,
    usernameSearchQuery: String,
    labelText: String = "Search",
    onQueryChange: (String) -> Unit,
    border: Color = Color.Transparent
) {
    Row(modifier = modifier) {
        TextField(
            value = usernameSearchQuery,
            onValueChange = onQueryChange,
            label = { Text(text = labelText, color = AppTheme.colors.primary) },
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
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    fontSize: TextUnit = TextUnit.Unspecified,
    textColor: Color = AppTheme.colors.onSecondary,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    var isFocused: Boolean by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (value.isBlank() && placeholder.isNotBlank() && !isFocused) {
            Caption(
                text = placeholder,
                fontSize = fontSize,
                modifier = Modifier.clickable { focusRequester.requestFocus() }
            )
        }
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
            keyboardOptions = keyboardOptions,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ModalBottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier.fillMaxSize(),
    sheetState: ModalBottomSheetState,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = ModalBottomSheetDefaults.Elevation,
    sheetBackgroundColor: Color = AppTheme.colors.primary,
    sheetContentColor: Color = AppTheme.colors.onSecondary,
    scrimColor: Color = ModalBottomSheetDefaults.scrimColor,
    content: @Composable () -> Unit
) {
    androidx.compose.material.ModalBottomSheetLayout(
        sheetContent = sheetContent,
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetElevation = sheetElevation,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        scrimColor = scrimColor,
        content = content
    )
}
