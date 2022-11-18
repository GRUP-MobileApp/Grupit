package com.grup

import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.ITransactionRecordRepository
import com.grup.interfaces.IUserRepository
import com.grup.repositories.*
import org.koin.dsl.module

import com.grup.service.UserService
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { TransactionRecordService() }
}

internal val testRepositoriesModule = module {
    single<IUserRepository> { UserRepository() }
    single<IGroupRepository> { GroupRepository() }
    single<ITransactionRecordRepository> { TransactionRecordRepository() }
}
