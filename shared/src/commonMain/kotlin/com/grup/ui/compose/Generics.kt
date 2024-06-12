package com.grup.ui.compose

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.library.MR
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.platform.signin.AuthManager
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.apptheme.venmo
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.LoginViewModel
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TEXT_SCALE_REDUCTION_INTERVAL = 0.9f

@Composable
internal fun H1Text(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onSecondary,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    fontWeight: FontWeight? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        text = text,
        color = color,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
        fontWeight = fontWeight,
        maxLines = maxLines,
        overflow = overflow,
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

@Composable
internal fun ProfileTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    fontSize: TextUnit = AppTheme.typography.textFieldFont,
    error: String? = null,
    showCheck: Boolean = false,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                decorationBox = { innerTextField ->
                    Box {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            H1Text(
                                text = placeholder,
                                fontSize = fontSize,
                                color = AppTheme.colors.onPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Visible
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = TextStyle(
                    color = AppTheme.colors.onSecondary,
                    fontFamily = AppTheme.typography.h1.fontFamily,
                    fontSize = fontSize
                ),
                singleLine = true,
                enabled = enabled,
                cursorBrush = SolidColor(Color.White),
                interactionSource = interactionSource
            )
            if (showCheck) {
                Spacer(modifier = Modifier.width(AppTheme.dimensions.spacingSmall))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Check",
                    tint = AppTheme.colors.confirm,
                    modifier = Modifier.size(AppTheme.dimensions.tinyIconSize)
                )
            }
        }
        Caption(text = error ?: "", color = AppTheme.colors.deny)
    }
}

@Composable
internal fun Caption(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.onPrimary,
    fontSize: TextUnit = AppTheme.typography.smallFont,
    maxLines: Int = 1,
    overflow: TextOverflow = TextOverflow.Ellipsis
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        style = AppTheme.typography.caption,
        fontSize = fontSize,
        maxLines = maxLines,
        overflow = overflow
    )
}

@Composable
internal fun SmallIcon(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    iconSize: Dp = AppTheme.dimensions.smallIconSize,
    tint: Color = AppTheme.colors.onSecondary
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(iconSize)
            .clip(AppTheme.shapes.circleShape)
            .then(modifier)
    )
}

@Composable
internal fun H1ConfirmTextButton(
    modifier: Modifier = Modifier,
    text: String,
    scale: Float = 1f,
    width: Dp = AppTheme.dimensions.textButtonWidth,
    height: Dp = AppTheme.dimensions.textButtonHeight,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    color: Color = AppTheme.colors.confirm,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color,
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
    width: Dp = AppTheme.dimensions.textButtonWidth,
    height: Dp = AppTheme.dimensions.textButtonHeight,
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
    width: Dp = AppTheme.dimensions.textButtonWidth,
    height: Dp = AppTheme.dimensions.textButtonHeight,
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
internal fun AcceptRejectButtons(
    acceptOnClick: () -> Unit,
    rejectOnClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight().width(IntrinsicSize.Min)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.caption)
                .clickable(onClick = acceptOnClick)
                .padding(AppTheme.dimensions.paddingSmall)
        ) {
            H1Text(
                text = "Accept",
                fontSize = AppTheme.typography.smallFont,
                color = AppTheme.colors.confirm
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.caption)
                .clickable(onClick = rejectOnClick)
                .padding(AppTheme.dimensions.paddingSmall)
        ) {
            H1Text(
                text = "Reject",
                fontSize = AppTheme.typography.smallFont,
                color = AppTheme.colors.deny
            )
        }
    }
}

@Composable
internal fun AcceptRejectRow(
    modifier: Modifier = Modifier,
    acceptOnClick: () -> Unit,
    rejectOnClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().then(modifier)
    ) {
        H1ConfirmTextButton(
            text = "Accept",
            onClick = acceptOnClick
        )
        Spacer(modifier = Modifier.width(AppTheme.dimensions.appPadding))
        H1DenyTextButton(
            text = "Reject",
            onClick = rejectOnClick
        )
    }
}

@Composable
internal fun GoogleSignInButton(
    loginResult: LoginViewModel.LoginResult,
    signIn: () -> Unit
) {
    Button(
        onClick = {
            if (loginResult !is LoginViewModel.LoginResult.PendingLogin) {
                signIn()
            }
        },
        shape = AppTheme.shapes.medium,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4285F4),
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.dimensions.signInButtonHeight)
            .padding(horizontal = AppTheme.dimensions.paddingLarge),
    ) {
        if (loginResult.isPendingLoginAuthProvider(AuthManager.AuthProvider.Google)) {
            LoadingSpinner()
        } else {
            Image(
                painter = painterResource(MR.images.google_icon),
                contentDescription = "Google Sign In",
                modifier = Modifier
                    .size(AppTheme.dimensions.smallIconSize)
                    .clip(AppTheme.shapes.circleShape)
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.spacing))
            H1Text(text = "Sign in with Google")
        }
    }
}

