package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.exceptions.APIException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.TransactionRecord.Companion.DataTransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class TransactionViewModel : LoggedInViewModel() {
    companion object {
        const val DEBT = "Request"
        const val SETTLE = "Settle"
        const val SETTLE_TRANSACTION = "Add"
    }

    private val selectedGroup
        get() = MainViewModel.selectedGroup

    // Hot flow containing UserInfo's belonging to the selectedGroup. Assumes selectedGroup does not
    // change during lifecycle.
    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
        .map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.groupId == selectedGroup.id
            }
        }
    val userInfos: StateFlow<List<UserInfo>> = _userInfosFlow.map { userInfos ->
        userInfos.filter { userInfo ->
            userInfo.user.id != userObject.id
        }
    }.asState()

    val myUserInfo: StateFlow<UserInfo> = _userInfosFlow.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.user.id == userObject.id
        }!!
    }.asState()


    sealed class SplitStrategy {
        companion object {
            val strategies = listOf(EvenSplit, UnevenSplit, PercentageSplit)
        }
        abstract val name: String
        open val editable: Boolean = true
        open val showMoneyAmount: Boolean = false

        abstract fun generateSplit(
            totalMoneyAmount: Double,
            rawSplitStrategyAmounts: Map<UserInfo, Double?>
        ): Map<UserInfo, Double>

        abstract fun splitToMoneyAmounts(
            totalMoneyAmount: Double,
            splitStrategyAmounts: Map<UserInfo, Double>
        ): Map<UserInfo, Double>

        open fun isValid(
            totalMoneyAmount: Double,
            rawSplitStrategyAmounts: Map<UserInfo, Double>
        ): Boolean =
            rawSplitStrategyAmounts.values.sum() == totalMoneyAmount

        object EvenSplit : SplitStrategy() {
            override val name: String = "Even Split"
            override val editable: Boolean = false
            override fun generateSplit(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.keys.associateWith {
                totalMoneyAmount / rawSplitStrategyAmounts.size
            }

            override fun splitToMoneyAmounts(
                totalMoneyAmount: Double,
                splitStrategyAmounts: Map<UserInfo, Double>
            ): Map<UserInfo, Double> = splitStrategyAmounts

            override fun isValid(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double>
            ): Boolean = true
        }

        object UnevenSplit : SplitStrategy() {
            override val name: String = "Uneven Split"

            override fun generateSplit(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.mapValues { entry ->
                entry.value ?: rawSplitStrategyAmounts.values.let { values ->
                    (totalMoneyAmount - values.filterNotNull().sum()) / values.count { it == null }
                }
            }

            override fun splitToMoneyAmounts(
                totalMoneyAmount: Double,
                splitStrategyAmounts: Map<UserInfo, Double>
            ): Map<UserInfo, Double> = splitStrategyAmounts
        }

        object PercentageSplit : SplitStrategy() {
            override val name: String = "Percentage Split"
            override val showMoneyAmount: Boolean = true

            override fun generateSplit(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.mapValues { entry ->
                entry.value ?: rawSplitStrategyAmounts.values.let { values ->
                    (100 - values.filterNotNull().sum()) / values.count { it == null }
                }
            }

            override fun splitToMoneyAmounts(
                totalMoneyAmount: Double,
                splitStrategyAmounts: Map<UserInfo, Double>
            ): Map<UserInfo, Double> = splitStrategyAmounts

            override fun isValid(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double>
            ): Boolean =
                rawSplitStrategyAmounts.values.sum() == 100.0
        }
    }

    // DebtAction
    fun createDebtAction(
        debtAmounts: Map<UserInfo, Double>,
        message: String
    ) = apiServer.createDebtAction(
        debtAmounts.map { (userInfo, balanceChange) ->
            DataTransactionRecord(userInfo, balanceChange)
        },
        myUserInfo.value,
        message
    )

    // SettleAction
    fun createSettleAction(
        settleAmount: Double, onSuccess: (SettleAction) -> Unit, onFailure: (String?) -> Unit
    ) = coroutineScope.launch {
        try {
            apiServer.createSettleAction(settleAmount, myUserInfo.value).let(onSuccess)
        } catch (e: APIException) {
            onFailure(e.message)
        }

    }

    fun createSettleActionTransaction(
        settleAction: SettleAction,
        amount: Double,
        myUserInfo: UserInfo
    ) = coroutineScope.launch {
        apiServer.createSettleActionTransaction(
            settleAction,
            DataTransactionRecord(myUserInfo, amount)
        )
    }

    fun getSettleAction(settleActionId: String) =
        apiServer.getAllSettleActionsAsFlow().map { settleActions ->
            settleActions.find { settleActionId == it.id }!!
        }.asState()
}
