package com.qyub.novis.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.qyub.novis.ui.screens.GameScreen
import com.qyub.novis.ui.screens.MenuScreen
import com.qyub.novis.ui.screens.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var showSplash by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        NavHost(
            navController = navController,
            startDestination = if (showSplash) "splash" else "menu"
        ) {
            composable("splash") {
                SplashScreen {
                    showSplash = false
                    navController.navigate("menu") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
            composable("menu") { MenuScreen(navController) }
            composable("game") { GameScreen(navController) }
        }
    }
}