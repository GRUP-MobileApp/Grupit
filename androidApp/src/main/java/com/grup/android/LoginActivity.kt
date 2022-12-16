package com.grup.android

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.grup.android.*
import com.grup.android.ui.*

import androidx.compose.material.*
import androidx.compose.ui.platform.LocalContext


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LoginPage()
            }
        }
    }

}

@Composable
fun LoginPage() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.primary)) {

        val context = LocalContext.current
        ClickableText(
            text = AnnotatedString("Forgot Password?"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {context.startActivity(Intent(context, RegisterActivity::class.java)) },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                color = AppTheme.colors.onSecondary
            )
        )

    }
    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val username = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }

        Text(text = "Login",
            style = TextStyle(
                fontSize = 40.sp,
                fontFamily = FontFamily.Monospace
            ),
            color = AppTheme.colors.onSecondary)

        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            label = {
                Text(
                    text = "Username",
                    color = AppTheme.colors.onSecondary
                ) },
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            singleLine = true,
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = {
                Text(
                    text = "Password",
                    color = AppTheme.colors.onSecondary
                ) },
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            singleLine = true,
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            val context = LocalContext.current

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    },
                    shape = AppTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.secondary
                    ),
                    modifier = Modifier
                        .width(125.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Sign Up",
                        color = AppTheme.colors.onSecondary
                    )
                }
                
                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                    },
                    shape = AppTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.confirm
                    ),
                    modifier = Modifier
                        .width(125.dp)
                        .height(50.dp)
                ) {
                    Text(
                        text = "Login",
                        color = AppTheme.colors.onSecondary
                    )
                }
            }
        }
    }
}