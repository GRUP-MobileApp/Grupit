package com.example.grup.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.grup.android.ui.*
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grup.android.ui.ExampleTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }
    }
}

@Composable
fun MainLayout() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeAppBar(
                Color.White,
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.1f)
            )
        }
    ) {
        Column {
            GroupDetails()
        }
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Text(
                    text = "Group Name"
                )
            }
        },
        backgroundColor = backgroundColor,
        navigationIcon = {
            IconButton(
                onClick = { /* TODO: Open search */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account"
                    )
                }
            }
        },
        modifier = modifier
    )
}

@Composable
fun TransactionHistory(content: List<String>) {
    LazyColumn(modifier = Modifier.fillMaxHeight()) {
        items(content.size) {
            index -> Text(text = content[index])
        }
    }
}

@Composable
fun GroupDetails() {
    val sampleList = listOf<String>("test1", "test2", "test3")

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp)
    ) {
        ExampleTheme {
            Text("Balance:", color = Color.Black, style = MaterialTheme.typography.h2)
        }
        ExampleTheme {
            Text("10 dollar", color = Color.Black, style = MaterialTheme.typography.h1, fontSize = 30.sp)
        }
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(fraction = 0.5f)
        ) {
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Request")
            }
            Button(onClick = { /*TODO*/ }) {
                Text(text = "Settle")
            }
        }
        TransactionHistory(sampleList)
    }
}
