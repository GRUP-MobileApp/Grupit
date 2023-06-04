package com.grup.di

import com.grup.repositories.TestUserRepository
import com.grup.interfaces.*
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.platform.signin.AuthManager
import com.grup.repositories.*
import com.grup.repositories.SyncedUserRepository
import com.grup.service.*
import com.grup.service.GroupService
import com.grup.service.DebtActionService
import com.grup.service.UserService
import org.koin.core.KoinApplication
import org.koin.core.context.loadKoinModules
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserInfoService() }
    single { GroupInviteService() }
    single { DebtActionService() }
    single { SettleActionService() }
}

internal val releaseRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
}

internal val debugRepositoriesModule = module {
    single<IUserRepository> { DevSyncedUserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<IGroupInviteRepository> { TestGroupInviteRepository() }
    single<IDebtActionRepository> { TestDebtActionRepository() }
    single<ISettleActionRepository> { TestSettleActionRepository() }
}

internal val defaultAuthManager = module {
    single { AuthManager() }
}

internal val releaseAppModules =
    listOf(servicesModule, releaseRepositoriesModule, defaultAuthManager)

internal val debugAppModules =
    listOf(servicesModule, debugRepositoriesModule, defaultAuthManager)

expect fun initKoin()

fun initAuthManager(authManager: AuthManager) {
    loadKoinModules(
        module {
            single { authManager }
        }
    )
}
