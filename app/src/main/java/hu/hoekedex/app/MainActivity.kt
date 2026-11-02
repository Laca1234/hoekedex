package hu.hoekedex.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import hu.hoekedex.app.ui.HoekedexApp
import hu.hoekedex.app.ui.HoekedexTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HoekedexTheme {
                HoekedexApp()
            }
        }
    }
}
