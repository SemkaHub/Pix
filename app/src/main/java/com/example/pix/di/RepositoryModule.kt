package com.example.pix.di

import com.example.pix.data.repository.PictureRepositoryImpl
import com.example.pix.domain.repository.PictureRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPictureRepository(
        pictureRepositoryImpl: PictureRepositoryImpl
    ): PictureRepository
}