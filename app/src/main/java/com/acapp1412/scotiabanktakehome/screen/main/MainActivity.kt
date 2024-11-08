package com.acapp1412.scotiabanktakehome.screen.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.target
import com.acapp1412.scotiabanktakehome.R
import com.acapp1412.scotiabanktakehome.SpacingItemDecoration
import com.acapp1412.scotiabanktakehome.collectRepeatedly
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.User
import com.acapp1412.scotiabanktakehome.databinding.ActivityMainBinding
import com.acapp1412.scotiabanktakehome.dpToPx
import com.acapp1412.scotiabanktakehome.fadeIn
import com.acapp1412.scotiabanktakehome.hideKeyboard
import com.acapp1412.scotiabanktakehome.screen.detail.DetailActivity
import com.acapp1412.scotiabanktakehome.screen.detail.DetailViewModel
import com.acapp1412.scotiabanktakehome.screen.main.MainPageViewModel.SearchResultState

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainPageViewModel> { MainPageViewModel.Factory }
    private lateinit var binding: ActivityMainBinding
    private val repoAdapter = RepoAdapter {
        viewModel.showRepo(it.id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUi()
    }

    private fun setupUi() {
        binding.apply {
            btnSearch.setOnClickListener {
                currentFocus?.hideKeyboard()
                viewModel.searchUser(etUserId.text?.toString().orEmpty())
            }
            rvRepos.apply {
                adapter = repoAdapter
                val spacing = 8f.dpToPx(this@MainActivity)
                addItemDecoration(SpacingItemDecoration(spacing, spacing))
            }
        }

        viewModel.searchResultState.collectRepeatedly(this, Lifecycle.State.CREATED) {
            when (it) {
                SearchResultState.Init -> {
                    emptyUserAvatar()
                }

                is SearchResultState.UserFound -> {
                    showUserAvatar(it.user)
                }

                SearchResultState.NoUserFound -> {
                    emptyUserAvatar()
                }
            }
        }

        viewModel.repos.collectRepeatedly(this, Lifecycle.State.CREATED) {
            showRepos(it)
        }

        viewModel.error.collectRepeatedly(this, Lifecycle.State.CREATED) {
            Toast.makeText(this, it.toString(this), Toast.LENGTH_SHORT).show()
        }

        viewModel.navToDetailEvent.collectRepeatedly(this, Lifecycle.State.CREATED) {
            startActivity(
                Intent(
                    this,
                    DetailActivity::class.java
                ).putExtra(DetailViewModel.KeyRepoDetail, it)
            )
        }
    }

    private fun emptyUserAvatar() {
        binding.apply {
            ivUser.setImageDrawable(null)
            tvUser.text = ""
        }
    }

    private fun showUserAvatar(user: User) {
        binding.containerUserInfo.fadeIn()
        val request = ImageRequest.Builder(this)
            .data(user.avatarUrl)
            .crossfade(true)
            .target(binding.ivUser)
            .build()
        imageLoader.enqueue(request)
        binding.tvUser.text = user.name
    }

    private fun showRepos(repos: List<Repo>) {
        binding.rvRepos.fadeIn()
        repoAdapter.submitList(repos)
    }
}