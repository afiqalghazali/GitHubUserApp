package com.scifi.githubuserapp.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.scifi.githubuserapp.R
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity
import com.scifi.githubuserapp.data.response.DetailUserResponse
import com.scifi.githubuserapp.databinding.ActivityDetailBinding
import com.scifi.githubuserapp.ui.MainActivity
import com.scifi.githubuserapp.ui.SectionsPagerAdapter
import com.scifi.githubuserapp.ui.favorite.FavoriteActivity
import com.scifi.githubuserapp.ui.favorite.FavoriteViewModel
import com.scifi.githubuserapp.ui.favorite.ViewModelFactory
import com.scifi.githubuserapp.ui.setting.SettingActivity

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var isFavorite: Boolean = false

    private val detailViewModel by viewModels<DetailViewModel>()
    private val favoriteViewModel by viewModels<FavoriteViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_USERNAME = "extra_username"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.toolbarLayout)
        setTitle(getString(R.string.activity_detail))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        detailViewModel.data.observe(this) { data ->
            setDetailData(data)
        }

        val username = intent.getStringExtra(EXTRA_USERNAME)
        detailViewModel.getUserData(username!!)

        detailViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val sectionsPagerAdapter = SectionsPagerAdapter(this)

        username.let {
            sectionsPagerAdapter.username = it
        }

        val viewPager: ViewPager2 = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = binding.tabs
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

       favoriteViewModel.getFavoriteUsers().observe(this) { favorites ->
           isFavorite = favorites.any { it.username == username}
           updateFavoriteButton(isFavorite)
        }

        binding.btnFavorite.setOnClickListener {
            isFavorite = !isFavorite
            updateFavoriteButton(isFavorite)

            detailViewModel.data.value?.let { user ->
                val favoriteEntity = FavoriteEntity(username = user.login, avatarUrl = user.avatarUrl, isFavorite = isFavorite)
                if (isFavorite) {
                    favoriteViewModel.saveFavorites(favoriteEntity)
                } else {
                    favoriteViewModel.deleteFavorites(favoriteEntity)
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.favorites -> {
                val intent = Intent(this, FavoriteActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.settings -> {
                val intent = Intent(this, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            android.R.id.home -> { // Handle the back button click
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite)
        }
    }

    private fun setDetailData(data: DetailUserResponse) {
        binding.tvUsername.text = data.login
        binding.tvFullname.text = data.name
        binding.tvRepository.text = data.publicRepos.toString()
        binding.tvFollowers.text = data.followers.toString()
        binding.tvFollowing.text = data.following.toString()
        binding.tvBio.text = data.bio
        Glide.with(this)
            .load(data.avatarUrl)
            .circleCrop()
            .into(binding.ivPhoto)
    }



    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}