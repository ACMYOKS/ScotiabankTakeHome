package com.acapp1412.scotiabanktakehome.screen.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.acapp1412.scotiabanktakehome.MainDispatcherRule
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.screen.detail.DetailViewModel.Companion.KeyRepoDetail
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DetailViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `on vm init, savedStateHandle getStateFlow() is called`() {
        val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
        verify(exactly = 0) { savedStateHandle.getStateFlow(KeyRepoDetail, null) }
        DetailViewModel(savedStateHandle)
        verify(exactly = 1) { savedStateHandle.getStateFlow(KeyRepoDetail, null) }
    }

    @Test
    fun `when savedStateHandle contains KeyRepoDetail, repoDetail emits a non-null RepoDisplayDetail`() =
        runTest {
            val savedStateHandle = SavedStateHandle()
            val vm = DetailViewModel(savedStateHandle)
            vm.repoDetail.test {
                // null since there is no value for KeyRepoDetail
                assertEquals(null, awaitItem())

                val detail = RepoDisplayDetail("user1", Repo(1, "repo1", "repo1", null, "1", 0, 1))
                savedStateHandle[KeyRepoDetail] = detail

                // emit detail set for KeyRepoDetail
                assertEquals(detail, awaitItem())
            }
        }
}