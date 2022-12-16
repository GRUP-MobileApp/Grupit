package com.grup.repositories.abstract

import com.grup.interfaces.IPendingRequestRepository
import com.grup.models.PendingRequest
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmPendingRequestRepository : IPendingRequestRepository {
    protected abstract val realm: Realm

    override fun createPendingRequest(pendingRequest: PendingRequest): PendingRequest? {
        return realm.writeBlocking {
            copyToRealm(pendingRequest)
        }
    }

    override fun findAllPendingRequestsAsFlow(): Flow<List<PendingRequest>> {
        return realm.query<PendingRequest>().asFlow().map { it.list }
    }

    override fun updatePendingRequestStatus(pendingRequest: PendingRequest,
                                            status: PendingRequest.RequestStatus): PendingRequest {
        return realm.writeBlocking {
            findLatest(pendingRequest)!!.apply {
                pendingRequest.status = status
            }
        }
    }

    override fun deletePendingRequest(pendingRequest: PendingRequest) {
        realm.writeBlocking {
            delete(pendingRequest)
        }
    }

    override fun close() {
        realm.close()
    }
}