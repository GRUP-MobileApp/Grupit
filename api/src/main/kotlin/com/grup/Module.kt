package com.grup

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
    single { UserRepository() }
}

val testRepositoriesModule = module {
    single { TestUserRepository() }
    single { TestGroupRepository() }
    single { TestUserBalanceRepository() }
    single { TestTransactionRecordRepository() }
}