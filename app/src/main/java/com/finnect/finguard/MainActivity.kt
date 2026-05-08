package com.finnect.finguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.finnect.finguard.ui.FinGuardApp
import com.finnect.finguard.ui.theme.FinGuardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinGuardTheme {
                FinGuardApp()
            }
        }
    }
}
