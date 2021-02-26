package com.vb.anonymous.di

import com.vb.anonymous.data.repository.BoardApi
import com.vb.anonymous.domain.repository.BoardRepo
import com.vb.anonymous.data.repository.BoardRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    @Singleton
    fun provideBoardService(): BoardApi = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
        .baseUrl("https://2ch.hk/")
        .build()
        .create(BoardApi::class.java)

    @Provides
    @Singleton
    fun provideBoardRepo(boardApi: BoardApi): BoardRepo = BoardRepoImpl(boardApi)

}