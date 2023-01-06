package com.grup.android

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.grup.APIServer
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class MainViewModel : ViewModel() {
    val groupsList: StateFlow<List<Group>> = APIServer.getAllGroupsAsFlow().let { flow ->
        runBlocking { flow.first() }.let { initialGroupsList ->
            flow.stateIn(viewModelScope, SharingStarted.Eagerly, initialGroupsList)
        }
    }

    private val userInfosMap: MutableMap<String, StateFlow<List<UserInfo>>> = mutableMapOf()
    fun getUserInfosByGroup(group: Group) = userInfosMap.getOrPut(group.getId()) {
        APIServer.getUserInfosByGroupIdAsFlow(group).let { flow ->
            runBlocking { flow.first() }.let { initialUserInfosList ->
                flow.stateIn(viewModelScope, SharingStarted.Eagerly, initialUserInfosList)
            }
        }
    }

    val groupInvitesList: StateFlow<List<GroupInvite>> = APIServer.getAllGroupInvitesAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}