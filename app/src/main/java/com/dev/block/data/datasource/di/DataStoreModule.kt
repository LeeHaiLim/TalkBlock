package com.dev.block.data.datasource.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.dev.block.data.datasource.DataStoreHelper
//import com.example.block.data.LocalProperties
//import com.example.block.data.MailHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataStoreModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(PREFERENCES_STORE_NAME) }
        )

    @Provides
    @Singleton
    fun provideDataStore(dataStore: DataStore<Preferences>): DataStoreHelper =
        DataStoreHelper(dataStore)

    companion object {
        private const val PREFERENCES_STORE_NAME = "AppDataStore"
    }
}