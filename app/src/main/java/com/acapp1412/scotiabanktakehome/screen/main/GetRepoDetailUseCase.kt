package com.acapp1412.scotiabanktakehome.screen.main

import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.UserWithRepo
import com.acapp1412.scotiabanktakehome.screen.detail.RepoDisplayDetail

class GetRepoDetailUseCase {
    private var selectedUserWithRepo: UserWithRepo? = null

    fun putUserWithRepos(userId: String, repos: List<Repo>) {
        selectedUserWithRepo = UserWithRepo(userId, repos)
    }

    fun getRepoDetail(repoId: Long): RepoDisplayDetail? {
        return selectedUserWithRepo?.let { userWithRepo ->
            userWithRepo.repos.find { it.id == repoId }?.let { repo ->
                RepoDisplayDetail(userWithRepo.userId, repo, userWithRepo.totalForks)
            }
        }
    }
}
