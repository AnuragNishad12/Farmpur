package com.example.farmpur

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.farmpur.ui.theme.FarmpurTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmpurTheme {
//                GetStartedActivity()
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent() {
    AppNavHost()
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "registrationScreen") {
        composable("registrationScreen") { RegistrationActivity(navController) }  // Call RegistrationScreen here
        composable("customerScreen") { CustomerScreen() }
        composable("farmerScreen") { FarmerScreen() }
    }
}

