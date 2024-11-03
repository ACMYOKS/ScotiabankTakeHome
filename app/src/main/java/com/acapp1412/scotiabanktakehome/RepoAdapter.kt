package com.acapp1412.scotiabanktakehome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.databinding.CellRepoBinding

class RepoAdapter : ListAdapter<Repo, RepoViewHolder>(Diff) {
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
        holder.setRepo(repo)
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
    fun setRepo(repo: Repo) {
        binding.tvName.text = repo.name
        binding.tvDescription.text = repo.description
    }
}