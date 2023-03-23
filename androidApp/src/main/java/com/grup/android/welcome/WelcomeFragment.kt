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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.google.accompanist.pager.*
import com.grup.android.R
import com.grup.android.ui.H1ConfirmTextButton
import com.grup.android.ui.H1Text
import com.grup.android.ui.apptheme.AppTheme
import kotlinx.coroutines.launch

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
    val usernameValidity: WelcomeViewModel.UsernameValidity
        by welcomeViewModel.usernameValidity.collectAsStateWithLifecycle()
    var displayName: String by remember { mutableStateOf("") }
    var pfpUri: Uri by remember { mutableStateOf(Uri.EMPTY) }

    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            pfpUri = result.uriContent!!
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
    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            userScrollEnabled = false
        ) { page ->
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
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                1 ->
                    SetProfilePicture(
                        promptProfilePictureOnClick = {
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                2 ->
                    SetDisplayName(
                        displayName = displayName,
                        onDisplayNameChange = { displayName = it },
                        registerOnClick = {
                            val pictureInputStream =
                                context.contentResolver
                                    .openInputStream(pfpUri)
                            welcomeViewModel.registerUserObject(
                                username,
                                displayName,
                                pictureInputStream!!.readBytes()
                            )
                            pictureInputStream.close()
                            scope.launch {
                                pagerState.animateScrollToPage(3)
                            }
                        }
                    )
                3 ->
                    TutorialRequest(
                        onClick = {navController.navigate(R.id.startMainFragment)}
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
    usernameValidity: WelcomeViewModel.UsernameValidity,
    onClickContinue: () -> Unit
) {
    val borderColor: Color =
        when(usernameValidity) {
            WelcomeViewModel.UsernameValidity.Valid -> AppTheme.colors.confirm
            WelcomeViewModel.UsernameValidity.Invalid -> AppTheme.colors.error
            WelcomeViewModel.UsernameValidity.Pending -> Color.LightGray
            WelcomeViewModel.UsernameValidity.None -> Color.Gray
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        H1Text(
            text = "Welcome!",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        H1Text(
            text = "Enter a Username",
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
            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = {
                    if (usernameValidity is WelcomeViewModel.UsernameValidity.Valid) {
                        onClickContinue()
                    }
                },
                shape = AppTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.confirm
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)) {
                H1Text(
                    text = "Confirm",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary,
                )
            }
        }
    }
}

@Composable
fun SetDisplayName(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    registerOnClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        H1Text(
            text = "Welcome!",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        H1Text(
            text = "Enter your display name",
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            color = AppTheme.colors.onSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TextField(
                value = displayName,
                onValueChange = onDisplayNameChange,
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { H1Text(text = "Display Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )

            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = registerOnClick,
                shape = AppTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.confirm
                ),
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(50.dp)) {
                H1Text(
                    text = "Confirm",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary,
                )
            }
        }
    }
}

@Composable
fun SetProfilePicture(
    promptProfilePictureOnClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        H1Text(text = "Upload Profile Picture")
        H1ConfirmTextButton(
            text = "Choose Photo",
            onClick = promptProfilePictureOnClick
        )
    }
}

@Composable
fun TutorialRequest(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = AppTheme.dimensions.paddingExtraLarge,
                bottom = 100.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        H1Text(text = "How it works")

        Text(
            text = "GRUP is an app for tracking expenses in a group! A negative balance " +
                    "means that you owe money, while a positive balance means you are owed money." +
                    "You can request money to record how much is owed to you. When you have a " +
                    "positive balance, you can choose to settle debts. Anyone that has a negative " +
                    "balance can choose to pay for settle actions."
        )
        H1ConfirmTextButton(
            text = "Get Started",
            onClick = onClick
        )
    }

}