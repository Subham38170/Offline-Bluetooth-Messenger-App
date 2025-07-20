package com.example.bluetoothmessenger.di

import android.content.Context
import com.example.bluetoothmessenger.data.chat.AndroidBluetoothController
import com.example.bluetoothmessenger.domain.chat.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BluetoothModule {
    @Provides
    @Singleton
    fun provideBluetoothController(
        @ApplicationContext context: Context
    ): BluetoothController {
        return AndroidBluetoothController(context)
    }
}