package com.scifi.githubuserapp.data.di

import android.content.Context
import com.scifi.githubuserapp.data.FavoriteRepository
import com.scifi.githubuserapp.data.local.room.FavoriteDatabase
import com.scifi.githubuserapp.data.retrofit.ApiConfig
import com.scifi.githubuserapp.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context) : FavoriteRepository {
        val apiService = ApiConfig.getApiService()
        val database = FavoriteDatabase.getInstance(context)
        val dao = database.favoriteDao()
        val appExecutors = AppExecutors()
        return FavoriteRepository.getInstance(apiService, dao, appExecutors)
    }
}