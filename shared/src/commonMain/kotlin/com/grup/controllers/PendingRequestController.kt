package com.grup.controllers

import com.grup.models.PendingRequest
import com.grup.service.PendingRequestService
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object PendingRequestController : KoinComponent {
    private val pendingRequestService: PendingRequestService by inject()

    fun getAllPendingRequestsAsFlow(): Flow<List<PendingRequest>> {
        return pendingRequestService.getAllPendingRequestsAsFlow()
    }
}