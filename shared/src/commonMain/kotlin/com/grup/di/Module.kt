package com.grup.di

import com.grup.device.DeviceManager
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IImagesRepository
import com.grup.interfaces.ISettleActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.platform.di.httpClient
import com.grup.repositories.AWSImagesRepository
import com.grup.repositories.SyncedDebtActionRepository
import com.grup.repositories.SyncedGroupInviteRepository
import com.grup.repositories.SyncedGroupRepository
import com.grup.repositories.SyncedSettleActionRepository
import com.grup.repositories.SyncedUserInfoRepository
import com.grup.repositories.SyncedUserRepository
import com.grup.repositories.TestDebtActionRepository
import com.grup.repositories.TestGroupInviteRepository
import com.grup.repositories.TestGroupRepository
import com.grup.repositories.TestSettleActionRepository
import com.grup.repositories.TestUserInfoRepository
import com.grup.repositories.TestUserRepository
import io.realm.kotlin.Realm
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal val releaseRealmRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
}

internal val debugRealmRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository(isDebug = true) }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
}

internal val testRealmRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<IGroupInviteRepository> { TestGroupInviteRepository() }
    single<IDebtActionRepository> { TestDebtActionRepository() }
    single<ISettleActionRepository> { TestSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
}

internal fun realmModules(realm: Realm, isDebug: Boolean = false) = module {
    includes(
        if (isDebug) debugRealmRepositoriesModule else releaseRealmRepositoriesModule,
        module {
            single { realm }
        }
    )
}

fun initKoin() {
    startKoin {
        allowOverride(false)
        modules(
            module {
                single { httpClient }
            }
        )
    }
}

fun initDeviceManager(deviceManager: DeviceManager) {
    loadKoinModules(
        module {
            single { deviceManager }
        }
    )
}
