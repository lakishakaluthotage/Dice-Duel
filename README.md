# Dice-Duel
Implemented an Android dice game application in Kotlin using Jetpack Compose with full game logic,  computer AI strategy, and responsive UI.
📱 Overview

This project implements a two-player dice game where a human player competes against a computer opponent.
Both players roll five dice per turn, aiming to reach a configurable target score (default: 101) before the opponent.
The app follows the coursework specifications strictly, without using third-party libraries.

🎮 Game Rules

Both the human and computer players roll five dice simultaneously.

Each turn allows up to three rolls (one initial roll + two optional rerolls).

Players can choose to keep certain dice between rerolls.

After each turn, the total score is updated.

The first player to reach the target score wins.

In case of a tie, players roll all five dice repeatedly until the tie is broken (no rerolls allowed in tie-breaks).

🛠️ Tech Stack

Language: Kotlin

Framework: Jetpack Compose

IDE: Android Studio

Libraries: Only standard Android API libraries (no third-party dependencies).
