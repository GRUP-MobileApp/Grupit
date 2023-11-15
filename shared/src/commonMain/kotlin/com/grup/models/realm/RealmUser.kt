package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.User
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@PersistedName("User")
@Serializable
internal class RealmUser() : User(), RealmObject {
    constructor(realmId: String) : this() {
        this._id = realmId
    }

    @PrimaryKey override var _id: String = createId()

    override val username: String
        get() = _username ?: throw MissingFieldException("User with id $_id missing username")
    override var displayName: String
        get() = _displayName ?: throw MissingFieldException("User with id $_id missing displayName")
        set(value) { _displayName = value }
    override var venmoUsername: String?
        get() = _venmoUsername
        set(value) { _venmoUsername = value }
    override var profilePictureURL: String
        get() = _profilePictureURL
            ?: throw MissingFieldException("User with id $_id missing displayName")
        set(value) { _profilePictureURL = value }
    override var latestViewDate: String
        get() = _latestViewDate
        set(value) { _latestViewDate = value }

    @PersistedName("username") @SerialName("username")
    var _username: String? = null
    @PersistedName("displayName") @SerialName("displayName")
    var _displayName: String? = null
    @PersistedName("venmoUsername") @SerialName("venmoUsername")
    var _venmoUsername: String? = null
    @PersistedName("profilePictureURL") @SerialName("profilePictureURL")
    var _profilePictureURL: String? = null
    @PersistedName("latestViewDate") @SerialName("latestViewDate")
    var _latestViewDate: String = getCurrentTime()
}
