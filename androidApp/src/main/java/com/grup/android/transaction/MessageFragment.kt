package com.grup.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.AddToGroupButton
import com.grup.android.R
import com.grup.android.UsernameSearchBar
import com.grup.android.UsersList
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.h1Text
import kotlinx.coroutines.launch

class MessageFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onSecondary
                ) {
                    MessageActionLayout(
                        navController = findNavController(),
                        debtActionAmount = requireArguments().getDouble("amount")
                    )
                }
            }
        }
    }
}

@Composable
fun MessageActionLayout(
    navController: NavController,
    debtActionAmount: Double
) {

    var transactionMessage: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Members", color = AppTheme.colors.onSecondary) },
                backgroundColor = AppTheme.colors.primary,
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            Modifier.background(AppTheme.colors.primary)
                        )
                    }
                }
            )
        },
        backgroundColor = AppTheme.colors.primary,
        drawerContent = { Text(text = "drawerContent") }
    ) { padding ->

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.primary)
        ) {
            
            h1Text(text = "You are requesting", fontSize = 40.sp)
            h1Text(
                text = "$$debtActionAmount",
                color = AppTheme.colors.onSecondary,
                fontSize = 100.sp
            )
            h1Text(text = "From", fontSize = 40.sp)
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(AppTheme.shapes.large)
                    .padding(padding)
                    .background(AppTheme.colors.secondary)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxSize()
                ) {
                    h1Text(text = "What is this for?", fontSize = 40.sp)

                    TextField(
                        value = transactionMessage,
                        onValueChange = {transactionMessage = it},
                        shape = RectangleShape,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(AppTheme.shapes.large)
                            .background(AppTheme.colors.secondary)
                            .padding(all = AppTheme.dimensions.paddingMedium)
                            .height(200.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = AppTheme.colors.primary,
                            disabledTextColor = Color.Transparent,
                            backgroundColor = AppTheme.colors.onPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )

                    Column(
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(top = 20.dp, bottom = 40.dp)
                        ) {
                            Button(
                                onClick = { },
                                shape = AppTheme.shapes.CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = AppTheme.colors.error
                                ),
                                modifier = Modifier
                                    .width(175.dp)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Cancel Request",
                                    color = AppTheme.colors.onSecondary
                                )
                            }

                            Spacer(modifier = Modifier.width(20.dp))

                            Button(
                                onClick = { },
                                shape = AppTheme.shapes.CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = AppTheme.colors.confirm
                                ),
                                modifier = Modifier
                                    .width(175.dp)
                                    .height(50.dp)
                            ) {
                                Text(
                                    text = "Confirm Request",
                                    color = AppTheme.colors.onSecondary
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}