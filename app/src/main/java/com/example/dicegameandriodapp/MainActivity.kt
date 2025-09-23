//Link to the video demonstration - https://drive.google.com/file/d/1dljRlBuSLM8qW7WtTK61v0Gj1LkVpnqY/view?usp=sharing
//UOW ID- W1867117
//IIT ID- 20210372


package com.example.dicegameandriodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.dicegameandriodapp.ui.theme.DiceGameApp

//Main activity class for the Dice Game Application,inheriting from ComponentActivity
class MainActivity : ComponentActivity() {
    //overrides the onCreate method to set up the activity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            //setting the content view using jetpack compose
        setContent {
            //calling the composable functions that sets up the theme and the main UI
            DiceGameApp()
        }
    }
}
