package com.pokertrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pokertrainer.navigation.PokerTrainerApp
import com.pokertrainer.ui.theme.PokerTrainerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokerTrainerTheme {
                PokerTrainerApp()
            }
        }
    }
}
