package com.grup.ui

abstract class NavigationController {
    // From MainView
    abstract fun navigateActionAmount(transactionType: String, actionId: String? = null)
    abstract fun navigateCreateGroup()
    abstract fun navigateGroupMembers()
    abstract fun navigateGroupNotifications()
    abstract fun navigateGroupInvites()

    // From WelcomeView
    abstract fun navigateMainView()

    // From ActionAmountView
    abstract fun navigateDebtAction(amount: Double, message: String = "")

    abstract fun onBackPress()
}