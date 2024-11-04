package com.acapp1412.scotiabanktakehome.screen.main

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.databinding.CellRepoBinding

class RepoAdapter(
    var itemOnClickListener: ItemOnClickListener? = null
) : ListAdapter<Repo, RepoViewHolder>(Diff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        return RepoViewHolder(
            CellRepoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = getItem(position)
        holder.setRepo(repo, itemOnClickListener)
    }

    fun interface ItemOnClickListener {
        fun onClickItem(repo: Repo)
    }

    companion object {
        val Diff = object : ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class RepoViewHolder(private val binding: CellRepoBinding) : ViewHolder(binding.root) {
    fun setRepo(repo: Repo, itemOnClickListener: RepoAdapter.ItemOnClickListener?) {
        binding.tvName.text = repo.name
        binding.tvDescription.text = repo.description
        binding.root.setOnClickListener {
            itemOnClickListener?.onClickItem(repo)
        }
    }
}