package com.grup.android.transaction

import com.grup.APIServer
import com.grup.android.MainViewModel
import com.grup.android.ViewModel
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class TransactionViewModel : ViewModel() {
    companion object {
        const val DEBT = "Request"
        const val SETTLE = "Settle"
        const val SETTLE_TRANSACTION = "Add"
    }

    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = APIServer.getAllUserInfosAsFlow()
        .map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.groupId == selectedGroup.getId()
            }
        }
    val userInfos: StateFlow<List<UserInfo>> = _userInfosFlow.map { userInfos ->
        userInfos.filter { userInfo ->
            userInfo.userId != userObject.getId()
        }
    }.asState()

    val myUserInfo: StateFlow<UserInfo> = _userInfosFlow.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.userId == userObject.getId()
        }!!
    }.asState()

    sealed class SplitStrategy {
        abstract val name: String
        abstract fun generateSplit(transactionAmount: Double, numPeople: Int): List<Double>

        object EvenSplit : SplitStrategy() {
            override val name: String = "Even Split"

            override fun generateSplit(transactionAmount: Double, numPeople: Int): List<Double> =
                List(numPeople) { transactionAmount / numPeople }
        }
    }

    // DebtAction
    fun createDebtAction(userInfos: List<UserInfo>,
                         debtAmounts: List<Double>,
                         message: String) =
        APIServer.createDebtAction(
            userInfos.zip(debtAmounts).map { (userInfo, balanceChange) ->
                TransactionRecord().apply {
                    this.debtorUserInfo = userInfo
                    this.balanceChange = balanceChange
                }
            },
            myUserInfo.value,
            message
        )

    // SettleAction
    fun createSettleAction(settleAmount: Double) =
        APIServer.createSettleAction(settleAmount, myUserInfo.value)

    fun createSettleActionTransaction(
        settleAction: SettleAction,
        amount: Double,
        myUserInfo: UserInfo
    ) = APIServer.createSettleActionTransaction(
            settleAction,
            TransactionRecord().apply {
                this.balanceChange = amount
                this.debtorUserInfo = myUserInfo
            }
        )

    fun getSettleAction(settleActionId: String) =
        APIServer.getAllSettleActionsAsFlow().map { settleActions ->
            settleActions.find { settleActionId == it.getId() }!!
        }.asState()
}
