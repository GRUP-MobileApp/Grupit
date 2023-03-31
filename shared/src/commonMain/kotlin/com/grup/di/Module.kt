package com.grup.di

import com.grup.repositories.TestUserRepository
import com.grup.interfaces.*
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.repositories.*
import com.grup.repositories.SyncedUserRepository
import com.grup.service.*
import com.grup.service.GroupService
import com.grup.service.DebtActionService
import com.grup.service.UserService
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

internal val servicesModule = module {
    single { UserService() }
    single { GroupService() }
    single { UserInfoService() }
    single { GroupInviteService() }
    single { DebtActionService() }
    single { SettleActionService() }
}

internal val repositoriesModule = module {
    single<IUserRepository> { SyncedUserRepository() }
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

internal val httpClientModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        prettyPrint = true
                    },
                    contentType = ContentType.Application.Json
                )
            }
        }
    }
}


internal val releaseAppModules = listOf(
    servicesModule, repositoriesModule, httpClientModule
)
