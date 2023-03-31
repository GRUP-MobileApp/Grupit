package com.grup.android.welcome

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.accompanist.pager.*
import com.grup.android.R
import com.grup.android.ui.Caption
import com.grup.android.ui.H1ConfirmTextButton
import com.grup.android.ui.H1Text
import com.grup.android.ui.apptheme.AppTheme
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class WelcomeFragment : Fragment() {
    private val welcomeViewModel: WelcomeViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (welcomeViewModel.hasUserObject) {
            findNavController().navigate(R.id.startMainFragment)
        }
    }

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            }
        )
        return ComposeView(requireContext()).apply {
            setContent {
                WelcomeLayout(
                    welcomeViewModel = welcomeViewModel,
                    navController = findNavController()
                )
            }
        }
    }
}

@ExperimentalPagerApi
@Composable
fun WelcomeLayout(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val context = LocalContext.current

    var username: String by remember { mutableStateOf("") }
    val usernameValidity: WelcomeViewModel.NameValidity
        by welcomeViewModel.usernameValidity.collectAsStateWithLifecycle()
    var firstName: String by remember { mutableStateOf("") }
    val firstNameValidity: WelcomeViewModel.NameValidity
        by welcomeViewModel.firstNameValidity.collectAsStateWithLifecycle()
    var lastName: String by remember { mutableStateOf("") }
    val lastNameValidity: WelcomeViewModel.NameValidity
            by welcomeViewModel.lastNameValidity.collectAsStateWithLifecycle()

    var pfpByteArray: ByteArray by remember { mutableStateOf(ByteArray(0)) }

    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            pfpByteArray = context.contentResolver.openInputStream(
                result.uriContent ?: Uri.EMPTY
            )?.let { pictureInputStream ->
                pictureInputStream.readBytes().also {
                    pictureInputStream.close()
                }
            } ?: pfpByteArray
        } else {
            // an error occurred cropping
        }
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageCropLauncher.launch(
            CropImageContractOptions(
                uri,
                CropImageOptions(
                    imageSourceIncludeCamera = true,
                    imageSourceIncludeGallery = true,
                    cropShape = CropImageView.CropShape.OVAL,
                    cornerShape = CropImageView.CropCornerShape.OVAL,
                    guidelines = CropImageView.Guidelines.ON,
                    fixAspectRatio = true,
                    outputCompressFormat = Bitmap.CompressFormat.PNG
                )
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
            .padding(horizontal = AppTheme.dimensions.appPadding)
    ) {
        HorizontalPager(
            count = 4,
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
                0 ->
                    SetUsername(
                        username = username,
                        onUsernameChange = {
                            username = it
                            welcomeViewModel.checkUsername(username)
                        },
                        usernameValidity = usernameValidity,
                        onClickContinue = {
                            if (usernameValidity is WelcomeViewModel.NameValidity.Valid) {
                                scrollNext()
                            }
                        }
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
                        selectedPhotoAsByteArray = pfpByteArray,
                        promptProfilePictureOnClick = {
                            imagePickerLauncher.launch("image/*")
                        },
                        onClickBack = scrollBack,
                        onClickContinue = scrollNext
                    )
                3 -> TutorialPage(
                    onClickBack = scrollBack,
                    registerOnClick = {
                        welcomeViewModel.registerUserObject(
                            username,
                            "$firstName $lastName".trim(),
                            pfpByteArray
                        )
                        navController.navigate(R.id.startMainFragment)
                    }
                )

            }
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = AppTheme.dimensions.paddingExtraLarge),
        ) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .padding(16.dp),
                activeColor = AppTheme.colors.onPrimary,
            )
        }
    }
}

@Composable
fun SetUsername(
    username: String,
    onUsernameChange: (String) -> Unit,
    usernameValidity: WelcomeViewModel.NameValidity,
    onClickContinue: () -> Unit
) {
    val borderColor: Color =
        when(usernameValidity) {
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
            text = "Welcome!",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        H1Text(
            text = "Enter a unique username",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )

        Spacer(modifier = Modifier.height(15.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { H1Text(text = "Username") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = borderColor,
                    unfocusedBorderColor = borderColor
                ),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )
            if (usernameValidity is WelcomeViewModel.NameValidity.Invalid) {
                Caption(text = usernameValidity.error, color = AppTheme.colors.error)
            }
            Spacer(modifier = Modifier.weight(1.0f))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                H1Text(
                    text = "Next >",
                    modifier = Modifier.clickable(onClick = onClickContinue)
                )
            }
        }
    }
}

@Composable
fun SetProfilePicture(
    selectedPhotoAsByteArray: ByteArray,
    promptProfilePictureOnClick: () -> Unit,
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
            Image(
                painter =
                if (selectedPhotoAsByteArray.isNotEmpty()) {
                    rememberAsyncImagePainter(model = selectedPhotoAsByteArray)
                } else {
                    rememberVectorPainter(image = Icons.Default.Face)
                },
                contentDescription = "Selected picture",
                modifier = Modifier
                    .fillMaxHeight(0.3f)
                    .aspectRatio(1f)
                    .clip(AppTheme.shapes.circleShape)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        H1ConfirmTextButton(
            text = "Choose Photo",
            onClick = promptProfilePictureOnClick
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            H1Text(text = "< Back", modifier = Modifier.clickable(onClick = onClickBack))
            H1Text(text = "Next >", modifier = Modifier.clickable(onClick = onClickContinue))
        }
    }
}

@Composable
fun SetDisplayName(
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
                Caption(text = firstNameValidity.error, color = AppTheme.colors.error)
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
                Caption(text = lastNameValidity.error, color = AppTheme.colors.error)
            }
        }
        Spacer(modifier = Modifier.weight(1.0f))
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            H1Text(text = "< Back", modifier = Modifier.clickable(onClick = onClickBack))
            H1Text(text = "Next >", modifier = Modifier.clickable(onClick = onClickContinue))
        }
    }
}

@Composable
fun TutorialPage(
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
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(top = AppTheme.dimensions.paddingLarge)
        ) {
            H1Text(text = "< Back", modifier = Modifier.clickable(onClick = onClickBack))
            H1Text(text = "Finish >", modifier = Modifier.clickable(onClick = registerOnClick))
        }
    }
}
