package com.grup.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.android.ui.AppTheme

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                RegisterPage()
            }
        }
    }

}


@Composable
fun RegisterPage() {

    val emailValue = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()
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
                        value = emailValue.value,
                        onValueChange = { emailValue.value = it },
                        label = {
                                Text(
                                    text = "",
                                    color = AppTheme.colors.onSecondary)
                                },
                        textStyle = TextStyle(color = AppTheme.colors.onSecondary),
                        placeholder = { Text(text = "Email Address") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                    )

                    Spacer(modifier = Modifier.padding(10.dp))

                    val context = LocalContext.current
                    Text(
                        text = "Login Instead",
                        color = AppTheme.colors.onSecondary,
                        modifier = Modifier.clickable(
                            onClick = {
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            }
                        )
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                    Button(
                        onClick = { },
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

