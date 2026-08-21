package com.example.vigil

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.vigil.ui.theme.VigilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            VigilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    OnboardingScreen(
                        onFinished = {
                            // TODO: navigate to your home/dashboard screen
                        },
                        onLoginClick = {
                            // TODO: navigate to login screen
                        }
                    )
                }
            }
        }
    }
}