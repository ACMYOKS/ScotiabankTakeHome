package com.acapp1412.scotiabanktakehome.screen.detail

import android.graphics.Color
import com.acapp1412.scotiabanktakehome.data.Repo
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RepoDisplayDetailTest {
    @Test
    fun `getForkTextColor returns RED when totalForks is larger than 5000, or else returns GRAY`() {
        assertEquals(Color.GRAY, createEmptyDetail(0).getForkTextColor())
        assertEquals(Color.GRAY, createEmptyDetail(10).getForkTextColor())
        assertEquals(Color.GRAY, createEmptyDetail(100).getForkTextColor())
        assertEquals(Color.GRAY, createEmptyDetail(4999).getForkTextColor())
        assertEquals(Color.GRAY, createEmptyDetail(5000).getForkTextColor())
        assertEquals(Color.RED, createEmptyDetail(5001).getForkTextColor())
        assertEquals(Color.RED, createEmptyDetail(6000).getForkTextColor())
        assertEquals(Color.RED, createEmptyDetail(10000).getForkTextColor())
    }

    private fun createEmptyDetail(totalForks: Int) = RepoDisplayDetail(
        "",
        Repo(0, "", "", "", "", 0, 0),
        totalForks
    )
}