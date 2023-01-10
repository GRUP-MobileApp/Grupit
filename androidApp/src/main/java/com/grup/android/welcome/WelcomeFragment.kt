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
import com.grup.android.R
import com.grup.android.ui.apptheme.AppTheme

class WelcomeFragment : Fragment() {
    private val welcomeViewModel: WelcomeViewModel by viewModels()

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


@Composable
fun RegisterPage(
    welcomeViewModel: WelcomeViewModel,
    navController: NavController
) {
    var username: String by remember { mutableStateOf("") }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.primary)) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .background(AppTheme.colors.primary)
                .padding(10.dp, top = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
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
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Enter a Username",
                    fontSize = 25.sp,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = AppTheme.colors.onSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                    )

                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(
                        onClick = {
                            welcomeViewModel.registerUserObject(username)
                            navController.navigate(R.id.endWelcomeSlideshow)
                        },
                        shape = AppTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.confirm
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
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
    }
}