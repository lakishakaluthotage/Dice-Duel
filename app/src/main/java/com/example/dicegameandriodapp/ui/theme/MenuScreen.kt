package com.example.dicegameandriodapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MenuScreen(
    targetScore: Int, //initial target
    onTargetScoreChange: (Int) -> Unit, //callback function to update the target score
    onNewGameClick: () -> Unit, //callback func for starting the new game
    onAboutClick: () -> Unit //callback func to display the about info
) {
    // Using a local state variable for the target input, initialized with the target score
    var targetInput by remember { mutableStateOf(targetScore.toString()) }
    Column(
        modifier = Modifier
            .fillMaxSize() //make the column fill the entire screen
            .padding(16.dp), //padding around content
        verticalArrangement = Arrangement.Center, //center content vertically
        horizontalAlignment = Alignment.CenterHorizontally //center the content horizontally
    ) {
        Text("Dice Game 🎲", fontSize = 38.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(30.dp))

        // Input field for setting the target score
        OutlinedTextField(
            value = targetInput, //display the current input value
            onValueChange = { newVal -> //update the input when user changes it
                targetInput = newVal
                //parse the input to an integer and update the target score if valid
                newVal.toIntOrNull()?.let { onTargetScoreChange(it) }
            },
            label = { Text("Target Score (Default Target is 101)", fontWeight = FontWeight.SemiBold) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        //row to arrange the new game and about buttons horizontally
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, //distribute buttons evenly
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onNewGameClick) {
                Text("New Game")
            }
            Button(onClick = onAboutClick) {
                Text("About")
            }
        }
    }
}
