package com.grup

import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.ITransactionRecordRepository
import com.grup.interfaces.IUserBalanceRepository
import com.grup.interfaces.IUserRepository
import com.grup.repositories.*
import org.koin.dsl.module

import com.grup.service.UserService
import com.grup.service.GroupService
import com.grup.service.UserBalanceService
import com.grup.service.TransactionRecordService

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserBalanceService() }
    single { TransactionRecordService() }
}

internal val repositoriesModule = module {
    single<IUserRepository> { UserRepository() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserBalanceRepository> { TestUserBalanceRepository() }
    single<ITransactionRecordRepository> { TestTransactionRecordRepository() }
}
