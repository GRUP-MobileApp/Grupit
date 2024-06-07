package com.grup.ui.viewmodel

import com.grup.exceptions.UserNotInGroupException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.ui.compose.roundTwoDecimalPlaces
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

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

    private val myUserInfo: StateFlow<UserInfo?> = _userInfosFlow.map { userInfos ->
        userInfos.find { userInfo ->
            userInfo.user.id == userObject.id
        }
    }.asState()


    sealed class SplitStrategy {
        companion object {
            val strategies = listOf(EvenSplit, UnevenSplit, PercentageSplit)
        }

        abstract val name: String
        open val editable: Boolean = true

        abstract fun generateSplitStrategyAmounts(
            totalMoneyAmount: Double,
            rawSplitStrategyAmounts: Map<UserInfo, Double?>
        ): Map<UserInfo, Double>

        abstract fun generateMoneyAmounts(
            totalMoneyAmount: Double,
            rawSplitStrategyAmounts: Map<UserInfo, Double?>
        ): Map<UserInfo, Double>

        fun isValid(
            totalMoneyAmount: Double,
            rawSplitStrategyAmounts: Map<UserInfo, Double?>
        ): Boolean = generateMoneyAmounts(
            totalMoneyAmount,
            rawSplitStrategyAmounts
        ).values.sum() == totalMoneyAmount

        protected fun Map<UserInfo, Double>.distributeRemainder(
            totalMoneyAmount: Double,
            keys: Set<UserInfo>,
            increment: Double
        ): Map<UserInfo, Double> = this.toMutableMap().apply {
                val keyIterator = keys.iterator()
                while (keyIterator.hasNext() && this.values.sum() < totalMoneyAmount) {
                    this[keyIterator.next()]?.let {
                        this[keyIterator.next()] = it + increment
                    }
                }
            }

        data object EvenSplit : SplitStrategy() {
            override val name: String = "Even Split"
            override val editable: Boolean = false

            override fun generateSplitStrategyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.keys.associateWith {
                (totalMoneyAmount / rawSplitStrategyAmounts.size).roundTwoDecimalPlaces()
            }.distributeRemainder(totalMoneyAmount, rawSplitStrategyAmounts.keys, 0.01)

            override fun generateMoneyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> =
                generateSplitStrategyAmounts(totalMoneyAmount, rawSplitStrategyAmounts)
        }

        data object UnevenSplit: SplitStrategy() {
            override val name: String = "Uneven Split"
            override fun generateSplitStrategyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.mapValues { entry ->
                    entry.value ?: rawSplitStrategyAmounts.values.let { values ->
                        (totalMoneyAmount - values.filterNotNull().sum()) /
                                values.count { it == null }
                    }.roundTwoDecimalPlaces()
                }.distributeRemainder(
                    totalMoneyAmount,
                    rawSplitStrategyAmounts.filter { (_, amount) ->
                        amount == null
                    }.keys,
                    0.01
                )

            override fun generateMoneyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> =
                generateSplitStrategyAmounts(totalMoneyAmount, rawSplitStrategyAmounts)
        }

        data object PercentageSplit: SplitStrategy() {
            override val name: String = "Percentage Split"

            override fun generateSplitStrategyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> = rawSplitStrategyAmounts.mapValues { (_, percent) ->
                percent ?: rawSplitStrategyAmounts.values.let { values ->
                    (100 - values.filterNotNull().sum()) / values.count { it == null }
                }
            }

            override fun generateMoneyAmounts(
                totalMoneyAmount: Double,
                rawSplitStrategyAmounts: Map<UserInfo, Double?>
            ): Map<UserInfo, Double> =
                generateSplitStrategyAmounts(totalMoneyAmount, rawSplitStrategyAmounts)
                    .mapValues { (_, percent) ->
                        totalMoneyAmount * percent / 100.0
                    }
        }
    }

    // DebtAction
    fun createDebtAction(
        debtActionAmounts: Map<UserInfo, Double>,
        message: String,
        onSuccess: (DebtAction) -> Unit
    ) = launchJob {
        apiServer.createDebtAction(
            myUserInfo.value ?: throw UserNotInGroupException(),
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
    ) = launchJob {
        apiServer.createDebtAction(
            myUserInfo.value ?: throw UserNotInGroupException(),
            debtActionAmounts.map { (userInfo, balanceChange) ->
                TransactionRecord.Companion.DataTransactionRecord(userInfo, balanceChange)
            },
            message,
            DebtAction.Platform.Venmo
        ).let(onSuccess)
    }
}
