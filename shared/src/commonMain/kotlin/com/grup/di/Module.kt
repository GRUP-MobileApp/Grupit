package com.grup.di

import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IImagesRepository
import com.grup.interfaces.ISettingsDataStore
import com.grup.interfaces.ISettleActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.platform.di.httpClient
import com.grup.platform.signin.AuthManager
import com.grup.repositories.AWSImagesRepository
import com.grup.repositories.SettingsDataStore
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
import com.grup.service.AccountSettingsService
import com.grup.service.DebtActionService
import com.grup.service.GroupInviteService
import com.grup.service.GroupService
import com.grup.service.SettleActionService
import com.grup.service.UserInfoService
import com.grup.service.UserService
import com.grup.service.ValidationService
import com.grup.ui.viewmodel.AccountSettingsViewModel
import com.grup.ui.viewmodel.CreateGroupViewModel
import com.grup.ui.viewmodel.GroupDetailsViewModel
import com.grup.ui.viewmodel.GroupMembersViewModel
import com.grup.ui.viewmodel.GroupsViewModel
import com.grup.ui.viewmodel.LoginViewModel
import com.grup.ui.viewmodel.NotificationsViewModel
import com.grup.ui.viewmodel.StartViewModel
import com.grup.ui.viewmodel.TransactionViewModel
import com.grup.ui.viewmodel.WelcomeViewModel
import io.realm.kotlin.Realm
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

internal val releaseRealmRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val debugRealmRepositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository(isDebug = true) }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<ISettleActionRepository> { SyncedSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val testRealmRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<IGroupInviteRepository> { TestGroupInviteRepository() }
    single<IDebtActionRepository> { TestDebtActionRepository() }
    single<ISettleActionRepository> { TestSettleActionRepository() }

    single<IImagesRepository> { AWSImagesRepository() }
    single<ISettingsDataStore> { SettingsDataStore() }
}

internal val viewModelsModule = module {
    factory { GroupsViewModel() }
    factory { GroupDetailsViewModel() }
    factory { GroupMembersViewModel() }
    factory { NotificationsViewModel() }
    factory { CreateGroupViewModel() }
    factory { TransactionViewModel() }
    factory { AccountSettingsViewModel() }
    factory { StartViewModel() }
    factory { LoginViewModel() }
    factory { WelcomeViewModel() }
}

internal fun realmModules(realm: Realm, isDebug: Boolean = false) = module {
    includes(
        servicesModule,
        if (isDebug) debugRealmRepositoriesModule else releaseRealmRepositoriesModule,
        module {
            single { realm }
        }
    )
}

fun initKoin() {
    startKoin {
        modules(
            module {
                single { httpClient }
                single { AuthManager() }
            },
            viewModelsModule,
            servicesModule
        )
    }
}

fun initAuthManager(authManager: AuthManager = AuthManager()) {
    loadKoinModules(
        module {
            single { authManager }
        }
    )
}
