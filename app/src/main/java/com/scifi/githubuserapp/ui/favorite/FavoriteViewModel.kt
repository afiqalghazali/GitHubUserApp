package com.scifi.githubuserapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scifi.githubuserapp.data.FavoriteRepository
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.launch

class FavoriteViewModel(private val favoriteRepository: FavoriteRepository): ViewModel() {

    fun getUsers()  = favoriteRepository.getUsers()

    fun getFavoriteUsers() = favoriteRepository.getFavoriteUsers()

    fun saveFavorites(favoriteEntity: FavoriteEntity) = viewModelScope.launch {
        favoriteRepository.setFavoriteUsers(favoriteEntity)
    }

    fun deleteFavorites(favoriteEntity: FavoriteEntity) =  viewModelScope.launch {
        favoriteRepository.removeFavoriteUsers(favoriteEntity)
    }
}