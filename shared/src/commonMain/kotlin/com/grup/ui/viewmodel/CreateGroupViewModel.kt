package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.ValidationException

internal class CreateGroupViewModel : LoggedInViewModel() {
    // Group
    fun createGroup(
        groupName: String,
        onSuccess: () -> Unit,
        onValidationError: (String?) -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.createGroup(groupName)
            onSuccess()
        } catch (e: ValidationException) {
            onValidationError(e.message)
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}
