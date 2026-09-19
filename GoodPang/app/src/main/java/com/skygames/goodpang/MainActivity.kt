package com.skygames.goodpang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.skygames.goodpang.ui.delivery.DeliveryScreen
import com.skygames.goodpang.ui.splash.SplashScreen
import com.skygames.goodpang.ui.theme.GoodPangTheme
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1200L

class MainActivity : ComponentActivity() {
    private var keepSystemSplashOnScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSystemSplashOnScreen }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoodPangTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    keepSystemSplashOnScreen = false
                    delay(SPLASH_DURATION_MS)
                    showSplash = false
                }

                if (showSplash) {
                    SplashScreen()
                } else {
                    DeliveryScreen()
                }
            }
        }
    }
}
