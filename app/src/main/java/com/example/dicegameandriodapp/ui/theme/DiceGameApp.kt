package com.example.dicegameandriodapp.ui.theme

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun DiceGameApp() {
    // Using a simple state string for navigation ("menu", "game", "about")
    var currentScreen by rememberSaveable { mutableStateOf("menu") }
    var targetScore by rememberSaveable { mutableStateOf(101) }
    // Win counters persist during the app session.
    var humanWins by rememberSaveable { mutableStateOf(0) }
    var computerWins by rememberSaveable { mutableStateOf(0) }

    when (currentScreen) {
        "menu" -> MenuScreen(
            targetScore = targetScore,
            onTargetScoreChange = { targetScore = it },
            onNewGameClick = { currentScreen = "game" },
            onAboutClick = { currentScreen = "about" }
        )
        "game" -> GameScreen(
            targetScore = targetScore,
            humanWins = humanWins,
            computerWins = computerWins,
            onWinUpdate = { newHuman, newComputer ->
                humanWins = newHuman
                computerWins = newComputer
            },
            onGameEnd = { currentScreen = "menu" },
            onResetTargetScore = {
                // Here we reset targetScore to the default:
                targetScore = 101
            }
        )
        "about" -> AboutScreen(onDismiss = { currentScreen = "menu"})
    }
}

