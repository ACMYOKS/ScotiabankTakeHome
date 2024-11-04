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

class MainPageViewModel(private val githubService: GithubService) : ViewModel() {
    private val _searchResultState = MutableStateFlow<SearchResultState>(SearchResultState.Init)
    val searchResultState = _searchResultState.asStateFlow()

    private val _repos = MutableStateFlow<List<Repo>>(emptyList())
    val repos = _repos.asStateFlow()

    private val _error = MutableSharedFlow<Message>(0, 1, BufferOverflow.DROP_OLDEST)
    val error = _error.asSharedFlow()

    private val _navToDetailEvent =
        MutableSharedFlow<RepoDisplayDetail>(0, 1, BufferOverflow.DROP_OLDEST)
    val navToDetailEvent = _navToDetailEvent.asSharedFlow()

    fun searchUser(userId: String) {
        viewModelScope.launch {
            try {
                val response = githubService.getUser(userId)
                when {
                    response.isSuccessful -> {
                        _searchResultState.value = SearchResultState.UserFound(response.body()!!)
                        getRepos(userId)
                    }

                    response.code() == 404 -> {
                        _searchResultState.value = SearchResultState.NoUserFound
                        _error.tryEmit(Message.OfResource(R.string.error_msg_no_user_found))
                        _repos.value = emptyList()
                    }

                    else -> {
                        _error.tryEmit(Message.OfString(response.message()))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.tryEmit(Message.OfString(e.message.orEmpty()))
            }
        }
    }

    @VisibleForTesting
    fun getRepos(userId: String) {
        viewModelScope.launch {
            try {
                val response = githubService.getUserRepos(userId)
                when {
                    response.isSuccessful -> {
                        _repos.value = response.body()!!
                    }

                    response.code() == 404 -> {
                        _repos.value = emptyList()
                    }

                    else -> {
                        _repos.value = emptyList()
                        _error.tryEmit(Message.OfString(response.message()))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.tryEmit(Message.OfString(e.message.orEmpty()))
            }
        }
    }

    fun showRepo(repoId: Long) {
        when (val searchResultState = searchResultState.value) {
            is SearchResultState.UserFound -> {
                repos.value.firstOrNull { it.id == repoId }?.let {
                    _navToDetailEvent.tryEmit(
                        RepoDisplayDetail(
                            searchResultState.user.name,
                            it
                        )
                    )
                }
            }

            else -> {}
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
                MainPageViewModel(githubService)
            }
        }
    }

}