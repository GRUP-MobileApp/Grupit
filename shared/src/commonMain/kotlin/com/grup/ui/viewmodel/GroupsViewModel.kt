package com.grup.ui.viewmodel

import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow

internal class GroupsViewModel : LoggedInViewModel() {
    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
    val userInfosFlow: StateFlow<List<UserInfo>> = _userInfosFlow.asState()
}