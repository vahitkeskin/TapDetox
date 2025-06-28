package com.vahitkeskin.tapdetox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vahitkeskin.tapdetox.extensions.statusBarHeightDp
import com.vahitkeskin.tapdetox.screens.HomeScreen
import com.vahitkeskin.tapdetox.ui.theme.BackgroundDark
import com.vahitkeskin.tapdetox.ui.theme.TapDetoxTheme
import com.vahitkeskin.tapdetox.utils.TapDetoxPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapDetoxTheme {
                Scaffold(
                    modifier = Modifier
                        .background(BackgroundDark)
                        .fillMaxSize()
                        .padding(top = this.statusBarHeightDp())
                ) { innerPadding ->
                    HomeScreen()
                }
            }
        }
    }
}

@TapDetoxPreview
@Composable
fun GreetingPreview() {
    TapDetoxTheme {
        HomeScreen()
    }
}