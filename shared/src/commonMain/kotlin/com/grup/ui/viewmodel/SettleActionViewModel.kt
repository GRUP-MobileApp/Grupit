package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.NotFoundException
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class SettleActionViewModel(private val selectedGroupId: String) : LoggedInViewModel() {
    private val _myUserInfosFlow: Flow<List<UserInfo>> = apiServer.getMyUserInfosAsFlow()

    val myUserInfo: StateFlow<UserInfo?> = _myUserInfosFlow.map { userInfos ->
        userInfos.find { it.group.id == selectedGroupId }
    }.asState()

    // SettleAction
    fun createSettleAction(
        amount: Double,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.createSettleAction(
                myUserInfo.value ?: throw NotFoundException("User info not found"),
                amount
            )
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}