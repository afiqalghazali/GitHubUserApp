package com.scifi.githubuserapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.scifi.githubuserapp.BuildConfig
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity
import com.scifi.githubuserapp.data.local.room.FavoriteDao
import com.scifi.githubuserapp.data.response.GithubResponse
import com.scifi.githubuserapp.data.retrofit.ApiService
import com.scifi.githubuserapp.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoriteRepository private constructor(
    private val apiService: ApiService,
    private val favoriteDao: FavoriteDao,
    private val appExecutors: AppExecutors
){
    private val result = MediatorLiveData<Result<List<FavoriteEntity>>>()

    fun getUsers(): LiveData<Result<List<FavoriteEntity>>> {
        result.value = Result.Loading
        val client = apiService.getUserByUsername(BuildConfig.API_KEY)
        client.enqueue(object : Callback<GithubResponse> {
            override fun onResponse(
                call: Call<GithubResponse>,
                response: Response<GithubResponse>
            ) {
                if (response.isSuccessful) {
                    val users = response.body()?.items
                    val userList = ArrayList<FavoriteEntity>()
                    appExecutors.diskIO.execute {
                        users?.forEach { users ->
                            val isFavorite = favoriteDao.isFavorites(users.login)
                            val favoriteEntityUser = FavoriteEntity(
                                users.login,
                                users.avatarUrl,
                                isFavorite
                            )
                            userList.add(favoriteEntityUser)
                        }
                    }

                }
            }

            override fun onFailure(call: Call<GithubResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })

        val localData = favoriteDao.getAllUsers()
        result.addSource(localData) { newData: List<FavoriteEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    fun getFavoriteUsers(): LiveData<List<FavoriteEntity>> {
        return favoriteDao.getAllUsers()
    }

    fun setFavoriteUsers(favoriteEntity: FavoriteEntity) {
        appExecutors.diskIO.execute {
            favoriteDao.insertUser(favoriteEntity)
        }
    }

    fun removeFavoriteUsers(favoriteEntity: FavoriteEntity) {
        appExecutors.diskIO.execute {
            favoriteDao.deleteUser(favoriteEntity)
            favoriteDao.clearUser()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FavoriteRepository? = null
        fun getInstance(
            apiService: ApiService,
            favoriteDao: FavoriteDao,
            appExecutors: AppExecutors
        ) : FavoriteRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: FavoriteRepository(apiService, favoriteDao, appExecutors)
            }.also { INSTANCE = it }
    }
}