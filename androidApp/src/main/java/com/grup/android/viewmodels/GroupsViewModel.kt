package com.grup.android.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.grup.APIServer
import com.grup.models.Group
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GroupsViewModel : ViewModel() {
    val groupsList: StateFlow<List<Group>> = APIServer.getAllGroupsAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
}