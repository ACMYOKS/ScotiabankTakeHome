package com.acapp1412.scotiabanktakehome.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserWithRepoTest {

    @Test
    fun `totalForks is the sum of fork of all repos`() {
        val model1 = UserWithRepo("", listOf())
        assertEquals(0, model1.totalForks)

        val model2 = UserWithRepo(
            "", listOf(
                createEmptyRepo(1),
                createEmptyRepo(10),
                createEmptyRepo(4)
            )
        )
        assertEquals(15, model2.totalForks)
    }

    private fun createEmptyRepo(forks: Int) = Repo(
        0L, "", "", "", "", 0, forks
    )
}