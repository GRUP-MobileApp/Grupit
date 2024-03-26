package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import kotlinx.coroutines.launch

internal class CreateGroupViewModel : LoggedInViewModel() {
    // Group
    fun createGroup(
        groupName: String,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            apiServer.createGroup(groupName)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}
