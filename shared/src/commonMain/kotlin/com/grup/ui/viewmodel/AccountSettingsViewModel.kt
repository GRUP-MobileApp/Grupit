package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.device.DeviceManager
import com.grup.device.SettingsManager.AccountSettings
import com.grup.exceptions.ValidationException
import com.grup.models.User
import com.grup.platform.image.cropCenterSquareImage
import dev.icerock.moko.media.Bitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject


internal class AccountSettingsViewModel : LoggedInViewModel() {
    enum class Pages(val pageNumber: Int) {
        MAIN_SETTINGS_PAGE(0),
        EDIT_PROFILE_PAGE(1),
        EDIT_DISPLAY_NAME_PAGE(2),
        EDIT_VENMO_USERNAME_PAGE(3)
    }

    companion object {
        val groupNotificationEntries:
                Map<String, Array<AccountSettings.GroupNotificationType>> = mapOf(
            "Incoming money requests" to arrayOf(
                AccountSettings.GroupNotificationType.NewDebtAction,
                AccountSettings.GroupNotificationType.NewSettleActionTransaction
            ),
            "Updates to your outgoing requests" to arrayOf(
                AccountSettings.GroupNotificationType.AcceptDebtAction,
                AccountSettings.GroupNotificationType.AcceptSettleActionTransaction
            ),
            "Group requests" to arrayOf(
                AccountSettings.GroupNotificationType.NewSettleAction,
            ),
            "Group invites" to arrayOf(
                AccountSettings.GroupNotificationType.NewGroupInvite
            )
        )

    }

    private val deviceManager: DeviceManager by inject()

    public override val userObject: User
        get() = super.userObject

    fun getGroupNotificationType(
        vararg notificationTypes: AccountSettings.GroupNotificationType
    ): Boolean = notificationTypes.fold(true) { and, notificationType ->
        and && AccountSettings.getGroupNotificationType(notificationType.name)
    }
    fun toggleGroupNotificationType(
        vararg notificationTypes: AccountSettings.GroupNotificationType
    ): Boolean = notificationTypes.map { notificationType ->
        AccountSettings.toggleGroupNotificationType(notificationType.name)
    }.reduce { acc, isToggled ->
        acc && isToggled
    }

    fun editUser(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit,
        block: User.() -> Unit,
    ) = launchJob {
        try {
            apiServer.updateUser(block)
            onSuccess()
        } catch (e: ValidationException) {
            onError(e.message)
        }
    }

    fun editProfilePicture(pfpBitmap: Bitmap) = screenModelScope.launch {
        apiServer.updateProfilePicture(cropCenterSquareImage(pfpBitmap))
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun logOut(onSuccess: () -> Unit) = launchJob(GlobalScope) {
        deviceManager.authManager.getSignInManagerFromProvider(apiServer.authProvider)?.signOut()
        apiServer.logOut()
        onSuccess()
    }
}