package com.grup.android.welcome

import androidx.lifecycle.ViewModel
import com.grup.APIServer
import com.grup.models.User

class WelcomeViewModel : ViewModel() {
    fun registerUserObject(username: String) = APIServer.registerUser(username)
}