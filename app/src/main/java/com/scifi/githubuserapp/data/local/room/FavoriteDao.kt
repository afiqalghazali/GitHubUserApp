package com.scifi.githubuserapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Query("SELECT * from favorite_database ORDER BY username DESC")
    fun getAllUsers(): LiveData<List<FavoriteEntity>>

    @Query("SELECT * FROM favorite_database WHERE isFavorite = 1")
    fun getFavoriteUser(): LiveData<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertUser(favorite: FavoriteEntity)

    @Delete
    fun deleteUser(favorite: FavoriteEntity)

    @Query("DELETE FROM favorite_database WHERE isFavorite = 0")
    fun clearUser()

    @Query("SELECT EXISTS(SELECT * FROM favorite_database WHERE username = :username AND isFavorite = 1)")
    fun isFavorites(username: String): Boolean
}