package com.grup.ui.compose.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.InvisibleTextField
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.WelcomeViewModel
import dev.icerock.moko.media.Bitmap
import dev.icerock.moko.media.compose.BindMediaPickerEffect
import dev.icerock.moko.media.compose.rememberMediaPickerControllerFactory
import dev.icerock.moko.media.compose.toImageBitmap
import dev.icerock.moko.media.picker.CanceledException
import dev.icerock.moko.media.picker.MediaSource
import dev.icerock.moko.permissions.DeniedException
import kotlinx.coroutines.launch
import kotlin.math.min

internal class WelcomeView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val welcomeViewModel= rememberScreenModel { WelcomeViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            WelcomeLayout(
                welcomeViewModel = welcomeViewModel,
                navigator = navigator
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WelcomeLayout(
    welcomeViewModel: WelcomeViewModel,
    navigator: Navigator
) {
    val mediaFactory = rememberMediaPickerControllerFactory()
    val picker = remember(mediaFactory) { mediaFactory.createMediaPickerController() }
    BindMediaPickerEffect(picker)

    val scope = rememberCoroutineScope()
    val pageCount = 4
    val pagerState = rememberPagerState(pageCount = { pageCount })

    var username: String by remember { mutableStateOf("") }
    val usernameValidity: WelcomeViewModel.NameValidity
            by welcomeViewModel.usernameValidity.collectAsStateWithLifecycle()
    var firstName: String by remember { mutableStateOf("") }
    val firstNameValidity: WelcomeViewModel.NameValidity
            by welcomeViewModel.firstNameValidity.collectAsStateWithLifecycle()
    var lastName: String by remember { mutableStateOf("") }
    val lastNameValidity: WelcomeViewModel.NameValidity
            by welcomeViewModel.lastNameValidity.collectAsStateWithLifecycle()
    var venmoUsername: String by remember { mutableStateOf("") }
    val venmoUsernameValidity: WelcomeViewModel.NameValidity
            by welcomeViewModel.venmoUsernameValidity.collectAsStateWithLifecycle()

    var pfpBitmap: Bitmap? by remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
            .padding(horizontal = AppTheme.dimensions.appPadding)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            userScrollEnabled = false
        ) { page ->
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = AppTheme.dimensions.paddingExtraLarge,
                        bottom = 100.dp,
                    )
            ) {
                when (page) {
                    0 -> WelcomePage()
                    1 ->
                        ProfilePage(
                            username = username,
                            onUsernameChange = {
                                username = it.substring(0, min(it.length, 14))
                                welcomeViewModel.checkUsername(username)
                            },
                            usernameValidity = usernameValidity,
                            firstName = firstName,
                            onFirstNameChange = {
                                firstName = it.substring(0, min(it.length, 14))
                                welcomeViewModel.checkFirstNameValidity(firstName)
                            },
                            firstNameValidity = firstNameValidity,
                            lastName = lastName,
                            onLastNameChange = {
                                lastName = it.substring(0, min(it.length, 14))
                                welcomeViewModel.checkLastNameValidity(lastName)
                            },
                            lastNameValidity = lastNameValidity,
                            venmoUsername = venmoUsername,
                            onVenmoUsernameChange = {
                                venmoUsername = it.substring(0, min(it.length, 30))
                                welcomeViewModel.checkVenmoUsernameValidity(venmoUsername)
                            },
                            venmoUsernameValidity = venmoUsernameValidity
                        )

                    2 ->
                        SetProfilePicture(
                            profilePictureBitmap = pfpBitmap,
                            choosePhotoOnClick = {
                                scope.launch {
                                    try {
                                        pfpBitmap = picker.pickImage(MediaSource.GALLERY)
                                    } catch (exc: DeniedException) {
                                        println("denied - $exc")
                                    } catch (exc: CanceledException) {
                                        println("cancelled - $exc")
                                    }
                                }
                            }
                        )

                    3 -> FinalPage()
                }
            }
        }
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = AppTheme.dimensions.paddingExtraLarge)
        ) {
            ArrowRow(
                pagerState = pagerState,
                allowNext = { page ->
                    when (page) {
                        1 ->
                            usernameValidity is WelcomeViewModel.NameValidity.Valid &&
                            firstNameValidity is WelcomeViewModel.NameValidity.Valid &&
                            (
                                lastNameValidity is WelcomeViewModel.NameValidity.Valid ||
                                lastNameValidity is WelcomeViewModel.NameValidity.None
                            ) &&
                            (
                                venmoUsernameValidity is WelcomeViewModel.NameValidity.Valid ||
                                venmoUsernameValidity is WelcomeViewModel.NameValidity.None
                            )
                        else -> true
                    }
                },
                onLastNext = {
                    welcomeViewModel.registerUserObject(
                        username = username,
                        displayName = "$firstName $lastName".trim(),
                        venmoUsername = venmoUsername,
                        profilePictureBitmap = pfpBitmap,
                        onSuccess = { navigator.pop() },
                        onError = { }
                    )
                }
            )
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            H1Text(
                text = "Welcome!",
                fontSize = 50.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppTheme.colors.onSecondary,
            )
            H1Text(
                text = "Grupit records your person-to-person debts in a group and simplifies it " +
                        "into one overall balance.",
            )
        }
    }
}

