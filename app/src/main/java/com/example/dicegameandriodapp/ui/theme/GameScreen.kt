package com.example.dicegameandriodapp.ui.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicegameandriodapp.R
import kotlin.random.Random

@Composable
fun GameScreen(
    targetScore: Int,
    humanWins: Int,
    computerWins: Int,
    onWinUpdate: (Int, Int) -> Unit, //callback fun that updates the win count when someone wins
    onGameEnd: () -> Unit, //callback fun that triggers game end
    //Reset the target score
    onResetTargetScore: () -> Unit
) {
    // Handle the Android Back button.
    BackHandler(enabled = true) {
        onGameEnd()
    }

    var rollCount by rememberSaveable { mutableStateOf(0) }
    var turnNumber by rememberSaveable { mutableStateOf(1) }
    var humanTotalScore by rememberSaveable { mutableStateOf(0) }
    var computerTotalScore by rememberSaveable { mutableStateOf(0) }
    var gameEnded by rememberSaveable { mutableStateOf(false) }
    var tieBreaker by rememberSaveable { mutableStateOf(false) }

    val humanDice = remember { mutableStateListOf(0, 0, 0, 0, 0) }
    val humanHeld = remember { mutableStateListOf(false, false, false, false, false) }
    val computerDice = remember { mutableStateListOf(0, 0, 0, 0, 0) }

    var tieBreakerHumanDice by remember { mutableStateOf(List(5) { 0 }) }
    var tieBreakerComputerDice by remember { mutableStateOf(List(5) { 0 }) }
    var tieBreakerResult by remember { mutableStateOf("") }

    //Func to roll single dice and get random values between 1 and 6 including both
    fun rollDie() = Random.nextInt(1, 7)

    //Func to roll the human dice
    fun rollHumanDice() { //iterate over each dice in humanDice list
        for (i in humanDice.indices) {
            if (!humanHeld[i]) { //check if the die is not locked in place
                humanDice[i] = rollDie() //roll dice and update values
            }
        }
    }

    /**
     * 12th Specification
     * Computer Strategy Implementation
     *
     * The function allows simulation of the computer's move through its
     * maximum of three roll attempts (involving its initial roll together with potential re-rolls). .
     *
     * The strategy:
     * 1.  Verify whether the computer currently shows a lower score than the human.If trailing, use aggressive mode
     *    - The specific target total sets to 22 while the computer system uses the dice re-roll process for values under 5
     *    Otherwise (tied or ahead):
     *    - The computer operates with an aim of reaching 18 with the option to roll again any dice worth under 4.
     *
     * 2.  When performing a re-roll operation the computer system should roll again
     * only dice that produce sums currently lower than the predetermined target value.
     * Advantages:
     * - Taking more risks against the opponent becomes necessary for players who fall behind in the score.
     * - Preserves high-value dice.
     *
     * Disadvantages:
     * - The use of heuristic thresholds does not always deliver optimal results throughout all situations.
     *
     * @param initialDice The dice values from the initial roll.
     * @param currentRoll The current roll number.
     * @param humanScore The human player's total score.
     * @param computerScore The computer player's total score.
     * @return A list of dice values after applying the strategy.
     */
    fun simulateComputerTurn(
        initialDice: List<Int>, //list of dice values computer start with
        currentRoll: Int,
        humanScore: Int,
        computerScore: Int
    ): List<Int> { //list of int representing the dice values of computer's turn

        var dice = initialDice.toMutableList()
        var currentRollNumber = currentRoll

        //isTrailing is a boolean indicating if the computer score is less than human score
        val isTrailing = computerScore < humanScore
        //targetSum -score that computer aims to score during its turn
        val targetSum = if (isTrailing) 22 else 18 //if trailing, become more aggressive with a higher target
        //rerollThreshold -the minimum value dia that computer is willing to keep
        val rerollThreshold = if (isTrailing) 5 else 4

        while (currentRollNumber < 3) {
            val currentSum = dice.sum()
            if (currentSum >= targetSum) break
            for (i in dice.indices) {
                if (dice[i] < rerollThreshold) {
                    dice[i] = rollDie()
                }
            }
            currentRollNumber++
        }
        return dice //returns the updates list of dice values after computer turn is done
    }

    //fun to calculate the score for the current turn and increment the total scores
    fun scoreTurn() {
        val humanScoreThisTurn = humanDice.sum() //sum up the human dice values

        //determine the final dice value of the computer
        //if the roll count < 3, simulate the computer's turn to potentially re-roll dice
        val finalComputerDice = if (rollCount < 3) {
            simulateComputerTurn(computerDice, rollCount, humanTotalScore, computerTotalScore)
        } else {
            computerDice.toList() //else current dice values are scored without re-rolling
        }

        //calculate the computer's score for this turn
        val computerScoreThisTurn = finalComputerDice.sum()
        //update human player score and computer player score for this turn
        humanTotalScore += humanScoreThisTurn
        computerTotalScore += computerScoreThisTurn

        //update the computer's dice list with the final dice values obtained from simulation
        for (i in computerDice.indices) {
            computerDice[i] = finalComputerDice[i]
        }

        //check if either the human player or computer player has reached the target score
        if (humanTotalScore >= targetScore || computerTotalScore >= targetScore) {
            //update the win count accordingly,with the player's winning count incremented
            if (humanTotalScore > computerTotalScore) {
                gameEnded = true
                onWinUpdate(humanWins + 1, computerWins)
            } else if (computerTotalScore > humanTotalScore) {
                gameEnded = true
                onWinUpdate(humanWins, computerWins + 1)
            } else { //else if both player scores are equal, trigger the tie-breaker round
                tieBreaker = true
            }
        }

        //of the game is not ended and its not a tie, reset the roll count and no of turns for the next turn
        if (!gameEnded && !tieBreaker) {
            rollCount = 0
            turnNumber++
            //reset the "held" status of the dice, so it's available to be re-rolled
            for (i in humanHeld.indices) {
                humanHeld[i] = false
            }
        }
    }
    //if the human dice and the computer dice get the same score after a roll, then  tie breaker roll
    fun tieBreakerRoll() {
        tieBreakerHumanDice = List(5) { rollDie() }//generate a list of 5 random dice rolls for H
        tieBreakerComputerDice = List(5) { rollDie() }//generate a list of 5 random dice rolls for C
        val sumHuman = tieBreakerHumanDice.sum()
        val sumComputer = tieBreakerComputerDice.sum()

        //compare the sums and output the winning player
        when {
            sumHuman > sumComputer -> {
                tieBreakerResult = "You win! 🏆"
                onWinUpdate(humanWins + 1, computerWins) //updates the human win count
                tieBreaker = false //reset the tie-breaker flag
                gameEnded = true //game ended
            }
            sumComputer > sumHuman -> {
                tieBreakerResult = "You lose, Computer Win "
                onWinUpdate(humanWins, computerWins + 1)//updates the computer win count
                tieBreaker = false //reset the tie breaker flag
                gameEnded = true //game ended
            }
            else -> {
                tieBreakerResult = "Tie. Roll again 🎲." // if again its a tie, re-roll again
            }
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(28.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("H:$humanWins/C:$computerWins", fontSize = 18.sp,fontWeight = FontWeight.Bold)
            Text("Score: H $humanTotalScore - C $computerTotalScore", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!tieBreaker) {
            Text("Turn: $turnNumber  Re-Roll: ${if (rollCount == 0) "-" else rollCount}", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(18.dp))
        }

        Text("Human Dice 👧🏻:", fontSize = 18.sp,fontWeight = FontWeight.Bold)
        DiceRow(diceValues = humanDice, held = humanHeld, onDiceClick = { index ->
            if (rollCount in 1 until 3) {
                if (humanHeld[index]) {
                    humanHeld[index] = false
                } else {
                    for (i in humanHeld.indices) {
                        humanHeld[i] = false
                    }
                    humanHeld[index] = true
                }
            }
        })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Computer Dice 🤖:", fontSize = 18.sp,fontWeight = FontWeight.Bold)
        DiceRow(diceValues = computerDice, held = null)
        Spacer(modifier = Modifier.height(16.dp))


        if (tieBreaker) {

            Text("Tie-breaker round!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { tieBreakerRoll() }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Roll Tie-Break")
            }
            Spacer(modifier = Modifier.height(8.dp))
            //check if both tie breaker dice lists are not empty,indicating a tie breaker round
            if (tieBreakerHumanDice.isNotEmpty() && tieBreakerComputerDice.isNotEmpty()) {
                //display the human player's tie breaker dice values as a string
                Text("Your tie-break dice: ${tieBreakerHumanDice.joinToString(" ")}")
                //display the computer player's tie breaker dice values as a string
                Text("Computer tie-break dice: ${tieBreakerComputerDice.joinToString(" ")}"
                )
                //display the result of  the tie-breaker
                Text(tieBreakerResult, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        } else { //if its not a tie breaker situation,display the buttons for game actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly //distribute buttons evenly
            ) {
                //Button for throwing the dice
                Button(onClick = {
                    if (!gameEnded) { //check if the game is ended
                        if (rollCount == 0) { //if it's the first roll of the turn
                            for (i in humanDice.indices) { //roll of the human dice
                                humanDice[i] = rollDie()
                            }
                            //roll of the computer dice
                            for (i in computerDice.indices) {
                                computerDice[i] = rollDie()
                            }
                        }//if the player has already rolled at least once but no more than twice
                        else if (rollCount in 1 until 3) {
                            rollHumanDice() //perform another roll for human player
                        }
                        rollCount++ //increment the roll count to track the number of rolls
                        //reset all "held" dice flags after a roll to ensure they are not locked
                        for (i in humanHeld.indices) {
                            humanHeld[i] = false
                        }
                        //if roll count is 3, calculate and score the turn automatically
                        if (rollCount == 3) {
                            scoreTurn()
                        }
                    }
                }) {
                    Text("Throw")
                }
                //button for manually scoring the current turn
                Button(onClick = {
                    //of the game is not ended and the player has made at least one roll
                    if (!gameEnded && rollCount in 1 until 3) {
                        //calculate and score the current turn
                        scoreTurn()
                    }
                }) {
                    Text("Score")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (gameEnded) {
            val resultText = if (humanTotalScore > computerTotalScore) "You win 🏆!" else "You lose, Computer Wins! "
            val resultColor = if (humanTotalScore > computerTotalScore) Color.Green else Color.Red
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Game Over 🎮", color =Color.Black, fontWeight = FontWeight.Bold)},
                text = { Text("$resultText!\n\n", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color =resultColor)},
                confirmButton = {
                    Button(onClick = {
                        // 1) Reset the target score:
                        onResetTargetScore()
                        // 2) Then navigate back (end the game screen):
                        onGameEnd()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}
//Display a row of dice with the optional click and hold functionalities
@Composable
fun DiceRow(
    diceValues: List<Int>,
    held: List<Boolean>? = null,//optional list indicating which dice is held
    onDiceClick: ((Int) -> Unit)? = null //optional callback fun triggered when a dice is clicked
) {
    //display a row to arrange the dice horizontally
    Row(modifier = Modifier.fillMaxWidth(), //Row fills the entire width of the screen
        horizontalArrangement = Arrangement.Center //center the dice horizontally within the row
    ) {
        //loops through each dice value using its index
        diceValues.forEachIndexed { index, value ->
            val imageRes = when (value) {
                1 -> R.drawable.diceface_1
                2 -> R.drawable.diceface_2
                3 -> R.drawable.diceface_3
                4 -> R.drawable.diceface_4
                5 -> R.drawable.diceface_5
                6 -> R.drawable.diceface_6
                else -> R.drawable.diceface_1 //fallback to dice face 1 if value is out of range
            }
            //Create a box layout to hold each dice image with clickable options
            Box(
                modifier = Modifier
                    .padding(4.dp) //add space around each dice
                    .size(60.dp)  //fixed size for each dice
                    .then(if (onDiceClick != null) Modifier.clickable { onDiceClick(index) } else Modifier),
                contentAlignment = Alignment.Center ///center the image within the box
            ) {
                //display the dice image inside the box
                Image(
                    painter = painterResource(id = imageRes), //load appropriate dice face image
                    contentDescription = "Dice: $value",
                    modifier = Modifier
                        .fillMaxSize()
                        //if the dice is held, red broader is drown to show that it is selected
                        .then(
                            if (held != null && held.getOrNull(index) == true)
                                Modifier.border(BorderStroke(4.dp, Color.Red), shape = RoundedCornerShape(4.dp)
                                )
                            else Modifier //no border if the dice is not held
                        )
                )
            }
        }
    }
}
