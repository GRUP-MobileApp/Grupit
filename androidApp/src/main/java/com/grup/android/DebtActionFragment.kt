package com.grup.android

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.grup.APIServer
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.apptheme.h1Text
import com.grup.models.UserInfo

class DebtActionFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                RequestLayout(mainViewModel = mainViewModel)
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun RequestLayout(
    mainViewModel: MainViewModel
) {
    val userInfos: List<UserInfo> by mainViewModel.userInfos.collectAsState()
    Scaffold(
        topBar = {
            RequestTopAppBar()
        }
    ) {
        Column {
            RequestBody(userInfos = userInfos)
        }
    }

}

@Composable
fun RequestBody(
    userInfos: List<UserInfo>
) {
    val myUserInfo: UserInfo = userInfos.find { it.userId == APIServer.user.getId() }!!
    var debtActionAmount: String by remember { mutableStateOf("0") }
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onPrimary
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            modifier = Modifier
                .fillMaxHeight()
                .background(AppTheme.colors.primary)
        ) {
            h1Text(
                text = "Balance: $${myUserInfo.userBalance}",
                fontSize = 50.sp
            )
            h1Text(
                text = "$$debtActionAmount",
                fontSize = 65.sp
            )
            KeyPad(
                onKeyPress = { key ->
                    when(key) {
                        '.' -> {
                            if (!debtActionAmount.contains('.')) {
                                debtActionAmount += key
                            }
                        }
                        '<' -> {
                            debtActionAmount = if (debtActionAmount.length > 1) {
                                debtActionAmount.substring(0, debtActionAmount.length - 1)
                            } else {
                                "0"
                            }
                        }
                        else -> {
                            if (key.isDigit() &&
                                (debtActionAmount.length < 3 ||
                                        debtActionAmount[debtActionAmount.length - 3] != '.')) {
                                if (debtActionAmount == "0") {
                                    debtActionAmount = ""
                                }
                                debtActionAmount += key
                            }
                        }
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                SettleButton()

                RequestButton()
            }
        }
    }
}

@Composable
fun KeyPad(
    onKeyPress: (Char) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(top = 50.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)
        ) {
            Button(
                onClick = { onKeyPress('1') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "1",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('2') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "2",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('3') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "3",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)
        ) {
            Button(
                onClick = { onKeyPress('4') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "4",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('5') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "5",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('6') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "6",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)
        ) {
            Button(
                onClick = { onKeyPress('7') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "7",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('8') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "8",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('9') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "9",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)
        ) {
            Button(
                onClick = { onKeyPress('.') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = ".",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('0') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "0",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
            Button(
                onClick = { onKeyPress('<') },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            ) {
                Text(
                    text = "<",
                    fontSize = 20.sp,
                    color = AppTheme.colors.onSecondary
                )
            }
        }
    }
}

@Composable
fun SettleButton() {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.secondary),
        modifier = Modifier
            .padding(bottom = AppTheme.dimensions.paddingMedium)
            .width(175.dp)
            .height(40.dp),
        shape = AppTheme.shapes.large,
        onClick = { /*TODO*/ }
    ) {
        Text(
            text = "Settle",
            color = AppTheme.colors.onSecondary,
        )
    }
}

@Composable
fun RequestButton() {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.caption),
        modifier = Modifier
            .padding(bottom = AppTheme.dimensions.paddingMedium)
            .width(175.dp)
            .height(40.dp),
        shape = AppTheme.shapes.large,
        onClick = { /*TODO*/ }
    ) {
        Text(
            text = "Request",
            color = AppTheme.colors.onSecondary,
        )
    }
}

@Composable
fun RequestTopAppBar() {
    TopAppBar(
        title = { Text("") },
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            val context = LocalContext.current
            IconButton(
                onClick = {context.startActivity(Intent(context, MainActivity::class.java))}
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        }
    )
}
