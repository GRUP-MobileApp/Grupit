package com.grup.ui.compose.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.User
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1DenyTextButton
import com.grup.ui.compose.H1ErrorTextButton
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.ProfileTextField
import com.grup.ui.compose.SmallIcon
import com.grup.ui.viewmodel.AccountSettingsViewModel
import com.grup.ui.viewmodel.AccountSettingsViewModel.Pages
import dev.icerock.moko.media.Bitmap
import dev.icerock.moko.media.compose.BindMediaPickerEffect
import dev.icerock.moko.media.compose.rememberMediaPickerControllerFactory
import dev.icerock.moko.media.picker.CanceledException
import dev.icerock.moko.media.picker.MediaSource
import dev.icerock.moko.permissions.DeniedException
import kotlinx.coroutines.launch

internal class AccountSettingsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val accountSettingsViewModel = rememberScreenModel { AccountSettingsViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        AccountSettingsLayout(
            accountSettingsViewModel = accountSettingsViewModel,
            navigator = navigator
        )
    }
}

@Composable
private fun AccountSettingsLayout(
    accountSettingsViewModel: AccountSettingsViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val editProfilePageScaffoldState = rememberScaffoldState()

    var currentPage: Pages by remember { mutableStateOf(Pages.MAIN_SETTINGS_PAGE) }

    var error: String? by remember { mutableStateOf(null) }

    val returnEditProfilePage: () -> Unit = {
        currentPage = Pages.EDIT_PROFILE_PAGE
        error = null
    }

    val logOut: () -> Unit = {
        accountSettingsViewModel.logOut {
            navigator.popUntilRoot()
        }
    }

    accountSettingsViewModel.user?.let { user ->
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState.pageNumber > initialState.pageNumber) {
                    (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> -height } + fadeOut())
                } else {
                    (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                        slideOutHorizontally { height -> height } + fadeOut())
                }.using(
                    SizeTransform(clip = false)
                )
            }
        ) { page ->
            when (page) {
                Pages.MAIN_SETTINGS_PAGE -> MainSettingsPage(
                    accountSettingsViewModel = accountSettingsViewModel,
                    changePageEditProfilePage = { currentPage = Pages.EDIT_PROFILE_PAGE },
                    logOut = logOut
                )

                Pages.EDIT_PROFILE_PAGE -> EditProfilePage(
                    scaffoldState = editProfilePageScaffoldState,
                    user = user,
                    onBackPress = { currentPage = Pages.MAIN_SETTINGS_PAGE },
                    updateProfilePicture = { pfpBitmap ->
                        accountSettingsViewModel.editProfilePicture(pfpBitmap)
                    },
                    changePageEditDisplayName = { currentPage = Pages.EDIT_DISPLAY_NAME_PAGE },
                    changePageEditVenmoUsername = {
                        currentPage = Pages.EDIT_VENMO_USERNAME_PAGE
                    },
                    deleteAccount = {
                        accountSettingsViewModel.deleteAccount(
                            onSuccess = {
                                navigator.popUntilRoot()
                            },
                            onError = {
                                it?.let { message ->
                                    scope.launch {
                                        editProfilePageScaffoldState.snackbarHostState
                                            .showSnackbar(message)
                                    }
                                }
                            }
                        )
                    }
                )

                Pages.EDIT_DISPLAY_NAME_PAGE -> EditDisplayNamePage(
                    displayName = user.displayName,
                    error = error,
                    onBackPress = returnEditProfilePage,
                    editDisplayName = { displayName ->
                        accountSettingsViewModel.editUser(
                            onSuccess = returnEditProfilePage,
                            onError = { error = it }
                        ) { this.displayName = displayName }
                    }
                )

                Pages.EDIT_VENMO_USERNAME_PAGE -> EditVenmoUsernamePage(
                    venmoUsername = user.venmoUsername ?: "",
                    error = error,
                    onBackPress = returnEditProfilePage,
                    editVenmoUserName = { venmoUsername ->
                        accountSettingsViewModel.editUser(
                            onSuccess = returnEditProfilePage,
                            onError = { error = it }
                        ) {
                            if (venmoUsername.isBlank()) {
                                this.venmoUsername = null
                            } else {
                                this.venmoUsername = venmoUsername
                            }
                        }
                    }
                )
            }
        }
    } ?: run(logOut)
}

