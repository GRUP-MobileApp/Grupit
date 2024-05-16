package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.User
import com.grup.other.DateTimeToRealmInstantDeserializer
import com.grup.other.createId
import com.grup.other.toInstant
import com.grup.other.toRealmInstant
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@PersistedName("User")
@Serializable
internal class RealmUser() : User(), RealmObject {
    constructor(username: String) : this() {
        this._username = username
    }
    constructor(realmUser: io.realm.kotlin.mongodb.User, username: String) : this() {
        this._username = username
        this._id = realmUser.id
    }

    @PrimaryKey override var _id: String = createId()

    override val username: String
        get() = _username ?: throw MissingFieldException("User with id $_id missing username")
    override var displayName: String
        get() = _displayName ?: throw MissingFieldException("User with id $_id missing displayName")
        set(value) { _displayName = value }
    override var venmoUsername: String
        get() = _venmoUsername ?: "None"
        set(value) { _venmoUsername = value }
    override var profilePictureURL: String?
        get() = _profilePictureURL
        set(value) { _profilePictureURL = value }
    override var latestViewDate: Instant
        get() = _latestViewDate.toInstant()
        set(value) { _latestViewDate = value.toRealmInstant() }

    @PersistedName("username") @SerialName("username")
    private var _username: String? = null
    @PersistedName("displayName") @SerialName("displayName")
    private var _displayName: String? = null
    @PersistedName("venmoUsername") @SerialName("venmoUsername")
    private var _venmoUsername: String? = null
    @PersistedName("profilePictureURL") @SerialName("profilePictureURL")
    private var _profilePictureURL: String? = null
    @PersistedName("latestViewDate")
    @SerialName("latestViewDate") @Serializable(with = DateTimeToRealmInstantDeserializer::class)
    private var _latestViewDate: RealmInstant = RealmInstant.now()
}
