package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class DebtActionViewModel(private val selectedGroupId: String) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _userInfosFlow = apiServer.getAllUserInfosAsFlow()
        .map { userInfos ->
            userInfos.filter { userInfo ->
                userInfo.group.id == selectedGroupId
            }
        }
    val userInfos: StateFlow<List<UserInfo>> = _userInfosFlow.asState()

    private val myUserInfo: StateFlow<UserInfo> = _userInfosFlow.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.user.id == userObject.id
        } ?: throw UserNotInGroupException()
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

        data object EvenSplit : SplitStrategy() {
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
        }

        data object UnevenSplit : SplitStrategy() {
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

        data object PercentageSplit : SplitStrategy() {
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
        debtActionAmounts: Map<UserInfo, Double>,
        message: String,
        onSuccess: (DebtAction) -> Unit
    ) = screenModelScope.launch {
        apiServer.createDebtAction(
            myUserInfo.value,
            debtActionAmounts.map { (userInfo, balanceChange) ->
                TransactionRecord.Companion.DataTransactionRecord(userInfo, balanceChange)
            },
            message,
            DebtAction.Platform.Grupit
        ).let(onSuccess)
    }

    fun createDebtActionVenmo(
        debtActionAmounts: Map<UserInfo, Double>,
        message: String,
        onSuccess: (DebtAction) -> Unit
    ) = screenModelScope.launch {
        apiServer.createDebtAction(
            myUserInfo.value,
            debtActionAmounts.map { (userInfo, balanceChange) ->
                TransactionRecord.Companion.DataTransactionRecord(userInfo, balanceChange)
            },
            message,
            DebtAction.Platform.Venmo
        ).let(onSuccess)
    }
}
