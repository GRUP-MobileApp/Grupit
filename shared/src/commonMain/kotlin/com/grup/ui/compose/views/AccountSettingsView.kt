package com.grup.ui.compose.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomSheetState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.User
import com.grup.other.AccountSettings
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressModalBottomSheetLayout
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1DenyTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.compose.SmallIcon
import com.grup.ui.viewmodel.AccountSettingsViewModel
import kotlinx.coroutines.launch

internal class AccountSettingsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val accountSettingsViewModel = getScreenModel<AccountSettingsViewModel>()
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
private fun AccountSettingsLayout(
    accountSettingsViewModel: AccountSettingsViewModel,
    navigator: Navigator,
    modifier: Modifier = Modifier
) {
//    val editProfileBottomSheetState = rememberModalBottomSheetState(
//        ModalBottomSheetValue.Hidden,
//    )
//    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
//        BackPressModalBottomSheetLayout(
//            sheetState = editProfileBottomSheetState,
//            sheetContent = {
//                // TODO: TYSU
//               Scaffold(
//                   topBar = {
//                       TopAppBar(
//                           title = { },
//                           backgroundColor = AppTheme.colors.primary,
//                           navigationIcon = {
//                               IconButton(
//                                   onClick = {
//                                       scope.launch {
//                                           editProfileBottomSheetState.hide()
//                                       }
//                                   }
//                               ) {
//                                   Icon(
//                                       imageVector = Icons.Filled.ArrowBack,
//                                       contentDescription = "Back",
//                                       tint = AppTheme.colors.onSecondary
//                                   )
//                               }
//                           }
//                       )
//                   },
//                   backgroundColor = AppTheme.colors.secondary
//               ) {
//                   Column(
//                       verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
//                       modifier = modifier
//                           .fillMaxSize()
//                           .padding(AppTheme.dimensions.appPadding)
//                   ) {
//                       H1Text(text = "Change Display Name")
//                       H1Text(text = "Edit Profile Picture")
//                       H1Text(text = "Edit Venmo Information")
//                   }
//               }
//
//
//            },
//            content = content
//        )
//    }

    var currentPage: AccountSettingsViewModel.Pages by remember {
        mutableStateOf(AccountSettingsViewModel.Pages.MAIN_SETTINGS_PAGE)
    }

    fun changePage(destination: AccountSettingsViewModel.Pages) {
        currentPage = destination
    }

    AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
            // Compare the incoming number with the previous number.
            if (targetState > initialState) {
                (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                    slideOutHorizontally { height -> -height } + fadeOut())
            } else {
                (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                    slideOutHorizontally { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        }
    ) { targetCount ->
        when(targetCount) {
            AccountSettingsViewModel.Pages.MAIN_SETTINGS_PAGE -> MainSettingsPage(
                accountSettingsViewModel = accountSettingsViewModel,
                changePageEditProfilePage = {
                    changePage(AccountSettingsViewModel.Pages.EDIT_PROFILE_PAGE)
                },
                logOutOnClick = {
                    accountSettingsViewModel.logOut {
                        navigator.popUntilRoot()
                    }
                }
            )
            AccountSettingsViewModel.Pages.EDIT_PROFILE_PAGE -> EditProfilePage(
                //go back to main settings page
                changePageEditDisplayName = {
                    changePage(AccountSettingsViewModel.Pages.EDIT_DISPLAY_NAME_PAGE)
                }
            )
            AccountSettingsViewModel.Pages.EDIT_DISPLAY_NAME_PAGE -> EditDisplayNamePage(

            )
        }
    }
}

@Composable
private fun MainSettingsPage(
    accountSettingsViewModel: AccountSettingsViewModel,
    changePageEditProfilePage: () -> Unit,
    logOutOnClick: () -> Unit
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

    SimpleLazyListPage(pageName = "Account Settings") {
        item {
            ProfileSettings(
                user = accountSettingsViewModel.userObject,
                onEditProfile = changePageEditProfilePage
            )
        }
        item {
            SettingHeader(text = "Group Notifications")
            NotificationSettings(
                groupNotificationEntries = groupNotificationEntries,
                toggleGroupNotification = { notificationName ->
                    groupNotificationEntries[notificationName] =
                        accountSettingsViewModel.toggleGroupNotificationType(
                            *AccountSettingsViewModel.groupNotificationEntries[notificationName]!!
                        )
                }
            )
        }
        item {
            H1DenyTextButton(
                text = "Log Out",
                onClick = logOutOnClick
            )
        }
    }
}

@Composable
private fun ProfileSettings(
    user: User,
    onEditProfile: () -> Unit
) {
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
                user = user,
                iconSize = 50.dp
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)
            ) {
                H1Text(text = user.displayName)
                Caption(text = user.username)
            }
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Edit Profile",
                onClick = onEditProfile,
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
            .padding(vertical = AppTheme.dimensions.tinySpace)
            .padding(horizontal = AppTheme.dimensions.cardPadding)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
            modifier = Modifier
                .fillMaxWidth()
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
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AppTheme.colors.confirm)
        )
    }
}

@Composable
private fun EditProfilePage(changePageEditDisplayName: () -> Unit) {
    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(AppTheme.dimensions.appPadding)
    ) {
        H1Text(
            text = "Edit Display Name",
            modifier = Modifier.clickable(onClick = changePageEditDisplayName)
        )
    }
}

@Composable
private fun EditDisplayNamePage() {
    Column(
        modifier = Modifier.fillMaxSize()
    ){
        H1Text(text = "my ass hurts")
    }
}
