package com.dev.block.data.repository.di

import com.dev.block.data.datasource.DataStoreHelper
import com.dev.block.data.network.EmailSender
import com.dev.block.data.repository.BlockStateRepository
import com.dev.block.data.repository.EmailRepository
import com.dev.block.data.repository.PasswordRepository
import com.dev.block.data.repository.PermissionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideBlockStateRepository(dataStoreHelper: DataStoreHelper) =
        BlockStateRepository(dataStoreHelper)

    @Provides
    @Singleton
    fun providePasswordRepository(dataStoreHelper: DataStoreHelper) =
        PasswordRepository(dataStoreHelper)

    @Provides
    @Singleton
    fun provideEmailRepository(emailSender: EmailSender, dataStoreHelper: DataStoreHelper) =
        EmailRepository(emailSender, dataStoreHelper)

    @Provides
    @Singleton
    fun providePermissionRepository(dataStoreHelper: DataStoreHelper) =
        PermissionRepository(dataStoreHelper)
}