@Composable
private fun ProfilePage(
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameValidity: WelcomeViewModel.NameValidity,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    firstNameValidity: WelcomeViewModel.NameValidity,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    lastNameValidity: WelcomeViewModel.NameValidity,
    venmoUsername: String,
    onVenmoUsernameChange: (String) -> Unit,
    venmoUsernameValidity: WelcomeViewModel.NameValidity,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            )
    ) {
        H1Text(
            text = "Let's set up your profile.",
            fontWeight = FontWeight.Medium,
            color = AppTheme.colors.onSecondary,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(30.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraLarge),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            ProfileTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "Username",
                valueValidity = usernameValidity,
                modifier = Modifier.align(Alignment.Start)
            )
            ProfileTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                placeholder = "First Name",
                valueValidity = firstNameValidity,
                modifier = Modifier.align(Alignment.Start)
            )
            ProfileTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                placeholder = "Last Name",
                valueValidity = lastNameValidity,
                modifier = Modifier.align(Alignment.Start)
            )
            ProfileTextField(
                value = venmoUsername,
                onValueChange = onVenmoUsernameChange,
                placeholder = "Venmo Username",
                valueValidity = venmoUsernameValidity,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }
}

@Composable
private fun SetProfilePicture(
    profilePictureBitmap: Bitmap?,
    choosePhotoOnClick: () -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            )
    ) {
        H1Text(
            text = "Profile Picture",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )
        Box(
            modifier = Modifier
                .border(width = 5.dp, color = AppTheme.colors.secondary)
                .padding(AppTheme.dimensions.cardPadding)
        ) {
            profilePictureBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.toImageBitmap(),
                    contentDescription = "Selected picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .aspectRatio(1f)
                        .clip(AppTheme.shapes.circleShape)
                )
            } ?: Image(
                    painter = rememberVectorPainter(image = Icons.Default.Face),
                    contentDescription = "Selected picture",
                    modifier = Modifier
                        .fillMaxHeight(0.3f)
                        .aspectRatio(1f)
                        .clip(AppTheme.shapes.circleShape)
                )
        }
        Spacer(modifier = Modifier.weight(1f))
        H1ConfirmTextButton(text = "Choose Photo", onClick = choosePhotoOnClick)
    }
}

@Composable
private fun FinalPage() {
    val textList: List<AnnotatedString> = listOf(
        buildAnnotatedString {
            append("To record a transaction, send a ")
            withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                append("Debt Request")
            }
            append(" to the debtors involved in the transaction.")
        },
        buildAnnotatedString {
            append("A positive ")
            withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                append("Group Balance")
            }
            append(" means you are owed money overall within the group. A negative balance " +
                    "means you owe money overall.")
        },
        buildAnnotatedString {
            append("To settle your positive balance, create a ")
            withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                append("Settle Request")
            }
            append(" to the group. Anyone with a negative balance can send ")
            withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                append("Settle Transaction")
            }
            append(" as repayment.")
        },
        buildAnnotatedString {
            append("When you receive a ")
            withStyle(SpanStyle(fontWeight = FontWeight.Black)) {
                append("Settle Transaction")
            }
            append(" verify that the sender has actually paid the indicated " +
                    "amount(externally via Venmo, Zelle, etc.) and accept the transaction.")
        }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp
            )
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(textList) { text ->
                H1Text(text = text)
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String? = null,
    valueValidity: WelcomeViewModel.NameValidity,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
        modifier = modifier
    ) {
        InvisibleTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            indicatorColor = when(valueValidity) {
                is WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
                is WelcomeViewModel.NameValidity.Invalid -> AppTheme.colors.error
                else -> AppTheme.colors.primary
            }
        )
        Caption(
            text = when(valueValidity) {
                is WelcomeViewModel.NameValidity.Invalid -> valueValidity.error
                else -> ""
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArrowRow(
    pagerState: PagerState,
    allowNext: (Int) -> Boolean,
    onLastNext: () -> Unit,
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
                H1Text(
                    text = "< Back",
                    modifier = Modifier.clickable {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            H1Text(
                text = if (isFinalPage) "Finish >"
                       else "Next >",
                modifier = Modifier.clickable {
                    if (isFinalPage) {
                        onLastNext()
                    } else if (allowNext(pagerState.currentPage)) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
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
