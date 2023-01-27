package com.grup.android.welcome

import com.grup.APIServer
import com.grup.android.ViewModel
import com.grup.exceptions.login.UserObjectNotFoundException

class WelcomeViewModel : ViewModel() {
    val hasUserObject: Boolean
        get() = try {
            userObject
            true
        } catch (e: UserObjectNotFoundException) {
            false
        }

    fun registerUserObject(username: String) = APIServer.registerUser(username)
}