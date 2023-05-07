package com.example.myapplication.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max

class GameViewModel : ViewModel() {
    /* LiveData with maximum size Rectangle*/
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    /**
     * Calculate biggest rectangle based on StateList
     *
     * @param stateList from Compose
     */
    fun calculateBiggestScore(input: SnapshotStateList<MutableList<Boolean>>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val intMatrix = input.toArray()
                maxRectangle(intMatrix)
            }
        }
    }

    /**
     * Calculate max Rectangle size and emit it via LiveData
     *
     * @param intMatrix representation of game field in 2D array
     */
    internal fun maxRectangle(intMatrix: Array<IntArray>) {
        var result = maxHistogram(intMatrix[0].size, intMatrix[0])

        for (i in 1 until intMatrix.size) {
            for (j in 0 until intMatrix[0].size) {
                if (intMatrix[i][j] == 1) {
                    intMatrix[i][j] += intMatrix[i - 1][j]
                }
            }

            result = max(result, maxHistogram(intMatrix[0].size, intMatrix[i]))
        }

        _score.postValue(result)
    }

    /**
     * Calculate Max Histogram
     *
     * @param column number of column
     * @param row current row
     *
     * @return max size rectangle
     */
    fun maxHistogram(column: Int, row: IntArray): Int {
        val result = Stack<Int>()
        var topStack: Int
        var maxArea = 0
        var area: Int

        var i = 0
        while (i < column) {
            if (result.empty() || row[result.peek()] <= row[i]) {
                result.push(i++)
            } else {
                topStack = row[result.peek()]
                result.pop()
                area = topStack * i
                if (!result.empty()) {
                    area = topStack * (i - result.peek() - 1)
                }
                maxArea = max(area, maxArea)
            }
        }

        while (!result.empty()) {
            topStack = row[result.peek()]
            result.pop()
            area = topStack * i
            if (!result.empty()) {
                area = topStack * (i - result.peek() - 1)
            }
            maxArea = max(area, maxArea)
        }
        return maxArea
    }

    /**
     * Create new 2D Array based on input StateList
     */
    private fun SnapshotStateList<MutableList<Boolean>>.toArray(): Array<IntArray> {
        val intMatrix = Array(ROW_COUNT) { IntArray(COLUMN_COUNT) }

        for (i in indices) {
            for (j in this[0].indices) {
                intMatrix[i][j] = if (this[i][j]) 1 else 0
            }
        }

        return intMatrix
    }

    companion object {
        const val COLUMN_COUNT = 15
        const val ROW_COUNT = 15
    }
}