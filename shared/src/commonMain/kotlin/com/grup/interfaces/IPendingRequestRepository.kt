package com.grup.interfaces

import com.grup.models.PendingRequest
import kotlinx.coroutines.flow.Flow

internal interface IPendingRequestRepository : IRepository {
    fun createPendingRequest(pendingRequest: PendingRequest): PendingRequest?

    fun findAllPendingRequestsAsFlow(): Flow<List<PendingRequest>>

    fun updatePendingRequestStatus(pendingRequest: PendingRequest,
                                   status: PendingRequest.RequestStatus): PendingRequest

    fun deletePendingRequest(pendingRequest: PendingRequest)
}