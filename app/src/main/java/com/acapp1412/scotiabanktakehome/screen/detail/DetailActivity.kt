package com.acapp1412.scotiabanktakehome.screen.detail

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import com.acapp1412.scotiabanktakehome.R
import com.acapp1412.scotiabanktakehome.collectRepeatedly
import com.acapp1412.scotiabanktakehome.databinding.ActivityDetailBinding
import com.acapp1412.scotiabanktakehome.fadeIn
import kotlinx.coroutines.flow.filterNotNull
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class DetailActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailViewModel> { DetailViewModel.Factory }
    private lateinit var binding: ActivityDetailBinding

    private val dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUi()
    }

    private fun setupUi() {
        binding.tvDescription.movementMethod = ScrollingMovementMethod()
        viewModel.repoDetail.filterNotNull().collectRepeatedly(this, Lifecycle.State.CREATED) {
            showRepoDetail(it)
        }
    }

    private fun showRepoDetail(detail: RepoDisplayDetail) {
        setTitle(detail.userId, detail.repo.name)
        setForkCard(detail.repo.forks)
        setStarCard(detail.repo.stargazersCount)
        setUpdateAt(detail.repo.updatedAt)
        setDescription(detail.repo.description)
    }

    private fun setTitle(userName: String, repoName: String) {
        binding.tvName.apply {
            text = getString(R.string.text_repo_title, userName, repoName)
            fadeIn()
        }
    }

    private fun setForkCard(forkCount: Int) {
        binding.tvForkCount.text = forkCount.toString()
        binding.cvFork.fadeIn()
    }

    private fun setStarCard(starCount: Int) {
        binding.tvStarCount.text = starCount.toString()
        binding.cvStar.fadeIn()
    }

    private fun setUpdateAt(updateAt: String) {
        binding.tvUpdateAt.apply {
            text = getString(
                R.string.text_update_at,
                ZonedDateTime.parse(updateAt).format(dtFormatter)
            )
            fadeIn()
        }
    }

    private fun setDescription(description: String?) {
        binding.tvDescription.apply {
            text = description
            fadeIn()
        }
    }
}