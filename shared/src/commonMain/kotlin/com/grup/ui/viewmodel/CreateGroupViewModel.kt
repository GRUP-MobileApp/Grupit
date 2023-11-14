package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.models.Group
import kotlinx.coroutines.launch

internal class CreateGroupViewModel : LoggedInViewModel() {
    // Group
    fun createGroup(
        groupName: String,
        onSuccess: (Group) -> Unit,
        onFailure: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            apiServer.createGroup(groupName).let(onSuccess)
        } catch (e: APIException) {
            onFailure(e.message)
        }
    }
}