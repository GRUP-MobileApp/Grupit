package com.grup.android.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.*
import com.grup.android.R
import com.grup.android.ui.apptheme.AppTheme
import kotlinx.coroutines.launch

class WelcomeFragment : Fragment() {
    private val welcomeViewModel: WelcomeViewModel by viewModels()

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

@OptIn(ExperimentalLifecycleComposeApi::class)
@ExperimentalPagerApi
@Composable
fun WelcomeLayout(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    var username: String by remember { mutableStateOf("") }
    val usernameValidity: WelcomeViewModel.UsernameValidity
        by welcomeViewModel.usernameValidity.collectAsStateWithLifecycle()
    var displayName: String by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
    ) {
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth(),
            userScrollEnabled = true
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
                    SetDisplayName(
                        displayName = displayName,
                        onDisplayNameChange = { displayName = it },
                        registerOnClick = {
                            welcomeViewModel.registerUserObject(username, displayName)
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

@ExperimentalPagerApi
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
        Text(
            text = "Welcome!",
            fontSize = 50.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Enter a Username",
            fontSize = 23.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
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
                label = {
                    Text(
                        text = "",
                        color = AppTheme.colors.onSecondary
                    )
                },
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { Text(text = "Username") },
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
                Text(
                    text = "Confirm",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary,
                )
            }
        }
    }
}

@ExperimentalPagerApi
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
        Text(
            text = "Welcome!",
            fontSize = 50.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = AppTheme.colors.onSecondary
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Enter your display name",
            fontSize = 23.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
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
                label = {
                    Text(
                        text = "",
                        color = AppTheme.colors.onSecondary)
                },
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { Text(text = "Display Name") },
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
                Text(
                    text = "Confirm",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary,
                )
            }
        }
    }
}