@Composable
private fun MainSettingsPage(
    accountSettingsViewModel: AccountSettingsViewModel,
    changePageEditProfilePage: () -> Unit,
    logOut: () -> Unit
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

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            H1Header(
                text = "Account Settings",
                color = AppTheme.colors.onSecondary,
                modifier = Modifier.fillMaxWidth()
            )
        }
        accountSettingsViewModel.user?.let { user ->
            item {
                ProfileSettings(
                    user = user,
                    onEditProfile = changePageEditProfilePage
                )
            }
        }
        item {
            Caption(
                text = "Group Notifications",
                modifier = Modifier
                    .padding(bottom = AppTheme.dimensions.paddingMedium)
                    .fillMaxWidth(0.95f)
            )
            NotificationSettings(
                groupNotificationEntries = groupNotificationEntries,
                toggleGroupNotification = { notificationName ->
                    AccountSettingsViewModel.groupNotificationEntries[notificationName]?.let {
                        groupNotificationEntries[notificationName] =
                            accountSettingsViewModel.toggleGroupNotificationType(*it)
                    }
                }
            )
        }
        item {
            H1ErrorTextButton(
                text = "Log Out",
                onClick = logOut,
                scale = 0.9f
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
            ProfileIcon(user = user)
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
            .padding(vertical = AppTheme.dimensions.spacingSmall)
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
private fun SettingSlider(
    text: String,
    toggled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        H1Text(text = text)
        Switch(
            checked = toggled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = AppTheme.colors.confirm),
            modifier = Modifier.scale(0.8f)
        )
    }
}

@Composable
private fun EditProfilePage(
    scaffoldState: ScaffoldState,
    user: User,
    onBackPress: () -> Unit,
    updateProfilePicture: (Bitmap) -> Unit,
    changePageEditDisplayName: () -> Unit,
    changePageEditVenmoUsername: () -> Unit,
    deleteAccount: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val mediaFactory = rememberMediaPickerControllerFactory()
    val picker = remember(mediaFactory) { mediaFactory.createMediaPickerController() }
    BindMediaPickerEffect(picker)

    val settingsMap: Map<String, Pair<String, () -> Unit>> = mapOf(
        "Display Name" to Pair(user.displayName, changePageEditDisplayName),
        "Venmo Username" to Pair(user.venmoUsername ?: "Not set", changePageEditVenmoUsername)
    )

    var showDeleteAccountDialog: Boolean by remember { mutableStateOf(false) }
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { H1Text(text = "Account Deletion") },
            text = @Composable { Caption(text = "Are you sure you want to delete this account?") },
            backgroundColor = AppTheme.colors.secondary,
            confirmButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    H1Text(text = "Cancel")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        deleteAccount()
                    }
                ) { H1Text(text = "Confirm") }
            }
        )
    }

    BackPressScaffold(
        scaffoldState = scaffoldState,
        title = { H1Header(text = "Edit Profile", fontWeight = FontWeight.SemiBold) },
        onBackPress = onBackPress
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            Box(modifier = Modifier.padding(vertical = AppTheme.dimensions.spacingExtraLarge)) {
                ProfileIcon(
                    user = user,
                    modifier = Modifier.clickable {
                        scope.launch {
                            try {
                                updateProfilePicture(picker.pickImage(MediaSource.GALLERY))
                            } catch (exc: DeniedException) {
                                println("denied - $exc")
                            } catch (exc: CanceledException) {
                                println("cancelled - $exc")
                            }
                        }
                    }
                )
            }
            Caption(
                text = "Basic Info",
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(0.95f)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shapes.extraLarge)
                    .background(AppTheme.colors.secondary)
                    .padding(vertical = AppTheme.dimensions.cardPadding)
            ) {
                settingsMap.forEach { (settingName, settingValue) ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = settingValue.second)
                            .padding(
                                horizontal = AppTheme.dimensions.cardPadding,
                                vertical = AppTheme.dimensions.spacingSmall
                            )
                    ) {
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)
                        ) {
                            Caption(text = settingName)
                            H1Text(text = settingValue.first)
                        }
                        SmallIcon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Edit",
                            tint = AppTheme.colors.confirm,
                            iconSize = AppTheme.dimensions.tinyIconSize
                        )
                    }
                }
            }
            H1DenyTextButton(text = "Delete Account", onClick = { showDeleteAccountDialog = true })
        }
    }
}

@Composable
private fun EditDisplayNamePage(
    displayName: String,
    error: String? = null,
    onBackPress: () -> Unit,
    editDisplayName: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    var name: String by remember { mutableStateOf(displayName) }

    BackPressScaffold(onBackPress = onBackPress) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimensions.appPadding)
        ) {
            H1Text(text = "Edit your first and last name")
            Caption(text = "This is name that is displayed on your transactions")
            ProfileTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Display Name",
                error = error,
                modifier = Modifier.focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Save",
                onClick = { editDisplayName(name) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun EditVenmoUsernamePage(
    venmoUsername: String,
    error: String? = null,
    onBackPress: () -> Unit,
    editVenmoUserName: (String) -> Unit
) {
    var username: String by remember { mutableStateOf(venmoUsername ?: "") }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BackPressScaffold(onBackPress = onBackPress) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimensions.appPadding)
        ) {
            H1Text(text = "Edit your Venmo username")
            Caption(text = "This is what others will be using to settle")
            ProfileTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Venmo Username",
                error = error,
                modifier = Modifier.focusRequester(focusRequester)
            )
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Save",
                onClick = { editVenmoUserName(username) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
