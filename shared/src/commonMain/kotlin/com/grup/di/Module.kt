package com.grup.di

import com.grup.repositories.TestUserRepository
import com.grup.interfaces.*
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.platform.di.httpClient
import com.grup.platform.signin.AuthManager
import com.grup.repositories.*
import com.grup.repositories.SyncedUserRepository
import com.grup.service.*
import com.grup.service.GroupService
import com.grup.service.DebtActionService
import com.grup.service.UserService
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserInfoService() }
    single { GroupInviteService() }
    single { DebtActionService() }
    single { SettleActionService() }
    single { AccountSettingsService() }
    single { ValidationService() }
}

internal val releaseRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val debugRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository(isDebug = true) }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<IGroupInviteRepository> { TestGroupInviteRepository() }
    single<IDebtActionRepository> { TestDebtActionRepository() }
    single<ISettleActionRepository> { TestSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val releaseAppModule = module {
    includes(servicesModule, releaseRepositoriesModule)
}

internal val debugAppModule = module {
    includes(servicesModule, debugRepositoriesModule)
}

fun initKoin() {
    startKoin {
        modules(
            module {
                single { httpClient }
                single { AuthManager() }
            }
        )
    }
}

fun initAuthManager(authManager: AuthManager) {
    loadKoinModules(
        module {
            single { authManager }
        }
    )
}
