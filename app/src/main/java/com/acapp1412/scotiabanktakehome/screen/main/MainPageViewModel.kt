package com.acapp1412.scotiabanktakehome.screen.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.acapp1412.scotiabanktakehome.BaseApplication
import com.acapp1412.scotiabanktakehome.GithubService
import com.acapp1412.scotiabanktakehome.Message
import com.acapp1412.scotiabanktakehome.R
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.User
import com.acapp1412.scotiabanktakehome.screen.detail.RepoDisplayDetail
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Main page, which depends on GithubService to call Github APIs, and
 * GetRepoDetailUseCase to get the repository detail to display
 * */
class MainPageViewModel(
    private val githubService: GithubService,
    private val getRepoDetailUseCase: GetRepoDetailUseCase
) : ViewModel() {
    private val _searchResultState = MutableStateFlow<SearchResultState>(SearchResultState.Init)
    val searchResultState = _searchResultState.asStateFlow()

    private val _repos = MutableStateFlow<List<Repo>>(emptyList())
    val repos = _repos.asStateFlow()

    private val _error = MutableSharedFlow<Message>(0, 1, BufferOverflow.DROP_OLDEST)
    val error = _error.asSharedFlow()

    private val _navToDetailEvent =
        MutableSharedFlow<RepoDisplayDetail>(0, 1, BufferOverflow.DROP_OLDEST)
    val navToDetailEvent = _navToDetailEvent.asSharedFlow()

    /**
     * function for UI to search user by userId, emits new SearchResultState, or error message when
     * errors like network disconnection occurs.
     * @param userId userId that the user inputs
     */
    fun searchUser(userId: String) {
        viewModelScope.launch {
            val response = githubService.getUser(userId)
            when {
                // user found, emits new state with the user detail and fetches the user's repos
                response.isSuccessful -> {
                    val user = response.body()!!
                    _searchResultState.value = SearchResultState.UserFound(user)
                    getRepos(user.login)
                }

                // user not found, emits new state without user detail, clears the repo list
                // and emits an error message
                response.code() == 404 -> {
                    _searchResultState.value = SearchResultState.NoUserFound
                    _error.tryEmit(Message.OfResource(R.string.error_msg_no_user_found))
                    _repos.value = emptyList()
                }

                // error occurs, emits error message
                else -> {
                    _error.tryEmit(Message.OfString(response.message()))
                }
            }
        }
    }

    /**
     * function to obtain the user's repos, emits the repo list, or error message when errors like
     * network disconnection occurs.
     * @param userId
     * */
    @VisibleForTesting
    fun getRepos(userId: String) {
        viewModelScope.launch {
            val response = githubService.getUserRepos(userId)
            when {
                // repo list is not empty, emits list of repos
                response.isSuccessful -> {
                    val repos = response.body()!!
                    _repos.value = repos
                    // update useCase of the new userId-repos pair
                    getRepoDetailUseCase.putUserWithRepos(userId, repos)
                }

                // repo list is empty, emits empty repo list
                response.code() == 404 -> {
                    _repos.value = emptyList()
                    // update useCase of the new userId-repos pair
                    getRepoDetailUseCase.putUserWithRepos(userId, emptyList())
                }

                // error occurs, clears the list and emits error message
                else -> {
                    _repos.value = emptyList()
                    _error.tryEmit(Message.OfString(response.message()))
                }
            }

        }
    }

    /**
     * function to emit nav event to repo detail page if needed.
     * @param repoId
     * */
    fun showRepo(repoId: Long) {
        getRepoDetailUseCase.getRepoDetail(repoId)?.let {
            // repo detail found for repoId, emits nav event
            _navToDetailEvent.tryEmit(it)
        }
    }

    sealed interface SearchResultState {
        data object Init : SearchResultState
        data object NoUserFound : SearchResultState
        data class UserFound(val user: User) : SearchResultState
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val githubService = (this[APPLICATION_KEY] as BaseApplication).githubService
                MainPageViewModel(githubService, GetRepoDetailUseCase())
            }
        }
    }

}