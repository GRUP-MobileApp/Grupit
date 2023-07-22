package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.profilePicturePainter
import com.grup.ui.viewmodel.AccountSettingsViewModel
import org.koin.core.component.get

class AccountSettingsView : Screen {
    @Composable
    override fun Content() {
        val accountSettingsViewModel: AccountSettingsViewModel = rememberScreenModel { AccountSettingsViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            AccountSettingsLayout(
                accountSettingsViewModel = accountSettingsViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun AccountSettingsLayout(
    accountSettingsViewModel: AccountSettingsViewModel,
    navigator: Navigator
) {
    SimpleLazyListPage(
        pageName = "Account Settings",
        onBackPress = { navigator.pop() }
    ) {
        item {
            ProfileSettings(accountSettingsViewModel = accountSettingsViewModel)
        }
        item {
            SettingHeader(text = "Group Notifications")
            NotificationSettings(accountSettingsViewModel = accountSettingsViewModel)
        }
    }
}

@Composable
private fun ProfileSettings(
    accountSettingsViewModel: AccountSettingsViewModel
) {
    val pfpPainter = profilePicturePainter(accountSettingsViewModel.userObject.profilePictureURL)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.extraLarge)
            .background(AppTheme.colors.secondary)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.cardPadding)
        ) {
            ProfileIcon(
                painter = pfpPainter,
                iconSize = 50.dp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)
            ) {
                accountSettingsViewModel.userObject.let {  user ->
                    H1Text(text = user.displayName!!)
                    Caption(text = "@${user.username!!}")
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Edit Profile",
                onClick = {

                },
                scale = 0.8f
            )
        }
    }
}

@Composable
private fun NotificationSettings(
    accountSettingsViewModel: AccountSettingsViewModel
) {
    var groupNotificationNewSettleRequests: Boolean by remember {
        mutableStateOf(accountSettingsViewModel.getGroupNotificationNewSettleRequests())
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.extraLarge)
            .background(AppTheme.colors.secondary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            modifier = Modifier.fillMaxWidth().padding(AppTheme.dimensions.cardPadding)
        ) {
            SettingSlider(
                text = "New group settle requests",
                toggled = groupNotificationNewSettleRequests,
                onToggle = {
                    groupNotificationNewSettleRequests =
                        accountSettingsViewModel.toggleGroupNotificationNewSettleRequests()
                }
            )
        }
    }
}

@Composable
private fun SettingHeader(
    text: String,
    textSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier.padding(bottom = AppTheme.dimensions.paddingSmall)
) {
    Caption(text = text, fontSize = textSize, modifier = modifier.fillMaxWidth(0.95f))
}

@Composable
private fun SettingSlider(
    text: String,
    textSize: TextUnit = 16.sp,
    toggled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        H1Text(text = text, fontSize = textSize)
        Switch(
            checked = toggled,
            onCheckedChange = onToggle
        )
    }
}
