package com.dev.block.data.network.di

import com.dev.block.data.network.EmailSender
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EmailModule {
    @Provides
    @Singleton
    fun provideEmailSender(): EmailSender = EmailSender()
}