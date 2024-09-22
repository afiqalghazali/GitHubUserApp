package com.scifi.githubuserapp.ui.favorite

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.scifi.githubuserapp.R
import com.scifi.githubuserapp.data.Result
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity
import com.scifi.githubuserapp.databinding.ActivityFavoriteBinding
import com.scifi.githubuserapp.ui.MainActivity

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvFavorite.addItemDecoration(itemDecoration)

        favoriteViewModel.getFavoriteUsers().observe(this) { favorite ->
            false.showLoading()
            setUserData(favorite)
        }

        setSupportActionBar(binding.toolbar.toolbarLayout)
        setTitle(getString(R.string.activity_favorite))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        favoriteViewModel.getUsers().observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading-> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val favoriteData = result.data
                        FavoriteAdapter().submitList(favoriteData)
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(
                            this,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }


    private fun setUserData(favoriteEntity: List<FavoriteEntity>) {
        val adapter = FavoriteAdapter()
        adapter.submitList(favoriteEntity)
        binding.rvFavorite.adapter = adapter
    }

    private fun Boolean.showLoading() {
        binding.progressBar.visibility = if (this) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val settings = menu?.findItem(R.id.settings)
        val favorite = menu?.findItem(R.id.favorites)
        settings?.isVisible = false
        favorite?.isVisible = false
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // Handle the back button click
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}