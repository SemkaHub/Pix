package com.example.pix.di

import com.example.pix.data.error.ErrorHandlerImpl
import com.example.pix.domain.error.ErrorHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideErrorHandler(impl: ErrorHandlerImpl): ErrorHandler
}