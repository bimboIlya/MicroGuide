package com.example.microguide.di

import com.example.microguide.data.repository.PlaceholderRepository
import com.example.microguide.data.repository.PlaceholderRepositoryImpl
import com.example.microguide.data.repository.RxPlaceholderRepository
import com.example.microguide.data.repository.RxPlaceholderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindRxPlaceholderRepository(repository: RxPlaceholderRepositoryImpl) : RxPlaceholderRepository

    @Binds
    fun bindPlaceholderRepository(repository: PlaceholderRepositoryImpl) : PlaceholderRepository
}