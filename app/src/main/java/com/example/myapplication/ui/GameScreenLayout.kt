package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun GameScreenLayout(gameViewModel: GameViewModel = viewModel()) {
    val gridCells = remember {
        mutableStateListOf<MutableList<State>>().apply {
            for (i in 0 until GameViewModel.ROW_COUNT) {
                add(mutableStateListOf<State>().apply {
                    for (j in 0 until GameViewModel.COLUMN_COUNT) {
                        add(State.UNSELECTED)
                    }
                })
            }
        }
    }
    val myLiveData: LiveData<Int> = gameViewModel.score
    
    val myScoreState: Int? by myLiveData.observeAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(GameViewModel.ROW_COUNT) { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (column in gridCells[0].indices) {
                        Box(
                            Modifier
                                .weight(1f)
                                .aspectRatio(1.0F)
                                .padding(1.dp)
                                .background(
                                    when (gridCells[row][column]) {
                                        State.HIGHLIGHTED -> Color.Red
                                        State.UNSELECTED -> Color.LightGray
                                        State.SELECTED -> Color.Gray
                                    }
                                )
                                .clickable(onClick = {
                                    if (gridCells[row][column] == State.UNSELECTED) {
                                        gridCells[row][column] = State.SELECTED
                                    } else {
                                        gridCells[row][column] = State.UNSELECTED
                                    }
                                    gameViewModel.calculateBiggestScore(gridCells)
                                })
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        myScoreState?.let { score ->
            Text(text = "Biggest Rectangle: $score")
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(onClick = {
            for (i in gridCells.indices) {
                for (j in gridCells[0].indices) {
                    gridCells[i][j] = State.UNSELECTED
                    gameViewModel.resetScore()
                }
            }
        }) {
            Text(text = "Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GameScreenLayout()
    }
}

enum class State {
    UNSELECTED,
    SELECTED,
    HIGHLIGHTED
}