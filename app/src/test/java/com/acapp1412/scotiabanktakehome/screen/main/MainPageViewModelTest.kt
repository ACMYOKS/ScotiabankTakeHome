package com.acapp1412.scotiabanktakehome.screen.main

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import com.acapp1412.scotiabanktakehome.GithubService
import com.acapp1412.scotiabanktakehome.MainDispatcherRule
import com.acapp1412.scotiabanktakehome.Message
import com.acapp1412.scotiabanktakehome.R
import com.acapp1412.scotiabanktakehome.data.Repo
import com.acapp1412.scotiabanktakehome.data.User
import com.acapp1412.scotiabanktakehome.screen.detail.RepoDisplayDetail
import com.acapp1412.scotiabanktakehome.screen.main.MainPageViewModel.SearchResultState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit4.MockKRule
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class MainPageViewModelTest {
    @get:Rule
    val mockkRule = MockKRule(this)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK(relaxed = true)
    lateinit var mockGithubSvc: GithubService

    @MockK(relaxed = true)
    lateinit var getRepoDetailUseCase: GetRepoDetailUseCase

    @Test
    fun `when searchUser(userId) is called, then githubService getUser is called with the userId`() {
        coEvery { mockGithubSvc.getUser(any()) } returns mockk<Response<User>>()

        val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)
        // searchUser is not called, GithubSvc.getUser should not be called
        coVerify(exactly = 0) { mockGithubSvc.getUser("user1") }

        vm.searchUser("user1")
        // getUser("user1") should be called
        coVerify(exactly = 1) { mockGithubSvc.getUser("user1") }

        vm.searchUser("user2")
        // getUser("user1") should be called
        coVerify(exactly = 1) { mockGithubSvc.getUser("user2") }
    }

    @Test
    fun `searchResultState returns Init and repos returns empty list initially`() = runTest {
        turbineScope {
            val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)
            val state = vm.searchResultState.testIn(backgroundScope)
            val repos = vm.repos.testIn(backgroundScope)
            assertEquals(SearchResultState.Init, state.expectMostRecentItem())
            assertTrue(repos.expectMostRecentItem().isEmpty())
        }
    }

    @Test
    fun `when searchUser is called and GithubService getUser returns with code 200, searchResultState returns UserFound with corresponding User value`() =
        runTest {
            val user1 = User(1, "user1", "user1", "url1", 10)
            val response1 = Response.success(200, user1)
            val user2 = User(2, "user2", "user2", "url2", 2)
            val response2 = Response.success(200, user2)
            coEvery { mockGithubSvc.getUser("user1") } returns response1
            coEvery { mockGithubSvc.getUser("user2") } returns response2
            val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)

            vm.searchResultState.test {
                assertEquals(SearchResultState.Init, awaitItem())
                vm.searchUser("user1")
                assertEquals(SearchResultState.UserFound(user1), awaitItem())

                vm.searchUser("user2")
                assertEquals(SearchResultState.UserFound(user2), awaitItem())
            }
        }

    @Test
    fun `when searchUser is called and GithubService getUser returns with code 404, searchResultState returns NoUserFound`() =
        runTest {
            val user1 = User(1, "user1", "user1", "url1", 10)
            val response1 = Response.success(200, user1)
            val response2 = Response.error<User>(404, "no user found".toResponseBody())
            coEvery { mockGithubSvc.getUser("user1") } returns response1
            coEvery { mockGithubSvc.getUser("some user") } returns response2
            val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)

            vm.searchResultState.test {
                assertEquals(SearchResultState.Init, awaitItem())
                vm.searchUser("user1")
                assertEquals(SearchResultState.UserFound(user1), awaitItem())

                vm.searchUser("some user")
                assertEquals(SearchResultState.NoUserFound, awaitItem())
            }
        }

    @Test
    fun `when GithubService getUser returns 404 or throw exception, error emits a Message`() =
        runTest {
            turbineScope {
                val user1 = User(1, "user1", "user1", "url1", 10)
                val response1 = Response.success(200, user1)
                coEvery { mockGithubSvc.getUser("user1") } returns response1
                coEvery { mockGithubSvc.getUser("") } returns Response.error(
                    404,
                    "no user found".toResponseBody()
                )
                coEvery { mockGithubSvc.getUser("errorUser") } throws IOException("network issue")
                val vm = spyk(MainPageViewModel(mockGithubSvc, getRepoDetailUseCase))
                // bypass getRepo flow
                justRun { vm.getRepos(any()) }

                val error = vm.error.testIn(backgroundScope)
                error.expectNoEvents()

                vm.searchUser("user1")
                error.expectNoEvents()

                vm.searchUser("")
                // there is new Message emitted
                assertEquals(
                    Message.OfResource(R.string.error_msg_no_user_found),
                    error.expectMostRecentItem()
                )

                vm.searchUser("errorUser")
                assertEquals(Message.OfString("network issue"), error.expectMostRecentItem())
            }
        }

    @Test
    fun `when searchUser is called with userId and GithubService returns a user, getRepos is called with that userId`() =
        runTest {
            val user1 = User(1, "user1", "user1", "url1", 10)
            val response1 = Response.success(200, user1)
            val user2 = User(2, "user2", "user2", "url2", 2)
            val response2 = Response.success(200, user2)
            val response3 = Response.error<User>(404, "no user found".toResponseBody())

            coEvery { mockGithubSvc.getUser("user1") } returns response1
            coEvery { mockGithubSvc.getUser("user2") } returns response2
            coEvery { mockGithubSvc.getUser("user3") } returns response3
            val vm = spyk(MainPageViewModel(mockGithubSvc, getRepoDetailUseCase))
            // getRepos is not called until getUser is called and successfully get a response
            verify(exactly = 0) { vm.getRepos("user1") }
            verify(exactly = 0) { vm.getRepos("user2") }

            vm.searchUser("user1")
            verify(exactly = 1) { vm.getRepos("user1") }

            vm.searchUser("user2")
            verify(exactly = 1) { vm.getRepos("user1") }

            vm.searchUser("user3")
            verify(exactly = 0) { vm.getRepos("user3") }
        }

    @Test
    fun `when getRepos is called and GithubService getUserRepos returns a valid repo list, repos emits the repo list`() =
        runTest {
            val list1 = listOf(
                Repo(1, "r1", "repo1", "desc1", "ts1", 0, 1),
                Repo(2, "r2", "repo2", "desc2", "ts2", 0, 1)
            )
            coEvery { mockGithubSvc.getUserRepos("user1") } returns Response.success(list1)
            coEvery { mockGithubSvc.getUserRepos("user2") } returns Response.success(emptyList())
            val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)

            vm.repos.test {
                // initially it is empty
                assertTrue(expectMostRecentItem().isEmpty())

                vm.getRepos("user1")
                assertEquals(list1, awaitItem())

                vm.getRepos("user2")
                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun `when getRepos is called and GithubService getUserRepos returns 404, repos emits an emptyList`() =
        runTest {
            val list1 = listOf(
                Repo(1, "r1", "repo1", "desc1", "ts1", 0, 1),
                Repo(2, "r2", "repo2", "desc2", "ts2", 0, 1)
            )
            coEvery { mockGithubSvc.getUserRepos("user1") } returns Response.success(list1)
            coEvery { mockGithubSvc.getUserRepos("user2") } returns Response.error(
                404,
                "no repo found".toResponseBody()
            )
            val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)

            vm.repos.test {
                // initially it is empty
                assertTrue(expectMostRecentItem().isEmpty())

                vm.getRepos("user1")
                assertEquals(list1, awaitItem())

                vm.getRepos("user2")
                assertTrue(awaitItem().isEmpty())
            }
        }

    @Test
    fun `when getRepos is called and GithubService getUserRepos throws an exception, repos emits an emptyList and error emits a Message`() =
        runTest {
            turbineScope {
                coEvery { mockGithubSvc.getUserRepos("user1") } throws IOException("network exception")
                val vm = MainPageViewModel(mockGithubSvc, getRepoDetailUseCase)

                val repos = vm.repos.testIn(backgroundScope)
                val error = vm.error.testIn(backgroundScope)

                assertTrue(repos.expectMostRecentItem().isEmpty())
                error.expectNoEvents()

                vm.getRepos("user1")
                // no events due to no data change
                repos.expectNoEvents()
                assertEquals(Message.OfString("network exception"), error.expectMostRecentItem())
            }
        }

    @Test
    fun `when showRepo is called, getRepoDetailUseCase getRepoDetail will be called, and navToDetailEvent emits new value with correct detail`() =
        runTest {
            val vm = spyk(MainPageViewModel(mockGithubSvc, getRepoDetailUseCase))
            val repoDetail = RepoDisplayDetail(
                "user1",
                Repo(1, "repo1", "repo1", null, "ts1", 10, 100), 1000
            )
            every { getRepoDetailUseCase.getRepoDetail(1) } returns repoDetail

            vm.navToDetailEvent.test {
                expectNoEvents()
                verify(exactly = 0) { getRepoDetailUseCase.getRepoDetail(1) }

                vm.showRepo(1)
                verify(exactly = 1) { getRepoDetailUseCase.getRepoDetail(1) }
                assertEquals(repoDetail, awaitItem())
            }
        }
}