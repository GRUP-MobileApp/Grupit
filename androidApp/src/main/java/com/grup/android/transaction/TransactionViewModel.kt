package com.grup.android.transaction

import com.grup.APIServer
import com.grup.android.MainViewModel
import com.grup.android.ViewModel
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TransactionViewModel : ViewModel() {
    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup
    private val _userInfosFlow = APIServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.combine(selectedGroup) { userInfos, selectedGroup ->
            selectedGroup?.let { nonNullGroup ->
                userInfos.filter { userInfo ->
                    userInfo.groupId == nonNullGroup.getId()
                }
            } ?: emptyList()
        }.asState()

    val myUserInfo: StateFlow<UserInfo> = userInfos.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.userId == userObject.getId()
        }!!
    }.asState()

    // DebtAction operations
    fun createDebtAction(userInfos: List<UserInfo>, debtAmounts: List<Double>) =
        APIServer.createDebtAction(
            userInfos.zip(debtAmounts).map { (userInfo, balanceChange) ->
                TransactionRecord().apply {
                    this.debtor = userInfo.userId!!
                    this.debtorName = userInfo.nickname!!
                    this.balanceChange = balanceChange
                }
            },
            myUserInfo.value
        )
}