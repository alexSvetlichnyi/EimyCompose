package com.example.myapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.ui.GameViewModel
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    val gameViewModel = GameViewModel()

    @Test
    fun checkEmptyMatrixReturnsZero() {
        val array = arrayOf(
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0),
        )

        gameViewModel.maxRectangle(array)

        assertEquals(gameViewModel.score.value, 0)
    }

    @Test
    fun checkFullMatrixReturns16() {
        val array = arrayOf(
            intArrayOf(1, 1, 1, 1),
            intArrayOf(1, 1, 1, 1),
            intArrayOf(1, 1, 1, 1),
            intArrayOf(1, 1, 1, 1),
        )

        gameViewModel.maxRectangle(array)

        assertEquals(gameViewModel.score.value, 16)
    }

    @Test
    fun checkMatrixReturns4() {
        val array = arrayOf(
            intArrayOf(1, 1, 1, 1),
            intArrayOf(1, 0, 0, 1),
            intArrayOf(1, 0, 0, 1),
            intArrayOf(1, 1, 1, 1),
        )

        gameViewModel.maxRectangle(array)

        assertEquals(gameViewModel.score.value, 4)
    }

    @Test
    fun checkMatrixReturns1() {
        val array = arrayOf(
            intArrayOf(1, 0, 1, 0),
            intArrayOf(0, 0, 0, 1),
            intArrayOf(1, 0, 0, 0),
            intArrayOf(0, 1, 0, 1),
        )

        gameViewModel.maxRectangle(array)

        assertEquals(gameViewModel.score.value, 1)
    }
}