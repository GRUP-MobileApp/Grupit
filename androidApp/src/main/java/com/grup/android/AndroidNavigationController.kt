package com.grup.android

import android.os.Bundle
import androidx.navigation.NavController
import com.grup.ui.NavigationController
import com.grup.ui.viewmodel.TransactionViewModel

class AndroidNavigationController(
    private val navController: NavController
) : NavigationController() {
    override fun navigateActionAmount(transactionType: String, actionId: String?) {
        navController.navigate(
            R.id.actionAmountFragment,
            Bundle().apply {
                this.putString(
                    "actionType",
                    transactionType
                )
                actionId?.let { actionId ->
                    this.putString(
                        "actionId",
                        actionId
                    )
                }
            }
        )
    }

    override fun navigateCreateGroup() {
        navController.navigate(R.id.createGroup)
    }

    override fun navigateGroupMembers() {
        navController.navigate(R.id.viewMembers)
    }

    override fun navigateGroupNotifications() {
        navController.navigate(R.id.openNotifications)
    }

    override fun navigateGroupInvites() {
        navController.navigate(R.id.openGroupInvites)
    }

    override fun navigateMainView() {
        navController.navigate(R.id.startMainFragment)
    }

    override fun navigateDebtAction(amount: Double, message: String) {
        navController.navigate(
            R.id.createDebtAction,
            Bundle().apply {
                this.putDouble("amount", amount)
                this.putString("message", message)
            }
        )
    }

    override fun onBackPress() {
        navController.popBackStack()
    }
}