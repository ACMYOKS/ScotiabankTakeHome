package com.acapp1412.scotiabanktakehome.screen.main

import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.screen.detail.RepoDisplayDetail
import io.mockk.spyk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetRepoDetailUseCaseTest {
    @Test
    fun `when putUserWithRepos is not called, getRepoDetail returns null`() {
        val useCase = spyk(GetRepoDetailUseCase())
        verify(exactly = 0) { useCase.putUserWithRepos(any(), any()) }
        assertNull(useCase.getRepoDetail(1))
    }

    @Test
    fun `when putUserWithRepo is called, getRepoDetail returns non null value only when getRepoDetail is called with an existing repoId`() {
        val useCase = spyk(GetRepoDetailUseCase())
        val repo = Repo(1, "", "", "", "", 0, 10)
        useCase.putUserWithRepos("", listOf(repo))
        assertNull(useCase.getRepoDetail(2))
        assertEquals(RepoDisplayDetail("", repo, 10), useCase.getRepoDetail(1))
    }
}