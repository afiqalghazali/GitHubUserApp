package com.scifi.githubuserapp.ui.favorite

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scifi.githubuserapp.data.local.entity.FavoriteEntity
import com.scifi.githubuserapp.databinding.ItemUserBinding
import com.scifi.githubuserapp.ui.detail.DetailActivity
import com.scifi.githubuserapp.ui.favorite.FavoriteAdapter.MyViewHolder.Companion.DIFF_CALLBACK

class FavoriteAdapter : ListAdapter<FavoriteEntity, FavoriteAdapter.MyViewHolder> (DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val favorite = getItem(position)
        holder.bind(favorite)
    }

    class MyViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(
        binding.root
    ) {
        fun bind(favoriteEntity: FavoriteEntity) {
            binding.tvUsername.text = favoriteEntity.username
            Glide.with(itemView.context)
                .load(favoriteEntity.avatarUrl)
                .circleCrop()
                .into(binding.ivPhoto)
            itemView.setOnClickListener {
                val intentDetail = Intent(itemView.context, DetailActivity::class.java)
                intentDetail.putExtra("extra_username", favoriteEntity.username)
                itemView.context.startActivity(intentDetail)
            }
        }

        companion object {
            val DIFF_CALLBACK: DiffUtil.ItemCallback<FavoriteEntity> =
                object : DiffUtil.ItemCallback<FavoriteEntity>() {
                    override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                        return oldItem.username == newItem.username
                    }

                    @SuppressLint("DiffUtilEquals")
                    override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
                        return oldItem == newItem
                    }
                }
        }
    }
}