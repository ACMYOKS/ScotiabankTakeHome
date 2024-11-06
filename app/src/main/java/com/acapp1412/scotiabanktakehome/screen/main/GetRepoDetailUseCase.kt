package com.acapp1412.scotiabanktakehome.screen.main

import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.UserWithRepo
import com.acapp1412.scotiabanktakehome.screen.detail.RepoDisplayDetail

/**
 * UseCase to get repo detail with the requested repoId
 * */
class GetRepoDetailUseCase {
    private var selectedUserWithRepo: UserWithRepo? = null

    // puts userId with repos into a UserWithRepo model
    fun putUserWithRepos(userId: String, repos: List<Repo>) {
        selectedUserWithRepo = UserWithRepo(userId, repos)
    }

    // returns a RepoDisplayDetail for the requested repoId if there is a match
    fun getRepoDetail(repoId: Long): RepoDisplayDetail? {
        return selectedUserWithRepo?.let { userWithRepo ->
            userWithRepo.repos.find { it.id == repoId }?.let { repo ->
                RepoDisplayDetail(userWithRepo.userId, repo, userWithRepo.totalForks)
            }
        }
    }
}