@Composable
internal fun AppleSignInButton(
    loginResult: LoginViewModel.LoginResult,
    signIn: () -> Unit
) {
    Button(
        onClick = {
            if (loginResult !is LoginViewModel.LoginResult.PendingLogin) {
                signIn()
            }
        },
        shape = AppTheme.shapes.medium,
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(AppTheme.dimensions.signInButtonHeight)
            .padding(horizontal = AppTheme.dimensions.paddingLarge),
    ) {
        if (loginResult.isPendingLoginAuthProvider(AuthManager.AuthProvider.Apple)) {
            LoadingSpinner()
        } else {

            Image(
                painter = painterResource(MR.images.apple_icon),
                contentDescription = "Apple Sign In",
                modifier = Modifier
                    .size(AppTheme.dimensions.smallIconSize)
            )
            Spacer(modifier = Modifier.width(AppTheme.dimensions.spacing))
            H1Text(text = "Sign in with Apple")
        }
    }
}

@Composable
fun LoadingSpinner() {
    CircularProgressIndicator()
}

@Composable
internal fun Modifier.shimmer() = this.run {
    composed {
        var size by remember { mutableStateOf(IntSize.Zero) }
        val transition = rememberInfiniteTransition()
        val startOffsetX by transition.animateFloat(
            initialValue = -2 * size.width.toFloat(),
            targetValue = 2 * size.width.toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(1000)
            )
        )

        background(
            brush = Brush.linearGradient(
                colors = listOf(
                    AppTheme.colors.secondary,
                    AppTheme.colors.caption,
                    AppTheme.colors.secondary,
                ),
                start = Offset(startOffsetX, 0f),
                end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
            )
        ).onGloballyPositioned { size = it.size }
    }
}

@Composable
internal fun ProfileIcon(
    modifier: Modifier = Modifier,
    user: User,
    iconSize: Dp = AppTheme.dimensions.iconSize
) {
    user.profilePictureURL?.let { url ->
        val painterResource: Resource<Painter> =
            asyncPainterResource(data = Url(url)) {
                // CoroutineContext to be used while loading the image.
                coroutineContext = Job() + Dispatchers.IO
            }
        KamelImage(
            resource = painterResource,
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            onLoading = {
                Box(
                    modifier = Modifier
                        .clip(AppTheme.shapes.circleShape)
                        .size(iconSize)
                        .shimmer()
                        .then(modifier)
                )
            },
            modifier = Modifier
                .clip(AppTheme.shapes.circleShape)
                .size(iconSize)
                .then(modifier)
        )
    } ?: Image(
        imageVector = Icons.Default.Person,
        contentDescription = "Profile Picture",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .clip(AppTheme.shapes.circleShape)
            .size(iconSize)
            .then(modifier)
    )
}

@Composable
internal fun UserCaption(user: User) {
    if (user.venmoUsername != null) {
        Caption(text = "@${user.venmoUsername}", color = venmo)
    } else {
        Caption(text = user.username)
    }
}

@Composable
internal fun GroupIcon(
    modifier: Modifier = Modifier,
    group: Group,
    iconSize: Dp = AppTheme.dimensions.iconSize
) {
    Image(
        imageVector = Icons.Default.Person,
        contentDescription = group.groupName,
        modifier = Modifier
            .size(iconSize)
            .clip(AppTheme.shapes.small)
            .then(modifier)
    )
}

@Composable
internal fun BackPressScaffold(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onBackPress: () -> Unit,
    title: @Composable () -> Unit = { },
    actions: @Composable RowScope.() -> Unit = { },
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = title,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PagerArrowRow(
    pagerState: PagerState,
    onClickNext: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    val isFinalPage: Boolean = pagerState.currentPage == pagerState.pageCount - 1

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom,
            modifier = modifier.fillMaxWidth(0.9f)
        ) {
            if (pagerState.currentPage != 0) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                ) { H1Text(text = "< Back") }
            }
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = { onClickNext(pagerState.currentPage) }) {
                H1Text(text = if (isFinalPage) "Finish >" else "Next >")
            }

        }
        Row {
            repeat(pagerState.pageCount) { iteration ->
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(AppTheme.shapes.circleShape)
                        .background(
                            if (pagerState.currentPage == iteration) Color.DarkGray
                            else Color.LightGray
                        )
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
internal fun IconRowCard(
    modifier: Modifier = Modifier,
    mainContent: @Composable RowScope.() -> Unit,
    sideContent: (@Composable () -> Unit)? = {},
    iconContent: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.rowCardPadding),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            iconContent()
            mainContent()
        }
        Spacer(modifier = Modifier.width(AppTheme.dimensions.spacing))
        sideContent?.let { it() }
    }
}

