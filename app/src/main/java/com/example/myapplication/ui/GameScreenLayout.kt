package com.example.myapplication.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
        mutableStateListOf<MutableList<Boolean>>().apply {
            for (i in 0 until GameViewModel.ROW_COUNT) {
                add(mutableStateListOf<Boolean>().apply {
                    for (j in 0 until GameViewModel.COLUMN_COUNT) {
                        add(false)
                    }
                })
            }
        }
    }
    val myLiveData: LiveData<Int> = gameViewModel.score
    
    val myScoreState: Int? by myLiveData.observeAsState()

    // Use the state variable in your Composable

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
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
                                .size(25.dp)
                                .aspectRatio(1.0F)
                                .padding(1.dp)
                                .background(if (gridCells[row][column]) Color.Red else Color.Gray)
                                .clickable(onClick = {
                                    gridCells[row][column] = !gridCells[row][column]
                                    gameViewModel.calculateBiggestScore(gridCells)
                                })
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        myScoreState?.let { score ->
            Text(text = "Selected items: $score")
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