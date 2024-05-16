package com.grup.ui.viewmodel

import com.grup.exceptions.APIException

internal class CreateGroupViewModel : LoggedInViewModel() {
    // Group
    fun createGroup(
        groupName: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.createGroup(groupName)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}