@Composable
internal fun UserInfoRowCard(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    coloredMoneyAmount: Boolean = true,
    mainContent: @Composable ColumnScope.() -> Unit = {
        H1Text(text = userInfo.user.displayName)
        UserCaption(user = userInfo.user)
    }
) {
    UserRowCard(
        user = userInfo.user,
        mainContent = mainContent,
        sideContent = {
            MoneyAmount(
                moneyAmount = userInfo.userBalance,
                color = if (coloredMoneyAmount) {
                    if (userInfo.userBalance >= 0) AppTheme.colors.confirm else AppTheme.colors.deny
                } else {
                    AppTheme.colors.onSecondary
                }
            )
        },
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
    iconSize: Dp = AppTheme.dimensions.iconSize,
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
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                horizontalAlignment = Alignment.Start
            ) {
                mainContent()
            }
        },
        sideContent = sideContent?.let { content ->
            {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End,
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
                fontSize = AppTheme.typography.tinyFont
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
                MoneyAmount(moneyAmount = transactionActivity.amount)
            }
        },
        modifier = modifier.height(AppTheme.dimensions.itemRowCardHeight)
    )
}

@Composable
internal fun GroupRowCard(
    modifier: Modifier = Modifier,
    userInfo: UserInfo,
    color: Color,
    membersCount: Int
) {
    IconRowCard(
        mainContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                horizontalAlignment = Alignment.Start
            ) {
                H1Text(text = userInfo.group.groupName)
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
                    moneyAmount = userInfo.userBalance,
                    color = if (userInfo.userBalance >= 0) AppTheme.colors.confirm
                            else AppTheme.colors.deny
                )
            }
        },
        iconContent = { GroupIcon(group = userInfo.group, modifier = Modifier.background(color)) },
        modifier = modifier
    )
}

@Composable
internal fun MoneyAmount(
    modifier: Modifier = Modifier,
    moneyAmount: Double,
    fontSize: TextUnit = AppTheme.typography.moneyAmountFont,
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

@Composable
internal fun KeyPadScreenLayout(
    moneyAmount: String,
    onMoneyAmountChange: (String) -> Unit,
    message: String? = null,
    onMessageChange: ((String) -> Unit)? = null,
    confirmButton: @Composable () -> Unit,
    moneyAmountFontSize: TextUnit = 98.sp,
    onBackPress: () -> Unit
) {
    BackPressScaffold(onBackPress = onBackPress) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                KeyPadMoneyAmount(moneyAmount = moneyAmount, fontSize = moneyAmountFontSize)
            }
            message?.let { message ->
                TransparentTextField(
                    value = message,
                    placeholder = "What is this for?",
                    onValueChange = { onMessageChange?.invoke(it.take(50)) },
                    modifier = Modifier.height(IntrinsicSize.Max)
                )
            }
            KeyPad(moneyAmount = moneyAmount, onMoneyAmountChange = onMoneyAmountChange)
            confirmButton()
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
                            .clip(AppTheme.shapes.circleShape)
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
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        items(userInfoMoneyAmounts.toList()) { (userInfo, moneyAmount) ->
            UserRowCard(
                user = userInfo.user,
                mainContent = {
                    H1Text(text = userInfo.user.displayName)
                    UserCaption(user = userInfo.user)
                },
                sideContent = {
                    MoneyAmount(
                        moneyAmount = moneyAmount,
                        color =
                        if (userInfoHasSetAmount(userInfo))
                            AppTheme.colors.onSecondary
                        else
                            AppTheme.colors.caption,
                        modifier = Modifier.clickable {
                            userInfoAmountOnClick(userInfo)
                        }
                    )
                }
            )
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
    TextField(
        value = usernameSearchQuery,
        onValueChange = onQueryChange,
        placeholder = { H1Text(text = labelText, color = AppTheme.colors.secondary) },
        singleLine = true,
        textStyle = AppTheme.typography.h1,
        shape = AppTheme.shapes.medium,
        trailingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "SearchIcon")
        },
        modifier = modifier
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

@Composable
internal fun TransparentTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    fontSize: TextUnit = AppTheme.typography.textFieldFont,
    textColor: Color = AppTheme.colors.onSecondary,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(color = textColor, fontSize = fontSize),
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.background(AppTheme.colors.primary)) {
                if (value.isEmpty() && placeholder.isNotEmpty()) {
                    Caption(
                        text = placeholder,
                        fontSize = fontSize,
                        overflow = TextOverflow.Visible
                    )
                }
                innerTextField()
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ModalBottomSheetLayout(
    modifier: Modifier = Modifier.fillMaxSize(),
    sheetState: ModalBottomSheetState,
    sheetShape: Shape = AppTheme.shapes.topLargeShape,
    sheetBackgroundColor: Color = AppTheme.colors.primary,
    sheetContentColor: Color = AppTheme.colors.onSecondary,
    sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.material.ModalBottomSheetLayout(
        modifier = modifier,
        sheetState = sheetState,
        sheetShape = sheetShape,
        sheetBackgroundColor = sheetBackgroundColor,
        sheetContentColor = sheetContentColor,
        sheetContent = sheetContent,
        content = content
    )
}
