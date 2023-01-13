package com.grup.di

import com.grup.interfaces.*
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.repositories.*
import com.grup.service.*
import com.grup.service.GroupService
import com.grup.service.DebtActionService
import com.grup.service.UserService
import org.koin.dsl.module

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserInfoService() }
    single { DebtActionService() }
    single { GroupInviteService() }
}

internal val repositoriesModule = module {
    single<IUserRepository> { UserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<IDebtActionRepository> { SyncedDebtActionRepository() }
    single<IGroupInviteRepository> { SyncedGroupInviteRepository() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { UserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<IDebtActionRepository> { TestDebtActionRepository() }
}
