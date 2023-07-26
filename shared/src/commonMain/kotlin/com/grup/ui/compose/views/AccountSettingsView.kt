package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
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
import com.grup.ui.compose.profilePicturePainter
import com.grup.ui.viewmodel.AccountSettingsViewModel

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
    val groupNotificationEntries: SnapshotStateMap<String, Boolean> = remember {
        mutableStateMapOf<String, Boolean>().apply {
            putAll(
                AccountSettingsViewModel.groupNotificationEntries
                    .mapValues { (_, notificationTypes) ->
                        accountSettingsViewModel.getGroupNotificationType(*notificationTypes)
                    }
            )
        }
    }

    SimpleLazyListPage(
        pageName = "Account Settings",
        onBackPress = { navigator.pop() }
    ) {
        item {
            ProfileSettings(accountSettingsViewModel = accountSettingsViewModel)
        }
        item {
            SettingHeader(text = "Group Notifications")
            NotificationSettings(
                groupNotificationEntries = groupNotificationEntries,
                toggleGroupNotification = { notificationName ->
                    accountSettingsViewModel.toggleGroupNotificationType(
                        *AccountSettingsViewModel.groupNotificationEntries[notificationName]!!
                    )
                }
            )
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
    groupNotificationEntries: Map<String, Boolean>,
    toggleGroupNotification: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(AppTheme.shapes.extraLarge)
            .background(AppTheme.colors.secondary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimensions.cardPadding)
        ) {
            groupNotificationEntries.forEach { (notificationName, toggled) ->
                SettingSlider(
                    text = notificationName,
                    toggled = toggled,
                    onToggle = {
                        toggleGroupNotification(notificationName)
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingHeader(
    text: String,
    textSize: TextUnit = TextUnit.Unspecified,
    modifier: Modifier = Modifier.padding(bottom = AppTheme.dimensions.paddingMedium)
) {
    Caption(text = text, fontSize = textSize, modifier = modifier.fillMaxWidth(0.95f))
}

@Composable
private fun SettingSlider(
    text: String,
    textSize: TextUnit = 18.sp,
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
