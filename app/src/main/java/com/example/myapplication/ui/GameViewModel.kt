package com.example.myapplication.ui

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.*
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameViewModel : ViewModel() {

    /* LiveData with maximum size Rectangle*/
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    /**
     * Calculate biggest rectangle based on StateList
     *
     * @param input stateList from Compose
     */
    fun calculateBiggestScore(input: SnapshotStateList<MutableList<State>>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val intMatrix = input.toArray()
                val rectAngle = maxRectangle(intMatrix)
                val topLeft = rectAngle.topLeft
                val bottomRight = rectAngle.bottomRight
                for (i in input.indices) {
                    for (j in input[0].indices) {
                        if (j >= topLeft.row && j <= bottomRight.row && i >= topLeft.column && i <= bottomRight
                                .column ) {
                            input[i][j] = State.HIGHLIGHTED
                        } else if (input[i][j] == State.HIGHLIGHTED) {
                            input[i][j] = State.SELECTED
                        }
                    }
                }
            }
        }
    }

    /**
     * Reset biggest rectangle score
     */
    fun resetScore() {
        _score.value = 0
    }

    /**
     * Calculate max Rectangle size and emit it via LiveData
     *
     * @param intMatrix representation of game field in 2D array
     *
     * @return left top and right bottom rectangle coordinates
     */
    internal fun maxRectangle(intMatrix: Array<IntArray>): Rect {
        var maxHistogram = maxHistogram(intMatrix[0].size, intMatrix[0])
        var topLeft = Coordinate(maxHistogram.first, 0)
        var bottomRight = Coordinate(maxHistogram.second, 0)
        var result = maxHistogram.third

        for (i in 1 until intMatrix.size) {
            for (j in 0 until intMatrix[0].size) {
                if (intMatrix[i][j] == 1) {
                    intMatrix[i][j] += intMatrix[i - 1][j]
                }
            }
            maxHistogram = maxHistogram(intMatrix[0].size, intMatrix[i])
            if (maxHistogram.third > result) {
                val left = maxHistogram.first
                val right = maxHistogram.second
                val top = i - (maxHistogram.third / (right - left + 1)) + 1
                topLeft = Coordinate(left, top)
                bottomRight = Coordinate(right, i)
            }
            result = max(result, maxHistogram.third)
        }

        _score.postValue(result)

        return Rect(topLeft, bottomRight)
    }

    /**
     * Calculate Max Histogram
     *
     * @param columnNumber number of column
     * @param row current row
     *
     * @return 3 values: rectangle left coordinate, rectangle right coordinate, rectangle score
     */
    private fun maxHistogram(columnNumber: Int, row: IntArray): Triple<Int, Int, Int> {
        val result = Stack<Int>()
        var topStack: Int
        var maxArea = 0
        var area: Int
        var left: Int
        var leftRect = -1
        var rightRect = -1

        var i = 0
        while (i < columnNumber) {
            if (result.empty() || row[result.peek()] <= row[i]) {
                result.push(i++)
            } else {
                left = result.peek()
                topStack = row[result.peek()]
                result.pop()
                area = topStack * i
                if (!result.empty()) {
                    left = result.peek() + 1
                    area = topStack * (i - result.peek() - 1)
                }
                if (area > maxArea) {
                    leftRect = left
                    rightRect = i - 1
                }
                maxArea = max(area, maxArea)
            }
        }

        while (!result.empty()) {
            left = result.peek()
            topStack = row[result.peek()]
            result.pop()
            area = topStack * i
            if (!result.empty()) {
                left = result.peek() + 1
                area = topStack * (i - result.peek() - 1)
            }
            if (area > maxArea) {
                leftRect = left
                rightRect = columnNumber - 1
            }
            maxArea = max(area, maxArea)
        }
        return Triple(leftRect, rightRect, maxArea)
    }

    /**
     * Create new 2D Array based on input StateList
     */
    private fun SnapshotStateList<MutableList<State>>.toArray(): Array<IntArray> {
        val intMatrix = Array(ROW_COUNT) { IntArray(COLUMN_COUNT) }

        for (i in indices) {
            for (j in this[0].indices) {
                intMatrix[i][j] = if (this[i][j] == State.UNSELECTED) 0 else 1
            }
        }

        return intMatrix
    }

    /**
     * Class to return left top and right bottom rectangle coordinates
     */
    internal class Rect(val topLeft: Coordinate, val bottomRight: Coordinate)

    /**
     * Class to describe coordinate in 2D array
     */
    internal class Coordinate(val row: Int, val column: Int)

    companion object {
        const val COLUMN_COUNT = 15
        const val ROW_COUNT = 15
    }
}