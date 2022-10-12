package com.grup

import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import com.grup.service.UserBalanceService
import com.grup.service.UserService
import org.koin.dsl.module
import org.litote.kmongo.KMongo

val serviceModule = module {
    single { KMongo.createClient() }
    single { UserService() }
    single { GroupService() }
    single { UserBalanceService() }
    single { TransactionRecordService() }
}