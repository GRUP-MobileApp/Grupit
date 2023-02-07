package com.grup.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.google.accompanist.pager.HorizontalPager
import com.grup.android.R
import com.grup.android.asMoneyAmount
import com.grup.android.ui.IconRowCard
import com.grup.android.ui.ProfileIcon
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.h1Text
import kotlinx.coroutines.launch

class SettleDetailsFragment : Fragment() {

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
                    SettleDetailsLayout(
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun SettleDetailsLayout(
    navController: NavController
) {
    Scaffold(
        topBar = {
            SettleDetailTopBar (
                onBackPress = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.primary)
        ) {
            ProfileIcon(
                imageVector = Icons.Default.Face,
                iconSize = 100.dp
            )
            h1Text(
                text = "[NAME] Requested",
                color = AppTheme.colors.onSecondary,
                fontSize = 30.sp
            )
            h1Text(
                text = "DEBT",
                color = AppTheme.colors.onSecondary,
                fontSize = 75.sp
            )
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(AppTheme.shapes.large)
                    .background(AppTheme.colors.secondary)
            ) {
                Column(
                    verticalArrangement = Arrangement
                        .spacedBy(AppTheme.dimensions.spacingLarge),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = AppTheme.dimensions.spacing,
                            start = AppTheme.dimensions.spacing
                        )
                ) {
                    h1Text(
                        text = "Settlers",
                        color = AppTheme.colors.onSecondary,
                        fontSize = 30.sp
                    )
                    /* TODO List*/
                }
            }
        }
    }
}


@Composable
fun SettleDetailTopBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = { },
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppTheme.colors.onSecondary
                )
            }
        }
    )
}