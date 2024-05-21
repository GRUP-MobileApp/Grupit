package com.grup.ui.compose.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.PagerArrowRow
import com.grup.ui.compose.ProfileTextField
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
        val welcomeViewModel = rememberScreenModel { WelcomeViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        WelcomeLayout(
            welcomeViewModel = welcomeViewModel,
            navigator = navigator
        )
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
            .padding(AppTheme.dimensions.appPadding)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            userScrollEnabled = false
        ) { page ->
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
        PagerArrowRow(
            pagerState = pagerState,
            onClickNext = { page ->
                when (page) {
                    1 -> if (
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
                    ) {
                        scope.launch { pagerState.scrollToPage(page + 1) }
                    }
                    pagerState.pageCount - 1 -> welcomeViewModel.registerUserObject(
                        username = username,
                        displayName = "$firstName $lastName".trim(),
                        venmoUsername = venmoUsername,
                        profilePictureBitmap = pfpBitmap,
                        onSuccess = { navigator.pop() },
                        onError = { }
                    )
                    else -> scope.launch { pagerState.scrollToPage(page + 1) }
                }
            }
        )
    }
}

@Composable
private fun WelcomePage() {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth()
            .padding(AppTheme.dimensions.appPadding)
    ) {
        H1Text(
            text = "Welcome!",
            fontSize = 50.sp,
            fontWeight = FontWeight.Medium
        )
        H1Text(
            text = "Grupit records your person-to-person debts in a group and simplifies it " +
                    "into one overall balance."
        )
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
        Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingExtraLarge))
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraLarge),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            ProfileTextField(
                value = username,
                onValueChange = onUsernameChange,
                placeholder = "Username",
                indicatorColor = when(usernameValidity) {
                    is WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
                    else -> AppTheme.colors.primary
                },
                error = when(usernameValidity) {
                    is WelcomeViewModel.NameValidity.Invalid -> usernameValidity.error
                    else -> null
                },
                modifier = Modifier
                    .run {
                        if (username.isNotEmpty())
                            width(IntrinsicSize.Min)
                        else this
                    }
            )
            ProfileTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                placeholder = "First Name",
                indicatorColor = when(firstNameValidity) {
                    is WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
                    else -> AppTheme.colors.primary
                },
                error = when(firstNameValidity) {
                    is WelcomeViewModel.NameValidity.Invalid -> firstNameValidity.error
                    else -> null
                },
                modifier = Modifier
                    .run {
                        if (firstName.isNotEmpty())
                            width(IntrinsicSize.Min)
                        else this
                    }
            )
            ProfileTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                placeholder = "Last Name",
                indicatorColor = when(lastNameValidity) {
                    is WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
                    else -> AppTheme.colors.primary
                },
                error = when(lastNameValidity) {
                    is WelcomeViewModel.NameValidity.Invalid -> lastNameValidity.error
                    else -> null
                },
                modifier = Modifier
                    .run {
                        if (lastName.isNotEmpty())
                            width(IntrinsicSize.Min)
                        else this
                    }
            )
            ProfileTextField(
                value = venmoUsername,
                onValueChange = onVenmoUsernameChange,
                placeholder = "Venmo Username",
                indicatorColor = when(venmoUsernameValidity) {
                    is WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
                    else -> AppTheme.colors.primary
                },
                error = when(venmoUsernameValidity) {
                    is WelcomeViewModel.NameValidity.Invalid -> venmoUsernameValidity.error
                    else -> null
                },
                modifier = Modifier
                    .run {
                        if (venmoUsername.isNotEmpty())
                            width(IntrinsicSize.Min)
                        else this
                    }
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
            fontWeight = FontWeight.Medium
        )
        Box(
            modifier = Modifier
                .border(width = 5.dp, color = AppTheme.colors.onSecondary)
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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight(0.5f)
            .fillMaxWidth()
            .padding(AppTheme.dimensions.appPadding)
    ) {
        textList.forEach { text ->
            H1Text(text = text, fontSize = AppTheme.typography.mediumFont)
        }
    }
}
