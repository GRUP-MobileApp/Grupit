package com.grup.di

import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.ITransactionRecordRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.repositories.*
import org.koin.dsl.module

import com.grup.service.UserService
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import com.grup.service.UserInfoService

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserInfoService() }
    single { TransactionRecordService() }
}

internal val repositoriesModule = module {
    single<IUserRepository> { UserRepository() }
    single<IGroupRepository> { SyncedGroupRepository() }
    single<IUserInfoRepository> { SyncedUserInfoRepository() }
    single<ITransactionRecordRepository> { SyncedTransactionRecordRepository() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { UserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserInfoRepository> { TestUserInfoRepository() }
    single<ITransactionRecordRepository> { TestTransactionRecordRepository() }
}
