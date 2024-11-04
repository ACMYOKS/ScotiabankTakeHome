package com.acapp1412.scotiabanktakehome.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class DetailViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val repoDetail = savedStateHandle.getStateFlow<RepoDisplayDetail?>(KeyRepoDetail, null)

    companion object {
        val KeyRepoDetail = "key_repo_detail"
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                DetailViewModel(savedStateHandle)
            }
        }
    }
}