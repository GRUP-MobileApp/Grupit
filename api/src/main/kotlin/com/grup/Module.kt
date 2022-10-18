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

val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserBalanceService() }
    single { TransactionRecordService() }
}

val repositoriesModule = module {
    single<IUserRepository> { UserRepository() }
}

val testRepositoriesModule = module {
    single<IUserRepository> { TestUserRepository() }
    single<IGroupRepository> { TestGroupRepository() }
    single<IUserBalanceRepository> { TestUserBalanceRepository() }
    single<ITransactionRecordRepository> { TestTransactionRecordRepository() }
}