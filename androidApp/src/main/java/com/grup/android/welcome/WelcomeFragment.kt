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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.*
import com.grup.android.R
import com.grup.android.ui.apptheme.AppTheme
import kotlinx.coroutines.launch
import com.grup.android.ui.apptheme.*

class WelcomeFragment : Fragment() {
    private val welcomeViewModel: WelcomeViewModel by viewModels()

    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RegisterPage(
                    welcomeViewModel = welcomeViewModel,
                    navController = findNavController()
                )
            }
        }
    }

}

@ExperimentalPagerApi
@Composable
fun RegisterPage(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
    ) {
        WelcomeContent(
            welcomeViewModel = welcomeViewModel,
            navController = navController
        )
    }


}

@ExperimentalPagerApi
@Composable
fun WelcomeContent(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {
    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier.fillMaxSize()
    )
        {
            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth(),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> {
                        WelcomeScreen1(welcomeViewModel, navController)
                    }

                    1 -> {
                        WelcomeScreen2(welcomeViewModel, navController)
                    }

                    2 -> {
                        WelcomeScreen3(welcomeViewModel, navController)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = AppTheme.dimensions.paddingExtraLarge
                    ),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
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
fun PagerIndicator(pagerState: PagerState, content: @Composable BoxScope.()->Unit) {
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier = Modifier
            .padding(16.dp),
        activeColor = AppTheme.colors.onPrimary,
    )
}

@ExperimentalPagerApi
@Composable
fun WelcomeScreen1(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {

    val pagerState = rememberPagerState()
    var username: String by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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

            TextField(
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        text = "",
                        color = AppTheme.colors.onSecondary)
                },
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )

            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = {
                    welcomeViewModel.registerUserObject(username)
                    scope.launch {
                        pagerState.animateScrollToPage(1)
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
fun WelcomeScreen2(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {

    val pagerState = rememberPagerState()
    var username: String by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
            text = "This is Page 2",
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
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        text = "",
                        color = AppTheme.colors.onSecondary)
                },
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )

            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(2)
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
fun WelcomeScreen3(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {

    val pagerState = rememberPagerState()
    var username: String by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

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
            text = "This is Page 3",
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
                value = username,
                onValueChange = { username = it },
                label = {
                    Text(
                        text = "",
                        color = AppTheme.colors.onSecondary)
                },
                textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                placeholder = { Text(text = "Username") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(AppTheme.colors.secondary)
            )

            Spacer(modifier = Modifier.weight(1.0f))
            Button(
                onClick = {
                    welcomeViewModel.registerUserObject(username)
                    navController.navigate(R.id.endWelcomeSlideshow)
                    scope.launch {
                        pagerState.animateScrollToPage(1)
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
