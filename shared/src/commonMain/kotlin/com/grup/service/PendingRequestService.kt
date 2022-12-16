package com.grup.service

import com.grup.interfaces.IPendingRequestRepository
import com.grup.models.Group
import com.grup.models.PendingRequest
import com.grup.models.TransactionRecord
import com.grup.models.User
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class PendingRequestService: KoinComponent {
    private val pendingRequestRepository: IPendingRequestRepository by inject()

    fun createGroupInviteRequest(user: User, group: Group): PendingRequest? {
        return pendingRequestRepository.createPendingRequest(
            PendingRequest().apply {
                this.requester = group._id
                this.target = user._id
                this.requestType = PendingRequest.RequestType.GROUP_INVITE
            }
        )
    }

    fun createFriendRequest(user: User, friend: User): PendingRequest? {
        return pendingRequestRepository.createPendingRequest(
            PendingRequest().apply {
                this.requester = user._id
                this.target = friend._id
                this.requestType = PendingRequest.RequestType.FRIEND
            }
        )
    }

//    fun createTransactionRecordRequests(
//        user: User,
//        transactionRecord: TransactionRecord
//    ): List<PendingRequest> {
//        return transactionRecord.balanceChanges
//            .filter { balanceChangeRecord -> balanceChangeRecord.userId != user._id }
//            .map { balanceChangeRecord ->
//                pendingRequestRepository.createPendingRequest(
//                    PendingRequest().apply {
//                        this.requester = user._id
//                        this.target = balanceChangeRecord.userId
//                        this.requestType = PendingRequest.RequestType.TRANSACTION_RECORD
//                        this.requestObject = transactionRecord
//                    })
//                }
//            }

    fun acceptPendingRequest(pendingRequest: PendingRequest) {
        pendingRequestRepository.updatePendingRequestStatus(pendingRequest,
            PendingRequest.RequestStatus.ACCEPTED)
    }

    fun rejectPendingRequest(pendingRequest: PendingRequest) {
        pendingRequestRepository.updatePendingRequestStatus(pendingRequest,
            PendingRequest.RequestStatus.REJECTED)
    }

    fun getAllPendingRequestsAsFlow(): Flow<List<PendingRequest>> {
        return pendingRequestRepository.findAllPendingRequestsAsFlow()
    }
}