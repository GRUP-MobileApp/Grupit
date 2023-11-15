package com.grup.ui.compose.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.LocalContentColor
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldDefaults
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
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
import kotlin.math.max
import kotlin.math.min

class WelcomeView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val welcomeViewModel= getScreenModel<WelcomeViewModel>()
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
            val scrollNext: () -> Unit = {
                scope.launch {
                    pagerState.animateScrollToPage(
                        min(page + 1, 4)
                    )
                }
            }
            val scrollBack: () -> Unit = {
                scope.launch {
                    pagerState.animateScrollToPage(
                        max(page - 1, 0)
                    )
                }
            }
            when (page) {
                0 -> WelcomePage(onClickContinue = scrollNext)
                1 ->
                    ProfilePage(
                        username = username,
                        onUsernameChange = {
                            username = it.substring(0, 11)
                            welcomeViewModel.checkUsername(username)
                        },
                        usernameValidity = usernameValidity,
                        onClickContinue = {
                            if (usernameValidity is WelcomeViewModel.NameValidity.Valid) {
                                scrollNext()
                            }
                        },
                        onClickBack = scrollBack
                    )
                1 ->
                    SetDisplayName(
                        firstName = firstName,
                        onFirstNameChange = {
                            firstName = it
                            welcomeViewModel.checkFirstNameValidity(firstName)
                        },
                        firstNameValidity = firstNameValidity,
                        lastName = lastName,
                        onLastNameChange = {
                            lastName = it
                            welcomeViewModel.checkLastNameValidity(lastName)
                        },
                        lastNameValidity = lastNameValidity,
                        onClickBack = scrollBack,
                        onClickContinue = {
                            if (
                                firstNameValidity is WelcomeViewModel.NameValidity.Valid &&
                                (lastNameValidity is WelcomeViewModel.NameValidity.Valid ||
                                        lastNameValidity is WelcomeViewModel.NameValidity.None)
                            ) {
                                scrollNext()
                            }
                        }
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
                        },
                        onClickBack = scrollBack,
                        onClickContinue = scrollNext
                    )
                3 -> TutorialPage(
                    onClickBack = scrollBack,
                    registerOnClick = {
                        welcomeViewModel.registerUserObject(
                            username = username,
                            displayName = "$firstName $lastName".trim(),
                            venmoUsername = null,
                            profilePictureBitmap = pfpBitmap,
                            onSuccess = { navigator.pop() },
                            onFailure = {
                                println("Failed to register, error: $it")
                            }
                        )

                    }
                )

            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = AppTheme.dimensions.paddingExtraLarge)
        ) {
            repeat(pageCount) { iteration ->
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
private fun WelcomePage(
    onClickContinue: () -> Unit
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
            text = "Welcome!",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.height(50.dp))
        H1Text(
            text = "Grupit records your person-to-person debts in a group and simplifies it " +
                "into one overall balance.",
            modifier = Modifier.fillMaxWidth(0.8f)
        )
        Spacer(modifier = Modifier.weight(1f))
        ArrowRow(onClickContinue = onClickContinue)
    }
}

@Composable
private fun ProfilePage(
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameValidity: WelcomeViewModel.NameValidity,
    onClickContinue: () -> Unit,
    onClickBack: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.White)
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            )
    ) {
        H1Text(
            text = "Let's set up your profile.",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary,
            modifier = Modifier.fillMaxWidth(0.8f)
        )

        Spacer(modifier = Modifier.height(15.dp))

        InvisibleTextField(
            value = username,
            onValueChange = onUsernameChange,
            labelText = "Username",
            error = when(usernameValidity) {
                is WelcomeViewModel.NameValidity.Invalid -> usernameValidity.error
                else -> null
            },
            modifier = Modifier.align(Alignment.Start).border(1.dp, Color.White)
        )
        Spacer(modifier = Modifier.weight(1.0f))
        ArrowRow(onClickBack = onClickBack, onClickContinue = onClickContinue)
    }
}

@Composable
private fun SetProfilePicture(
    profilePictureBitmap: Bitmap?,
    choosePhotoOnClick: () -> Unit,
    onClickContinue: () -> Unit,
    onClickBack: () -> Unit
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            H1Text(
                text = "< Back",
                modifier = Modifier.clickable(onClick = onClickBack)
            )
            H1Text(
                text = "Next >",
                modifier = Modifier.clickable(onClick = onClickContinue)
            )
        }
    }
}

@Composable
private fun SetDisplayName(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    firstNameValidity: WelcomeViewModel.NameValidity,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    lastNameValidity: WelcomeViewModel.NameValidity,
    onClickBack: () -> Unit,
    onClickContinue: () -> Unit
) {
    val firstNameBorderColor: Color =
        when(firstNameValidity) {
            WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
            is WelcomeViewModel.NameValidity.Invalid -> AppTheme.colors.error
            WelcomeViewModel.NameValidity.Pending -> Color.LightGray
            WelcomeViewModel.NameValidity.None -> Color.Gray
        }
    val lastNameBorderColor: Color =
        when(lastNameValidity) {
            WelcomeViewModel.NameValidity.Valid -> AppTheme.colors.confirm
            is WelcomeViewModel.NameValidity.Invalid -> AppTheme.colors.error
            WelcomeViewModel.NameValidity.Pending -> Color.LightGray
            WelcomeViewModel.NameValidity.None -> Color.Gray
        }

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
            text = "Display Name",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        H1Text(
            text = "This is what others will see you as",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )

        Column {
            OutlinedTextField(
                value = firstName,
                onValueChange = onFirstNameChange,
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { H1Text(text = "First Name") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = firstNameBorderColor,
                    unfocusedBorderColor = firstNameBorderColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )
            if (firstNameValidity is WelcomeViewModel.NameValidity.Invalid) {
                Caption(
                    text = firstNameValidity.error,
                    color = AppTheme.colors.error
                )
            }
        }

        Column {
            OutlinedTextField(
                value = lastName,
                onValueChange = onLastNameChange,
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { H1Text(text = "Last Name") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = lastNameBorderColor,
                    unfocusedBorderColor = lastNameBorderColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )
            if (lastNameValidity is WelcomeViewModel.NameValidity.Invalid) {
                Caption(
                    text = lastNameValidity.error,
                    color = AppTheme.colors.error
                )
            }
        }
        Spacer(modifier = Modifier.weight(1.0f))

        ArrowRow(onClickBack = onClickBack, onClickContinue = onClickContinue)
    }
}

@Composable
private fun TutorialPage(
    onClickBack: () -> Unit,
    registerOnClick: () -> Unit
) {
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
        ArrowRow(onClickBack = onClickBack, onClickContinue = registerOnClick)
    }
}

@Composable
private fun ArrowRow(
    onClickBack: (() -> Unit)? = null,
    onClickContinue: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        onClickBack?.let {
            H1Text(
                text = "< Back",
                modifier = Modifier.clickable(onClick = it)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        H1Text(
            text = "Next >",
            modifier = Modifier.clickable(onClick = onClickContinue)
        )
    }
}
