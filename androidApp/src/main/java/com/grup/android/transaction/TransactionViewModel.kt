package com.grup.android.transaction

import com.grup.APIServer
import com.grup.android.MainViewModel
import com.grup.android.ViewModel
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class TransactionViewModel : ViewModel() {
    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = APIServer.getAllUserInfosAsFlow()
    val userInfos: StateFlow<List<UserInfo>> =
        _userInfosFlow.map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.groupId == selectedGroup.getId()
            }
        }.asState()

    val myUserInfo: StateFlow<UserInfo> = userInfos.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.userId == userObject.getId()
        }!!
    }.asState()

    // DebtAction
    fun createDebtAction(userInfos: List<UserInfo>, debtAmounts: List<Double>) =
        APIServer.createDebtAction(
            userInfos.zip(debtAmounts).map { (userInfo, balanceChange) ->
                TransactionRecord().apply {
                    this.debtorUserInfo = userInfo
                    this.balanceChange = balanceChange
                }
            },
            myUserInfo.value
        )

    // SettleAction
    fun createSettleAction(settleAmount: Double) =
        APIServer.createSettleAction(settleAmount, myUserInfo.value)
}
