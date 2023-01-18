/*
package com.grup.android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.grup.android.MoneyRequestActivity
import com.grup.android.R
import com.grup.android.ui.*
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Welcome()
            }
        }
    }
}

@Composable
fun Welcome() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Welcome!",
            fontSize = 28.sp, fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        val username = remember { mutableStateOf(TextFieldValue()) }

        TextField(
            label = {
                Text(
                    text = "Password",
                    color = AppTheme.colors.onSecondary
                ) },
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            singleLine = true,
            value = username.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { username.value = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

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

    }
}*/
